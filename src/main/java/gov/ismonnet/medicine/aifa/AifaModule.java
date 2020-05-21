package gov.ismonnet.medicine.aifa;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class AifaModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MedicineService.class).to(AifaMedicineService.class);
        bind(AifaMedicineService.class).in(Singleton.class);
    }
}
