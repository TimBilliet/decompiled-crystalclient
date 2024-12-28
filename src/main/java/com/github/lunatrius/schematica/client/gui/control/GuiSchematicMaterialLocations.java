package com.github.lunatrius.schematica.client.gui.control;

import co.crystaldev.client.feature.impl.factions.Schematica;
import com.github.lunatrius.core.client.gui.GuiScreenBase;
import com.github.lunatrius.schematica.client.util.BlockList;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import java.io.IOException;
import java.util.List;

public class GuiSchematicMaterialLocations extends GuiScreenBase {
  private GuiSchematicMaterialLocationSlot guiSchematicMaterialsLocationSlot;
  
  private GuiButton btnTraceAll = null;
  
  private GuiButton btnUntraceAll = null;
  
  private GuiButton btnDone = null;
  
  protected final List<BlockList.WrappedItemStack> blockList;
  
  protected int index;
  
  public GuiSchematicMaterialLocations(List<BlockList.WrappedItemStack> blockList, int index, GuiSchematicMaterials parent) {
    super((GuiScreen)parent);
    this.blockList = blockList;
    this.index = index;
  }
  
  public void initGui() {
    int id = 0;
    this.btnTraceAll = new GuiButton(++id, this.width / 2 - 154, this.height - 30, 100, 20, "Trace All");
    this.buttonList.add(this.btnTraceAll);
    this.btnUntraceAll = new GuiButton(++id, this.width / 2 - 50, this.height - 30, 100, 20, "Untrace All");
    this.buttonList.add(this.btnUntraceAll);
    this.btnTraceAll.enabled = (Schematica.getInstance()).schemEsp;
    this.btnUntraceAll.enabled = (Schematica.getInstance()).schemEsp;
    this.btnDone = new GuiButton(++id, this.width / 2 + 54, this.height - 30, 100, 20, I18n.format("schematica.gui.done", new Object[0]));
    this.buttonList.add(this.btnDone);
    this.guiSchematicMaterialsLocationSlot = new GuiSchematicMaterialLocationSlot(this);
  }
  
  public void handleMouseInput() throws IOException {
    super.handleMouseInput();
    this.guiSchematicMaterialsLocationSlot.handleMouseInput();
  }
  
  protected void actionPerformed(GuiButton guiButton) {
    if (guiButton.enabled)
      if (guiButton.id == this.btnDone.id) {
        this.mc.displayGuiScreen(this.parentScreen);
      } else {
        this.guiSchematicMaterialsLocationSlot.actionPerformed(guiButton);
      }  
  }
  
  public void renderToolTip(ItemStack stack, int x, int y) {
    super.renderToolTip(stack, x, y);
  }
  
  public void drawScreen(int x, int y, float partialTicks) {
    this.guiSchematicMaterialsLocationSlot.drawScreen(x, y, partialTicks);
    drawString(this.fontRendererObj, "Location", this.width / 2 - 99, 4, 16777215);
    drawString(this.fontRendererObj, "Tracer", this.width / 2 + 108 - this.fontRendererObj.getStringWidth("Location"), 4, 16777215);
    super.drawScreen(x, y, partialTicks);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\gui\control\GuiSchematicMaterialLocations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */