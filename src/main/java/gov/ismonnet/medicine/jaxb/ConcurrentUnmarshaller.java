package gov.ismonnet.medicine.jaxb;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.bind.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

public class ConcurrentUnmarshaller implements Unmarshaller {

    private final ThreadLocal<Unmarshaller> unmarshaller;

    public ConcurrentUnmarshaller(UncheckedSupplier<Unmarshaller> unmarshaller) throws JAXBException {
        // Try to get one and throw exceptions instantly if it does
        unmarshaller.get();
        // The first one was fine, the others _should_ be too
        this.unmarshaller = ThreadLocal.withInitial(() -> {
            try {
                return unmarshaller.get();
            } catch (JAXBException ex) {
                // This should be unreachable
                throw new AssertionError("Couldn't get marshaller subsequent times", ex);
            }
        });
    }

    private Unmarshaller getUnmarshaller() {
        return unmarshaller.get();
    }

    // Delegate

    @Override
    public Object unmarshal(File f) throws JAXBException {
        return getUnmarshaller().unmarshal(f);
    }

    @Override
    public Object unmarshal(InputStream is) throws JAXBException {
        return getUnmarshaller().unmarshal(is);
    }

    @Override
    public Object unmarshal(Reader reader) throws JAXBException {
        return getUnmarshaller().unmarshal(reader);
    }

    @Override
    public Object unmarshal(URL url) throws JAXBException {
        return getUnmarshaller().unmarshal(url);
    }

    @Override
    public Object unmarshal(InputSource source) throws JAXBException {
        return getUnmarshaller().unmarshal(source);
    }

    @Override
    public Object unmarshal(Node node) throws JAXBException {
        return getUnmarshaller().unmarshal(node);
    }

    @Override
    public <T> JAXBElement<T> unmarshal(Node node, Class<T> declaredType) throws JAXBException {
        return getUnmarshaller().unmarshal(node, declaredType);
    }

    @Override
    public Object unmarshal(Source source) throws JAXBException {
        return getUnmarshaller().unmarshal(source);
    }

    @Override
    public <T> JAXBElement<T> unmarshal(Source source, Class<T> declaredType) throws JAXBException {
        return getUnmarshaller().unmarshal(source, declaredType);
    }

    @Override
    public Object unmarshal(XMLStreamReader reader) throws JAXBException {
        return getUnmarshaller().unmarshal(reader);
    }

    @Override
    public <T> JAXBElement<T> unmarshal(XMLStreamReader reader, Class<T> declaredType) throws JAXBException {
        return getUnmarshaller().unmarshal(reader, declaredType);
    }

    @Override
    public Object unmarshal(XMLEventReader reader) throws JAXBException {
        return getUnmarshaller().unmarshal(reader);
    }

    @Override
    public <T> JAXBElement<T> unmarshal(XMLEventReader reader, Class<T> declaredType) throws JAXBException {
        return getUnmarshaller().unmarshal(reader, declaredType);
    }

    @Override
    public UnmarshallerHandler getUnmarshallerHandler() {
        return getUnmarshaller().getUnmarshallerHandler();
    }

    @Override
    public void setValidating(boolean validating) throws JAXBException {
        getUnmarshaller().setValidating(validating);
    }

    @Override
    public boolean isValidating() throws JAXBException {
        return getUnmarshaller().isValidating();
    }

    @Override
    public void setEventHandler(ValidationEventHandler handler) throws JAXBException {
        getUnmarshaller().setEventHandler(handler);
    }

    @Override
    public ValidationEventHandler getEventHandler() throws JAXBException {
        return getUnmarshaller().getEventHandler();
    }

    @Override
    public void setProperty(String name, Object value) throws PropertyException {
        getUnmarshaller().setProperty(name, value);
    }

    @Override
    public Object getProperty(String name) throws PropertyException {
        return getUnmarshaller().getProperty(name);
    }

    @Override
    public void setSchema(Schema schema) {
        getUnmarshaller().setSchema(schema);
    }

    @Override
    public Schema getSchema() {
        return getUnmarshaller().getSchema();
    }

    @Override
    public void setAdapter(XmlAdapter adapter) {
        getUnmarshaller().setAdapter(adapter);
    }

    @Override
    public <A extends XmlAdapter> void setAdapter(Class<A> type, A adapter) {
        getUnmarshaller().setAdapter(type, adapter);
    }

    @Override
    public <A extends XmlAdapter> A getAdapter(Class<A> type) {
        return getUnmarshaller().getAdapter(type);
    }

    @Override
    public void setAttachmentUnmarshaller(AttachmentUnmarshaller au) {
        getUnmarshaller().setAttachmentUnmarshaller(au);
    }

    @Override
    public AttachmentUnmarshaller getAttachmentUnmarshaller() {
        return getUnmarshaller().getAttachmentUnmarshaller();
    }

    @Override
    public void setListener(Listener listener) {
        getUnmarshaller().setListener(listener);
    }

    @Override
    public Listener getListener() {
        return getUnmarshaller().getListener();
    }
}
