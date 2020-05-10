package gov.ismonnet.medicine.api;

import gov.ismonnet.medicine.database.Tables;
import gov.ismonnet.medicine.database.tables.records.EventiRecord;
import gov.ismonnet.medicine.jaxb.ws.CalendarBean;
import gov.ismonnet.medicine.jaxb.ws.EditEventBean;
import gov.ismonnet.medicine.jaxb.ws.NewEventBean;
import org.jooq.DSLContext;
import org.jooq.UpdateSetMoreStep;
import org.jooq.UpdateSetStep;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.stream.Collectors;

@Path("eventi")
public class Events {

    private final DSLContext ctx;

    @Inject Events(DSLContext ctx) {
        this.ctx = ctx;
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public CalendarBean getEvents(@QueryParam(value = "id_porta_medicine") Integer deviceId,
                                  @QueryParam(value = "data") LocalDate date,
                                  @QueryParam(value = "granularita") Granularity granularity) {
        if(granularity == null)
            throw new BadRequestException();
        if(date == null)
            date = LocalDate.now();

        // TODO: how to get this?
        final int userId = 0;
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

        return new CalendarBean(ctx
                .select(
                        Tables.EVENTI.ID, Tables.EVENTI.DATA, Tables.EVENTI.ORA,
                        Tables.FARMACI.COD_AIC, Tables.FARMACI.NOME,
                        Tables.ASSUNZIONI.ID, Tables.ASSUNZIONI.DATA, Tables.ASSUNZIONI.ORA
                )
                .from(Tables.EVENTI)

                .join(Tables.ASSOCIATI)
                .on(Tables.EVENTI.ID_PORTA_MEDICINE.eq(Tables.ASSOCIATI.ID_PORTA_MEDICINE))

                .join(Tables.FARMACI)
                .on(Tables.EVENTI.AIC_FARMACO.eq(Tables.FARMACI.COD_AIC))

                .leftJoin(Tables.ASSUNZIONI)
                .on(Tables.EVENTI.ID_ASSUNZIONE.eq(Tables.ASSUNZIONI.ID))

                .where(Tables.ASSOCIATI.ID_UTENTE.eq(userId))
                .and(deviceId == null ?
                        DSL.noCondition() :
                        Tables.ASSOCIATI.ID_PORTA_MEDICINE.eq(deviceId))
                .and(granularity == Granularity.ALL ?
                        DSL.noCondition() :
                        Tables.EVENTI.DATA.between(Date.valueOf(startDate), Date.valueOf(endDate)))

                .fetch()
                .stream()
                .map(r -> new CalendarBean.Evento(
                        BigInteger.valueOf(r.get(Tables.EVENTI.ID)),
                        r.get(Tables.EVENTI.DATA).toLocalDate(),
                        r.get(Tables.EVENTI.ORA).toLocalTime(),
                        new CalendarBean.Evento.Medicina(
                                r.get(Tables.FARMACI.NOME),
                                r.get(Tables.FARMACI.COD_AIC).toBigInteger()
                        ),
                        r.get(Tables.ASSUNZIONI.ID) == null ?
                                null :
                                new CalendarBean.Evento.Assunzione(
                                        r.get(Tables.ASSUNZIONI.DATA).toLocalDate(),
                                        r.get(Tables.ASSUNZIONI.ORA).toLocalTime()
                                )
                ))
                .collect(Collectors.toList()));
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Path("{id_porta_medicine}")
    public String addEvent(@PathParam(value = "id_porta_medicine") int deviceId,
                           NewEventBean eventBean) {
        if(eventBean == null)
            throw new BadRequestException();

        // TODO: how to get this?
        final int userId = 0;
        checkAuthorizedForDevice(userId, deviceId);
        return "<id>" +
                ctx.insertInto(Tables.EVENTI)
                        .columns(
                                Tables.EVENTI.DATA,
                                Tables.EVENTI.ORA,
                                Tables.EVENTI.ID_PORTA_MEDICINE,
                                Tables.EVENTI.AIC_FARMACO
                        )
                        .values(
                                Date.valueOf(eventBean.getData()),
                                Time.valueOf(eventBean.getOra()),
                                deviceId,
                                UInteger.valueOf(eventBean.getAicFarmaco().longValueExact())
                        )
                        .returningResult(Tables.EVENTI.fields())
                        .fetchOne()
                        .map(r -> r.getValue(Tables.EVENTI.ID)) +
                "</id>";
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Path("{id_evento}")
    public void editEvent(@PathParam(value = "id_evento") int eventId,
                          EditEventBean eventBean) {
        if(eventBean == null)
            throw new BadRequestException();

        // TODO: how to get this?
        final int userId = 0;
        checkAuthorizedForEvent(userId, eventId);

        final UpdateSetStep<EventiRecord> update = ctx.update(Tables.EVENTI);
        if(eventBean.getData() != null)
            update.set(Tables.EVENTI.DATA, Date.valueOf(eventBean.getData()));
        if(eventBean.getOra() != null)
            update.set(Tables.EVENTI.ORA, Time.valueOf(eventBean.getOra()));
        if(eventBean.getAicFarmaco() != null)
            update.set(Tables.EVENTI.AIC_FARMACO, UInteger.valueOf(eventBean.getAicFarmaco().longValueExact()));

        if(!(update instanceof UpdateSetMoreStep))
            return;
        ((UpdateSetMoreStep<EventiRecord>) update)
                .where(Tables.EVENTI.ID.eq(eventId))
                .execute();
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
            if(param == null || param.isEmpty())
                return null;

            try {
                return valueOf(param.toUpperCase());
            } catch (Exception e) {
                throw new BadRequestException();
            }
        }
    }
}
