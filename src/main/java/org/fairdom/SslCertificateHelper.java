package org.fairdom;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import ch.systemsx.cisd.base.exceptions.CheckedExceptionTunnel;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Helper class for retrieving and locally storing SSL certificates from a server.
 * 
 * @author Chandrasekhar Ramakrishnan, Tomasz Zielinski
 */
public class SslCertificateHelper
{

    static final ConcurrentHashMap<String,Boolean> trusted = new ConcurrentHashMap<>();

    static Certificate[] getServerCertificate(String addr)
    {
        workAroundABugInJava6();

        // Create a trust manager that does not validate certificate chains
        setUpAllAcceptingTrustManager();
        setUpAllAcceptingHostNameVerifier();
        try
        {
            URL url = new URL(addr);
            int port = url.getPort();
            if (port == -1)
            {
                port = 443; // standard port for https
            }
            String hostname = url.getHost();
            SSLSocketFactory factory = HttpsURLConnection.getDefaultSSLSocketFactory();
            
            try (SSLSocket socket = (SSLSocket) factory.createSocket(hostname, port)) {
                socket.startHandshake();
                return socket.getSession().getPeerCertificates();
            }
        } catch (Exception e)
        {
            throw CheckedExceptionTunnel.wrapIfNecessary(e);
        } 
    }

    static void setUpAllAcceptingTrustManager()
    {
        TrustManager[] trustAllCerts = new TrustManager[]
        { new X509TrustManager()
            {
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers()
                {
                    return null;
                }

                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] certs,
                        String authType)
                {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] certs,
                        String authType)
                {
                }
            } };

        // Install the all-trusting trust manager
        try
        {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e)
        {
            throw CheckedExceptionTunnel.wrapIfNecessary(e);
        }
    }

    static void setUpAllAcceptingHostNameVerifier()
    {
        HostnameVerifier acceptAllHostNames = new HostnameVerifier()
            {
                @Override
                public boolean verify(String hostname, SSLSession session)
                {
                    return true;
                }
            };
        HttpsURLConnection.setDefaultHostnameVerifier(acceptAllHostNames);
    }

    // WORKAROUND: see comment submitted on 31-JAN-2008 for
    // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6514454
    static void workAroundABugInJava6()
    {
        try
        {
            SSLContext.getInstance("SSL").createSSLEngine();
        } catch (Exception ex)
        {
            // Ignore this one.
            //throw CheckedExceptionTunnel.wrapIfNecessary(ex);
        }
    }

    public static void addTrustedUrl(String url) {
        addTrustedUrl(url, false);
    }
    
    public static void addTrustedUrl(String url, boolean renew)
    {
        if (!url.startsWith("https://")) throw new IllegalArgumentException("URL must starts with https:// not: "+url);;
        
        if (trusted.containsKey(url) && !renew) return;
        
        try
        {
            Certificate[] certificates = getServerCertificate(url);

            KeyStore keyStore = loadKeyStore();

            addCertificates(keyStore,url,certificates);

            saveKeyStore(keyStore);

            trusted.put(url, true);
        
            
        } catch (Exception ex)
        {
            throw CheckedExceptionTunnel.wrapIfNecessary(ex);
        }
    }

    static KeyStore loadKeyStore() throws IOException, GeneralSecurityException {
        
        String location = System.getProperty("javax.net.ssl.trustStore");
        if (location == null) {
            location = Files.createTempFile(null, null).toAbsolutePath().toString();
            System.setProperty("javax.net.ssl.trustStore",location);
        }
        
        Path path = Paths.get(location);
        KeyStore keyStore = KeyStore.getInstance("JKS");
        
        if (Files.size(path) > 0) {
            try (InputStream input = Files.newInputStream(path)) {
                keyStore.load(input, "changeit".toCharArray());
            }
        } else {
            keyStore.load(null, null);
        }        

        return keyStore;
    } 
    
    static void saveKeyStore(KeyStore keyStore) throws IOException, GeneralSecurityException {
        
        String location = System.getProperty("javax.net.ssl.trustStore");
        if (location == null) throw new IllegalStateException("Unexpected empty location at: System::javax.net.ssl.trustStore");
        
        Path path = Paths.get(location);
        try (OutputStream out = Files.newOutputStream(path)) {
            keyStore.store(out, "changeit".toCharArray());
        }
    }
    
    
    static void addCertificates(KeyStore keyStore, String url, Certificate[] certificates) throws KeyStoreException {
        
        for (int i = 0; i < certificates.length; i++)
        {
            keyStore.setCertificateEntry(url + i, certificates[i]);
        }
    }
    
    
    
}