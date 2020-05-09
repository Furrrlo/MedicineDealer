package gov.ismonnet.medicine.jaxb;

import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.validation.Schema;
import java.io.File;
import java.io.OutputStream;
import java.io.Writer;

public class ConcurrentMarshaller implements Marshaller {

    private final ThreadLocal<Marshaller> marshaller;

    public ConcurrentMarshaller(UncheckedSupplier<Marshaller> marshaller) throws JAXBException {
        // Try to get one and throw exceptions instantly if it does
        marshaller.get();
        // The first one was fine, the others _should_ be too
        this.marshaller = ThreadLocal.withInitial(() -> {
            try {
                return marshaller.get();
            } catch (JAXBException ex) {
                // This should be unreachable
                throw new AssertionError("Couldn't get marshaller subsequent times", ex);
            }
        });
    }

    private Marshaller getMarshaller() {
        return marshaller.get();
    }

    // Delegate

    @Override
    public void marshal(Object jaxbElement, Result result) throws JAXBException {
        getMarshaller().marshal(jaxbElement, result);
    }

    @Override
    public void marshal(Object jaxbElement, OutputStream os) throws JAXBException {
        getMarshaller().marshal(jaxbElement, os);
    }

    @Override
    public void marshal(Object jaxbElement, File output) throws JAXBException {
        getMarshaller().marshal(jaxbElement, output);
    }

    @Override
    public void marshal(Object jaxbElement, Writer writer) throws JAXBException {
        getMarshaller().marshal(jaxbElement, writer);
    }

    @Override
    public void marshal(Object jaxbElement, ContentHandler handler) throws JAXBException {
        getMarshaller().marshal(jaxbElement, handler);
    }

    @Override
    public void marshal(Object jaxbElement, Node node) throws JAXBException {
        getMarshaller().marshal(jaxbElement, node);
    }

    @Override
    public void marshal(Object jaxbElement, XMLStreamWriter writer) throws JAXBException {
        getMarshaller().marshal(jaxbElement, writer);
    }

    @Override
    public void marshal(Object jaxbElement, XMLEventWriter writer) throws JAXBException {
        getMarshaller().marshal(jaxbElement, writer);
    }

    @Override
    public Node getNode(Object contentTree) throws JAXBException {
        return getMarshaller().getNode(contentTree);
    }

    @Override
    public void setProperty(String name, Object value) throws PropertyException {
        getMarshaller().setProperty(name, value);
    }

    @Override
    public Object getProperty(String name) throws PropertyException {
        return getMarshaller().getProperty(name);
    }

    @Override
    public void setEventHandler(ValidationEventHandler handler) throws JAXBException {
        getMarshaller().setEventHandler(handler);
    }

    @Override
    public ValidationEventHandler getEventHandler() throws JAXBException {
        return getMarshaller().getEventHandler();
    }

    @Override
    public void setAdapter(XmlAdapter adapter) {
        getMarshaller().setAdapter(adapter);
    }

    @Override
    public <A extends XmlAdapter> void setAdapter(Class<A> type, A adapter) {
        getMarshaller().setAdapter(type, adapter);
    }

    @Override
    public <A extends XmlAdapter> A getAdapter(Class<A> type) {
        return getMarshaller().getAdapter(type);
    }

    @Override
    public void setAttachmentMarshaller(AttachmentMarshaller am) {
        getMarshaller().setAttachmentMarshaller(am);
    }

    @Override
    public AttachmentMarshaller getAttachmentMarshaller() {
        return getMarshaller().getAttachmentMarshaller();
    }

    @Override
    public void setSchema(Schema schema) {
        getMarshaller().setSchema(schema);
    }

    @Override
    public Schema getSchema() {
        return getMarshaller().getSchema();
    }

    @Override
    public void setListener(Listener listener) {
        getMarshaller().setListener(listener);
    }

    @Override
    public Listener getListener() {
        return getMarshaller().getListener();
    }
}
