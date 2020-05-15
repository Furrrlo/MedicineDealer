package gov.ismonnet.medicine.converters;

import gov.ismonnet.medicine.authentication.Authenticated;
import gov.ismonnet.medicine.authentication.Authenticator;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;

import javax.inject.Inject;
import java.util.function.Function;

public class AuthenticatedValueParamProvider implements ValueParamProvider {

    private final Authenticator.UserIdParamProvider userIdProvider;

    @Inject AuthenticatedValueParamProvider(Authenticator.UserIdParamProvider userIdProvider) {
        this.userIdProvider = userIdProvider;
    }

    @Override
    public Function<ContainerRequest, ?> getValueProvider(Parameter parameter) {
        final boolean isInteger = parameter.getType() == Integer.TYPE || parameter.getType() == Integer.class;
        if (isInteger && parameter.isAnnotationPresent(Authenticated.class))
            return userIdProvider;
        return null;
    }

    @Override
    public PriorityType getPriority() {
        return Priority.NORMAL;
    }
}
