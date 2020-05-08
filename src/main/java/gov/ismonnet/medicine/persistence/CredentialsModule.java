package gov.ismonnet.medicine.persistence;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class CredentialsModule extends AbstractModule {

    @Provides
    @Singleton
    public CredentialsService provideCredentialsService(@Credentials String credentialsFile) {

        final Path file = Paths.get(credentialsFile);
        try(Reader reader = Files.newBufferedReader(file,StandardCharsets.UTF_8)) {
            final Properties properties = new Properties();
            properties.load(reader);
            return new PropertiesCredentialsService(properties);
        } catch (IOException ex) {
            throw new UncheckedIOException("Cannot read credentials file", ex);
        }
    }
}
