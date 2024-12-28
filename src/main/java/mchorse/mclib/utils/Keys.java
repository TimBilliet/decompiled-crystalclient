package mchorse.mclib.utils;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public class Keys {
  public static final String[] KEYS = new String[256];
  
  public static String getKeyName(int key) {
    if (key < 0 || key >= 256)
      return null; 
    if (KEYS[key] == null)
      KEYS[key] = getKey(key); 
    return KEYS[key];
  }
  
  private static String getKey(int key) {
    switch (key) {
      case 12:
        return "-";
      case 13:
        return "=";
      case 26:
        return "[";
      case 27:
        return "]";
      case 39:
        return ";";
      case 40:
        return "'";
      case 43:
        return "\\";
      case 51:
        return ",";
      case 52:
        return ".";
      case 53:
        return "/";
      case 41:
        return "`";
      case 15:
        return "Tab";
      case 58:
        return "Caps Lock";
      case 42:
        return "L. Shift";
      case 29:
        return "L. Ctrl";
      case 56:
        return "L. Alt";
      case 219:
        return Minecraft.isRunningOnMac ? "L. Cmd" : "L. Win";
      case 54:
        return "R. Shift";
      case 157:
        return "R. Ctrl";
      case 184:
        return "R. Alt";
      case 220:
        return Minecraft.isRunningOnMac ? "R. Cmd" : "R. Win";
      case 181:
        return "Numpad /";
      case 55:
        return "Numpad *";
      case 74:
        return "Numpad -";
      case 78:
        return "Numpad +";
      case 83:
        return "Numpad .";
    } 
    String name = Keyboard.getKeyName(key);
    if (name.length() > 1)
      name = name.charAt(0) + name.substring(1).toLowerCase(); 
    if (name.startsWith("Numpad"))
      name = name.replace("Numpad", "Numpad "); 
    return name;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\Keys.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */