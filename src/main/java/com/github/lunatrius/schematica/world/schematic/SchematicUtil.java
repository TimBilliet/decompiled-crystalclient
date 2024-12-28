package com.github.lunatrius.schematica.world.schematic;

import com.github.lunatrius.schematica.reference.Reference;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public final class SchematicUtil {
  public static final ItemStack DEFAULT_ICON = new ItemStack((Block)Blocks.grass);
  
  public static final RegistryNamespaced<ResourceLocation, Block> BLOCK_REGISTRY = (RegistryNamespaced<ResourceLocation, Block>)Block.blockRegistry;
  
  public static final RegistryNamespaced<ResourceLocation, Item> ITEM_REGISTRY = Item.itemRegistry;
  
  public static NBTTagCompound readTagCompoundFromFile(File file) throws IOException {
    try {
      return CompressedStreamTools.readCompressed(new FileInputStream(file));
    } catch (Exception ex) {
      Reference.logger.warn("Failed compressed read, trying normal read...", ex);
      return CompressedStreamTools.read(file);
    } 
  }
  
  public static ItemStack getIconFromName(String iconName) {
    ResourceLocation rl = null;
    int damage = 0;
    String[] parts = iconName.split(",");
    if (parts.length >= 1) {
      rl = new ResourceLocation(parts[0]);
      if (parts.length >= 2)
        try {
          damage = Integer.parseInt(parts[1]);
        } catch (NumberFormatException numberFormatException) {} 
    } 
    if (rl == null)
      return DEFAULT_ICON.copy(); 
    ItemStack block = new ItemStack((Block)BLOCK_REGISTRY.getObject(rl), 1, damage);
    if (block.getItem() != null)
      return block; 
    ItemStack item = new ItemStack((Item)ITEM_REGISTRY.getObject(rl), 1, damage);
    if (item.getItem() != null)
      return item; 
    return DEFAULT_ICON.copy();
  }
  
  public static ItemStack getIconFromNBT(NBTTagCompound tagCompound) {
    ItemStack icon = DEFAULT_ICON.copy();
    if (tagCompound != null && tagCompound.hasKey("Icon")) {
      icon.readFromNBT(tagCompound.getCompoundTag("Icon"));
      if (icon.getItem() == null)
        icon = DEFAULT_ICON.copy(); 
    } 
    return icon;
  }
  
  public static ItemStack getIconFromFile(File file) {
    try {
      return getIconFromNBT(readTagCompoundFromFile(file));
    } catch (Exception e) {
      Reference.logger.error("Failed to read schematic icon!", e);
      return DEFAULT_ICON.copy();
    } 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\world\schematic\SchematicUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */