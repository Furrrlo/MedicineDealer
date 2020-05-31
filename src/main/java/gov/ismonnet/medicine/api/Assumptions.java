package gov.ismonnet.medicine.api;

import gov.ismonnet.medicine.aifa.MedicineService;
import gov.ismonnet.medicine.authentication.Authenticated;
import gov.ismonnet.medicine.authentication.Authenticator;
import gov.ismonnet.medicine.database.Tables;
import gov.ismonnet.medicine.database.enums.EventiCadenza;
import gov.ismonnet.medicine.jaxb.ws.*;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.types.UByte;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

import static gov.ismonnet.medicine.utils.EventUtils.bitmaskToWeek;
import static gov.ismonnet.medicine.utils.EventUtils.getAllDates;

@Path("assunzioni")
public class Assumptions {

    private final DSLContext ctx;
    private final Authenticator authenticator;
    private final MedicineService medicineService;

    @Inject Assumptions(final DSLContext ctx,
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
                                  @QueryParam(value = "data_inizio") LocalDate startDateIn,
                                  @QueryParam(value = "data_fine") LocalDate endDateIn,
                                  @QueryParam(value = "data") LocalDate date,
                                  @QueryParam(value = "granularita") Granularity granularity) {

        if(deviceId != null)
            authenticator.checkAuthorizedForDevice(userId, deviceId);

        if(date == null && granularity == null && startDateIn == null && endDateIn == null)
            throw new BadRequestException("You must either specify [dataInizio, dataFine] or [data, granularita]");
        if((date != null || granularity != null) && (startDateIn != null || endDateIn != null))
            throw new BadRequestException("You cannot specify both [dataInizio, dataFine] and [data, granularita]");

        if(granularity != null && date == null)
            date = LocalDate.now();
        if(date != null && granularity == null)
            throw new BadRequestException("Missing granularita");

        if(endDateIn != null && startDateIn == null)
            throw new BadRequestException("Missing dataInizio");
        if(startDateIn != null && endDateIn == null)
            throw new BadRequestException("Missing dataFine");

        final LocalDate startDate, endDate;
        if(startDateIn != null /* && endDate0 != null */) {
            startDate = startDateIn;
            endDate = endDateIn;
        } else {
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
                default:
                    throw new AssertionError("Unimplemented value: " + granularity);
            }
        }

        final List<EventWithAssunzioni> events = fetchEventsWithAssumptions(ctx, Tables.ASSOCIATI.ID_UTENTE.eq(userId)
                .and(deviceId == null ?
                        DSL.noCondition() :
                        Tables.ASSOCIATI.ID_PORTA_MEDICINE.eq(deviceId))
                // If the event is not repeated, check if the date is between the two
                // otherwise it's handled further down
                .and(Tables.EVENTI.CADENZA.isNotNull().or(Tables.EVENTI.DATA.between(startDate, endDate)))
        );
        // Generate missing assumptions
        final LocalTime now = LocalTime.now();

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
            // TODO: use today instead of startDate
            //       rn there is no portamedicine actually working, so assumptions
            //       are not populated, causing issues
            getAllDates(event, startDate, endDate).forEach(d -> event.getOrari().stream()
//                    .filter(o -> d.isAfter(today) || o.isAfter(now))
                    .filter(o -> d.isAfter(startDate) || o.isAfter(now))
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

        return new CalendarBean(events.stream()
                .flatMap(e -> e.getAssunzioni().stream()
                        .map(a -> new AssunzioneEstesa()
                                .withIdEvento(e.getId())
                                .withIdPortaMedicine(e.getIdPortaMedicine())
                                .withNomeFarmaco(e.getNomeFarmaco())
                                .withAicFarmaco(e.getAicFarmaco())
                                .withData(a.getData())
                                .withOra(a.getOra())
                                .withDataReale(a.getDataReale())
                                .withOraReale(a.getOraReale())
                                .withCancellato(false)))
                .sorted(Comparator.comparing(AssunzioneEstesa::getData)
                        .thenComparing(AssunzioneEstesa::getOra))
                .collect(Collectors.toList()));
    }

    private List<EventWithAssunzioni> fetchEventsWithAssumptions(final DSLContext ctx, final Condition condition) {
        final Field<String> codAicRow = Tables.FARMACI.COD_AIC.cast(SQLDataType.CHAR(9));
        return ctx.select(
                Tables.EVENTI.ID, Tables.EVENTI.ID_PORTA_MEDICINE, Tables.EVENTI.DATA, Tables.EVENTI.FINITO,
                Tables.EVENTI.CADENZA, Tables.EVENTI.INTERVALLO, Tables.EVENTI.GIORNI_SETTIMANA,
                Tables.EVENTI.DATA_FINE_INTERVALLO, Tables.EVENTI.OCCORRENZE_FINE_INTERVALLO,
                Tables.ORARI.ORA,
                codAicRow, Tables.FARMACI.NOME,
                Tables.ASSUNZIONI.DATA, Tables.ASSUNZIONI.ORA, Tables.ASSUNZIONI.DATA_REALE, Tables.ASSUNZIONI.ORA_REALE
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

                .where(Tables.ASSUNZIONI.ORA.isNull()
                        .or(Tables.ASSUNZIONI.ORA.eq(Tables.ORARI.ORA))
                        .and(condition))

                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        r -> {
                            final EventiCadenza cadenza = r.get(Tables.EVENTI.CADENZA);
                            final UByte week = r.get(Tables.EVENTI.GIORNI_SETTIMANA);

                            final LocalDate endIntervalDate = r.get(Tables.EVENTI.DATA_FINE_INTERVALLO);
                            final Integer endOccurrences = r.get(Tables.EVENTI.OCCORRENZE_FINE_INTERVALLO);

                            //noinspection DuplicatedCode
                            return new EventWithAssunzioni()
                                    .withId(BigInteger.valueOf(r.get(Tables.EVENTI.ID)))
                                    .withIdPortaMedicine(BigInteger.valueOf(r.get(Tables.EVENTI.ID_PORTA_MEDICINE)))
                                    .withAicFarmaco(r.get(codAicRow))
                                    .withNomeFarmaco(r.get(Tables.FARMACI.NOME))
                                    .withData(r.get(Tables.EVENTI.DATA))
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
                                                                    .withData(endIntervalDate)
                                                                    .withOccorenze(endOccurrences != null ? BigInteger.valueOf(endOccurrences) : null))
                                    );
                        }, Collectors.mapping(r -> r, Collectors.toSet())
                ))
                .entrySet()
                .stream()
                .map(e -> e.getKey()
                        .withOrari(e.getValue().stream()
                                .map(r -> r.get(Tables.ORARI.ORA))
                                .collect(Collectors.toList()))
                        .withAssunzioni(e.getValue().stream()
                                .filter(r -> r.get(Tables.ASSUNZIONI.DATA) != null)
                                .filter(r -> r.get(Tables.ASSUNZIONI.ORA) != null)
                                .map(r -> new Assunzione()
                                        .withData(r.get(Tables.ASSUNZIONI.DATA))
                                        .withOra(r.get(Tables.ASSUNZIONI.ORA))
                                        .withDataReale(r.get(Tables.ASSUNZIONI.DATA_REALE))
                                        .withOraReale(r.get(Tables.ASSUNZIONI.ORA_REALE)))
                                .collect(Collectors.toList()))
                )
                .collect(Collectors.toList());
    }

    public enum Granularity {
        DAY, WEEK, MONTH, YEAR;

        public static Granularity fromString(String param) {
            try {
                return valueOf(param.toUpperCase());
            } catch (Exception e) {
                return null;
            }
        }
    }
}
