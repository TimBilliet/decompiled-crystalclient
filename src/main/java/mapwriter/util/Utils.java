package mapwriter.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.chunk.Chunk;

import java.io.File;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Utils {
  public static int[] integerListToIntArray(List<Integer> list) {
    int size = list.size();
    int[] array = new int[size];
    for (int i = 0; i < size; i++)
      array[i] = (Integer) list.get(i);
    return array;
  }
  
  public static String mungeString(String s) {
    s = s.replace('.', '_');
    s = s.replace('-', '_');
    s = s.replace(' ', '_');
    s = s.replace('/', '_');
    s = s.replace('\\', '_');
    return Reference.patternInvalidChars.matcher(s).replaceAll("");
  }
  
  public static String mungeStringForConfig(String s) {
    return Reference.patternInvalidChars2.matcher(s).replaceAll("");
  }
  
  public static File getFreeFilename(File dir, String baseName, String ext) {
    File outputFile;
    int i = 0;
    if (dir != null) {
      outputFile = new File(dir, baseName + "." + ext);
    } else {
      outputFile = new File(baseName + "." + ext);
    } 
    while (outputFile.exists() && i < 1000) {
      if (dir != null) {
        outputFile = new File(dir, baseName + "." + i + "." + ext);
      } else {
        outputFile = new File(baseName + "." + i + "." + ext);
      } 
      i++;
    } 
    return (i < 1000) ? outputFile : null;
  }
  
  public static void printBoth(String msg) {
    EntityPlayerSP thePlayer = (Minecraft.getMinecraft()).thePlayer;
    if (thePlayer != null)
      thePlayer.addChatMessage((IChatComponent)new ChatComponentText(msg)); 
    Logging.log("%s", msg);
  }
  
  public static File getDimensionDir(File worldDir, int dimension) {
    File dimDir;
    if (dimension != 0) {
      dimDir = new File(worldDir, "DIM" + dimension);
    } else {
      dimDir = worldDir;
    } 
    return dimDir;
  }
  
  public static IntBuffer allocateDirectIntBuffer(int size) {
    return ByteBuffer.allocateDirect(size * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
  }
  
  public static int nextHighestPowerOf2(int v) {
    v--;
    v |= v >> 1;
    v |= v >> 2;
    v |= v >> 4;
    v |= v >> 8;
    v |= v >> 16;
    return v + 1;
  }
  
  public static String getCurrentDateString() {
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmm");
    return dateFormat.format(new Date());
  }
  
  public static int distToChunkSq(int x, int z, Chunk chunk) {
    int dx = (chunk.xPosition << 4) + 8 - x;
    int dz = (chunk.zPosition << 4) + 8 - z;
    return dx * dx + dz * dz;
  }
  
  public static String getWorldName() {
    String worldName;//added
    if (Minecraft.getMinecraft().isIntegratedServerRunning()) {
      IntegratedServer server = Minecraft.getMinecraft().getIntegratedServer();
      worldName = (server != null) ? server.getFolderName() : "sp_world";
    } else {
      worldName = (Minecraft.getMinecraft().getCurrentServerData()).serverIP;
      if (!worldName.contains(":")) {
        worldName = worldName + "_25565";
      } else {
        worldName = worldName.replace(":", "_");
      } 
    } 
    worldName = mungeString(worldName);
    if (worldName.equals(""))
      worldName = "default"; 
    return worldName;
  }
  
  public static void openWebLink(URI p_175282_1_) {
    try {
      Class<?> oclass = Class.forName("java.awt.Desktop");
      Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
      oclass.getMethod("browse", new Class[] { URI.class }).invoke(object, new Object[] { p_175282_1_ });
    } catch (Throwable throwable) {
      Logging.logError("Couldn't open link %s", new Object[] { Arrays.toString((Object[])throwable.getStackTrace()) });
    } 
  }
  
  public static String stringArrayToString(String[] arr) {
    StringBuilder builder = new StringBuilder();
    for (String s : arr) {
      builder.append(I18n.format(s, new Object[0]));
      builder.append("\n");
    } 
    return builder.toString();
  }
  
  public static int getMaxWidth(String[] arr, String[] arr2) {
    FontRenderer fontRendererObj = (Minecraft.getMinecraft()).fontRendererObj;
    int Width = 1;
    for (int i = 0; i < arr.length; i++) {
      int w2 = 0;
      String s = I18n.format(arr[i], new Object[0]);
      int w1 = fontRendererObj.getStringWidth(s);
      if (arr2 != null && i < arr2.length) {
        s = I18n.format(arr2[i], new Object[0]);
        w2 = fontRendererObj.getStringWidth(s);
        w2 += 65;
      } 
      int wTot = Math.max(w1, w2);
      Width = Math.max(Width, wTot);
    } 
    return Width;
  }
  
  private static final int[] colours = new int[] { 16711680, 65280, 255, 16776960, 16711935, 65535, 16744448, 8388863 };
  
  public static int colourIndex = 0;
  
  private static int getColoursLengt() {
    return colours.length;
  }
  
  public static int getCurrentColour() {
    return 0xFF000000 | colours[colourIndex];
  }
  
  public static int getNextColour() {
    colourIndex = (colourIndex + 1) % getColoursLengt();
    return getCurrentColour();
  }
  
  public static int getPrevColour() {
    colourIndex = (colourIndex + getColoursLengt() - 1) % getColoursLengt();
    return getCurrentColour();
  }
  
  public static <K, V> Map<K, V> checkedMapByCopy(Map rawMap, Class<K> keyType, Class<V> valueType, boolean strict) throws ClassCastException {
    Map<K, V> m2 = new HashMap<>(rawMap.size() * 4 / 3 + 1);
    for (Object o : rawMap.entrySet()) {
      Map.Entry e = (Map.Entry)o;
      try {
        m2.put(keyType.cast(e.getKey()), valueType.cast(e.getValue()));
      } catch (ClassCastException x) {
        if (strict)
          throw x; 
        System.out.println("not assignable");
      } 
    } 
    return m2;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwrite\\util\Utils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */