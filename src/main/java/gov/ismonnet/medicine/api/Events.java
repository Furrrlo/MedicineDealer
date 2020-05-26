package gov.ismonnet.medicine.api;

import gov.ismonnet.medicine.aifa.MedicineService;
import gov.ismonnet.medicine.authentication.Authenticated;
import gov.ismonnet.medicine.authentication.Authenticator;
import gov.ismonnet.medicine.authentication.AuthorizedEvent;
import gov.ismonnet.medicine.database.Tables;
import gov.ismonnet.medicine.database.enums.EventiCadenza;
import gov.ismonnet.medicine.database.tables.records.AssunzioniRecord;
import gov.ismonnet.medicine.database.tables.records.EventiRecord;
import gov.ismonnet.medicine.database.tables.records.OrariRecord;
import gov.ismonnet.medicine.jaxb.ws.*;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Comparator;
import java.util.*;
import java.util.stream.Collectors;

import static gov.ismonnet.medicine.utils.EventUtils.*;

@Path("eventi")
public class Events {

    private final DSLContext ctx;
    private final Authenticator authenticator;
    private final MedicineService medicineService;

    @Inject Events(final DSLContext ctx,
                   final Authenticator authenticator,
                   final MedicineService medicineService) {
        this.ctx = ctx;
        this.authenticator = authenticator;
        this.medicineService = medicineService;
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public CalendarBean getEvents(@Authenticated int userId,
                                  @QueryParam(value = "id_porta_medicine") Integer deviceId,
                                  @QueryParam(value = "data") LocalDate date,
                                  @NotNull @QueryParam(value = "granularita") Granularity granularity) {
        if(date == null)
            date = LocalDate.now();
        if(deviceId != null)
            authenticator.checkAuthorizedForDevice(userId, deviceId);

        final LocalDate startDate;
        final LocalDate endDate;

        switch (granularity) {
            case DAY:
                startDate = endDate = date;
                break;
            case WEEK:
                final DayOfWeek firstDayOfWeek = WeekFields.ISO.getFirstDayOfWeek();
                final DayOfWeek lastDayOfWeek = DayOfWeek.of(((firstDayOfWeek.getValue() + 5) % DayOfWeek.values().length) + 1);

                startDate = date.with(TemporalAdjusters.previousOrSame(firstDayOfWeek));
                endDate = date.with(TemporalAdjusters.nextOrSame(lastDayOfWeek));
                break;
            case MONTH:
                startDate = date.with(TemporalAdjusters.firstDayOfMonth());
                endDate = date.with(TemporalAdjusters.lastDayOfMonth());
                break;
            case YEAR:
                startDate = date.with(TemporalAdjusters.firstDayOfYear());
                endDate = date.with(TemporalAdjusters.lastDayOfYear());
                break;
            case ALL:
                startDate = endDate = null;
                break;
            default:
                throw new AssertionError("Unimplemented value: " + granularity);
        }

        final List<EventWithAssunzioni> events = fetchEvent(ctx, Tables.ASSOCIATI.ID_UTENTE.eq(userId)
                .and(deviceId == null ?
                        DSL.noCondition() :
                        Tables.ASSOCIATI.ID_PORTA_MEDICINE.eq(deviceId))
                .and(granularity == Granularity.ALL ?
                        DSL.noCondition() :
                        // If the event is not repeated, check if the date is between the two
                        // otherwise it's handled further down
                        Tables.EVENTI.CADENZA.isNotNull().or(Tables.EVENTI.DATA.between(Date.valueOf(startDate), Date.valueOf(endDate))))
        );
        // Generate missing assumptions
        final LocalDate today = LocalDate.now();
        final LocalTime now = LocalTime.now();

        if(granularity != Granularity.ALL)
            events.forEach(event -> {
                // If it already happened and it's all done, no need to generate anything
                if(event.isFinito())
                    return;
                // Already sorted out in the query
                if(event.getCadenza() == null)
                    return;

                final Map<Map.Entry<LocalDate, LocalTime>, Assunzione> assumptions = new HashMap<>();
                // Populate already existing ones
                if(event.getAssunzioni() != null)
                    event.getAssunzioni().stream()
                            .filter(a -> !a.getData().isBefore(startDate))
                            .filter(a -> !a.getData().isAfter(endDate))
                            .forEach(assumption -> assumptions.put(
                                    new AbstractMap.SimpleEntry<>(assumption.getData(), assumption.getOra()),
                                    assumption));
                // For all the absent ones (which are supposed to be new), create new ones
                getAllDates(event, today, endDate).forEach(d -> event.getOrari().stream()
                        .filter(o -> d.isAfter(today) || o.isAfter(now))
                        .forEach(o -> assumptions.computeIfAbsent(
                                new AbstractMap.SimpleEntry<>(d, o),
                                e -> new Assunzione()
                                        .withData(e.getKey())
                                        .withOra(e.getValue()))));

                final List<Assunzione> newAssumptions = new ArrayList<>(assumptions.values());
                newAssumptions.sort(Comparator
                        .comparing(Assunzione::getData)
                        .thenComparing(Assunzione::getOra));
                event.setAssunzioni(newAssumptions);
            });

        events.removeIf(e -> e.getAssunzioni().isEmpty());
        events.sort(Comparator.comparing(EventWithAssunzioni::getData));
        return new CalendarBean(events);
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public String addEvent(@Authenticated int userId,
                           @NotNull NewEventBean eventBean) {
        final ImmutableEventBase event = eventBean.getValue();
        authenticator.checkAuthorizedForDevice(userId, event.getIdPortaMedicine().intValue());

        return ctx.transactionResult(conf -> {
            final DSLContext ctx = conf.dsl();

            final Medicina medicine = medicineService.getMedicineByAic(event.getAicFarmaco());
            if(medicine == null)
                throw new BadRequestException("Invalid medicine AIC");
            ctx.insertInto(Tables.FARMACI)
                    .set(Tables.FARMACI.COD_AIC, UInteger.valueOf(medicine.getAicFarmaco()))
                    .set(Tables.FARMACI.NOME, medicine.getName())
                    .onDuplicateKeyIgnore()
                    .execute();

            final InsertSetMoreStep<EventiRecord> insert = ctx.insertInto(Tables.EVENTI)
                    .set(Tables.EVENTI.ID_PORTA_MEDICINE, event.getIdPortaMedicine().intValue())
                    .set(Tables.EVENTI.AIC_FARMACO, UInteger.valueOf(event.getAicFarmaco()))
                    .set(Tables.EVENTI.DATA, Date.valueOf(event.getData()));

            final Cadenza cadenza = event.getCadenza();
            if(cadenza != null) {
                if(cadenza.getGiornaliera() != null) {
                    insert.set(Tables.EVENTI.CADENZA, EventiCadenza.giornaliera);
                    insert.set(Tables.EVENTI.INTERVALLO, cadenza.getIntervallo().intValue());
                } else if(cadenza.getSettimanale() != null) {
                    final Settimana week = cadenza.getSettimanale();

                    insert.set(Tables.EVENTI.CADENZA, EventiCadenza.settimanale);
                    insert.set(Tables.EVENTI.INTERVALLO, cadenza.getIntervallo().intValue());
                    insert.set(Tables.EVENTI.GIORNI_SETTIMANA, UByte.valueOf(weekToBitmask(week)));
                } else {
                    throw new AssertionError("XML cadenza choice element wasn't respected");
                }

                final FineCadenza fine = cadenza.getFine();
                if(fine != null) {
                    if(fine.getData() != null)
                        insert.set(Tables.EVENTI.DATA_FINE_INTERVALLO, Date.valueOf(fine.getData()));
                    else if(fine.getOccorenze() != null)
                        insert.set(Tables.EVENTI.OCCORRENZE_FINE_INTERVALLO, fine.getOccorenze().intValue());
                    else
                        throw new AssertionError("XML fine choice element wasn't respected");
                }
            }

            final int eventId = insert
                    .returningResult(Tables.EVENTI.fields())
                    .fetchOne()
                    .map(r -> r.getValue(Tables.EVENTI.ID));

            final InsertValuesStep2<OrariRecord, Integer, Time> insertHours = ctx
                    .insertInto(Tables.ORARI)
                    .columns(Tables.ORARI.ID_EVENTO, Tables.ORARI.ORA);
            event.getOrari().forEach(hour -> insertHours.values(eventId, Time.valueOf(hour)));
            insertHours.execute();

            return "<id>" + eventId + "</id>";
        });
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Path("{id_evento}")
    public void editEvent(@Authenticated int userId,
                          @AuthorizedEvent @PathParam(value = "id_evento") int oldEventId,
                          @NotNull EditEventBean eventBean) {
        final MutableEventBase eventEdit = eventBean.getValue();
        ctx.transaction(conf -> {
            final DSLContext ctx = conf.dsl();

            final Record record = ctx.select(Tables.EVENTI.fields())
                    .from(Tables.EVENTI)
                    .where(Tables.EVENTI.ID.eq(oldEventId))
                    .forShare()
                    .fetchOne();
            final EventWithAssunzioni oldEvent = fetchEvent(ctx, Tables.EVENTI.ID.eq(oldEventId)).get(0);

            // If it's a non repeating event it can only be edited if it did not happen yet
            if(record.get(Tables.EVENTI.FINITO) == 1)
                throw new BadRequestException("Cannot edit past request");

            if(eventEdit.getAicFarmaco() != null)
                record.set(Tables.EVENTI.AIC_FARMACO, UInteger.valueOf(eventEdit.getAicFarmaco()));
            if(eventEdit.getData() != null)
                record.set(Tables.EVENTI.DATA, Date.valueOf(eventEdit.getData()));

            if(eventEdit.getEliminaCadenza() != null) {
                record.set(Tables.EVENTI.CADENZA, null);
                record.set(Tables.EVENTI.INTERVALLO, null);
                record.set(Tables.EVENTI.GIORNI_SETTIMANA, null);
                record.set(Tables.EVENTI.DATA_FINE_INTERVALLO, null);
                record.set(Tables.EVENTI.OCCORRENZE_FINE_INTERVALLO, null);
            } else if(eventEdit.getCadenza() != null) {
                final DeletableCadenza cadenza = eventEdit.getCadenza();

                if(cadenza.getGiornaliera() != null) {
                    record.set(Tables.EVENTI.CADENZA, EventiCadenza.giornaliera);
                    record.set(Tables.EVENTI.INTERVALLO, cadenza.getIntervallo().intValue());
                    record.set(Tables.EVENTI.GIORNI_SETTIMANA, null);
                } else if(cadenza.getSettimanale() != null) {
                    final Settimana week = cadenza.getSettimanale();

                    record.set(Tables.EVENTI.CADENZA, EventiCadenza.settimanale);
                    record.set(Tables.EVENTI.INTERVALLO, cadenza.getIntervallo().intValue());
                    record.set(Tables.EVENTI.GIORNI_SETTIMANA, UByte.valueOf(weekToBitmask(week)));
                }

                if(cadenza.getEliminaFine() != null) {
                    record.set(Tables.EVENTI.DATA_FINE_INTERVALLO, null);
                    record.set(Tables.EVENTI.OCCORRENZE_FINE_INTERVALLO, null);
                } else if(cadenza.getFine() != null) {
                    final FineCadenza fine = cadenza.getFine();

                    if(fine.getData() != null) {
                        record.set(Tables.EVENTI.DATA_FINE_INTERVALLO, Date.valueOf(fine.getData()));
                        record.set(Tables.EVENTI.OCCORRENZE_FINE_INTERVALLO, null);
                    } else if(fine.getOccorenze() != null) {
                        record.set(Tables.EVENTI.DATA_FINE_INTERVALLO, null);
                        record.set(Tables.EVENTI.OCCORRENZE_FINE_INTERVALLO, fine.getOccorenze().intValue());
                    }
                }
            }

            // Set the old record as finished
            ctx.update(Tables.EVENTI)
                    .set(Tables.EVENTI.FINITO, (byte) 1)
                    .where(Tables.EVENTI.ID.eq(oldEventId))
                    .execute();
            // Insert all the missing assumptions on the old one
            final InsertValuesStep3<AssunzioniRecord, Integer, Date, Time> insertAssumptions = ctx
                    .insertInto(Tables.ASSUNZIONI)
                    .columns(Tables.ASSUNZIONI.ID_EVENTO, Tables.ASSUNZIONI.DATA, Tables.ASSUNZIONI.ORA);

            final LocalDate today = LocalDate.now();
            final LocalTime now = LocalTime.now();
            getAllDates(oldEvent, today, today).forEach(d -> oldEvent.getOrari().stream()
                    .filter(o -> !d.isAfter(today) && o.isBefore(now))
                    .forEach(o -> insertAssumptions.values(oldEventId, Date.valueOf(d), Time.valueOf(o))));

            insertAssumptions // The unique constraint will prevent stuff from being duped
                    .onDuplicateKeyIgnore()
                    .execute();
            // Insert a new record with the edits
            record.changed(true);
            record.changed(Tables.EVENTI.ID, false); // Do not insert the old ID
            final int newEventId = ctx
                    .insertInto(Tables.EVENTI)
                    .set(record)
                    .returningResult(Tables.EVENTI.fields())
                    .fetchOne()
                    .map(r -> r.getValue(Tables.EVENTI.ID));

            final InsertValuesStep2<OrariRecord, Integer, Time> insertHours = ctx
                    .insertInto(Tables.ORARI)
                    .columns(Tables.ORARI.ID_EVENTO, Tables.ORARI.ORA);
            if(eventEdit.getOrari() == null) // Clone the old ones
                oldEvent.getOrari().forEach(hour -> insertHours.values(newEventId, Time.valueOf(hour)));
            else // Put in the new ones
                eventEdit.getOrari().forEach(hour -> insertHours.values(oldEventId, Time.valueOf(hour)));
            insertHours.execute();
        });
    }

    private List<EventWithAssunzioni> fetchEvent(final DSLContext ctx, final Condition condition) {
        final Field<String> codAicRow = Tables.FARMACI.COD_AIC.cast(SQLDataType.CHAR(9));
        return ctx.select(
                Tables.EVENTI.ID, Tables.EVENTI.ID_PORTA_MEDICINE, Tables.EVENTI.DATA, Tables.EVENTI.FINITO,
                Tables.EVENTI.CADENZA, Tables.EVENTI.INTERVALLO, Tables.EVENTI.GIORNI_SETTIMANA,
                Tables.EVENTI.DATA_FINE_INTERVALLO, Tables.EVENTI.OCCORRENZE_FINE_INTERVALLO,
                Tables.ORARI.ORA,
                codAicRow, Tables.FARMACI.NOME,
                Tables.ASSUNZIONI.DATA, Tables.ASSUNZIONI.DATA_REALE, Tables.ASSUNZIONI.ORA_REALE
        )
                .from(Tables.EVENTI)

                .join(Tables.ASSOCIATI)
                .on(Tables.EVENTI.ID_PORTA_MEDICINE.eq(Tables.ASSOCIATI.ID_PORTA_MEDICINE))

                .join(Tables.FARMACI)
                .on(Tables.EVENTI.AIC_FARMACO.eq(Tables.FARMACI.COD_AIC))

                .join(Tables.ORARI)
                .on(Tables.EVENTI.ID.eq(Tables.ORARI.ID_EVENTO))

                .leftJoin(Tables.ASSUNZIONI)
                .on(Tables.EVENTI.ID.eq(Tables.ASSUNZIONI.ID_EVENTO))

                .where(condition)

                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        r -> {
                            final EventiCadenza cadenza = r.get(Tables.EVENTI.CADENZA);
                            final UByte week = r.get(Tables.EVENTI.GIORNI_SETTIMANA);

                            final Date endIntervalDate = r.get(Tables.EVENTI.DATA_FINE_INTERVALLO);
                            final Integer endOccurrences = r.get(Tables.EVENTI.OCCORRENZE_FINE_INTERVALLO);

                            return new EventWithAssunzioni()
                                    .withId(BigInteger.valueOf(r.get(Tables.EVENTI.ID)))
                                    .withIdPortaMedicine(BigInteger.valueOf(r.get(Tables.EVENTI.ID_PORTA_MEDICINE)))
                                    .withAicFarmaco(r.get(codAicRow))
                                    .withNomeFarmaco(r.get(Tables.FARMACI.NOME))
                                    .withData(r.get(Tables.EVENTI.DATA).toLocalDate())
                                    .withFinito(r.get(Tables.EVENTI.FINITO) == 1)
                                    .withCadenza(cadenza == null ? null :
                                            new Cadenza()
                                                    .withIntervallo(BigInteger.valueOf(r.get(Tables.EVENTI.INTERVALLO)))
                                                    .withGiornaliera(cadenza != EventiCadenza.giornaliera ?
                                                            null :
                                                            new Emtpy())
                                                    .withSettimanale(cadenza != EventiCadenza.settimanale ?
                                                            null :
                                                            bitmaskToWeek(week.intValue()))
                                                    .withFine((endIntervalDate == null && endOccurrences == null) ? null :
                                                            new FineCadenza()
                                                                    .withData(endIntervalDate != null ? endIntervalDate.toLocalDate() : null)
                                                                    .withOccorenze(endOccurrences != null ? BigInteger.valueOf(endOccurrences) : null))
                                    );
                        }, Collectors.mapping(r -> r, Collectors.toSet())
                ))
                .entrySet()
                .stream()
                .map(e -> e.getKey()
                        .withOrari(e.getValue().stream()
                                .map(r -> r.get(Tables.ORARI.ORA).toLocalTime())
                                .collect(Collectors.toList()))
                        .withAssunzioni(e.getValue().stream()
                                .filter(r -> r.get(Tables.ASSUNZIONI.DATA) != null)
                                .map(r -> {
                                    final Date assumptionRealDate = r.get(Tables.ASSUNZIONI.DATA_REALE);
                                    final Time assumptionRealTime = r.get(Tables.ASSUNZIONI.ORA_REALE);
                                    return new Assunzione()
                                            .withData(r.get(Tables.ASSUNZIONI.DATA).toLocalDate())
                                            .withOra(r.get(Tables.ORARI.ORA).toLocalTime())
                                            .withDataReale(assumptionRealDate != null ? assumptionRealDate.toLocalDate() : null)
                                            .withOraReale(assumptionRealTime != null ? assumptionRealTime.toLocalTime() : null);
                                })
                                .collect(Collectors.toList()))
                )
                .collect(Collectors.toList());
    }

    public enum Granularity {
        DAY, WEEK, MONTH, YEAR, ALL;

        public static Granularity fromString(String param) {
            try {
                return valueOf(param.toUpperCase());
            } catch (Exception e) {
                return null;
            }
        }
    }
}
