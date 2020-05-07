package gov.ismonnet.medicine;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("hello")
public class HelloWorld {

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getHelloWorld() {
        return "<message>CIAO PATO!</message>";
    }
}
