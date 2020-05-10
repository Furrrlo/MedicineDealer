package gov.ismonnet.medicine.converters;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalTime;

public class LocalTimeXmlAdapter extends XmlAdapter<String, LocalTime> {

    @Override
    public LocalTime unmarshal(String v) {
        return LocalTime.parse(v);
    }

    @Override
    public String marshal(LocalTime v) {
        return v.toString();
    }
}
