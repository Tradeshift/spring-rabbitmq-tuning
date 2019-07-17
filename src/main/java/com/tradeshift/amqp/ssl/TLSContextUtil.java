package com.tradeshift.amqp.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Objects;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public final class TLSContextUtil {
    private TLSContextUtil() {
    }

    public static SSLContext tls12ContextFromPKCS12(InputStream pkcs12, char[] password) {
        try {
            Objects.requireNonNull(pkcs12);
            Objects.requireNonNull(password);
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(pkcs12, password);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, password);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), SecureRandom.getInstance("SHA1PRNG"));
            return sslContext;
        } catch (CertificateException | IOException | NoSuchAlgorithmException | KeyManagementException | UnrecoverableKeyException | KeyStoreException var6) {
            throw new RuntimeException(var6);
        }
    }
}
