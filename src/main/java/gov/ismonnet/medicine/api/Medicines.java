package gov.ismonnet.medicine.api;

import gov.ismonnet.medicine.aifa.MedicineService;
import gov.ismonnet.medicine.jaxb.ws.MedicinesBean;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("farmaci")
public class Medicines {

//    private final String TEXT_CSV = "text/csv";
//    private final MediaType TEXT_CSV_TYPE = new MediaType("text", "csv");

    private final MedicineService medicineService;

    @Inject Medicines(final MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @GET
    @Path("{nome}")
    @Produces(MediaType.APPLICATION_XML)
    public MedicinesBean getMedicines(@PathParam(value = "nome") String name) {
        return new MedicinesBean(medicineService.findMedicinesByName(name));
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
