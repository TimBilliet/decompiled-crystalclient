package com.github.lunatrius.schematica.client.gui.buttons;

import com.github.lunatrius.schematica.client.util.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class GuiUnicodeGlyphButton extends GuiButtonExt {
  public String glyph;
  
  public float glyphScale;
  
  public GuiUnicodeGlyphButton(int id, int xPos, int yPos, int width, int height, String displayString, String glyph, float glyphScale) {
    super(id, xPos, yPos, width, height, displayString);
    this.glyph = glyph;
    this.glyphScale = glyphScale;
  }
  
  public void drawButton(Minecraft mc, int mouseX, int mouseY) {
    if (this.visible) {
      this.hovered = (mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height);
      int k = getHoverState(this.hovered);
      GuiUtils.drawContinuousTexturedBox(buttonTextures, this.xPosition, this.yPosition, 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
      mouseDragged(mc, mouseX, mouseY);
      int color = 14737632;
      if (this.packedFGColour != 0) {
        color = this.packedFGColour;
      } else if (!this.enabled) {
        color = 10526880;
      } else if (this.hovered) {
        color = 16777120;
      } 
      String buttonText = this.displayString;
      int glyphWidth = (int)(mc.fontRendererObj.getStringWidth(this.glyph) * this.glyphScale);
      int strWidth = mc.fontRendererObj.getStringWidth(buttonText);
      int elipsisWidth = mc.fontRendererObj.getStringWidth("...");
      int totalWidth = strWidth + glyphWidth;
      if (totalWidth > this.width - 6 && totalWidth > elipsisWidth)
        buttonText = mc.fontRendererObj.trimStringToWidth(buttonText, this.width - 6 - elipsisWidth).trim() + "...";
      strWidth = mc.fontRendererObj.getStringWidth(buttonText);
      totalWidth = glyphWidth + strWidth;
      GlStateManager.pushMatrix();
      GlStateManager.scale(this.glyphScale, this.glyphScale, 1.0F);
      drawCenteredString(mc.fontRendererObj, this.glyph, (int)((this.xPosition + this.width / 2 - strWidth / 2) / this.glyphScale - glyphWidth / 2.0F * this.glyphScale + 2.0F), (int)((this.yPosition + (this.height - 8) / this.glyphScale / 2.0F - 1.0F) / this.glyphScale), color);
      GlStateManager.popMatrix();
      drawCenteredString(mc.fontRendererObj, buttonText, (int)((this.xPosition + this.width / 2) + glyphWidth / this.glyphScale), this.yPosition + (this.height - 8) / 2, color);
    } 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\gui\buttons\GuiUnicodeGlyphButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */