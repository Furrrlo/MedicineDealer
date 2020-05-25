package gov.ismonnet.medicine.jaxb;

import org.glassfish.jersey.internal.LocalizationMessages;
import org.glassfish.jersey.message.internal.EntityInputStream;
import org.xml.sax.SAXException;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.xml.bind.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import javax.xml.validation.Schema;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ValidatingObjectJaxbProvider implements MessageBodyReader<Object>, MessageBodyWriter<Object> {

    private final SchemaResolver schemaResolver;
    private final UncheckedSupplier<Marshaller> marshallerFactory;
    private final UncheckedSupplier<Unmarshaller> unmarshallerFactory;

    private final ConcurrentMap<Class<?>, Schema> schemas = new ConcurrentHashMap<>();
    private final ConcurrentMap<Schema, Marshaller> marshallers = new ConcurrentHashMap<>();
    private final ConcurrentMap<Schema, Unmarshaller> unmarshallers = new ConcurrentHashMap<>();

    public ValidatingObjectJaxbProvider(SchemaResolver schemaResolver,
                                        UncheckedSupplier<Marshaller> marshallerFactory,
                                        UncheckedSupplier<Unmarshaller> unmarshallerFactory) {
        this.schemaResolver = schemaResolver;
        this.marshallerFactory = marshallerFactory;
        this.unmarshallerFactory = unmarshallerFactory;
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        try {
            return isSupported(mediaType) && getSchema(type) != null;
        } catch (SAXException cause) {
            throw new RuntimeException("There was an error while resolving the schema", cause);
        }
    }

    @Override
    public Object readFrom(Class<Object> type,
                           Type genericType,
                           Annotation[] annotations,
                           MediaType mediaType,
                           MultivaluedMap<String, String> httpHeaders,
                           InputStream inputStream) throws IOException, WebApplicationException {
        try {
            final EntityInputStream entityStream = EntityInputStream.create(inputStream);
            if (entityStream.isEmpty())
                throw new NoContentException(LocalizationMessages.ERROR_READING_ENTITY_MISSING());

            final Unmarshaller unmarshaller = getUnmarshaller(type);
            if(unmarshaller == null)
                throw new RuntimeException("Couldn't find unmarshaller");
            return unmarshaller.unmarshal(entityStream);

        } catch (UnmarshalException ex) {
            throw new UncheckedUnmarshalException(ex);
        } catch (JAXBException | SAXException ex) {
            throw new RuntimeException("Couldn't unmarshal entity", ex);
        }
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        try {
            return isSupported(mediaType) && getSchema(type) != null;
        } catch (SAXException cause) {
            throw new RuntimeException("There was an error while resolving the schema", cause);
        }
    }

    @Override
    public void writeTo(Object o,
                        Class<?> type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws WebApplicationException {
        try {
            final Marshaller marshaller = getMarshaller(type);
            if(marshaller == null)
                throw new InternalServerErrorException("Couldn't find marshaller");

            final String charsetName = (mediaType == null) ? null : mediaType.getParameters().get(MediaType.CHARSET_PARAMETER);
            final Charset charset = (charsetName == null) ? StandardCharsets.UTF_8 : Charset.forName(charsetName);

            if (charset != StandardCharsets.UTF_8)
                marshaller.setProperty(Marshaller.JAXB_ENCODING, charset.name());
            marshaller.marshal(o, entityStream);

        } catch (JAXBException | SAXException ex) {
            throw new RuntimeException("Couldn't marshal entity", ex);
        }
    }

    protected boolean isSupported(MediaType mediaType) {
        return true;
    }

    protected Schema getSchema(Class<?> type) throws SAXException {

        final XmlRootElement rootElement = type.getAnnotation(XmlRootElement.class);
        final boolean isJaxbElement = JAXBElement.class.isAssignableFrom(type);

        if(rootElement == null && !isJaxbElement)
            return null;

        final String name;
        final String namespace;
        if(rootElement != null) {
            name = rootElement.name();
            namespace = rootElement.namespace();
        } else /*if(isJaxbElement)*/ {
            try {
                // Try to instantiate the object using the no-args constructor
                // and get the QNAME
                //noinspection rawtypes
                final QName qname = ((JAXBElement) type.newInstance()).getName();
                name = qname.getLocalPart();
                namespace = qname.getNamespaceURI();
            } catch (Exception ex) {
                // Doesn't have a no-args constructor
                return null;
            }
        }

        try {
            return schemas.computeIfAbsent(type, t -> {
                try {
                    return schemaResolver.resolve(namespace, name);
                } catch (SAXException ex) {
                    throw new RuntimeException(ex);
                }
            });
        } catch (RuntimeException ex) {
            if(ex.getCause() instanceof SAXException)
                throw (SAXException) ex.getCause();
            throw ex;
        }
    }

    protected Marshaller getMarshaller(Class<?> type) throws JAXBException, SAXException {
        final Schema schema = getSchema(type);
        if(schema == null)
            return null;

        try {
            return marshallers.computeIfAbsent(schema, t -> {
                try {
                    final Marshaller marshaller = new ConcurrentMarshaller(() -> {
                        final Marshaller marshaller0 = marshallerFactory.get();
                        marshaller0.setSchema(schema);
                        return marshaller0;
                    });
                    return marshaller;
                } catch (JAXBException ex) {
                    throw new RuntimeException(ex);
                }
            });
        } catch (RuntimeException ex) {
            if(ex.getCause() instanceof JAXBException)
                throw (JAXBException) ex.getCause();
            throw ex;
        }
    }

    protected Unmarshaller getUnmarshaller(Class<?> type) throws JAXBException, SAXException {
        final Schema schema = getSchema(type);
        if(schema == null)
            return null;

        try {
            return unmarshallers.computeIfAbsent(schema, t -> {
                try {
                    final Unmarshaller unmarshaller = new ConcurrentUnmarshaller(() -> {
                        final Unmarshaller unmarshaller0 = unmarshallerFactory.get();
                        unmarshaller0.setSchema(schema);
                        return unmarshaller0;
                    });
                    return unmarshaller;
                } catch (JAXBException ex) {
                    throw new RuntimeException(ex);
                }
            });
        } catch (RuntimeException ex) {
            if(ex.getCause() instanceof JAXBException)
                throw (JAXBException) ex.getCause();
            throw ex;
        }
    }
}
