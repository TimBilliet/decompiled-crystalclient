package co.crystaldev.client.util.enums;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.regex.Pattern;

public enum ChatColor {
  BLACK('0'),
  DARK_BLUE('1'),
  DARK_GREEN('2'),
  DARK_AQUA('3'),
  DARK_RED('4'),
  DARK_PURPLE('5'),
  GOLD('6'),
  GRAY('7'),
  DARK_GRAY('8'),
  BLUE('9'),
  GREEN('a'),
  AQUA('b'),
  RED('c'),
  LIGHT_PURPLE('d'),
  YELLOW('e'),
  WHITE('f'),
  MAGIC('k', true),
  BOLD('l', true),
  STRIKETHROUGH('m', true),
  UNDERLINE('n', true),
  ITALIC('o', true),
  RESET('r');
  
  private static final Pattern STRIP_COLOR_PATTERN;
  
  private final char code;
  
  private final boolean isFormat;
  
  private static final Map<Character, ChatColor> BY_CHAR;
  
  static {
    STRIP_COLOR_PATTERN = Pattern.compile("(?i)\u00A7[0-9A-FK-OR]");
    BY_CHAR = Maps.newHashMap();
    for (ChatColor color : values())
      BY_CHAR.put(Character.valueOf(color.code), color); 
  }
  ChatColor(char code) {
    this.code = code;
    this.isFormat = false;
  }
  ChatColor(char code, boolean isFormat) {
    this.code = code;
    this.isFormat = isFormat;
  }

  public char getChar() {
    return this.code;
  }
  
  public boolean isFormat() {
    return this.isFormat;
  }
  
  public boolean isColor() {
    return (!this.isFormat && this != RESET);
  }
  
  public static ChatColor getByChar(char code) {
    return BY_CHAR.get(Character.valueOf(code));
  }
  
  public static ChatColor getByChar(String code) {
    return BY_CHAR.get(Character.valueOf(code.charAt(0)));
  }
  
  public static String stripColor(String input) {
    return (input == null) ? null : STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
  }
  
  public static String translate(String textToTranslate) {
    return translate('&', textToTranslate);
  }
  
  public static String translate(char altColorChar, String textToTranslate) {
    char[] b = textToTranslate.toCharArray();
    for (int i = 0; i < b.length - 1; i++) {
      if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
        b[i] = '\u00A7';
        b[i + 1] = Character.toLowerCase(b[i + 1]);
      } 
    } 
    return new String(b);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\enums\ChatColor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */