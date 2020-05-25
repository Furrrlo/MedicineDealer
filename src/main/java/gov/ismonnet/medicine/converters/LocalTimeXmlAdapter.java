package gov.ismonnet.medicine.converters;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.temporal.ChronoField.*;

public class LocalTimeXmlAdapter extends XmlAdapter<String, LocalTime> {

    private final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2)
            .toFormatter();

    @Override
    public LocalTime unmarshal(String v) {
        return LocalTime.parse(v);
    }

    @Override
    public String marshal(LocalTime v) {
        return v.format(formatter);
    }
}
