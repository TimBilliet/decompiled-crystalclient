package co.crystaldev.client.util;

import co.crystaldev.client.Reference;
import com.google.gson.JsonObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Hastebin {
  private static final String DEFAULT_URL = "https://paste.crystaldev.co/";
  
  public static String upload(String text) throws IOException {
    return upload(text, "https://paste.crystaldev.co/");
  }
  
  public static String upload(String text, String url) throws IOException {
    byte[] postData = text.getBytes(StandardCharsets.UTF_8);
    int postDataLength = postData.length;
    HttpsURLConnection conn = (HttpsURLConnection)(new URL(url + "documents")).openConnection();
    conn.setDoOutput(true);
    conn.setInstanceFollowRedirects(false);
    conn.setRequestMethod("POST");
    conn.setRequestProperty("User-Agent", "Minecraft/Crystal Client-v1.1.16-projectassfucker");
    conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
    conn.setUseCaches(false);
    DataOutputStream stream = new DataOutputStream(conn.getOutputStream());
    stream.write(postData);
    stream.close();
    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    StringBuilder content = new StringBuilder();
    String str;
    while ((str = in.readLine()) != null)
      content.append(str); 
    in.close();
    conn.disconnect();
    try {
      JsonObject obj = (JsonObject)Reference.GSON.fromJson(content.toString(), JsonObject.class);
      String key = obj.get("key").getAsString();
      return url + key;
    } catch (Exception ex) {
      return content.toString();
    } 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\Hastebin.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */