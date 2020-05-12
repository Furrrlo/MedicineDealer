package gov.ismonnet.medicine.api;

import gov.ismonnet.medicine.database.Tables;
import gov.ismonnet.medicine.jaxb.ws.RegistrationBean;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
    public RegistrationBean register(RegistrationBean registrationBean) {
        final String hash = passwordEncoder.encode(registrationBean.getPassword());


        Result<? extends Record> results = ctx.select(Tables.UTENTI.PASSWORD)
                .from(Tables.UTENTI)
                .where(Tables.UTENTI.EMAIL.equal(registrationBean.getEmail()))
                .fetch();

        if(results.size() == 0){
            //email is unique
            ctx.insertInto(Tables.UTENTI)
                    .set(Tables.UTENTI.NOME, registrationBean.getNome())
                    .set(Tables.UTENTI.COGNOME, registrationBean.getCognome())
                    .set(Tables.UTENTI.EMAIL, registrationBean.getEmail())
                    .set(Tables.UTENTI.DATA_NASCITA, Date.valueOf(registrationBean.getDataNascita()))
                    .set(Tables.UTENTI.PASSWORD, hash)
                    .execute();
        }

        return registrationBean;
    }

    @GET
    @Consumes(MediaType.APPLICATION_XML)
    public Response login(@QueryParam( value = "email" )  String  email,
                          @QueryParam( value = "password" ) String password) {
        // codes
        // 200: logged
        // 401: wrong credentials

        Result<? extends Record> results = ctx.select(Tables.UTENTI.PASSWORD)
                .from(Tables.UTENTI)
                .where(Tables.UTENTI.EMAIL.equal(email))
                .fetch();

        boolean logged = false;
        for(Record result : results) {
            final String pass = result.get(Tables.UTENTI.PASSWORD);
            if(passwordEncoder.matches(password, pass)) {
                logged = true;
                break;
            }
        }

        return Response.status(logged ?
                Response.Status.OK : // password correct
                Response.Status.UNAUTHORIZED // email not found or password not correct
        ).build();
    }
}
