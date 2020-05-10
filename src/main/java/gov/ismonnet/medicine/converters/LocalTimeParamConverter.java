package gov.ismonnet.medicine.converters;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class LocalTimeParamConverter implements ParamConverter<LocalTime> {

    private static final LocalTimeParamConverter INSTANCE = new LocalTimeParamConverter();

    @Override
    public LocalTime fromString(String value) {
        if(value == null || value.isEmpty())
            return null;

        try {
            return LocalTime.parse(value);
        } catch (DateTimeParseException ex) {
            throw new BadRequestException(ex);
        }
    }

    @Override
    public String toString(LocalTime value) {
        return value.toString();
    }

    @Provider
    private static class ConverterProvider implements ParamConverterProvider {
        @Override
        public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
            if (!LocalTime.class.equals(rawType))
                return null;
            //noinspection unchecked
            return (ParamConverter<T>) LocalTimeParamConverter.INSTANCE;
        }
    }
}
