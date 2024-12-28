package co.crystaldev.client.util.enums;

import net.minecraft.client.Minecraft;

public enum AnchorRegion {
  TOP_LEFT(true, false, false, true, false, false),
  TOP_CENTER(false, true, false, true, false, false),
  TOP_RIGHT(false, false, true, true, false, false),
  CENTER_LEFT(true, false, false, false, true, false),
  CENTER(false, true, false, false, true, false),
  CENTER_RIGHT(false, false, true, false, true, false),
  BOTTOM_LEFT(true, false, false, false, false, true),
  BOTTOM_CENTER(false, true, false, false, false, true),
  BOTTOM_RIGHT(false, false, true, false, false, true);
  
  AnchorRegion(boolean leftSided, boolean centerX, boolean rightSided, boolean topSided, boolean centerY, boolean bottomSided) {
    this.leftSided = leftSided;
    this.centerX = centerX;
    this.rightSided = rightSided;
    this.topSided = topSided;
    this.centerY = centerY;
    this.bottomSided = bottomSided;
  }
  
  private final boolean leftSided;
  
  private final boolean centerX;
  
  private final boolean rightSided;
  
  private final boolean topSided;
  
  private final boolean centerY;
  
  private final boolean bottomSided;
  
  public boolean isTopSided() {
    return this.topSided;
  }
  
  public boolean isBottomSided() {
    return this.bottomSided;
  }
  
  public boolean isLeftSided() {
    return this.leftSided;
  }
  
  public boolean isRightSided() {
    return this.rightSided;
  }
  
  public boolean isCenteredHorizontally() {
    return this.centerX;
  }
  
  public boolean isCenteredVertically() {
    return this.centerY;
  }
  
  public float getRelativeX() {
    return this.leftSided ? 0.0F : (this.centerX ? (
      (Minecraft.getMinecraft()).displayWidth / 4.0F) : (
      (Minecraft.getMinecraft()).displayWidth / 2.0F));
  }
  
  public float getRelativeY() {
    return this.topSided ? 0.0F : (this.centerY ? (
      (Minecraft.getMinecraft()).displayHeight / 4.0F) : (
      (Minecraft.getMinecraft()).displayHeight / 2.0F));
  }
  
  public boolean isInBounds(float x, float y) {
    return (x >= getBoundXMin() && x <= getBoundXMax() && y >= getBoundYMin() && y <= getBoundYMax());
  }
  
  public float getWidth() {
    return (Minecraft.getMinecraft()).displayWidth / 2.0F / 3.0F;
  }
  
  public float getHeight() {
    return (Minecraft.getMinecraft()).displayHeight / 2.0F / 3.0F;
  }
  
  public int clampX(int x) {
    switch (this) {
      case TOP_LEFT:
      case CENTER_LEFT:
      case BOTTOM_LEFT:
        return Math.max(0, x);
      case TOP_RIGHT:
      case CENTER_RIGHT:
      case BOTTOM_RIGHT:
        return Math.min((int)getBoundXMax(), x);
    } 
    return x;
  }
  
  public int clampY(int y) {
    switch (this) {
      case TOP_LEFT:
      case TOP_RIGHT:
      case TOP_CENTER:
        return Math.max(0, y);
      case BOTTOM_LEFT:
      case BOTTOM_RIGHT:
      case BOTTOM_CENTER:
        return Math.min((int)getBoundYMax(), y);
    } 
    return y;
  }
  
  private float getBoundXMin() {
    switch (this) {
      case TOP_LEFT:
      case CENTER_LEFT:
      case BOTTOM_LEFT:
        return 0.0F;
      case TOP_CENTER:
      case BOTTOM_CENTER:
      case CENTER:
        return (Minecraft.getMinecraft()).displayWidth / 2.0F / 3.0F;
    } 
    return (Minecraft.getMinecraft()).displayWidth / 2.0F / 3.0F * 2.0F;
  }
  
  private float getBoundXMax() {
    switch (this) {
      case TOP_LEFT:
      case CENTER_LEFT:
      case BOTTOM_LEFT:
        return (Minecraft.getMinecraft()).displayWidth / 2.0F / 3.0F;
      case TOP_CENTER:
      case BOTTOM_CENTER:
      case CENTER:
        return (Minecraft.getMinecraft()).displayWidth / 2.0F / 3.0F * 2.0F;
    } 
    return (Minecraft.getMinecraft()).displayWidth / 2.0F;
  }
  
  private float getBoundYMin() {
    switch (this) {
      case TOP_LEFT:
      case TOP_RIGHT:
      case TOP_CENTER:
        return 0.0F;
      case CENTER_LEFT:
      case CENTER_RIGHT:
      case CENTER:
        return (Minecraft.getMinecraft()).displayHeight / 2.0F / 3.0F;
    } 
    return (Minecraft.getMinecraft()).displayHeight / 2.0F / 3.0F * 2.0F;
  }
  
  private float getBoundYMax() {
    switch (this) {
      case TOP_LEFT:
      case TOP_RIGHT:
      case TOP_CENTER:
        return (Minecraft.getMinecraft()).displayHeight / 2.0F / 3.0F;
      case CENTER_LEFT:
      case CENTER_RIGHT:
      case CENTER:
        return (Minecraft.getMinecraft()).displayHeight / 2.0F / 3.0F * 2.0F;
    } 
    return (Minecraft.getMinecraft()).displayHeight / 2.0F;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\enums\AnchorRegion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */