package co.crystaldev.client.util;

import co.crystaldev.client.Reference;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MultipartUploader {
  private static final String LINE_FEED = "\r\n";
  
  private final String boundary;
  
  private final HttpURLConnection conn;
  
  private final OutputStream out;
  
  private final PrintWriter pw;
  
  public MultipartUploader(String destination, boolean useRealisticUserAgent) throws IOException {
    this.boundary = useRealisticUserAgent ? "----WebKitFormBoundarya35bnVbenwHuz4yU" : ("===" + Long.toHexString(System.currentTimeMillis()) + "===");
    URL url = new URL(destination);
    this.conn = (HttpURLConnection)url.openConnection();
    this.conn.setDoOutput(true);
    this.conn.setDoInput(true);
    this.conn.setRequestMethod("POST");
    this.conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + this.boundary);
    this.conn.setInstanceFollowRedirects(false);
    if (useRealisticUserAgent) {
      this.conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
    } else {
      this.conn.setRequestProperty("User-Agent", "Minecraft/Crystal Client v1.1.16-projectassfucker");
    } 
    this.conn.setConnectTimeout(5000);
    this.conn.setReadTimeout(5000);
    this.pw = new PrintWriter(new OutputStreamWriter(this.out = this.conn.getOutputStream(), StandardCharsets.UTF_8), true);
  }
  
  public MultipartUploader(String destination) throws IOException {
    this(destination, false);
  }
  
  public void addField(String name, String value) {
    this.pw.append("--").append(this.boundary)
      .append("\r\n");
    this.pw.append("Content-Disposition: form-data; name=\"").append(name).append("\"")
      .append("\r\n");
    this.pw.append("Content-Type: text/plain; charset=").append(String.valueOf(StandardCharsets.UTF_8))
      .append("\r\n");
    this.pw.append("\r\n");
    this.pw.append(value)
      .append("\r\n");
    this.pw.flush();
  }
  
  public void addPart(String name, File file) throws IOException {
    this.pw.append("--").append(this.boundary)
      .append("\r\n");
    this.pw.append("Content-Disposition: form-data; name=\"").append(name).append("\"; filename=\"").append(file.getName()).append("\"")
      .append("\r\n");
    this.pw.append("Content-Type: application/octet-stream")
      .append("\r\n");
    this.pw.append("Content-Transfer-Encoding: binary")
      .append("\r\n");
    this.pw.append("\r\n")
      .flush();
    FileInputStream is = new FileInputStream(file);
    byte[] buf = new byte[4096];
    int read = -1;
    while ((read = is.read(buf)) != -1)
      this.out.write(buf, 0, read); 
    this.out.flush();
    is.close();
    this.pw.append("\r\n")
      .flush();
  }
  
  public void addHeader(String name, String value) {
    this.pw.append(name).append(": ").append(value)
      .append("\r\n")
      .flush();
  }
  
  public List<String> finish() throws IOException {
    List<String> response = new ArrayList<>();
    this.pw.append("\r\n").flush();
    this.pw.append("--").append(this.boundary).append("--")
      .append("\r\n");
    this.pw.close();
    int status = this.conn.getResponseCode();
    if (status > 299 && status != 302) {
      if (this.conn.getErrorStream() != null)
        Reference.LOGGER.error(IOUtils.toString(this.conn.getErrorStream())); 
      throw new IOException("Server returned unexpected status code " + status);
    } 
    BufferedReader reader = new BufferedReader(new InputStreamReader(this.conn.getInputStream()));
    String line;
    while ((line = reader.readLine()) != null)
      response.add(line); 
    reader.close();
    this.conn.disconnect();
    return response;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\MultipartUploader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */