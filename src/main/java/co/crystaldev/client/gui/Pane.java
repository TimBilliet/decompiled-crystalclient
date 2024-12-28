package co.crystaldev.client.gui;

import net.minecraft.client.Minecraft;

public class Pane implements Cloneable {
  public int x;
  
  public int y;
  
  public int width;
  
  public int height;
  
  public String toString() {
    return "Pane(x=" + this.x + ", y=" + this.y + ", width=" + this.width + ", height=" + this.height + ")";
  }
  
  public Pane(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }
  
  public Pane(float x, float y, float width, float height) {
    this.x = (int)x;
    this.y = (int)y;
    this.width = (int)width;
    this.height = (int)height;
  }
  
  public Pane(double x, double y, double width, double height) {
    this.x = (int)x;
    this.y = (int)y;
    this.width = (int)width;
    this.height = (int)height;
  }
  
  public Pane scale(float scale) {
    return new Pane(this.x * scale, this.y * scale, this.width * scale, this.height * scale);
  }
  
  public boolean isHovered(int mouseX, int mouseY, boolean useScale) {
    float scale = 1.0F;
    if (useScale && (Minecraft.getMinecraft()).currentScreen instanceof Screen)
      scale = ((Screen)(Minecraft.getMinecraft()).currentScreen).getScaledScreen(); 
    return (mouseX >= this.x / scale && mouseX <= (this.x + this.width) / scale && mouseY >= this.y / scale && mouseY <= (this.y + this.height) / scale);
  }
  
  public Pane clone() {
    try {
      return (Pane)super.clone();
    } catch (CloneNotSupportedException ex) {
      throw new AssertionError();
    } 
  }
}