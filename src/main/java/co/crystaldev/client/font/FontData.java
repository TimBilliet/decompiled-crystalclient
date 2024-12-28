package co.crystaldev.client.font;

import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;
import java.awt.image.BufferedImage;

public final class FontData {
  private final CharacterData[] characterBounds = new CharacterData[256];
  
  private int fontHeight = 0;
  
  private int textureWidth;
  
  private int textureHeight;
  
  public int getFontHeight() {
    return this.fontHeight;
  }
  
  public int getTextureWidth() {
    return this.textureWidth;
  }
  
  public int getTextureHeight() {
    return this.textureHeight;
  }
  
  private int texId = -1;
  
  public void setFont(Font font, boolean antialias) {
    setFont(font, antialias, antialias, 16, 2);
  }
  
  private void setFont(Font font, boolean antiAlias, boolean fractionalMetrics, int characterCount, int padding) {
    if (this.texId == -1)
      this.texId = FontTextureManager.genTexture(); 
    FontMetrics fontMetrics = (new Canvas()).getFontMetrics(font);
    int charHeight = 0, positionX = 0, positionY = 0;
    for (int i = 0; i < this.characterBounds.length; i++) {
      char character = (char)i;
      int height = fontMetrics.getHeight();
      int width = fontMetrics.charWidth(character);
      if (i != 0 && i % characterCount == 0) {
        positionX = padding;
        positionY += charHeight + padding;
        charHeight = 0;
      } 
      if (height > charHeight) {
        charHeight = height;
        if (charHeight > this.fontHeight)
          this.fontHeight = charHeight; 
      } 
      this.characterBounds[i] = new CharacterData(positionX, positionY, width, height);
      positionX += width + padding;
      if (positionX + width + padding > this.textureWidth)
        this.textureWidth = positionX + width + padding; 
      if (positionY + height + padding > this.textureHeight)
        this.textureHeight = positionY + height + padding; 
    } 
    BufferedImage bufferedImage = new BufferedImage(this.textureWidth, this.textureHeight, 2);
    Graphics2D graphics2D = (Graphics2D)bufferedImage.getGraphics();
    graphics2D.setFont(font);
    fontMetrics = graphics2D.getFontMetrics(font);
    graphics2D.setColor(new Color(255, 255, 255, 0));
    graphics2D.fillRect(0, 0, this.textureWidth, this.textureHeight);
    graphics2D.setColor(Color.WHITE);
    graphics2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, fractionalMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
    graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antiAlias ? RenderingHints.VALUE_TEXT_ANTIALIAS_GASP : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    graphics2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, antiAlias ? RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY : RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
    for (int j = 0; j < this.characterBounds.length; j++)
      graphics2D.drawString(String.valueOf((char)j), (this.characterBounds[j]).x, (this.characterBounds[j]).y + fontMetrics.getAscent()); 
    FontTextureManager.applyTexture(this.texId, bufferedImage, antiAlias ? 9729 : 9728, 10497);
  }
  
  public void bind() {
    GlStateManager.bindTexture(this.texId);
  }
  
  public CharacterData getCharacterBounds(char character) {
    return this.characterBounds[character];
  }
  
  public int getStringWidth(String text) {
    int width = 0;
    for (char c : text.toCharArray())
      width += (this.characterBounds[c]).width; 
    return width;
  }
  
  public boolean hasBounds(char character) {
    return (character < '\u0100');
  }
  
  public boolean hasFont() {
    return (this.texId != -1);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\font\FontData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */