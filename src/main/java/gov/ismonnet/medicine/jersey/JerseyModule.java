package gov.ismonnet.medicine.jersey;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.glassfish.hk2.api.ServiceLocator;

import javax.inject.Singleton;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

/**
 * Taken from:
 * https://github.com/jersey/jersey/issues/2222#issuecomment-296903746
 *
 * Alternative to this:
 * https://psamsotha.github.io/jersey/2015/11/01/jersey-method-parameter-injection.html
 */
public class JerseyModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ServiceLocator.class).toProvider(ServiceLocatorContainer.class).in(Singleton.class);
        bind(ServiceLocatorContainer.class).in(Singleton.class);
    }

    @Provides
    public SecurityContext securityContext(ServiceLocator locator) {
        return locator.getService(SecurityContext.class);
    }

    @Provides
    public UriInfo uriInfo(ServiceLocator locator) {
        return locator.getService(UriInfo.class);
    }
}
