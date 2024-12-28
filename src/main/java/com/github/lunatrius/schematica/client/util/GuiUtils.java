package com.github.lunatrius.schematica.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class GuiUtils {
  public static final String UNDO_CHAR = "↶";
  
  public static final String RESET_CHAR = "☄";
  
  public static final String VALID = "✔";
  
  public static final String INVALID = "✕";
  
  public static int[] colorCodes = new int[] { 
      0, 170, 43520, 43690, 11141120, 11141290, 16755200, 11184810, 5592405, 5592575, 
      5635925, 5636095, 16733525, 16733695, 16777045, 16777215, 0, 42, 10752, 10794, 
      2752512, 2752554, 2763264, 2763306, 1381653, 1381695, 1392405, 1392447, 4134165, 4134207, 
      4144917, 4144959 };
  
  public static int getColorCode(char c, boolean isLighter) {
    return colorCodes[isLighter ? "0123456789abcdef".indexOf(c) : ("0123456789abcdef".indexOf(c) + 16)];
  }
  
  public static void drawContinuousTexturedBox(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int borderSize, float zLevel) {
    drawContinuousTexturedBox(x, y, u, v, width, height, textureWidth, textureHeight, borderSize, borderSize, borderSize, borderSize, zLevel);
  }
  
  public static void drawContinuousTexturedBox(ResourceLocation res, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int borderSize, float zLevel) {
    drawContinuousTexturedBox(res, x, y, u, v, width, height, textureWidth, textureHeight, borderSize, borderSize, borderSize, borderSize, zLevel);
  }
  
  public static void drawContinuousTexturedBox(ResourceLocation res, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int topBorder, int bottomBorder, int leftBorder, int rightBorder, float zLevel) {
    Minecraft.getMinecraft().getTextureManager().bindTexture(res);
    drawContinuousTexturedBox(x, y, u, v, width, height, textureWidth, textureHeight, topBorder, bottomBorder, leftBorder, rightBorder, zLevel);
  }
  
  public static void drawContinuousTexturedBox(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int topBorder, int bottomBorder, int leftBorder, int rightBorder, float zLevel) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.enableBlend();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    int fillerWidth = textureWidth - leftBorder - rightBorder;
    int fillerHeight = textureHeight - topBorder - bottomBorder;
    int canvasWidth = width - leftBorder - rightBorder;
    int canvasHeight = height - topBorder - bottomBorder;
    int xPasses = canvasWidth / fillerWidth;
    int remainderWidth = canvasWidth % fillerWidth;
    int yPasses = canvasHeight / fillerHeight;
    int remainderHeight = canvasHeight % fillerHeight;
    drawTexturedModalRect(x, y, u, v, leftBorder, topBorder, zLevel);
    drawTexturedModalRect(x + leftBorder + canvasWidth, y, u + leftBorder + fillerWidth, v, rightBorder, topBorder, zLevel);
    drawTexturedModalRect(x, y + topBorder + canvasHeight, u, v + topBorder + fillerHeight, leftBorder, bottomBorder, zLevel);
    drawTexturedModalRect(x + leftBorder + canvasWidth, y + topBorder + canvasHeight, u + leftBorder + fillerWidth, v + topBorder + fillerHeight, rightBorder, bottomBorder, zLevel);
    for (int i = 0; i < xPasses + ((remainderWidth > 0) ? 1 : 0); ) {
      drawTexturedModalRect(x + leftBorder + i * fillerWidth, y, u + leftBorder, v, (i == xPasses) ? remainderWidth : fillerWidth, topBorder, zLevel);
      drawTexturedModalRect(x + leftBorder + i * fillerWidth, y + topBorder + canvasHeight, u + leftBorder, v + topBorder + fillerHeight, (i == xPasses) ? remainderWidth : fillerWidth, bottomBorder, zLevel);
      int k = 0;
      for (;; i++) {
        if (k < yPasses + ((remainderHeight > 0) ? 1 : 0)) {
          drawTexturedModalRect(x + leftBorder + i * fillerWidth, y + topBorder + k * fillerHeight, u + leftBorder, v + topBorder, (i == xPasses) ? remainderWidth : fillerWidth, (k == yPasses) ? remainderHeight : fillerHeight, zLevel);
          k++;
          continue;
        } 
      } 
    } 
    for (int j = 0; j < yPasses + ((remainderHeight > 0) ? 1 : 0); j++) {
      drawTexturedModalRect(x, y + topBorder + j * fillerHeight, u, v + topBorder, leftBorder, (j == yPasses) ? remainderHeight : fillerHeight, zLevel);
      drawTexturedModalRect(x + leftBorder + canvasWidth, y + topBorder + j * fillerHeight, u + leftBorder + fillerWidth, v + topBorder, rightBorder, (j == yPasses) ? remainderHeight : fillerHeight, zLevel);
    } 
  }
  
  public static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height, float zLevel) {
    float uScale = 0.00390625F;
    float vScale = 0.00390625F;
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer wr = tessellator.getWorldRenderer();
    wr.begin(7, DefaultVertexFormats.POSITION_TEX);
    wr.pos(x, (y + height), zLevel).tex((u * uScale), ((v + height) * vScale)).endVertex();
    wr.pos((x + width), (y + height), zLevel).tex(((u + width) * uScale), ((v + height) * vScale)).endVertex();
    wr.pos((x + width), y, zLevel).tex(((u + width) * uScale), (v * vScale)).endVertex();
    wr.pos(x, y, zLevel).tex((u * uScale), (v * vScale)).endVertex();
    tessellator.draw();
  }
  
  public static void drawHoveringText(List<String> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font) {
    if (!textLines.isEmpty()) {
      GlStateManager.disableRescaleNormal();
      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableLighting();
      GlStateManager.disableDepth();
      int tooltipTextWidth = 0;
      for (String textLine : textLines) {
        int textLineWidth = font.getStringWidth(textLine);
        if (textLineWidth > tooltipTextWidth)
          tooltipTextWidth = textLineWidth; 
      } 
      boolean needsWrap = false;
      int titleLinesCount = 1;
      int tooltipX = mouseX + 12;
      if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
        tooltipX = mouseX - 16 - tooltipTextWidth;
        if (tooltipX < 4) {
          if (mouseX > screenWidth / 2) {
            tooltipTextWidth = mouseX - 12 - 8;
          } else {
            tooltipTextWidth = screenWidth - 16 - mouseX;
          } 
          needsWrap = true;
        } 
      } 
      if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth) {
        tooltipTextWidth = maxTextWidth;
        needsWrap = true;
      } 
      if (needsWrap) {
        int wrappedTooltipWidth = 0;
        List<String> wrappedTextLines = new ArrayList<>();
        for (int i = 0; i < textLines.size(); i++) {
          String textLine = textLines.get(i);
          List<String> wrappedLine = font.listFormattedStringToWidth(textLine, tooltipTextWidth);
          if (i == 0)
            titleLinesCount = wrappedLine.size(); 
          for (String line : wrappedLine) {
            int lineWidth = font.getStringWidth(line);
            if (lineWidth > wrappedTooltipWidth)
              wrappedTooltipWidth = lineWidth; 
            wrappedTextLines.add(line);
          } 
        } 
        tooltipTextWidth = wrappedTooltipWidth;
        textLines = wrappedTextLines;
        if (mouseX > screenWidth / 2) {
          tooltipX = mouseX - 16 - tooltipTextWidth;
        } else {
          tooltipX = mouseX + 12;
        } 
      } 
      int tooltipY = mouseY - 12;
      int tooltipHeight = 8;
      if (textLines.size() > 1) {
        tooltipHeight += (textLines.size() - 1) * 10;
        if (textLines.size() > titleLinesCount)
          tooltipHeight += 2; 
      } 
      if (tooltipY + tooltipHeight + 6 > screenHeight)
        tooltipY = screenHeight - tooltipHeight - 6; 
      int zLevel = 300;
      int backgroundColor = -267386864;
      drawGradientRect(300, tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, -267386864, -267386864);
      drawGradientRect(300, tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, -267386864, -267386864);
      drawGradientRect(300, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, -267386864, -267386864);
      drawGradientRect(300, tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, -267386864, -267386864);
      drawGradientRect(300, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, -267386864, -267386864);
      int borderColorStart = 1347420415;
      int borderColorEnd = 1344798847;
      drawGradientRect(300, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, 1347420415, 1344798847);
      drawGradientRect(300, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, 1347420415, 1344798847);
      drawGradientRect(300, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, 1347420415, 1347420415);
      drawGradientRect(300, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, 1344798847, 1344798847);
      for (int lineNumber = 0; lineNumber < textLines.size(); lineNumber++) {
        String line = textLines.get(lineNumber);
        font.drawStringWithShadow(line, tooltipX, tooltipY, -1);
        if (lineNumber + 1 == titleLinesCount)
          tooltipY += 2; 
        tooltipY += 10;
      } 
      GlStateManager.enableLighting();
      GlStateManager.enableDepth();
      RenderHelper.enableStandardItemLighting();
      GlStateManager.enableRescaleNormal();
    } 
  }
  
  public static void drawGradientRect(int zLevel, int left, int top, int right, int bottom, int startColor, int endColor) {
    float startAlpha = (startColor >> 24 & 0xFF) / 255.0F;
    float startRed = (startColor >> 16 & 0xFF) / 255.0F;
    float startGreen = (startColor >> 8 & 0xFF) / 255.0F;
    float startBlue = (startColor & 0xFF) / 255.0F;
    float endAlpha = (endColor >> 24 & 0xFF) / 255.0F;
    float endRed = (endColor >> 16 & 0xFF) / 255.0F;
    float endGreen = (endColor >> 8 & 0xFF) / 255.0F;
    float endBlue = (endColor & 0xFF) / 255.0F;
    GlStateManager.disableTexture2D();
    GlStateManager.enableBlend();
    GlStateManager.disableAlpha();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GlStateManager.shadeModel(7425);
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
    worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
    worldrenderer.pos(right, top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
    worldrenderer.pos(left, top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
    worldrenderer.pos(left, bottom, zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();
    worldrenderer.pos(right, bottom, zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();
    tessellator.draw();
    GlStateManager.shadeModel(7424);
    GlStateManager.disableBlend();
    GlStateManager.enableAlpha();
    GlStateManager.enableTexture2D();
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\clien\\util\GuiUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */