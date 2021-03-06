package gov.ismonnet.medicine.authentication;

import gov.ismonnet.medicine.database.Tables;
import org.glassfish.jersey.server.ContainerRequest;
import org.jooq.DSLContext;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Parameter;
import java.security.Principal;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Authenticator {

    private final AuthenticationService authenticationService;
    private final DSLContext ctx;

    public final String authorizationHeaderSchema;
    public final String authenticationCookieName;

    @Inject Authenticator(AuthenticationService authenticationService,
                          DSLContext ctx,
                          @AuthorizationSchema String authorizationHeaderSchema,
                          @AuthenticationCookie String authenticationCookieName) {
        this.authenticationService = authenticationService;
        this.ctx = ctx;
        this.authorizationHeaderSchema = authorizationHeaderSchema;
        this.authenticationCookieName = authenticationCookieName;
    }

    @Provider
    @Authenticated
    @Priority(Priorities.AUTHENTICATION)
    public static class Filter extends Authenticator implements ContainerRequestFilter {

        @Inject Filter(AuthenticationService authenticationService,
                       DSLContext ctx,
                       @AuthorizationSchema String authorizationHeaderSchema,
                       @AuthenticationCookie String authenticationCookieName) {
            super(authenticationService, ctx, authorizationHeaderSchema, authenticationCookieName);
        }

        @Override
        public void filter(ContainerRequestContext requestContext) {
            doAuth(requestContext);
        }
    }

    @Provider
    @Priority(Priorities.AUTHENTICATION)
    public static class ParameterFilter extends Authenticator implements ContainerRequestFilter {

        @Context
        private ResourceInfo resourceInfo;

        @Inject ParameterFilter(AuthenticationService authenticationService,
                                DSLContext ctx,
                                @AuthorizationSchema String authorizationHeaderSchema,
                                @AuthenticationCookie String authenticationCookieName) {
            super(authenticationService, ctx, authorizationHeaderSchema, authenticationCookieName);
        }

        @Override
        public void filter(ContainerRequestContext requestContext) {
            // Get it lazily if needed
            final IntSupplier userId = () -> {
                if(requestContext.getSecurityContext().getUserPrincipal() == null)
                    doAuth(requestContext);
                return ((AuthenticatedPrincipal) requestContext.getSecurityContext().getUserPrincipal()).getId();
            };
            final Map<String, Parameter> paramToAnnotations = Arrays.stream(resourceInfo.getResourceMethod().getParameters())
                    .filter(param -> Arrays.stream(param.getAnnotations())
                            .anyMatch(a -> a instanceof QueryParam || a instanceof PathParam))
                    .collect(Collectors.toMap(
                            param -> Arrays.stream(param.getAnnotations())
                                    .map(a -> {
                                        if(a instanceof QueryParam)
                                            return ((QueryParam) a).value();
                                        if(a instanceof PathParam)
                                            return ((PathParam) a).value();
                                        return null;
                                    })
                                    .filter(Objects::nonNull)
                                    .findFirst()
                                    .orElse(null),
                            param -> param
                    ));

            Stream.concat(
                    requestContext.getUriInfo().getQueryParameters().entrySet().stream(),
                    requestContext.getUriInfo().getPathParameters().entrySet().stream()
            ).forEach(e -> {
                final String k = e.getKey();
                final List<String> v = e.getValue();

                final Parameter param = paramToAnnotations.get(k);
                if(param == null)
                    return;

                final boolean isInteger = param.getType().equals(Integer.TYPE) || param.getType().equals(Integer.class);
                if(param.getAnnotation(AuthorizedDevice.class) != null && isInteger) {
                    try {
                        checkAuthorizedForDevice(userId.getAsInt(), Integer.parseInt(v.get(0)));
                    } catch (NumberFormatException | IndexOutOfBoundsException ex) {
                        throw new BadRequestException("Invalid porta_medicine id");
                    }
                } else if(param.getAnnotation(AuthorizedEvent.class) != null && isInteger) {
                    try {
                        checkAuthorizedForEvent(userId.getAsInt(), Integer.parseInt(v.get(0)));
                    } catch (NumberFormatException | IndexOutOfBoundsException ex) {
                        throw new BadRequestException("Invalid event id");
                    }
                }
            });
        }
    }

    public static class UserIdParamProvider extends Authenticator implements Function<ContainerRequest, Integer> {

        @Inject UserIdParamProvider(DSLContext ctx,
                                    AuthenticationService authenticationService,
                                    @AuthorizationSchema String authorizationHeaderSchema,
                                    @AuthenticationCookie String authenticationCookieName) {
            super(authenticationService, ctx, authorizationHeaderSchema, authenticationCookieName);
        }

        @Override
        public Integer apply(ContainerRequest containerRequest) {
            if(containerRequest.getSecurityContext().getUserPrincipal() == null)
                doAuth(containerRequest);
            return ((AuthenticatedPrincipal) containerRequest.getSecurityContext().getUserPrincipal()).getId();
        }
    }

    protected void doAuth(ContainerRequestContext requestContext) {

        final Optional<Map<String, Object>> username = hasAuthorizationHeader(requestContext)
                .map(Optional::of)
                .orElseGet(() -> hasSessionCookie(requestContext));
        if(!username.isPresent())
            throw new NotAuthorizedException("A valid authorization header or cookie must be provided");

        final AuthenticatedPrincipal simplePrincipal = new AuthenticatedPrincipal(
                (Integer) username.get().get("id"),
                (String) username.get().get("email"));
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

    protected Optional<Map<String, Object>> hasAuthorizationHeader(ContainerRequestContext requestContext) {
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

    protected Optional<Map<String, Object>> hasSessionCookie(ContainerRequestContext requestContext) {
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

    public void checkAuthorizedForDevice(int userId, int deviceId) {
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

    public void checkAuthorizedForEvent(int userId, int eventId) {
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
}
