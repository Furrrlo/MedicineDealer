package gov.ismonnet.medicine.persistence;

import java.security.*;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Date;
import java.util.Enumeration;

public abstract class DelegatingKeyStoreService implements KeyStoreService {

    @Override
    public Key getKey(String alias, char[] password) throws UnrecoverableKeyException {
        try {
            return getKeyStore().getKey(alias, password);
        } catch (KeyStoreException | NoSuchAlgorithmException e) {
            throw new AssertionError(e); // Unreachable
        }
    }

    @Override
    public Certificate[] getCertificateChain(String alias) {
        try {
            return getKeyStore().getCertificateChain(alias);
        } catch (KeyStoreException e) {
            throw new AssertionError(e); // Unreachable
        }
    }

    @Override
    public Certificate getCertificate(String alias) {
        try {
            return getKeyStore().getCertificate(alias);
        } catch (KeyStoreException e) {
            throw new AssertionError(e); // Unreachable
        }
    }

    @Override
    public Date getCreationDate(String alias) {
        try {
            return getKeyStore().getCreationDate(alias);
        } catch (KeyStoreException e) {
            throw new AssertionError(e); // Unreachable
        }
    }

    @Override
    public void setKeyEntry(String alias, Key key, char[] password, Certificate[] chain) throws KeyStoreException {
        getKeyStore().setKeyEntry(alias, key, password, chain);
    }

    @Override
    public void setKeyEntry(String alias, byte[] key, Certificate[] chain) throws KeyStoreException {
        getKeyStore().setKeyEntry(alias, key, chain);
    }

    @Override
    public void setCertificateEntry(String alias, Certificate cert) throws KeyStoreException {
        getKeyStore().setCertificateEntry(alias, cert);
    }

    @Override
    public void deleteEntry(String alias) throws KeyStoreException {
        getKeyStore().deleteEntry(alias);
    }

    @Override
    public Enumeration<String> aliases() {
        try {
            return getKeyStore().aliases();
        } catch (KeyStoreException e) {
            throw new AssertionError(e); // Unreachable
        }
    }

    @Override
    public boolean containsAlias(String alias) {
        try {
            return getKeyStore().containsAlias(alias);
        } catch (KeyStoreException e) {
            throw new AssertionError(e); // Unreachable
        }
    }

    @Override
    public int size() {
        try {
            return getKeyStore().size();
        } catch (KeyStoreException e) {
            throw new AssertionError(e); // Unreachable
        }
    }

    @Override
    public boolean isKeyEntry(String alias) {
        try {
            return getKeyStore().isKeyEntry(alias);
        } catch (KeyStoreException e) {
            throw new AssertionError(e); // Unreachable
        }
    }

    @Override
    public boolean isCertificateEntry(String alias) {
        try {
            return getKeyStore().isCertificateEntry(alias);
        } catch (KeyStoreException e) {
            throw new AssertionError(e); // Unreachable
        }
    }

    @Override
    public String getCertificateAlias(Certificate cert) {
        try {
            return getKeyStore().getCertificateAlias(cert);
        } catch (KeyStoreException e) {
            throw new AssertionError(e); // Unreachable
        }
    }

    @Override
    public KeyStore.Entry getEntry(String alias, KeyStore.ProtectionParameter protParam) throws UnrecoverableEntryException {
        try {
            return getKeyStore().getEntry(alias, protParam);
        } catch (KeyStoreException | NoSuchAlgorithmException e) {
            throw new AssertionError(e); // Unreachable
        }
    }

    @Override
    public void setEntry(String alias, KeyStore.Entry entry, KeyStore.ProtectionParameter protParam) throws KeyStoreException {
        getKeyStore().setEntry(alias, entry, protParam);
    }

    @Override
    public boolean entryInstanceOf(String alias, Class<? extends KeyStore.Entry> entryClass) {
        try {
            return getKeyStore().entryInstanceOf(alias, entryClass);
        } catch (KeyStoreException e) {
            throw new AssertionError(e); // Unreachable
        }
    }
}
