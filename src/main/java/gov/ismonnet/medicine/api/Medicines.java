package gov.ismonnet.medicine.api;

import gov.ismonnet.medicine.database.Tables;
import gov.ismonnet.medicine.jaxb.ws.Medicine;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.types.UInteger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("farmaci")
public class Medicines {

    private final String TEXT_CSV = "text/csv";
    private final MediaType TEXT_CSV_TYPE = new MediaType("text", "csv");

    private final DSLContext ctx;

    @Inject Medicines(DSLContext ctx) {
        this.ctx = ctx;
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Medicine getMedicines() {
        Result<Record2<UInteger, String>> query = ctx
                .select(Tables.FARMACI.COD_AIC, Tables.FARMACI.NOME)
                .from(Tables.FARMACI)
                .fetch();

        List<Medicine.Medicina> medicinaList = new ArrayList<>();
        for(Record2<UInteger, String> medicina : query)
            medicinaList.add(new Medicine.Medicina(medicina.value2(), medicina.value1().toBigInteger()));
        return new Medicine(medicinaList);
    }
}
