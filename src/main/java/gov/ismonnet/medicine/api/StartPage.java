package gov.ismonnet.medicine.api;

import gov.ismonnet.medicine.authentication.AuthenticationCookie;
import gov.ismonnet.medicine.authentication.AuthenticationService;
import gov.ismonnet.medicine.authentication.AuthorizationSchema;
import gov.ismonnet.medicine.database.Tables;
import gov.ismonnet.medicine.jaxb.ws.RegistrationBean;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.SQLStateClass;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.Date;
import java.util.Optional;

@Path("pagina_iniziale")
public class StartPage {

    private final DSLContext ctx;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;

    public final String authorizationHeaderSchema;
    public final String authenticationCookieName;

    @Inject StartPage(DSLContext ctx,
                      PasswordEncoder passwordEncoder,
                      AuthenticationService authenticationService,
                      @AuthorizationSchema String authorizationHeaderSchema,
                      @AuthenticationCookie String authenticationCookieName) {
        this.ctx = ctx;
        this.passwordEncoder = passwordEncoder;
        this.authenticationService = authenticationService;
        this.authorizationHeaderSchema = authorizationHeaderSchema;
        this.authenticationCookieName = authenticationCookieName;
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response register(@Context UriInfo uriInfo,
                             RegistrationBean registrationBean) {

        try {
            // email has unique constraint
            ctx.insertInto(Tables.UTENTI)
                    .set(Tables.UTENTI.NOME, registrationBean.getNome())
                    .set(Tables.UTENTI.COGNOME, registrationBean.getCognome())
                    .set(Tables.UTENTI.EMAIL, registrationBean.getEmail())
                    .set(Tables.UTENTI.DATA_NASCITA, Date.valueOf(registrationBean.getDataNascita()))
                    .set(Tables.UTENTI.PASSWORD, passwordEncoder.encode(registrationBean.getPassword()))
                    .execute();
        } catch (DataAccessException ex) {
            if(ex.sqlStateClass() == SQLStateClass.C23_INTEGRITY_CONSTRAINT_VIOLATION)
                // violated unique constraint
                return Response.status(Response.Status.CONFLICT).build();
            throw ex;
        }

        final String token = authenticationService.generateToken(registrationBean.getEmail(), uriInfo);
        return Response.ok()
                .header(HttpHeaders.AUTHORIZATION, authorizationHeaderSchema + " " + token)
                .cookie(makeAuthCookie(token))
                .build();
    }

    @GET
    @Consumes(MediaType.APPLICATION_XML)
    public Response login(@Context UriInfo uriInfo,
                          @QueryParam( value = "email" )  String  email,
                          @QueryParam( value = "password" ) String password) {
        // codes
        // 200: logged
        // 401: wrong credentials

        Optional<String> actualEmail = ctx
                .select(Tables.UTENTI.EMAIL, Tables.UTENTI.PASSWORD)
                .from(Tables.UTENTI)
                .where(Tables.UTENTI.EMAIL.equal(email))
                .fetch()
                .stream()
                .filter(r -> passwordEncoder.matches(password, r.get(Tables.UTENTI.PASSWORD)))
                .map(r -> r.get(Tables.UTENTI.EMAIL))
                .findFirst();

        if(!actualEmail.isPresent())
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .build();

        final String token = authenticationService.generateToken(actualEmail.get(), uriInfo);
        return Response.ok()
                .header(HttpHeaders.AUTHORIZATION, authorizationHeaderSchema + " " + token)
                .cookie(makeAuthCookie(token))
                .build();
    }

    private NewCookie makeAuthCookie(String token) {
        return new NewCookie(
                authenticationCookieName,
                token,
                null, // the URI path for which the cookie is valid
                null, // the host domain for which the cookie is valid. TODO: should probably set this
                NewCookie.DEFAULT_VERSION, // the version of the specification to which the cookie complies
                null, // the comment
                // No max-age and expiry set, cookie expires when the browser gets closed
                NewCookie.DEFAULT_MAX_AGE, // the maximum age of the cookie in seconds
                null, // the cookie expiry date
                false, // specifies whether the cookie will only be sent over a secure connection
                true // if {@code true} make the cookie HTTP only
        );
    }
}
