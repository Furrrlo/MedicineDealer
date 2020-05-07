package gov.ismonnet.medicine;

import com.google.inject.AbstractModule;
import gov.ismonnet.medicine.database.MySqlModule;

public class MedicineDealerModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new MySqlModule());
    }
}
