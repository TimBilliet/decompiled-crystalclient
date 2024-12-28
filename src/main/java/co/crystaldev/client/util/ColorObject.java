package co.crystaldev.client.util;

import co.crystaldev.client.Reference;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Field;

public class ColorObject extends Color implements Cloneable {
  public static ColorObject TRANSPARENT = new ColorObject(0, 0, 0, 0);
  
  private boolean chroma;
  
  private boolean bold;
  
  private boolean underline;
  
  private boolean italic;
  
  private Field valueField;
  
  public boolean isChroma() {
    return this.chroma;
  }
  
  public boolean isBold() {
    return this.bold;
  }
  
  public boolean isUnderline() {
    return this.underline;
  }
  
  public boolean isItalic() {
    return this.italic;
  }
  
  public ColorObject(int r, int g, int b, int a) {
    this(r, g, b, a, false);
  }
  
  public ColorObject(int r, int g, int b, int a, boolean chroma) {
    super(r, g, b, a);
    this.chroma = chroma;
    this.bold = false;
    this.underline = false;
    this.italic = false;
    try {
      this.valueField = getClass().getSuperclass().getDeclaredField("value");
      this.valueField.setAccessible(true);
    } catch (NoSuchFieldException ex) {
      Reference.LOGGER.error("Color reflection failed", ex);
    } 
  }
  
  public ColorObject(float r, float g, float b, float a, boolean chroma) {
    super(r, g, b, a);
    this.chroma = chroma;
    this.bold = false;
    this.underline = false;
    this.italic = false;
    try {
      this.valueField = getClass().getSuperclass().getDeclaredField("value");
      this.valueField.setAccessible(true);
    } catch (NoSuchFieldException ex) {
      Reference.LOGGER.error("Colour reflection failed", ex);
    } 
  }
  
  public ColorObject(int r, int g, int b, int a, boolean chroma, boolean bold, boolean underline, boolean italic) {
    super(r, g, b, a);
    this.chroma = chroma;
    this.bold = bold;
    this.underline = underline;
    this.italic = italic;
    try {
      this.valueField = getClass().getSuperclass().getDeclaredField("value");
      this.valueField.setAccessible(true);
    } catch (NoSuchFieldException ex) {
      Reference.LOGGER.error("Colour reflection failed", ex);
    } 
  }
  
  public ColorObject(float r, float g, float b, float a, boolean chroma, boolean bold, boolean underline, boolean italic) {
    super(r, g, b, a);
    this.chroma = chroma;
    this.bold = bold;
    this.underline = underline;
    this.italic = italic;
    try {
      this.valueField = getClass().getSuperclass().getDeclaredField("value");
      this.valueField.setAccessible(true);
    } catch (NoSuchFieldException ex) {
      Reference.LOGGER.error("Colour reflection failed", ex);
    } 
  }
  
  public ColorObject setRGB(int rgb) {
    try {
      if (this.valueField != null)
        this.valueField.setInt(this, rgb); 
    } catch (IllegalAccessException illegalAccessException) {}
    return this;
  }
  
  public ColorObject setAlpha(int alpha) {
    setRGB(getRGB() & 0xFFFFFF | alpha << 24);
    return this;
  }
  
  public ColorObject setChroma(boolean chroma) {
    this.chroma = chroma;
    return this;
  }
  
  public ColorObject setBold(boolean bold) {
    this.bold = bold;
    return this;
  }
  
  public ColorObject setUnderline(boolean underline) {
    this.underline = underline;
    return this;
  }
  
  public ColorObject setItalic(boolean italic) {
    this.italic = italic;
    return this;
  }
  
  public ColorObject clone() {
    try {
      return (ColorObject)super.clone();
    } catch (CloneNotSupportedException ex) {
      Reference.LOGGER.error("Unable to clone color", ex);
      return null;
    } 
  }
  
  public static ColorObject fromColor(Color color, boolean chroma) {
    return new ColorObject(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha(), chroma);
  }
  
  public static ColorObject fromColor(Color color) {
    return new ColorObject(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
  }
  
  public static ColorObject fromColor(int color, boolean chroma) {
    return new ColorObject(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >> 24 & 0xFF, chroma);
  }
  
  public static ColorObject fromColor(int color) {
    return new ColorObject(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >> 24 & 0xFF);
  }
  
  public static class Adapter extends TypeAdapter<ColorObject> {
    public void write(JsonWriter out, ColorObject value) throws IOException {
      out.beginObject()
        .name("r").value(value.getRed())
        .name("g").value(value.getGreen())
        .name("b").value(value.getBlue())
        .name("a").value(value.getAlpha())
        .name("chroma").value(value.isChroma())
        .name("bold").value(value.isBold())
        .name("underline").value(value.isUnderline())
        .name("italic").value(value.isItalic())
        .endObject();
    }
    
    public ColorObject read(JsonReader in) throws IOException {
      in.beginObject();
      int r = 0, g = 0, b = 0, a = 255;
      boolean chroma = false, bold = false, underline = false, italic = false;
      while (in.hasNext()) {
        JsonToken token = in.peek();
        String field = "";
        if (token.equals(JsonToken.NAME))
          field = in.nextName();
        //BREAKS TOEGEVOEGD
        switch (field) {
          case "r":
            r = in.nextInt();break;
          case "g":
            g = in.nextInt();break;
          case "b":
            b = in.nextInt();break;
          case "a":
            a = in.nextInt();break;
          case "chroma":
            chroma = in.nextBoolean();break;
          case "bold":
            bold = in.nextBoolean();break;
          case "underline":
            underline = in.nextBoolean();break;
          case "italic":
            italic = in.nextBoolean();break;
        } 
      } 
      in.endObject();
      return new ColorObject(r, g, b, a, chroma, bold, underline, italic);
    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\ColorObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */