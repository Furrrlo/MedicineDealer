package gov.ismonnet.medicine.api;

import gov.ismonnet.medicine.authentication.AuthenticationCookie;
import gov.ismonnet.medicine.authentication.AuthenticationService;
import gov.ismonnet.medicine.authentication.AuthorizationSchema;
import gov.ismonnet.medicine.database.Tables;
import gov.ismonnet.medicine.jaxb.ws.RegistrationBean;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.SQLStateClass;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Optional;

@Path("utenti")
public class Users {

    private final DSLContext ctx;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;

    public final String authorizationHeaderSchema;
    public final String authenticationCookieName;

    @Inject Users(DSLContext ctx,
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
                             @NotNull RegistrationBean registrationBean) {

        final Integer id;
        try {
            // email has unique constraint
            id = ctx.insertInto(Tables.UTENTI)
                    .set(Tables.UTENTI.NOME, registrationBean.getNome())
                    .set(Tables.UTENTI.COGNOME, registrationBean.getCognome())
                    .set(Tables.UTENTI.EMAIL, registrationBean.getEmail())
                    .set(Tables.UTENTI.DATA_NASCITA, registrationBean.getDataNascita())
                    .set(Tables.UTENTI.PASSWORD, passwordEncoder.encode(registrationBean.getPassword()))
                    .returningResult(Tables.UTENTI.fields())
                    .fetchOptional()
                    .map(r -> r.get(Tables.UTENTI.ID))
                    .orElse(null);
        } catch (DataAccessException ex) {
            if(ex.sqlStateClass() == SQLStateClass.C23_INTEGRITY_CONSTRAINT_VIOLATION)
                // violated unique constraint
                return Response.status(Response.Status.CONFLICT).build();
            throw ex;
        }

        if(id == null)
            throw new InternalServerErrorException("Generated id is null");

        final String token = authenticationService.generateToken(id, registrationBean.getEmail(), uriInfo);
        return Response.ok()
                .header(HttpHeaders.AUTHORIZATION, authorizationHeaderSchema + " " + token)
                .cookie(makeAuthCookie(token))
                .build();
    }

    @GET
    @Consumes(MediaType.APPLICATION_XML)
    public Response login(@Context UriInfo uriInfo,
                          @NotNull @QueryParam(value = "email") String email,
                          @NotNull @QueryParam(value = "password") String password) {
        // codes
        // 200: logged
        // 401: wrong credentials

        final Optional<Record3<Integer, String, String>> record = ctx
                .select(Tables.UTENTI.ID, Tables.UTENTI.EMAIL, Tables.UTENTI.PASSWORD)
                .from(Tables.UTENTI)
                .where(Tables.UTENTI.EMAIL.equal(email))
                .fetch()
                .stream()
                .filter(r -> passwordEncoder.matches(password, r.get(Tables.UTENTI.PASSWORD)))
                .findFirst();

        if(!record.isPresent())
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .build();

        final int id = record.get().get(Tables.UTENTI.ID);
        final String actualEmail = record.get().get(Tables.UTENTI.EMAIL);

        final String token = authenticationService.generateToken(id, actualEmail, uriInfo);
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
