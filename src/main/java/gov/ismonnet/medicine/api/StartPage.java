package gov.ismonnet.medicine.api;

import gov.ismonnet.medicine.database.Tables;
import gov.ismonnet.medicine.jaxb.ws.LoginBean;
import gov.ismonnet.medicine.jaxb.ws.RegistrationBean;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.Date;
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
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public RegistrationBean register(RegistrationBean registrationBean) {

        final String hash = passwordEncoder.encode(registrationBean.getPassword());

        ctx.insertInto(Tables.UTENTI)
                .set(Tables.UTENTI.NOME, registrationBean.getNome())
                .set(Tables.UTENTI.COGNOME, registrationBean.getCognome())
                .set(Tables.UTENTI.EMAIL, registrationBean.getEmail())
                .set(Tables.UTENTI.DATA_NASCITA, Date.valueOf(registrationBean.getDataNascita()))
                .set(Tables.UTENTI.PASSWORD, hash)
                .execute();

        return registrationBean;
    }

    @GET
    @Consumes(MediaType.APPLICATION_XML)
    public String login(LoginBean loginBean) {
        // To check if a password matches its hash get the has from the db then
        // passwordEncoder.matches(pw, hash)
        ctx.select(Tables.UTENTI.PASSWORD)
                .from(Tables.UTENTI)
                .where(Tables.UTENTI.EMAIL.equal(loginBean.getEmail()))
                .execute();


        return null;
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
