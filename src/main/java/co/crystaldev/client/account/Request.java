package co.crystaldev.client.account;


import co.crystaldev.client.Reference;
import co.crystaldev.client.gui.screens.ScreenLogin;

import javax.net.ssl.*;
import java.io.*;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Request {
    public HttpsURLConnection conn;
    public static final SSLContext FIXED_CONTEXT;
    static {
        SSLContext ctx = null;
        try {
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            try (InputStream in = MicrosoftAuthManager.class.getResourceAsStream("/iasjavafix.jks")) {
                ks.load(in, "iasjavafix".toCharArray());
            }
            TrustManagerFactory customTmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            customTmf.init(ks);
            TrustManagerFactory defaultTmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            defaultTmf.init((KeyStore) null);
            final List<X509TrustManager> managers = new ArrayList<>();
            managers.addAll(Arrays.stream(customTmf.getTrustManagers()).filter(tm -> tm instanceof X509TrustManager)
                    .map(tm -> (X509TrustManager) tm).collect(Collectors.toList()));
            managers.addAll(Arrays.stream(defaultTmf.getTrustManagers()).filter(tm -> tm instanceof X509TrustManager)
                    .map(tm -> (X509TrustManager) tm).collect(Collectors.toList()));
            TrustManager multiManager = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    CertificateException wrapper = new CertificateException("Unable to validate via any trust manager.");
                    for (X509TrustManager manager : managers) {
                        try {
                            manager.checkClientTrusted(chain, authType);
                            return;
                        } catch (Throwable t) {
                            wrapper.addSuppressed(t);
                        }
                    }
                    throw wrapper;
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    CertificateException wrapper = new CertificateException("Unable to validate via any trust manager.");
                    for (X509TrustManager manager : managers) {
                        try {
                            manager.checkServerTrusted(chain, authType);
                            return;
                        } catch (Throwable t) {
                            wrapper.addSuppressed(t);
                        }
                    }
                    throw wrapper;
                }

                public X509Certificate[] getAcceptedIssuers() {
                    List<X509Certificate> certificates = new ArrayList<>();
                    for (X509TrustManager manager : managers)
                        certificates.addAll(Arrays.asList(manager.getAcceptedIssuers()));
                    return certificates.toArray(new X509Certificate[0]);
                }
            };
            ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{multiManager}, new SecureRandom());
            Reference.LOGGER.info("Using shared SSL context. (behavior: default; custom + default certificates)");
        } catch (Throwable t) {
            Reference.LOGGER.error("Unable to init SSL context.", t);
            ScreenLogin.feedback = t.toString();
        }
        FIXED_CONTEXT = ctx;
    }

    public Request(String url, boolean ssl) throws IOException {
        this.conn = (HttpsURLConnection) (new URL(url)).openConnection();
        if (ssl && FIXED_CONTEXT != null) {
            this.conn.setSSLSocketFactory(FIXED_CONTEXT.getSocketFactory());
        }
        this.conn.setDoOutput(true);
        this.conn.setDoInput(true);
        this.conn.setConnectTimeout(8000);
        this.conn.setReadTimeout(8000);
    }

    public void header(String key, String value) {
        this.conn.setRequestProperty(key, value);
    }

    public void post(String s) throws IOException {
        this.conn.setRequestMethod("POST");
        byte[] out = s.getBytes(StandardCharsets.UTF_8);
        OutputStream os = this.conn.getOutputStream();
        Throwable var4 = null;

        try {
            os.write(out);
        } catch (Throwable var13) {
            var4 = var13;
            throw var13;
        } finally {
            if (os != null) {
                if (var4 != null) {
                    try {
                        os.close();
                    } catch (Throwable var12) {
                        var4.addSuppressed(var12);
                    }
                } else {
                    os.close();
                }
            }
        }
    }

    public void post(Map<Object, Object> map) throws IOException {
        StringJoiner sj = new StringJoiner("&");

        for (Entry<Object, Object> objectObjectEntry : map.entrySet()) {
            sj.add(URLEncoder.encode(objectObjectEntry.getKey().toString(), "UTF-8") + "=" + URLEncoder.encode(objectObjectEntry.getValue().toString(), "UTF-8"));
        }
        this.post(sj.toString());
    }

    public void get() throws ProtocolException {
        this.conn.setRequestMethod("GET");
    }

    public int response() throws IOException {
        return this.conn.getResponseCode();
    }

    public String body() throws IOException {
        StringBuilder sb = new StringBuilder();
        Reader r = new InputStreamReader(this.conn.getInputStream(), StandardCharsets.UTF_8);
        Throwable var3 = null;

        try {
            int i;
            try {
                while ((i = r.read()) >= 0) {
                    sb.append((char) i);
                }
            } catch (Throwable var12) {
                var3 = var12;
                throw var12;
            }
        } finally {
            if (var3 != null) {
                try {
                    r.close();
                } catch (Throwable var11) {
                    var3.addSuppressed(var11);
                }
            } else {
                r.close();
            }
        }

        return sb.toString();
    }

    public String error() throws IOException {
        StringBuilder sb = new StringBuilder();
        Reader r = new InputStreamReader(this.conn.getErrorStream(), StandardCharsets.UTF_8);
        Throwable var3 = null;

        try {
            int i;
            try {
                while ((i = r.read()) >= 0) {
                    sb.append((char) i);
                }
            } catch (Throwable var12) {
                var3 = var12;
                throw var12;
            }
        } finally {
            if (var3 != null) {
                try {
                    r.close();
                } catch (Throwable var11) {
                    var3.addSuppressed(var11);
                }
            } else {
                r.close();
            }
        }
        return sb.toString();
    }
}
