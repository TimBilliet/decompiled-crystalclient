package com.github.lunatrius.core.util;

import com.github.lunatrius.core.reference.Reference;

import java.io.File;
import java.io.IOException;

public class FileUtils {
  public static String humanReadableByteCount(long bytes) {
    int unit = 1024;
    if (bytes < 1024L)
      return bytes + " B"; 
    int exp = (int)(Math.log(bytes) / Math.log(1024.0D));
    String pre = "KMGTPE".charAt(exp - 1) + "i";
    return String.format("%3.0f %sB", new Object[] { Double.valueOf(bytes / Math.pow(1024.0D, exp)), pre });
  }
  
  public static boolean contains(File root, String filename) {
    return contains(root, new File(root, filename));
  }
  
  public static boolean contains(File root, File file) {
    try {
      return file.getCanonicalPath().startsWith(root.getCanonicalPath() + File.separator);
    } catch (IOException e) {
      Reference.logger.error("", e);
      return false;
    } 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\cor\\util\FileUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */