package gov.ismonnet.medicine.api;

import gov.ismonnet.medicine.aifa.MedicineService;
import gov.ismonnet.medicine.authentication.Authenticated;
import gov.ismonnet.medicine.authentication.AuthorizedDevice;
import gov.ismonnet.medicine.database.Tables;
import gov.ismonnet.medicine.jaxb.ws.Medicina;
import gov.ismonnet.medicine.jaxb.ws.MedicinesBean;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.SQLDataType;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.stream.Collectors;

@Path("farmaci")
public class Medicines {

//    private final String TEXT_CSV = "text/csv";
//    private final MediaType TEXT_CSV_TYPE = new MediaType("text", "csv");

    private final MedicineService medicineService;
    private final DSLContext ctx;

    @Inject Medicines(final DSLContext ctx,
                      final MedicineService medicineService) {
        this.ctx = ctx;
        this.medicineService = medicineService;
    }

    @GET
    @Path("{nome}")
    @Produces(MediaType.APPLICATION_XML)
    public MedicinesBean getMedicines(@NotNull @PathParam(value = "nome") String name) {
        return new MedicinesBean(medicineService.findMedicinesByName(name));
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public MedicinesBean getMedicines(@Authenticated int userId,
                                      @AuthorizedDevice @QueryParam(value = "id_porta_medicine") int deviceId) {
        final Field<String> codAicRow = Tables.FARMACI.COD_AIC.cast(SQLDataType.CHAR(9));
        return new MedicinesBean(ctx.select(Tables.FARMACI.NOME, codAicRow)
                .from(Tables.PORTA_MEDICINE)
                .join(Tables.EVENTI)
                .on(Tables.EVENTI.ID_PORTA_MEDICINE.eq(Tables.PORTA_MEDICINE.ID))
                .join(Tables.FARMACI)
                .on(Tables.FARMACI.COD_AIC.eq(Tables.EVENTI.AIC_FARMACO))
                .where(Tables.EVENTI.ID_PORTA_MEDICINE.equal(deviceId))
                .fetch()
                .stream()
                .map(r -> new Medicina(
                    r.get(Tables.FARMACI.NOME),
                    String.valueOf(r.get(codAicRow))
                ))
                .collect(Collectors.toList()));
    }

    // For the csv files
//    @POST
//    @Path("/csv")
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    public Response parseMedicines(@FormDataParam("file") InputStream uploadedInputStream,
//                                   @FormDataParam("file") FormDataBodyPart bodyPart) {
//
//
//        if(!bodyPart.getMediaType().equals(TEXT_CSV_TYPE))
//            return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).build();
//
//        final InsertValuesStep2<FarmaciRecord, UInteger, String> insert = ctx
//                .insertInto(Tables.FARMACI)
//                .columns(Tables.FARMACI.COD_AIC, Tables.FARMACI.NOME);
//
//        try(CSVParser parser = CSVFormat.DEFAULT.parse(new InputStreamReader(uploadedInputStream, StandardCharsets.UTF_8))) {
//            parser.forEach(record -> {
//                try {
//                    final UInteger aic = UInteger.valueOf(record.get(0));
//                    final String desc = record.get(1);
//
//                    insert.values(aic, desc);
//                } catch (NumberFormatException ex) {
//                    // Ignored
//                }
//            });
//        } catch (IOException ex) {
//            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
//        }
//
//        insert.execute();
//        return Response.ok().build();
//    }
}
