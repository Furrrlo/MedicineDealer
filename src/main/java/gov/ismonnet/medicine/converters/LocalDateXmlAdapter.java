package gov.ismonnet.medicine.converters;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;

public class LocalDateXmlAdapter extends XmlAdapter<String, LocalDate> {

    @Override
    public LocalDate unmarshal(String v) {
        return LocalDate.parse(v);
    }

    @Override
    public String marshal(LocalDate v) {
        return v.toString();
    }
}
