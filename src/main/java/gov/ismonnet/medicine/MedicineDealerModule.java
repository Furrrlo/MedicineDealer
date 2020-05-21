package gov.ismonnet.medicine;

import com.google.inject.AbstractModule;
import gov.ismonnet.medicine.aifa.AifaModule;
import gov.ismonnet.medicine.authentication.AuthenticationCookie;
import gov.ismonnet.medicine.authentication.AuthenticationModule;
import gov.ismonnet.medicine.authentication.AuthorizationSchema;
import gov.ismonnet.medicine.database.MySqlModule;
import gov.ismonnet.medicine.persistence.Credentials;
import gov.ismonnet.medicine.persistence.KeyStore;
import gov.ismonnet.medicine.persistence.PersistenceModule;

public class MedicineDealerModule extends AbstractModule implements Constants {

    @Override
    protected void configure() {
        bindConstant().annotatedWith(Credentials.class).to(CREDENTIALS_FILE);
        bindConstant().annotatedWith(KeyStore.class).to(KEYSTORE_FILE);
        bindConstant().annotatedWith(AuthorizationSchema.class).to(AUTHORIZATION_HEADER_SCHEMA);
        bindConstant().annotatedWith(AuthenticationCookie.class).to(AUTHENTICATION_COOKIE_NAME);

        install(new PersistenceModule());
        install(new MySqlModule());
        install(new AuthenticationModule());
        install(new AifaModule());
    }
}
