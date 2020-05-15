package gov.ismonnet.medicine.authentication;

import com.google.inject.AbstractModule;

public class AuthenticationModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AuthenticationService.class).to(JwtAuthenticationService.class);
        bind(JwtAuthenticationService.class).asEagerSingleton();
    }
}
