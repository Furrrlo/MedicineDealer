package gov.ismonnet.medicine.jaxb;

import org.w3c.dom.ls.LSInput;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

public abstract class BaseSchemaResolver implements SchemaResolver {

    private final SchemaFactory schemaFactory;

    public BaseSchemaResolver(Charset charset) {
        this.schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        this.schemaFactory.setResourceResolver((type, namespaceURI, publicId, systemId, baseURI) -> {
            // Not an xsd
            if(!type.equals(XMLConstants.W3C_XML_SCHEMA_NS_URI))
                return null;
            // Needs to not have a namespace
            if(namespaceURI != null)
                return null;
            // Needs to be a URI reference and not an external entity
            if(publicId != null || systemId == null)
                return null;
            // Needs to be a relative reference
            if(baseURI != null)
                return null;
            return new CharacterStreamOnlyLSInput(new InputStreamReader(
                    getSchemaInputStream(systemId),
                    charset));
        });
    }

    public Schema resolve(String namespace, String name) throws SAXException {
        final StreamSource source = new StreamSource(getSchemaInputStream(name + ".xsd"));
        return schemaFactory.newSchema(source);
    }

    protected abstract InputStream getSchemaInputStream(String name);

    static class CharacterStreamOnlyLSInput implements LSInput {

        private Reader characterStream;

        public CharacterStreamOnlyLSInput(Reader characterStream) {
            this.characterStream = characterStream;
        }

        @Override
        public Reader getCharacterStream() {
            return characterStream;
        }

        @Override
        public void setCharacterStream(Reader characterStream) {
            this.characterStream = characterStream;
        }

        @Override
        public InputStream getByteStream() {
            return null;
        }

        @Override
        public void setByteStream(InputStream byteStream) {
        }

        @Override
        public String getStringData() {
            return null;
        }

        @Override
        public void setStringData(String stringData) {
        }

        @Override
        public String getSystemId() {
            return null;
        }

        @Override
        public void setSystemId(String systemId) {
        }

        @Override
        public String getPublicId() {
            return null;
        }

        @Override
        public void setPublicId(String publicId) {
        }

        @Override
        public String getBaseURI() {
            return null;
        }

        @Override
        public void setBaseURI(String baseURI) {
        }

        @Override
        public String getEncoding() {
            return null;
        }

        @Override
        public void setEncoding(String encoding) {
        }

        @Override
        public boolean getCertifiedText() {
            return false;
        }

        @Override
        public void setCertifiedText(boolean certifiedText) {
        }
    }
}
