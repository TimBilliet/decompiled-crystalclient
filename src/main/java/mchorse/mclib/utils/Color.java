package mchorse.mclib.utils;

import org.apache.commons.lang3.StringUtils;

public class Color {
  public float r;
  
  public float g;
  
  public float b;
  
  public float a = 1.0F;
  
  public Color() {}
  
  public Color(float r, float g, float b) {
    this.r = r;
    this.g = g;
    this.b = b;
  }
  
  public Color(float r, float g, float b, float a) {
    this(r, g, b);
    this.a = a;
  }
  
  public Color set(float r, float g, float b, float a) {
    this.r = r;
    this.g = g;
    this.b = b;
    this.a = a;
    return this;
  }
  
  public Color set(float value, int component) {
    switch (component) {
      case 1:
        this.r = value;
        return this;
      case 2:
        this.g = value;
        return this;
      case 3:
        this.b = value;
        return this;
    } 
    this.a = value;
    return this;
  }
  
  public Color set(int color) {
    return set(color, true);
  }
  
  public Color set(int color, boolean alpha) {
    set((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, alpha ? ((color >> 24 & 0xFF) / 255.0F) : 1.0F);
    return this;
  }
  
  public Color copy() {
    return (new Color()).copy(this);
  }
  
  public Color copy(Color color) {
    set(color.r, color.g, color.b, color.a);
    return this;
  }
  
  public int getRGBAColor() {
    float r = MathUtils.clamp(this.r, 0.0F, 1.0F);
    float g = MathUtils.clamp(this.g, 0.0F, 1.0F);
    float b = MathUtils.clamp(this.b, 0.0F, 1.0F);
    float a = MathUtils.clamp(this.a, 0.0F, 1.0F);
    return (int)(a * 255.0F) << 24 | (int)(r * 255.0F) << 16 | (int)(g * 255.0F) << 8 | (int)(b * 255.0F);
  }
  
  public int getRGBColor() {
    return getRGBAColor() & 0xFFFFFF;
  }
  
  public String stringify() {
    return stringify(false);
  }
  
  public String stringify(boolean alpha) {
    if (alpha)
      return "#" + StringUtils.leftPad(Integer.toHexString(getRGBAColor()), 8, '0'); 
    return "#" + StringUtils.leftPad(Integer.toHexString(getRGBColor()), 6, '0');
  }
  
  public boolean equals(Object obj) {
    if (obj instanceof Color) {
      Color color = (Color)obj;
      return (color.getRGBAColor() == getRGBAColor());
    } 
    return super.equals(obj);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\Color.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */