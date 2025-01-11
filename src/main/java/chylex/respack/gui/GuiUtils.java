package chylex.respack.gui;

import chylex.respack.packs.ResourcePackListEntryFolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.Util;
import org.lwjgl.Sys;

import java.io.File;
import java.net.URI;
import java.util.List;

public final class GuiUtils {
  private static final Minecraft mc = Minecraft.getMinecraft();
  
  public static void openFolder(File file) {
    String s = file.getAbsolutePath();
    if (Util.getOSType() == Util.EnumOS.OSX) {
      try {
        Runtime.getRuntime().exec(new String[] { "/usr/bin/open", s });
        return;
      } catch (Exception exception) {}
    } else if (Util.getOSType() == Util.EnumOS.WINDOWS) {
      String command = String.format("cmd.exe /C start \"Open file\" \"%s\"", s);
      try {
        Runtime.getRuntime().exec(command);
        return;
      } catch (Exception exception) {}
    } 
    try {
      Class<?> cls = Class.forName("java.awt.Desktop");
      Object desktop = cls.getMethod("getDesktop", new Class[0]).invoke(null);
      cls.getMethod("browse", new Class[] { URI.class }).invoke(desktop, file.toURI());
    } catch (Throwable t) {
      Sys.openURL("file://" + s);
    } 
  }
  
  public static void renderFolderEntry(ResourcePackListEntryFolder entry, int x, int y, boolean isSelected) {
    entry.bindResourcePackIcon();
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.enableBlend();
    GlStateManager.blendFunc(770, 771);
    Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
    GlStateManager.disableBlend();
    if ((mc.gameSettings.touchscreen || isSelected) && entry.func_148310_d()) {
      Gui.drawRect(x, y, x + 32, y + 32, -1601138544);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    } 
    String s = entry.getResourcePackName();
    //int i2 = mc.fontRenderer.getStringWidth(s);
    int i2 = mc.fontRendererObj.getStringWidth(s);
    if (i2 > 157)
      s = mc.fontRendererObj.trimStringToWidth(s, 157 - mc.fontRendererObj.getStringWidth("...")) + "...";
    mc.fontRendererObj.drawStringWithShadow(s, (x + 32 + 2), (y + 1), 16777215);
    List<String> list = mc.fontRendererObj.listFormattedStringToWidth(entry.getResourcePackDescription(), 157);
    for (int j2 = 0; j2 < 2 && j2 < list.size(); j2++)
      mc.fontRendererObj.drawStringWithShadow(list.get(j2), (x + 32 + 2), (y + 12 + 10 * j2), 8421504);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\chylex\chylex.respack\gui\GuiUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */