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
        // To check if a password matches its hash get the has from the db then
        // passwordEncoder.matches(pw, hash)
        //
        // codes
        // 200: logged
        // 406: email not found
        // 407: password wrong

        int code;
        Result<? extends Record> results = ctx.select(Tables.UTENTI.PASSWORD)
                .from(Tables.UTENTI)
                .where(Tables.UTENTI.EMAIL.equal(email))
                .fetch();

        if(results.size() != 0){
            // Email found
            String pass;
            boolean logged = false;
            for(Record result : results) {
                pass = result.get(Tables.UTENTI.PASSWORD);
                if(passwordEncoder.matches(password, pass)) {
                    logged = true;
                    break;
                }
            }

            if(logged) {
                // password correct
                code = 200;
            } else {
                // password not correct
                code = 407;
            }
        } else {
            // Email not found
            code = 406;
        }

        return Response.status(code).build();
    }
}
