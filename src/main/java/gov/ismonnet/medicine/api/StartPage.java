package gov.ismonnet.medicine.api;

import gov.ismonnet.medicine.database.Tables;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.Optional;

@Path("pagina_iniziale")
public class StartPage {

    private final DSLContext ctx;
    private final PasswordEncoder passwordEncoder;

    @Inject StartPage(DSLContext ctx, PasswordEncoder passwordEncoder) {
        this.ctx = ctx;
        this.passwordEncoder = passwordEncoder;
    }

    @POST
    @Path("{cod_invito}")
    public void associate(@PathParam(value = "cod_invito") String codInvito) {

        // TODO: how to get this?
        final int userId = 0;

        Optional<Integer> optionalId = ctx.select(Tables.PORTA_MEDICINE.ID)
                .from(Tables.PORTA_MEDICINE)
                .where(Tables.PORTA_MEDICINE.COD_INVITO.equal(codInvito))
                .fetchOptional()
                .map(Record1::value1);
        if(!optionalId.isPresent())
            throw new NotFoundException();

        int idPortaMedicine = optionalId.get();
        ctx.insertInto(Tables.ASSOCIATI)
                .columns(Tables.ASSOCIATI.ID_UTENTE, Tables.ASSOCIATI.ID_PORTA_MEDICINE)
                .values(userId, idPortaMedicine);
    }
}
