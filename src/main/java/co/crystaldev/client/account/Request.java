package co.crystaldev.client.account;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;
import java.util.Map.Entry;

public class Request {
    public HttpURLConnection conn;

    public Request(String url) throws IOException {
        this.conn = (HttpURLConnection) (new URL(url)).openConnection();
        this.conn.setDoOutput(true);
        this.conn.setDoInput(true);
        this.conn.setConnectTimeout(6000);
        this.conn.setReadTimeout(6000);
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
