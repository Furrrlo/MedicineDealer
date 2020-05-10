package gov.ismonnet.medicine.converters;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class LocalDateParamConverter implements ParamConverter<LocalDate> {

    private static final LocalDateParamConverter INSTANCE = new LocalDateParamConverter();

    @Override
    public LocalDate fromString(String value) {
        if(value == null || value.isEmpty())
            return null;

        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            throw new BadRequestException(ex);
        }
    }

    @Override
    public String toString(LocalDate value) {
        return value.toString();
    }

    @Provider
    private static class ConverterProvider implements ParamConverterProvider {
        @Override
        public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
            if (!LocalDate.class.equals(rawType))
                return null;
            //noinspection unchecked
            return (ParamConverter<T>) LocalDateParamConverter.INSTANCE;
        }
    }
}
