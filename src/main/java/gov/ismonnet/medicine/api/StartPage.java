package gov.ismonnet.medicine.api;

import gov.ismonnet.medicine.database.Tables;
import gov.ismonnet.medicine.jaxb.ws.RegistrationBean;
import org.jooq.DSLContext;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.Date;

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
    public RegistrationBean registrazione(RegistrationBean registrationBean) {

        final String hash = passwordEncoder.encode(registrationBean.getPassword());
        ctx.insertInto(Tables.UTENTI)
                .set(Tables.UTENTI.NOME,registrationBean.getNome())
                .set(Tables.UTENTI.COGNOME,registrationBean.getCognome())
                .set(Tables.UTENTI.EMAIL,registrationBean.getEmail())
                .set(Tables.UTENTI.DATA_NASCITA, Date.valueOf(registrationBean.getDataNascita()))
                .set(Tables.UTENTI.PASSWORD, hash)
                .execute();

        return registrationBean;
    }

    @GET
    @Consumes(MediaType.APPLICATION_XML)
    public String accedi() {
        // To check if a password matches its hash get the has from the db then
        // passwordEncoder.matches(pw, hash)
        return null;
    }
}
