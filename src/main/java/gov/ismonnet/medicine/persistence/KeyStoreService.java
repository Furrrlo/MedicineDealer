package gov.ismonnet.medicine.persistence;

import java.io.OutputStream;
import java.security.*;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Enumeration;

public interface KeyStoreService {

    KeyStore getKeyStore();

    /** @see KeyStore#getKey(String, char[]) */
    Key getKey(String alias, char[] password) throws UnrecoverableKeyException;

    /** @see #getKey(String, char[]) */
    Key getKey(String alias) throws UnrecoverableKeyException;

    /** @see KeyStore#getCertificateChain(String) */
    Certificate[] getCertificateChain(String alias);

    /** @see KeyStore#getCertificate(String) */
    Certificate getCertificate(String alias);

    /** @see KeyStore#getCreationDate(String) */
    Date getCreationDate(String alias);

    /** @see KeyStore#setKeyEntry(String, Key, char[], Certificate[]) */
    void setKeyEntry(String alias, Key key, char[] password, Certificate[] chain) throws KeyStoreException;

    /** @see #setKeyEntry(String, Key, char[], Certificate[]) */
    void setKeyEntry(String alias, Key key, Certificate[] chain) throws KeyStoreException;

    /** @see KeyStore#setKeyEntry(String, byte[], Certificate[]) */
    void setKeyEntry(String alias, byte[] key, Certificate[] chain) throws KeyStoreException;

    /** @see KeyStore#setCertificateEntry(String, Certificate) */
    void setCertificateEntry(String alias, Certificate cert) throws KeyStoreException;

    /** @see KeyStore#deleteEntry(String) */
    void deleteEntry(String alias) throws KeyStoreException;

    /** @see KeyStore#aliases() */
    Enumeration<String> aliases();

    /** @see KeyStore#containsAlias(String) */
    boolean containsAlias(String alias);

    /** @see KeyStore#size() */
    int size();

    /** @see KeyStore#isKeyEntry(String) */
    boolean isKeyEntry(String alias);

    /** @see KeyStore#isCertificateEntry(String) */
    boolean isCertificateEntry(String alias);

    /** @see KeyStore#getCertificateAlias(Certificate) */
    String getCertificateAlias(Certificate cert);

    /** @see KeyStore#store(OutputStream, char[]) */
    void store() throws CertificateException;

    /** @see KeyStore#getEntry(String, KeyStore.ProtectionParameter) */
    KeyStore.Entry getEntry(String alias, KeyStore.ProtectionParameter protParam) throws UnrecoverableEntryException;

    /** @see KeyStore#setEntry(String, KeyStore.Entry, KeyStore.ProtectionParameter) */
    void setEntry(String alias, KeyStore.Entry entry, KeyStore.ProtectionParameter protParam) throws KeyStoreException;

    /** @see KeyStore#entryInstanceOf(String, Class) */
    boolean entryInstanceOf(String alias, Class<? extends KeyStore.Entry> entryClass);
}
