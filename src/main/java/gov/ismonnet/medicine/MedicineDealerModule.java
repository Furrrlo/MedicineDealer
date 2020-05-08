package gov.ismonnet.medicine;

import com.google.inject.AbstractModule;
import gov.ismonnet.medicine.database.MySqlModule;
import gov.ismonnet.medicine.persistence.Credentials;
import gov.ismonnet.medicine.persistence.CredentialsModule;

public class MedicineDealerModule extends AbstractModule implements Constants {

    @Override
    protected void configure() {
        bindConstant().annotatedWith(Credentials.class).to(CREDENTIALS_FILE);

        install(new CredentialsModule());
        install(new MySqlModule());
    }
}
