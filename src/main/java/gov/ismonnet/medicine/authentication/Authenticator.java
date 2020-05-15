package gov.ismonnet.medicine.authentication;

import gov.ismonnet.medicine.database.Tables;
import org.glassfish.jersey.server.ContainerRequest;
import org.jooq.DSLContext;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.security.Principal;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public abstract class Authenticator {

    private final AuthenticationService authenticationService;

    public final String authorizationHeaderSchema;
    public final String authenticationCookieName;

    protected Authenticator(AuthenticationService authenticationService,
                            @AuthorizationSchema String authorizationHeaderSchema,
                            @AuthenticationCookie String authenticationCookieName) {
        this.authenticationService = authenticationService;
        this.authorizationHeaderSchema = authorizationHeaderSchema;
        this.authenticationCookieName = authenticationCookieName;
    }

    @Provider
    @Authenticated
    @Priority(Priorities.AUTHENTICATION)
    public static class Filter extends Authenticator implements ContainerRequestFilter {

        @Inject Filter(AuthenticationService authenticationService,
                       @AuthorizationSchema String authorizationHeaderSchema,
                       @AuthenticationCookie String authenticationCookieName) {
            super(authenticationService, authorizationHeaderSchema, authenticationCookieName);
        }

        @Override
        public void filter(ContainerRequestContext requestContext) {
            doAuth(requestContext);
        }
    }

    public static class UserIdParamProvider extends Authenticator implements Function<ContainerRequest, Integer> {

        private final DSLContext ctx;

        @Inject UserIdParamProvider(DSLContext ctx,
                                    AuthenticationService authenticationService,
                                    @AuthorizationSchema String authorizationHeaderSchema,
                                    @AuthenticationCookie String authenticationCookieName) {
            super(authenticationService, authorizationHeaderSchema, authenticationCookieName);
            this.ctx = ctx;
        }

        @Override
        public Integer apply(ContainerRequest containerRequest) {
            if(containerRequest.getSecurityContext().getUserPrincipal() == null)
                doAuth(containerRequest);

            final String email = containerRequest.getSecurityContext().getUserPrincipal().getName();
            return ctx.select(Tables.UTENTI.ID)
                    .from(Tables.UTENTI)
                    .where(Tables.UTENTI.EMAIL.eq(email))
                    .fetchOne()
                    .value1();
        }
    }

    protected void doAuth(ContainerRequestContext requestContext) {

        final Optional<String> username = hasAuthorizationHeader(requestContext)
                .map(Optional::of)
                .orElseGet(() -> hasSessionCookie(requestContext));
        if(!username.isPresent())
            throw new NotAuthorizedException("A valid authorization header or cookie must be provided");

        final SimplePrincipal simplePrincipal = new SimplePrincipal(username.get());
        requestContext.setSecurityContext(new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return simplePrincipal;
            }

            @Override
            public boolean isUserInRole(String role) {
                return true;
            }

            @Override
            public boolean isSecure() {
                return false;
            }

            @Override
            public String getAuthenticationScheme() {
                return null;
            }
        });
    }

    protected Optional<String> hasAuthorizationHeader(ContainerRequestContext requestContext) {
        final String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if(authorizationHeader == null || !authorizationHeader.startsWith(authorizationHeaderSchema + " "))
            return Optional.empty();

        final String token = authorizationHeader.substring(authorizationHeaderSchema.length()).trim();
        try {
            return Optional.ofNullable(authenticationService.authenticate(token));
        } catch (AuthenticationException e) {
            return Optional.empty();
        }
    }

    protected Optional<String> hasSessionCookie(ContainerRequestContext requestContext) {
        final Cookie cookie = requestContext.getCookies().get(authenticationCookieName);
        if(cookie == null)
            return Optional.empty();

        final String token = cookie.getValue();
        try {
            return Optional.ofNullable(authenticationService.authenticate(token));
        } catch (AuthenticationException e) {
            return Optional.empty();
        }
    }

    private static class SimplePrincipal implements Principal {

        private final String name;

        public SimplePrincipal(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SimplePrincipal)) return false;
            SimplePrincipal that = (SimplePrincipal) o;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return "SimplePrincipal{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }
}
