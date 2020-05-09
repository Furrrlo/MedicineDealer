package gov.ismonnet.medicine.jaxb;

import javax.xml.bind.JAXBException;

@FunctionalInterface
public interface UncheckedSupplier<T> {
    T get() throws JAXBException;
}
