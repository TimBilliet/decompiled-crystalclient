package com.github.lunatrius.schematica.client.gui.load;

import co.crystaldev.client.Client;
import co.crystaldev.client.Reference;
import co.crystaldev.client.Resources;
import co.crystaldev.client.gui.GuiOptions;
import co.crystaldev.client.gui.override.CustomGuiSlot;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.SchematicUploader;
import co.crystaldev.client.util.objects.FadingColor;
import com.github.lunatrius.core.client.gui.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GuiSchematicLoadSlot extends CustomGuiSlot {
  private final Minecraft minecraft = Minecraft.getMinecraft();

  private final GuiSchematicLoad guiSchematicLoad;

  private final Map<GuiSchematicEntry, FadingColor> fadingColors = new HashMap<>();

  protected int selectedIndex = -1;

  private long lastClick = 0L;

  private long lastSelection = 0L;

  private boolean mouseDown = false;

  public GuiSchematicLoadSlot(GuiSchematicLoad guiSchematicLoad) {
    super(Minecraft.getMinecraft(), guiSchematicLoad.width, guiSchematicLoad.height, 16, guiSchematicLoad.height - 40, 24);
    this.guiSchematicLoad = guiSchematicLoad;
  }

  protected int getSize() {
    return this.guiSchematicLoad.schematicFiles.size();
  }

  protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
    boolean ignore = (Minecraft.getSystemTime() - this.lastClick < 500L);
    this.lastClick = Minecraft.getSystemTime();
    if (ignore)
      return;
    GuiSchematicEntry schematic = this.guiSchematicLoad.schematicFiles.get(slotIndex);
    if (schematic.isDirectory()) {
      this.guiSchematicLoad.changeDirectory(schematic.getName());
      this.selectedIndex = -1;
    } else {
      this.selectedIndex = slotIndex;
      this.lastSelection = System.currentTimeMillis();
    }
  }

  protected boolean isSelected(int index) {
    return (index == this.selectedIndex);
  }

  protected void drawBackground() {}

  protected void drawContainerBackground(Tessellator tessellator) {}

  protected void drawSlot(int index, int x, int y, int par4, int mouseX, int mouseY) {
    if (index < 0 || index >= this.guiSchematicLoad.schematicFiles.size())
      return;
    GuiSchematicEntry schematic = this.guiSchematicLoad.schematicFiles.get(index);
    String schematicName = schematic.getName();
    if (schematic.isDirectory()) {
      schematicName = schematicName + "/";
    } else {
      schematicName = schematicName.replaceAll("(?i)\\.schematic$", "");
    }
    if (isSelected(index) && !schematic.isDirectory() && schematic.getFile() != null) {
      GuiHelper.drawItemStackSlot(this.mc.getTextureManager(), x, y);
      this.fadingColors.putIfAbsent(schematic, new FadingColor((GuiOptions.getInstance()).neutralTextColor, (GuiOptions.getInstance()).hoveredTextColor));
      FadingColor fadingColor = this.fadingColors.get(schematic);
      boolean slotHovered = (mouseX >= x && mouseX <= x + par4 && mouseY >= y && mouseY <= y + par4);
      if (fadingColor != null) {
        fadingColor.fade(slotHovered);
        RenderUtils.setGlColor(fadingColor.getCurrentColor());
      }
      boolean blendState = GL11.glGetBoolean(3042);
      RenderUtils.drawCustomSizedResource(Resources.UPLOAD, x + 3, y + 3, par4 - 6, par4 - 6);
      GlStateManager.resetColor();
      if (blendState)
        GL11.glEnable(3042);
      if (Mouse.isButtonDown(0) != this.mouseDown) {
        this.mouseDown = !this.mouseDown;
        if (this.mouseDown &&
          slotHovered && System.currentTimeMillis() - this.lastSelection > 500L) {
          this.lastSelection = 0L;
          Client.getInstance().getExecutor().execute(() -> {
                try {
                  SchematicUploader.upload(schematic.getFile());
                } catch (IOException ex) {
                  Reference.LOGGER.error(ex);
                }
              });
          Minecraft.getMinecraft().displayGuiScreen(null);
        }
      }
    } else {
      GuiHelper.drawItemStackWithSlot(this.minecraft.getTextureManager(), schematic.getItemStack(), x, y);
    }
    this.guiSchematicLoad.drawString(this.minecraft.fontRendererObj, schematicName, x + 24, y + 6, 16777215);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\gui\load\GuiSchematicLoadSlot.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */