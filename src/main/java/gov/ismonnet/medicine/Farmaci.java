package gov.ismonnet.medicine;

import gov.ismonnet.medicine.database.Tables;
import org.jooq.DSLContext;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.stream.Collectors;

@Path("farmaci")
public class Farmaci {

    private final DSLContext ctx;

    @Inject Farmaci(DSLContext ctx) {
        this.ctx = ctx;
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getFarmaci() {
        return ctx.select(Tables.FARMACI.COD_AIC, Tables.FARMACI.NOME)
                .from(Tables.FARMACI)
                .fetch()
                .stream()
                .map(res -> "" +
                        "<farmaco>" +
                        "<cod_aic>" + res.value1() + "</cod_aic>" +
                        "<nome>" + res.value2() + "</nome>" +
                        "</farmaco>"
                )
                .collect(Collectors.joining("", "<farmaci>", "</farmaci>"));
    }
}
