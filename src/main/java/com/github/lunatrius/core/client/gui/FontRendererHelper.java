package com.github.lunatrius.core.client.gui;

import net.minecraft.client.gui.FontRenderer;

public class FontRendererHelper {
  public static void drawLeftAlignedString(FontRenderer fontRenderer, String str, int x, int y, int color) {
    fontRenderer.drawStringWithShadow(str, x, y, color);
  }
  
  public static void drawCenteredString(FontRenderer fontRenderer, String str, int x, int y, int color) {
    fontRenderer.drawStringWithShadow(str, (x - fontRenderer.getStringWidth(str) / 2), y, color);
  }
  
  public static void drawRightAlignedString(FontRenderer fontRenderer, String str, int x, int y, int color) {
    fontRenderer.drawStringWithShadow(str, (x - fontRenderer.getStringWidth(str)), y, color);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\core\client\gui\FontRendererHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */