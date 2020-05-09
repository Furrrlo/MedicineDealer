package gov.ismonnet.medicine.jaxb;

import org.xml.sax.SAXException;

import javax.xml.validation.Schema;

public interface SchemaResolver {
    Schema resolve(String namespace, String name) throws SAXException;
}
