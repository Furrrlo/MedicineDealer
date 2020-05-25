package gov.ismonnet.medicine.jaxb;

import javax.xml.bind.UnmarshalException;

public class UncheckedUnmarshalException extends RuntimeException {

    public UncheckedUnmarshalException(UnmarshalException cause) {
        super(cause);
    }

    @Override
    public UnmarshalException getCause() {
        return (UnmarshalException) super.getCause();
    }
}
