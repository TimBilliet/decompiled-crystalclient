package wdl.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.util.List;

class Utils {
  private static final Minecraft mc = Minecraft.getMinecraft();
  
  private static final Logger logger = LogManager.getLogger();
  
  public static void drawGuiInfoBox(String text, int guiWidth, int guiHeight, int bottomPadding) {
    drawGuiInfoBox(text, 300, 100, guiWidth, guiHeight, bottomPadding);
  }
  
  public static void drawGuiInfoBox(String text, int infoBoxWidth, int infoBoxHeight, int guiWidth, int guiHeight, int bottomPadding) {
    if (text == null)
      return; 
    int infoX = guiWidth / 2 - infoBoxWidth / 2;
    int infoY = guiHeight - bottomPadding - infoBoxHeight;
    int y = infoY + 5;
    GuiScreen.drawRect(infoX, infoY, infoX + infoBoxWidth, infoY + infoBoxHeight, 2130706432);
    List<String> lines = wordWrap(text, infoBoxWidth - 10);
    for (String s : lines) {
      mc.fontRendererObj.drawString(s, infoX + 5, y, 16777215);
      y += mc.fontRendererObj.FONT_HEIGHT;
    } 
  }
  
  public static List<String> wordWrap(String s, int width) {
    s = s.replace("\r", "");
    List<String> lines = mc.fontRendererObj.listFormattedStringToWidth(s, width);
    return lines;
  }
  
  public static void drawListBackground(int topMargin, int bottomMargin, int top, int left, int bottom, int right) {
    drawDarkBackground(top, left, bottom, right);
    drawBorder(topMargin, bottomMargin, top, left, bottom, right);
  }
  
  public static void drawDarkBackground(int top, int left, int bottom, int right) {
    GlStateManager.disableLighting();
    GlStateManager.disableFog();
    Tessellator t = Tessellator.getInstance();
    WorldRenderer wr = t.getWorldRenderer();
    mc.getTextureManager().bindTexture(Gui.optionsBackground);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    float textureSize = 32.0F;
    wr.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
    wr.pos(0.0D, bottom, 0.0D).tex((0.0F / textureSize), (bottom / textureSize))
      .color(32, 32, 32, 255).endVertex();
    wr.pos(right, bottom, 0.0D).tex((right / textureSize), (bottom / textureSize))
      .color(32, 32, 32, 255).endVertex();
    wr.pos(right, top, 0.0D).tex((right / textureSize), (top / textureSize))
      .color(32, 32, 32, 255).endVertex();
    wr.pos(left, top, 0.0D).tex((left / textureSize), (top / textureSize))
      .color(32, 32, 32, 255).endVertex();
    t.draw();
  }
  
  public static void drawBorder(int topMargin, int bottomMargin, int top, int left, int bottom, int right) {
    GlStateManager.disableLighting();
    GlStateManager.disableFog();
    GlStateManager.disableDepth();
    byte padding = 4;
    mc.getTextureManager().bindTexture(Gui.optionsBackground);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    float textureSize = 32.0F;
    Tessellator t = Tessellator.getInstance();
    WorldRenderer wr = t.getWorldRenderer();
    int upperBoxEnd = top + topMargin;
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    wr.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
    wr.pos(left, upperBoxEnd, 0.0D).tex(0.0D, (upperBoxEnd / textureSize))
      .color(64, 64, 64, 255).endVertex();
    wr.pos(right, upperBoxEnd, 0.0D).tex((right / textureSize), (upperBoxEnd / textureSize))
      .color(64, 64, 64, 255).endVertex();
    wr.pos(right, top, 0.0D).tex((right / textureSize), (top / textureSize))
      .color(64, 64, 64, 255).endVertex();
    wr.pos(left, top, 0.0D).tex(0.0D, (top / textureSize))
      .color(64, 64, 64, 255).endVertex();
    t.draw();
    int lowerBoxStart = bottom - bottomMargin;
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    wr.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
    wr.pos(left, bottom, 0.0D).tex(0.0D, (bottom / textureSize))
      .color(64, 64, 64, 255).endVertex();
    wr.pos(right, bottom, 0.0D).tex((right / textureSize), (bottom / textureSize))
      .color(64, 64, 64, 255).endVertex();
    wr.pos(right, lowerBoxStart, 0.0D)
      .tex((right / textureSize), (lowerBoxStart / textureSize))
      .color(64, 64, 64, 255).endVertex();
    wr.pos(left, lowerBoxStart, 0.0D).tex(0.0D, (lowerBoxStart / textureSize))
      .color(64, 64, 64, 255).endVertex();
    t.draw();
    GlStateManager.enableBlend();
    GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
    GlStateManager.disableAlpha();
    GlStateManager.shadeModel(7425);
    GlStateManager.disableTexture2D();
    wr.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
    wr.pos(left, (upperBoxEnd + padding), 0.0D).tex(0.0D, 1.0D)
      .color(0, 0, 0, 0).endVertex();
    wr.pos(right, (upperBoxEnd + padding), 0.0D).tex(1.0D, 1.0D)
      .color(0, 0, 0, 0).endVertex();
    wr.pos(right, upperBoxEnd, 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 255)
      .endVertex();
    wr.pos(left, upperBoxEnd, 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 255)
      .endVertex();
    t.draw();
    wr.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
    wr.pos(left, lowerBoxStart, 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 255)
      .endVertex();
    wr.pos(right, lowerBoxStart, 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 255)
      .endVertex();
    wr.pos(right, (lowerBoxStart - padding), 0.0D).tex(1.0D, 0.0D)
      .color(0, 0, 0, 0).endVertex();
    wr.pos(left, (lowerBoxStart - padding), 0.0D).tex(0.0D, 0.0D)
      .color(0, 0, 0, 0).endVertex();
    t.draw();
    GlStateManager.enableTexture2D();
    GlStateManager.shadeModel(7424);
    GlStateManager.enableAlpha();
    GlStateManager.disableBlend();
  }
  
  public static boolean isMouseOverTextBox(int mouseX, int mouseY, GuiTextField textBox) {
    int scaledX = mouseX - textBox.xPosition;
    int scaledY = mouseY - textBox.yPosition;
    int height = 20;
    return (scaledX >= 0 && scaledX < textBox.getWidth() && scaledY >= 0 && scaledY < 20);
  }
  
  public static void openLink(String path) {
    try {
      Class<?> desktopClass = Class.forName("java.awt.Desktop");
      Object desktop = desktopClass.getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
      desktopClass.getMethod("browse", new Class[] { URI.class }).invoke(desktop, new Object[] { new URI(path) });
    } catch (Throwable e) {
      logger.error("Couldn't open link", e);
    } 
  }
  
  public static void drawStringWithShadow(String s, int x, int y, int color) {
    mc.fontRendererObj.drawStringWithShadow(s, x, y, color);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\gui\Utils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */