package gov.ismonnet.medicine.database;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.sql.DataSource;

public class MySqlModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(DatabaseService.class).to(MySqlDatabaseService.class);
        bind(MySqlDatabaseService.class).in(Singleton.class);
    }

    @Provides
    public DataSource provideDataSource(DatabaseService databaseService) {
        return databaseService.getDataSource();
    }

    @Provides
    public DSLContext provideDsl(DataSource dataSource) {
        return DSL.using(dataSource, SQLDialect.MYSQL_8_0);
    }
}
