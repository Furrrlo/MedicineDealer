package gov.ismonnet.medicine.converters;

import gov.ismonnet.medicine.jaxb.UncheckedUnmarshalException;
import org.xml.sax.SAXException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.UnmarshalException;

@Provider
public class UnmarshalExceptionMapper implements ExceptionMapper<UncheckedUnmarshalException> {

    @Override
    public Response toResponse(UncheckedUnmarshalException exception) {

        final UnmarshalException ex = exception.getCause();
        final String errMessage = ex.getLinkedException() instanceof SAXException ?
                ex.getLinkedException().getMessage() :
                null;

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errMessage)
                .type(MediaType.TEXT_PLAIN)
                .build();
    }
}
