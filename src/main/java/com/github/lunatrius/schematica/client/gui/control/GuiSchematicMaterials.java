package com.github.lunatrius.schematica.client.gui.control;

import com.github.lunatrius.core.client.gui.GuiScreenBase;
//import com.github.lunatrius.schematica.Schematica;
import com.github.lunatrius.schematica.client.gui.buttons.GuiUnicodeGlyphButton;
import com.github.lunatrius.schematica.client.util.BlockList;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.handler.ConfigurationHandler;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import com.github.lunatrius.schematica.reference.Reference;
import com.github.lunatrius.schematica.util.ItemStackSortType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.apache.commons.io.IOUtils;
import co.crystaldev.client.feature.impl.factions.Schematica;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Formatter;
import java.util.List;

public class GuiSchematicMaterials extends GuiScreenBase {
  private GuiSchematicMaterialsSlot guiSchematicMaterialsSlot;
  
  private ItemStackSortType sortType = ItemStackSortType.fromString(ConfigurationHandler.sortType);
  
  private GuiUnicodeGlyphButton btnSort = null;
  
  private GuiButton btnDump = null;
  
  private GuiButton btnDone = null;
  
  private GuiButton btnTraceAll = null;
  
  private GuiButton btnUntraceAll = null;
  
  private final String strMaterialName = I18n.format("schematica.gui.materialname", new Object[0]);
  
  private final String strMaterialAmount = I18n.format("schematica.gui.materialamount", new Object[0]);
  
  protected final List<BlockList.WrappedItemStack> blockList;
  
  public GuiSchematicMaterials(GuiScreen guiScreen) {
    super(guiScreen);
    Minecraft minecraft = Minecraft.getMinecraft();
    SchematicWorld schematic = ClientProxy.currentSchematic.schematic;
    this.blockList = (new BlockList()).getList((EntityPlayer)minecraft.thePlayer, schematic, (World)minecraft.theWorld);
    this.sortType.sort(this.blockList);
  }
  
  public void initGui() {
    int id = 0;
    this.btnSort = new GuiUnicodeGlyphButton(++id, this.width / 2 - 154, this.height - 27, 100, 20, " " + I18n.format("schematica.gui.material" + this.sortType.label, new Object[0]), this.sortType.glyph, 2.0F);
    this.buttonList.add(this.btnSort);
    this.btnDump = new GuiButton(++id, this.width / 2 - 50, this.height - 27, 100, 20, I18n.format("schematica.gui.materialdump", new Object[0]));
    this.buttonList.add(this.btnDump);
    this.btnDone = new GuiButton(++id, this.width / 2 + 54, this.height - 27, 100, 20, I18n.format("schematica.gui.done", new Object[0]));
    this.buttonList.add(this.btnDone);
    this.btnTraceAll = new GuiButton(++id, 5, this.height - 27, 100, 20, "Trace All");
    this.buttonList.add(this.btnTraceAll);
    this.btnUntraceAll = new GuiButton(++id, this.width - 104, this.height - 27, 100, 20, "Untrace All");
    this.buttonList.add(this.btnUntraceAll);
    this.btnTraceAll.enabled = (Schematica.getInstance()).schemEsp;
    this.btnUntraceAll.enabled = (Schematica.getInstance()).schemEsp;
    this.guiSchematicMaterialsSlot = new GuiSchematicMaterialsSlot(this);
  }
  
  public void handleMouseInput() throws IOException {
    super.handleMouseInput();
    this.guiSchematicMaterialsSlot.handleMouseInput();
  }
  
  protected void actionPerformed(GuiButton guiButton) {
    if (guiButton.enabled)
      if (guiButton.id == this.btnSort.id) {
        this.sortType = this.sortType.next();
        this.sortType.sort(this.blockList);
        this.btnSort.displayString = " " + I18n.format("schematica.gui.material" + this.sortType.label, new Object[0]);
        this.btnSort.glyph = this.sortType.glyph;
      } else if (guiButton.id == this.btnDump.id) {
        dumpMaterialList(this.blockList);
      } else if (guiButton.id == this.btnDone.id) {
        this.mc.displayGuiScreen(this.parentScreen);
      } else if (guiButton.id != this.btnTraceAll.id) {
        if (guiButton.id == this.btnUntraceAll.id) {
          Schematica.getInstance().clearTracerLists();
        } else {
          this.guiSchematicMaterialsSlot.actionPerformed(guiButton);
        } 
      }  
  }
  
  public void renderToolTip(ItemStack stack, int x, int y) {
    super.renderToolTip(stack, x, y);
  }
  
  public void drawScreen(int x, int y, float partialTicks) {
    this.guiSchematicMaterialsSlot.drawScreen(x, y, partialTicks);
    drawString(this.fontRendererObj, this.strMaterialName, this.width / 2 - 108, 4, 16777215);
    drawString(this.fontRendererObj, this.strMaterialAmount, this.width / 2 + 108 - this.fontRendererObj.getStringWidth(this.strMaterialAmount), 4, 16777215);
    super.drawScreen(x, y, partialTicks);
  }
  
  private void dumpMaterialList(List<BlockList.WrappedItemStack> blockList) {
    if (blockList.size() <= 0)
      return; 
    int maxLengthName = 0;
    int maxSize = 0;
    for (BlockList.WrappedItemStack wrappedItemStack : blockList) {
      maxLengthName = Math.max(maxLengthName, wrappedItemStack.getItemStackDisplayName().length());
      maxSize = Math.max(maxSize, wrappedItemStack.total);
    } 
    int maxLengthSize = String.valueOf(maxSize).length();
    String formatName = "%-" + maxLengthName + "s";
    String formatSize = "%" + maxLengthSize + "d";
    StringBuilder stringBuilder = new StringBuilder((maxLengthName + 1 + maxLengthSize) * blockList.size());
    Formatter formatter = new Formatter(stringBuilder);
    for (BlockList.WrappedItemStack wrappedItemStack : blockList) {
      formatter.format(formatName, new Object[] { wrappedItemStack.getItemStackDisplayName() });
      stringBuilder.append(" ");
      formatter.format(formatSize, new Object[] { Integer.valueOf(wrappedItemStack.total) });
      stringBuilder.append(System.lineSeparator());
    } 
    File dumps = com.github.lunatrius.schematica.Schematica.proxy.getDirectory("dumps");
    try {
      FileOutputStream outputStream = new FileOutputStream(new File(dumps, "Schematica-materials.txt"));
      try {
        IOUtils.write(stringBuilder.toString(), outputStream);
      } finally {
        outputStream.close();
      } 
    } catch (Exception e) {
      Reference.logger.error("Could not dump the material list!", e);
    } 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\gui\control\GuiSchematicMaterials.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */