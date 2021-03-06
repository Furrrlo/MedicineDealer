package gov.ismonnet.medicine.api;

import gov.ismonnet.medicine.authentication.Authenticated;
import gov.ismonnet.medicine.database.Tables;
import gov.ismonnet.medicine.jaxb.ws.DevicesBean;
import gov.ismonnet.medicine.jaxb.ws.PortaMedicina;
import org.jooq.DSLContext;
import org.jooq.Record1;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.math.BigInteger;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("porta_medicine")
public class Devices {

    private final DSLContext ctx;

    @Inject Devices(DSLContext ctx) {
        this.ctx = ctx;
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public DevicesBean getDevices(@Authenticated int userId) {
        return new DevicesBean(ctx
                .select(Tables.PORTA_MEDICINE.ID, Tables.ASSOCIATI.NOME)
                .from(Tables.PORTA_MEDICINE)

                .join(Tables.ASSOCIATI)
                .on(Tables.ASSOCIATI.ID_PORTA_MEDICINE.eq(Tables.PORTA_MEDICINE.ID))

                .where(Tables.ASSOCIATI.ID_UTENTE.equal(userId))
                .fetch()
                .stream()
                .map(r -> new PortaMedicina(
                        BigInteger.valueOf(r.get(Tables.PORTA_MEDICINE.ID)),
                        r.get(Tables.ASSOCIATI.NOME)
                ))
                .collect(Collectors.toList()));
    }

    @POST
    @Path("{cod_invito}")
    public void associate(@Authenticated int userId,
                          @NotNull @PathParam(value = "cod_invito") String inviteCode) {
        final Optional<Integer> optionalId = ctx.select(Tables.PORTA_MEDICINE.ID)
                .from(Tables.PORTA_MEDICINE)
                .where(Tables.PORTA_MEDICINE.COD_INVITO.equal(inviteCode))
                .fetchOptional()
                .map(Record1::value1);
        if(!optionalId.isPresent())
            throw new NotFoundException();

        final int idDevices = optionalId.get();
        ctx.insertInto(Tables.ASSOCIATI)
                .columns(Tables.ASSOCIATI.ID_UTENTE, Tables.ASSOCIATI.ID_PORTA_MEDICINE)
                .values(userId, idDevices);
    }
}
