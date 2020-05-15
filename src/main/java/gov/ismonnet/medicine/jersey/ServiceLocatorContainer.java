package gov.ismonnet.medicine.jersey;

import com.google.inject.Provider;
import org.glassfish.hk2.api.ServiceLocator;

public class ServiceLocatorContainer implements Provider<ServiceLocator> {

    private volatile ServiceLocator locator;

    @Override
    public ServiceLocator get() {
        if (locator == null)
            throw new IllegalStateException("ServiceLocator not set");
        return locator;
    }

    public void setServiceLocator(ServiceLocator locator) {
        this.locator = locator;
    }
}
