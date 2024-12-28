package co.crystaldev.client.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class BrokenHash {
  public static String hash(String str) {
    try {
      byte[] digest = digest(str);
      return (new BigInteger(digest)).toString(16);
    } catch (NoSuchAlgorithmException ex) {
      throw new RuntimeException(ex);
    } 
  }
  
  private static byte[] digest(String str) throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance("SHA-1");
    byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);
    return md.digest(strBytes);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\BrokenHash.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */