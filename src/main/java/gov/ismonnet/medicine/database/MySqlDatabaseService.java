package gov.ismonnet.medicine.database;

import gov.ismonnet.medicine.persistence.CredentialsService;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.flywaydb.core.Flyway;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;

public class MySqlDatabaseService implements DatabaseService {

    private final PoolProperties properties;
    private final DataSource dataSource;

    @Inject MySqlDatabaseService(CredentialsService credentialsService) {
        properties = new PoolProperties();

        final String schemaName = credentialsService
                .getOptional("database.schema")
                .orElse("medicine_dealer");
        final String url = credentialsService
                .getOptional("database.url")
                .orElseGet(() -> "jdbc:mysql://localhost:" + credentialsService
                        .getOptional("database.port")
                        .orElse("3306") + "/");
        properties.setUrl(url + schemaName);
        properties.setDriverClassName(credentialsService
                .getOptional("database.driver")
                .orElse("com.mysql.cj.jdbc.Driver"));
        properties.setUsername(credentialsService.get("user"));
        properties.setPassword(credentialsService.get("password"));

        properties.getDbProperties().setProperty("createDatabaseIfNotExist", "true");
        // Unicode
        properties.getDbProperties().setProperty("useUnicode", "yes");
        properties.getDbProperties().setProperty("characterEncoding", "UTF-8");
        // Pool
        properties.setInitialSize(15);
        properties.setMinIdle(15);
        properties.setMaxActive(20);

        dataSource = new DataSource();
        dataSource.setPoolProperties(properties);

        // Making sure everything exists.
        final Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .schemas(schemaName)
                .encoding(StandardCharsets.UTF_8)
                .load();
        flyway.migrate();
    }

    @Override
    public javax.sql.DataSource getDataSource() {
        return dataSource;
    }
}
