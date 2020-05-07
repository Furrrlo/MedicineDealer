package gov.ismonnet.medicine.database;

import javax.sql.DataSource;

public interface DatabaseService {
    DataSource getDataSource();
}
