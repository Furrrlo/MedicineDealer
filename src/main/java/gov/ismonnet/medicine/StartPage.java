package gov.ismonnet.medicine;

import org.jooq.DSLContext;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Path("pagina_iniziale")
public class StartPage {

    private final DSLContext ctx;

    @Inject StartPage(DSLContext ctx) {
        this.ctx = ctx;
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public RegistrationBean registrazione(RegistrationBean registrationBean) {
        return registrationBean;
    }

    @XmlRootElement(name = "registrazione")
    static class RegistrationBean {
        @XmlElement(required = true) public String nome;
        @XmlElement(required = true) public String cognome;
        @XmlElement(required = true) public String email;
        @XmlElement(name = "data_nascita", required = true) public String dataNascita;
        @XmlElement(required = true) public String password;
    }

    @GET
    @Consumes(MediaType.APPLICATION_XML)
    public String accedi() {

        return null;
    }
}
