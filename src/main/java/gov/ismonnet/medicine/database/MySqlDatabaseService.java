package gov.ismonnet.medicine.database;

import gov.ismonnet.medicine.persistence.CredentialsService;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.flywaydb.core.Flyway;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;

public class MySqlDatabaseService implements DatabaseService {

    private static final String SCHEMA_NAME = "medicine_dealer";;

    private final PoolProperties properties;
    private final DataSource dataSource;

    @Inject MySqlDatabaseService(CredentialsService credentialsService) {
        properties = new PoolProperties();
        properties.setUrl("jdbc:mysql://localhost:3306/" + SCHEMA_NAME + "?" +
                "createDatabaseIfNotExist=true&" +
                "serverTimezone=UTC&" +
                "useUnicode=yes&" +
                "characterEncoding=UTF-8");
        properties.setDriverClassName("com.mysql.cj.jdbc.Driver");
        properties.setUsername(credentialsService.get("user"));
        properties.setPassword(credentialsService.get("password"));
        properties.setInitialSize(15);
        properties.setMinIdle(15);
        properties.setMaxActive(20);

        dataSource = new DataSource();
        dataSource.setPoolProperties(properties);

        // Making sure everything exists.
        final Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .schemas(SCHEMA_NAME)
                .encoding(StandardCharsets.UTF_8)
                .load();
        flyway.migrate();
    }

    @Override
    public javax.sql.DataSource getDataSource() {
        return dataSource;
    }
}
