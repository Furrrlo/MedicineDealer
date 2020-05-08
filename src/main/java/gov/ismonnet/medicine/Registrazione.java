package gov.ismonnet.medicine;

import org.jooq.DSLContext;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;

@Path("registrazione")
public class Registrazione {
    private final DSLContext ctx;

    public Registrazione(DSLContext ctx) {
        this.ctx = ctx;
    }

    @PUT
    public void nuovoUtente(){

    }
}
