package gov.ismonnet.medicine;

import com.google.inject.Guice;
import com.google.inject.Injector;
import gov.ismonnet.medicine.jersey.JerseyModule;
import gov.ismonnet.medicine.jersey.ServiceLocatorContainer;
import org.glassfish.hk2.api.ServiceLocator;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;
import org.jvnet.hk2.guice.bridge.api.HK2IntoGuiceBridge;

import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("api")
public class MedicineDealerApplication extends Application {

    @Inject MedicineDealerApplication(ServiceLocator serviceLocator) {
        final Injector injector = Guice.createInjector(
                new HK2IntoGuiceBridge(serviceLocator),
                new JerseyModule(),
                new MedicineDealerModule());
        injector.getInstance(ServiceLocatorContainer.class).setServiceLocator(serviceLocator);

        GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
        serviceLocator.getService(GuiceIntoHK2Bridge.class).bridgeGuiceInjector(injector);
    }
}
