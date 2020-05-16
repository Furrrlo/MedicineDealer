package gov.ismonnet.medicine.api;

import gov.ismonnet.medicine.authentication.Authenticated;
import gov.ismonnet.medicine.database.Tables;
import gov.ismonnet.medicine.database.enums.EventiCadenza;
import gov.ismonnet.medicine.database.tables.records.EventiRecord;
import gov.ismonnet.medicine.jaxb.ws.*;
import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.impl.DSL;
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
import java.util.*;
import java.util.stream.Collectors;

import static gov.ismonnet.medicine.utils.EventUtils.*;

@Path("eventi")
public class Events {

    private final DSLContext ctx;

    @Inject Events(DSLContext ctx) {
        this.ctx = ctx;
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
            checkAuthorizedForDevice(userId, deviceId);

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

        final List<EventWithAssunzioni> events = ctx.select(
                Tables.EVENTI.ID, Tables.EVENTI.ID_PORTA_MEDICINE, Tables.EVENTI.DATA,
                Tables.EVENTI.CADENZA, Tables.EVENTI.INTERVALLO, Tables.EVENTI.GIORNI_SETTIMANA,
                Tables.EVENTI.DATA_FINE_INTERVALLO, Tables.EVENTI.OCCORRENZE_FINE_INTERVALLO,
                Tables.ORARI.ORA,
                Tables.FARMACI.COD_AIC, Tables.FARMACI.NOME,
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
                .on(Tables.ORARI.ID.eq(Tables.ASSUNZIONI.ID_ORARIO))

                .where(Tables.ASSOCIATI.ID_UTENTE.eq(userId))
                .and(deviceId == null ?
                        DSL.noCondition() :
                        Tables.ASSOCIATI.ID_PORTA_MEDICINE.eq(deviceId))
                .and(granularity == Granularity.ALL ?
                        DSL.noCondition() :
                        // If the event is not repeated, check if the date is between the two
                        // otherwise it's handled further down
                        Tables.EVENTI.CADENZA.isNotNull()
                                .or(Tables.EVENTI.DATA.between(Date.valueOf(startDate), Date.valueOf(endDate))))

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
                                    .withAicFarmaco(r.get(Tables.FARMACI.COD_AIC).toBigInteger())
                                    .withData(r.get(Tables.EVENTI.DATA).toLocalDate())
                                    .withCadenza(cadenza == null ? null :
                                            new Cadenza()
                                                    .withIntervallo(BigInteger.valueOf(r.get(Tables.EVENTI.INTERVALLO)))
                                                    .withGiornaliera(cadenza != EventiCadenza.giornaliera ?
                                                            null :
                                                            new Object())
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
        // Generate missing assumptions
        events.forEach(event -> {
            // Already sorted out in the query
            if(event.getCadenza() == null)
                return;

            final Map<Map.Entry<LocalDate, LocalTime>, Assunzione> assumptions = new HashMap<>();
            // Populate already existing ones
            if(event.getAssunzioni() != null)
                event.getAssunzioni().stream()
                        .filter(a -> granularity == Granularity.ALL || a.getData().isAfter(startDate) || a.getData().isEqual(startDate))
                        .filter(a -> granularity == Granularity.ALL || a.getData().isBefore(endDate) || a.getData().isEqual(endDate))
                        .forEach(assumption -> assumptions.put(
                                new AbstractMap.SimpleEntry<>(assumption.getData(), assumption.getOra()),
                                assumption));
            // For all the absent ones, create a new one
            final Set<LocalDate> allDates = granularity == Granularity.ALL ?
                    getAllDates(event) :
                    getAllDates(event, startDate, endDate);
            allDates.forEach(d -> event.getOrari().forEach(o -> assumptions.computeIfAbsent(
                    new AbstractMap.SimpleEntry<>(d, o),
                    e -> new Assunzione()
                            .withData(e.getKey())
                            .withOra(e.getValue()))));
        });

        return new CalendarBean(events);
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public String addEvent(@Authenticated int userId,
                           @NotNull NewEventBean eventBean) {
        final ImmutableEventBase event = eventBean.getValue();
        checkAuthorizedForDevice(userId, event.getIdPortaMedicine().intValue());

        final InsertSetMoreStep<EventiRecord> insert = ctx.insertInto(Tables.EVENTI)
                .set(Tables.EVENTI.ID_PORTA_MEDICINE, event.getIdPortaMedicine().intValue())
                .set(Tables.EVENTI.AIC_FARMACO, UInteger.valueOf(event.getAicFarmaco().longValueExact()))
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

        return "<id>" + insert
                .returningResult(Tables.EVENTI.fields())
                .fetchOne()
                .map(r -> r.getValue(Tables.EVENTI.ID)) +
                "</id>";
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Path("{id_evento}")
    public void editEvent(@Authenticated int userId,
                          @PathParam(value = "id_evento") int eventId,
                          @NotNull EditEventBean eventBean) {
        // TODO: edit
//        final MutableEvent event = eventBean.getValue();
//        checkAuthorizedForEvent(userId, eventId);
//
//        final UpdateSetStep<EventiRecord> update = ctx.update(Tables.EVENTI);
//        if(event.getData() != null)
//            update.set(Tables.EVENTI.DATA, Date.valueOf(event.getData()));
//        if(event.getOra() != null)
//            update.set(Tables.EVENTI.ORA, Time.valueOf(event.getOra()));
//        if(event.getAicFarmaco() != null)
//            update.set(Tables.EVENTI.AIC_FARMACO, UInteger.valueOf(event.getAicFarmaco().longValueExact()));
//
//        if(!(update instanceof UpdateSetMoreStep))
//            return;
//        ((UpdateSetMoreStep<EventiRecord>) update)
//                .where(Tables.EVENTI.ID.eq(eventId))
//                .execute();
    }

    private void checkAuthorizedForDevice(int userId, int deviceId) {
        // Check if the device exists
        if(!ctx.select()
                .from(Tables.PORTA_MEDICINE)
                .where(Tables.PORTA_MEDICINE.ID.eq(deviceId))
                .fetchOptional()
                .isPresent())
            throw new NotFoundException();
        // Check if the existing device is associated to the user
        if(!ctx.select()
                .from(Tables.ASSOCIATI)
                .where(Tables.ASSOCIATI.ID_PORTA_MEDICINE.eq(deviceId))
                .and(Tables.ASSOCIATI.ID_UTENTE.eq(userId))
                .fetchOptional()
                .isPresent())
            throw new ForbiddenException();
    }

    private void checkAuthorizedForEvent(int userId, int eventId) {
        // Check if the event exists
        if(!ctx.select()
                .from(Tables.EVENTI)
                .where(Tables.EVENTI.ID.eq(eventId))
                .fetchOptional()
                .isPresent())
            throw new NotFoundException();
        // Check if the existing event device is associated to the user
        if(!ctx.select()
                .from(Tables.EVENTI)
                .join(Tables.ASSOCIATI)
                .on(Tables.EVENTI.ID_PORTA_MEDICINE.eq(Tables.ASSOCIATI.ID_PORTA_MEDICINE))
                .where(Tables.EVENTI.ID.eq(eventId))
                .and(Tables.ASSOCIATI.ID_UTENTE.eq(userId))
                .and(Tables.ASSOCIATI.ID_UTENTE.eq(userId))
                .fetchOptional()
                .isPresent())
            throw new ForbiddenException();
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
