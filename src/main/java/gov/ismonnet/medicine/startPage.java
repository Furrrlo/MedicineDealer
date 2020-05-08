package gov.ismonnet.medicine;

import org.jooq.DSLContext;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@Path("pagina_iniziale")
public class startPage {
    private final DSLContext ctx;

    public startPage(DSLContext ctx) {
        this.ctx = ctx;
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public String registrazione(){

        return null;
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public String accedi(){

        return null;
    }
}
