package gov.ismonnet.medicine.database;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.flywaydb.core.Flyway;

import java.nio.charset.StandardCharsets;

public class MySqlDatabaseService implements DatabaseService {

    private static final String SCHEMA_NAME = "medicine_dealer";;

    private final PoolProperties properties;
    private final DataSource dataSource;

    public MySqlDatabaseService() {
        properties = new PoolProperties();
        properties.setUrl("jdbc:mysql://localhost:3306/" + SCHEMA_NAME + "?" +
                "createDatabaseIfNotExist=true&" +
                "serverTimezone=UTC&" +
                "useUnicode=yes&" +
                "characterEncoding=UTF-8");
        properties.setDriverClassName("com.mysql.cj.jdbc.Driver");
        // TODO: temp
        properties.setUsername("jooq");
        properties.setPassword("password");
//        dataSource.setUser(credentialsService.get("mysql.username"));
//        dataSource.setPassword(credentialsService.get("mysql.password"));
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
