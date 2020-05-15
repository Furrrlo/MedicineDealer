package gov.ismonnet.medicine.persistence;

import java.util.Optional;

public interface CredentialsService {

    String get(String key);

    Optional<String> getOptional(String key);

    int getInt(String key);

    Optional<Integer> getOptionalInt(String key);

    double getDouble(String key);

    Optional<Double> getOptionalDouble(String key);
}
