package gov.ismonnet.medicine.converters;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;

import javax.inject.Singleton;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

@Provider
public class ParamValueProviderFeature implements Feature {

    @Override
    public boolean configure(FeatureContext context) {
        context.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(AuthenticatedValueParamProvider.class)
                        .to(ValueParamProvider.class)
                        .in(Singleton.class);
            }
        });
        return true;
    }
}
