package gov.ismonnet.medicine.api;

import gov.ismonnet.medicine.database.Tables;
import gov.ismonnet.medicine.jaxb.LocalDateTypeAdapter;
import org.jooq.DSLContext;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.sql.Date;
import java.time.LocalDate;

@Path("pagina_iniziale")
public class StartPage {

    private final DSLContext ctx;
    private final PasswordEncoder passwordEncoder;

    @Inject StartPage(DSLContext ctx, PasswordEncoder passwordEncoder) {
        this.ctx = ctx;
        this.passwordEncoder = passwordEncoder;
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public RegistrationBean registrazione(RegistrationBean registrationBean) {

        final String hash = passwordEncoder.encode(registrationBean.password);
        ctx.insertInto(Tables.UTENTI)
                .set(Tables.UTENTI.NOME,registrationBean.nome)
                .set(Tables.UTENTI.COGNOME,registrationBean.cognome)
                .set(Tables.UTENTI.EMAIL,registrationBean.email)
                .set(Tables.UTENTI.DATA_NASCITA, Date.valueOf(registrationBean.dataNascita))
                .set(Tables.UTENTI.PASSWORD, hash)
                .execute();

        return registrationBean;
    }

    @XmlRootElement(name = "registrazione")
    static class RegistrationBean {
        @XmlElement(required = true) public String nome;
        @XmlElement(required = true) public String cognome;
        @XmlElement(required = true) public String email;
        @XmlJavaTypeAdapter(value = LocalDateTypeAdapter.class, type = LocalDate.class)
        @XmlElement(name = "data_nascita", required = true)
        public LocalDate dataNascita;
        @XmlElement(required = true) public String password;
    }

    @GET
    @Consumes(MediaType.APPLICATION_XML)
    public String accedi() {
        // To check if a password matches its hash get the has from the db then
        // passwordEncoder.matches(pw, hash)
        return null;
    }
}
