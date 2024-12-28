package co.crystaldev.client.font;

import co.crystaldev.client.Reference;

import java.awt.*;
import java.io.InputStream;
import java.lang.reflect.Field;

public class Fonts {
  public static final FontRenderer JETBRAINS_20 = new FontRenderer();
  
  public static final FontRenderer JETBRAINS_16 = new FontRenderer();
  
  public static final FontRenderer JETBRAINS_12 = new FontRenderer();
  
  public static final FontRenderer SOURCE_SANS_36 = new FontRenderer();
  
  public static final FontRenderer SOURCE_SANS_28 = new FontRenderer();
  
  public static final FontRenderer SOURCE_SANS_20 = new FontRenderer();
  
  public static final FontRenderer SOURCE_SANS_18 = new FontRenderer();
  
  public static final FontRenderer SOURCE_SANS_16 = new FontRenderer();
  
  public static final FontRenderer SOURCE_SANS_12 = new FontRenderer();
  
  public static final FontRenderer NUNITO_REGULAR_28 = new FontRenderer();
  
  public static final FontRenderer NUNITO_REGULAR_24 = new FontRenderer();
  
  public static final FontRenderer NUNITO_REGULAR_20 = new FontRenderer();
  
  public static final FontRenderer NUNITO_REGULAR_16 = new FontRenderer();
  
  public static final FontRenderer NUNITO_REGULAR_12 = new FontRenderer();
  
  public static final FontRenderer NUNITO_SEMI_BOLD_36 = new FontRenderer();
  
  public static final FontRenderer NUNITO_SEMI_BOLD_28 = new FontRenderer();
  
  public static final FontRenderer NUNITO_SEMI_BOLD_24 = new FontRenderer();
  
  public static final FontRenderer NUNITO_SEMI_BOLD_20 = new FontRenderer();
  
  public static final FontRenderer NUNITO_SEMI_BOLD_18 = new FontRenderer();
  
  public static final FontRenderer NUNITO_SEMI_BOLD_16 = new FontRenderer();
  
  public static final FontRenderer NUNITO_SEMI_BOLD_12 = new FontRenderer();
  
  public static final FontRenderer PT_SANS_REGULAR_12 = new FontRenderer();
  
  public static final FontRenderer PT_SANS_REGULAR_16 = new FontRenderer();
  
  public static final FontRenderer PT_SANS_REGULAR_18 = new FontRenderer();
  
  public static final FontRenderer PT_SANS_REGULAR_20 = new FontRenderer();
  
  public static final FontRenderer PT_SANS_REGULAR_24 = new FontRenderer();
  
  public static final FontRenderer PT_SANS_BOLD_12 = new FontRenderer();
  
  public static final FontRenderer PT_SANS_BOLD_16 = new FontRenderer();
  
  public static final FontRenderer PT_SANS_BOLD_18 = new FontRenderer();
  
  public static final FontRenderer PT_SANS_BOLD_20 = new FontRenderer();
  
  public static final FontRenderer PT_SANS_BOLD_24 = new FontRenderer();
  
  public static final FontRenderer UPHEAVAL_12 = new FontRenderer();
  
  public static final FontRenderer UPHEAVAL_16 = new FontRenderer();
  
  public static final FontRenderer UPHEAVAL_18 = new FontRenderer();
  
  public static final FontRenderer UPHEAVAL_20 = new FontRenderer();
  
  public static void initialize() {
    for (Field field : Fonts.class.getDeclaredFields()) {
      try {
        Object obj = field.get(null);
        if (obj instanceof FontRenderer) {
          int i = ((FontRenderer)obj).getStringWidth("");
        }
      } catch (IllegalAccessException illegalAccessException) {}
    } 
  }
  
  static {
    try {
      String resourcePrefix = String.format("assets/%s/font/", "crystalclient");
      InputStream stream;
      Font font = Font.createFont(0, stream = Reference.class.getClassLoader().getResourceAsStream(resourcePrefix + "JetBrainsMono-Regular.ttf"));
      JETBRAINS_20.setFont(font.deriveFont(20.0F), true);
      JETBRAINS_16.setFont(font.deriveFont(16.0F), true);
      JETBRAINS_12.setFont(font.deriveFont(12.0F), true);
      stream.close();
      font = Font.createFont(0, stream = Reference.class.getClassLoader().getResourceAsStream(resourcePrefix + "SourceSansPro-Regular.ttf"));
      SOURCE_SANS_36.setFont(font.deriveFont(36.0F), true);
      SOURCE_SANS_28.setFont(font.deriveFont(28.0F), true);
      SOURCE_SANS_20.setFont(font.deriveFont(20.0F), true);
      SOURCE_SANS_18.setFont(font.deriveFont(18.0F), true);
      SOURCE_SANS_16.setFont(font.deriveFont(16.0F), true);
      SOURCE_SANS_12.setFont(font.deriveFont(12.0F), true);
      stream.close();
      font = Font.createFont(0, stream = Reference.class.getClassLoader().getResourceAsStream(resourcePrefix + "SourceSansPro-Regular.ttf"));
      SOURCE_SANS_20.setFont(font.deriveFont(20.0F), true);
      SOURCE_SANS_16.setFont(font.deriveFont(16.0F), true);
      SOURCE_SANS_12.setFont(font.deriveFont(12.0F), true);
      stream.close();
      font = Font.createFont(0, stream = Reference.class.getClassLoader().getResourceAsStream(resourcePrefix + "Nunito-Regular.ttf"));
      NUNITO_REGULAR_28.setFont(font.deriveFont(28.0F), true);
      NUNITO_REGULAR_24.setFont(font.deriveFont(24.0F), true);
      NUNITO_REGULAR_20.setFont(font.deriveFont(20.0F), true);
      NUNITO_REGULAR_16.setFont(font.deriveFont(16.0F), true);
      NUNITO_REGULAR_12.setFont(font.deriveFont(12.0F), true);
      stream.close();
      font = Font.createFont(0, stream = Reference.class.getClassLoader().getResourceAsStream(resourcePrefix + "Nunito-SemiBold.ttf"));
      NUNITO_SEMI_BOLD_36.setFont(font.deriveFont(36.0F), true);
      NUNITO_SEMI_BOLD_28.setFont(font.deriveFont(28.0F), true);
      NUNITO_SEMI_BOLD_24.setFont(font.deriveFont(24.0F), true);
      NUNITO_SEMI_BOLD_20.setFont(font.deriveFont(20.0F), true);
      NUNITO_SEMI_BOLD_18.setFont(font.deriveFont(18.0F), true);
      NUNITO_SEMI_BOLD_16.setFont(font.deriveFont(16.0F), true);
      NUNITO_SEMI_BOLD_12.setFont(font.deriveFont(12.0F), true);
      stream.close();
      font = Font.createFont(0, stream = Reference.class.getClassLoader().getResourceAsStream(resourcePrefix + "PTSans-Regular.ttf"));
      PT_SANS_REGULAR_24.setFont(font.deriveFont(24.0F), true);
      PT_SANS_REGULAR_20.setFont(font.deriveFont(20.0F), true);
      PT_SANS_REGULAR_18.setFont(font.deriveFont(18.0F), true);
      PT_SANS_REGULAR_16.setFont(font.deriveFont(16.0F), true);
      PT_SANS_REGULAR_12.setFont(font.deriveFont(12.0F), true);
      stream.close();
      font = Font.createFont(0, stream = Reference.class.getClassLoader().getResourceAsStream(resourcePrefix + "PTSans-Bold.ttf"));
      PT_SANS_BOLD_24.setFont(font.deriveFont(24.0F), true);
      PT_SANS_BOLD_20.setFont(font.deriveFont(20.0F), true);
      PT_SANS_BOLD_18.setFont(font.deriveFont(18.0F), true);
      PT_SANS_BOLD_16.setFont(font.deriveFont(16.0F), true);
      PT_SANS_BOLD_12.setFont(font.deriveFont(12.0F), true);
      stream.close();
      font = Font.createFont(0, stream = Reference.class.getClassLoader().getResourceAsStream(resourcePrefix + "upheavtt.ttf"));
      UPHEAVAL_20.setFont(font.deriveFont(20.0F), true);
      UPHEAVAL_18.setFont(font.deriveFont(18.0F), true);
      UPHEAVAL_16.setFont(font.deriveFont(16.0F), true);
      UPHEAVAL_12.setFont(font.deriveFont(12.0F), true);
      stream.close();
    } catch (FontFormatException|java.io.IOException ex) {
      Reference.LOGGER.error("An exception was raised while registering TrueType fonts.", ex);
    } 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\font\Fonts.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */