package gov.ismonnet.medicine.aifa;

import gov.ismonnet.medicine.jaxb.ws.Medicina;

import java.util.List;

public interface MedicineService {

    Medicina getMedicineByAic(String aic);

    List<Medicina> findMedicinesByAic(String aic);

    List<Medicina> findMedicinesByName(String name);
}
