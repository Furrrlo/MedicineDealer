package gov.ismonnet.medicine.persistence;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

public class FileKeyStoreService extends DelegatingKeyStoreService {

    private final Path keyStoreFile;
    private final CredentialsService credentialsService;

    private final KeyStore keyStore;

    @Inject FileKeyStoreService(@gov.ismonnet.medicine.persistence.KeyStore Path keyStoreFile,
                                CredentialsService credentialsService) throws Exception {

        this.keyStoreFile = keyStoreFile;
        this.credentialsService = credentialsService;

        this.keyStore = KeyStore.getInstance("JCEKS");
        if(Files.notExists(keyStoreFile)) {
            keyStore.load(null, credentialsService.get("keyStore.password").toCharArray());
            store();
            return;
        }

        try(InputStream is = Files.newInputStream(keyStoreFile)) {
            keyStore.load(is, credentialsService.get("keyStore.password").toCharArray());
        }
    }

    @Override
    public KeyStore getKeyStore() {
        return keyStore;
    }

    @Override
    public Key getKey(String alias) throws UnrecoverableKeyException {
        final char[] password = credentialsService.getOptional("keyStore." + alias + ".password")
                .orElseGet(() -> credentialsService.get("keyStore.password"))
                .toCharArray();
        return getKey(alias, password);
    }

    @Override
    public void setKeyEntry(String alias, Key key, Certificate[] chain) throws KeyStoreException {
        final char[] password = credentialsService.getOptional("keyStore." + alias + ".password")
                .orElseGet(() -> credentialsService.get("keyStore.password"))
                .toCharArray();
        setKeyEntry(alias, key, password, chain);
    }

    @Override
    public void store() throws CertificateException {
        try(OutputStream os = Files.newOutputStream(keyStoreFile)) {
            keyStore.store(os, credentialsService.get("keyStore.password").toCharArray());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            throw new AssertionError(e); // Unreachable
        }
    }
}
