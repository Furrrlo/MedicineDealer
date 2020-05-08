package gov.ismonnet.medicine.persistence;

public interface CredentialsService {

    String get(String key);

    int getInt(String key);

    double getDouble(String key);
}
