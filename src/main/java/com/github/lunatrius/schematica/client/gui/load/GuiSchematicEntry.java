package com.github.lunatrius.schematica.client.gui.load;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.io.File;

public class GuiSchematicEntry {
  private final String name;
  
  private final ItemStack itemStack;
  
  private final File file;
  
  private final boolean isDirectory;
  
  public GuiSchematicEntry(String name, ItemStack itemStack, File file, boolean isDirectory) {
//    this(name, itemStack.getItem(), itemStack.getItemDamageForDisplay(), file, isDirectory);
    this(name, itemStack.getItem(), itemStack.getItemDamage(), file, isDirectory);
  }
  
  public GuiSchematicEntry(String name, Item item, int itemDamage, File file, boolean isDirectory) {
    this.name = name;
    this.file = file;
    this.isDirectory = isDirectory;
    this.itemStack = new ItemStack(item, 1, itemDamage);
  }
  
  public GuiSchematicEntry(String name, Block block, int itemDamage, File file, boolean isDirectory) {
    this.name = name;
    this.file = file;
    this.isDirectory = isDirectory;
    this.itemStack = new ItemStack(block, 1, itemDamage);
  }
  
  public String getName() {
    return this.name;
  }
  
  public Item getItem() {
    return this.itemStack.getItem();
  }
  
  public int getItemDamage() {
    return this.itemStack.getItemDamage();
//    return this.itemStack.getItemDamageForDisplay();
  }
  
  public boolean isDirectory() {
    return this.isDirectory;
  }
  
  public ItemStack getItemStack() {
    return this.itemStack;
  }
  
  public File getFile() {
    return this.file;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\gui\load\GuiSchematicEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */