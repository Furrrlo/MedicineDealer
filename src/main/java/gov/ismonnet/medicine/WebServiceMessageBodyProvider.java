package gov.ismonnet.medicine;

import gov.ismonnet.medicine.jaxb.InJarSchemaResolver;
import gov.ismonnet.medicine.jaxb.ValidatingObjectJaxbProvider;
import gov.ismonnet.medicine.jaxb.ws.ObjectFactory;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import java.nio.charset.StandardCharsets;

public abstract class WebServiceMessageBodyProvider extends ValidatingObjectJaxbProvider {

    public WebServiceMessageBodyProvider() {
        super(
                new InJarSchemaResolver(StandardCharsets.ISO_8859_1, "schema/web-service"),
                () -> JAXBContext.newInstance(ObjectFactory.class).createMarshaller(),
                () -> JAXBContext.newInstance(ObjectFactory.class).createUnmarshaller()
        );
    }

    @Provider
    @Singleton
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public static final class App extends WebServiceMessageBodyProvider {}

    @Provider
    @Singleton
    @Produces(MediaType.TEXT_XML)
    @Consumes(MediaType.TEXT_XML)
    public static final class Text extends WebServiceMessageBodyProvider {}

    @Provider
    @Singleton
    @Produces("*/*")
    @Consumes("*/*")
    public static final class General extends WebServiceMessageBodyProvider {
        @Override
        protected boolean isSupported(MediaType m) {
            return m.getSubtype().endsWith("+xml");
        }
    }
}
