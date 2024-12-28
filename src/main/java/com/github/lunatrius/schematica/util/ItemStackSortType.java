package com.github.lunatrius.schematica.util;

import com.github.lunatrius.schematica.client.util.BlockList;
import com.github.lunatrius.schematica.reference.Reference;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public enum ItemStackSortType {
  NAME_ASC("name", "↑", new Comparator<BlockList.WrappedItemStack>() {
      public int compare(BlockList.WrappedItemStack wrappedItemStackA, BlockList.WrappedItemStack wrappedItemStackB) {
        String nameA = wrappedItemStackA.getItemStackDisplayName();
        String nameB = wrappedItemStackB.getItemStackDisplayName();
        return nameA.compareTo(nameB);
      }
    }),
  NAME_DESC("name", "↓", new Comparator<BlockList.WrappedItemStack>() {
      public int compare(BlockList.WrappedItemStack wrappedItemStackA, BlockList.WrappedItemStack wrappedItemStackB) {
        String nameA = wrappedItemStackA.getItemStackDisplayName();
        String nameB = wrappedItemStackB.getItemStackDisplayName();
        return nameB.compareTo(nameA);
      }
    }),
  SIZE_ASC("amount", "↑", new Comparator<BlockList.WrappedItemStack>() {
      public int compare(BlockList.WrappedItemStack wrappedItemStackA, BlockList.WrappedItemStack wrappedItemStackB) {
        return wrappedItemStackA.total - wrappedItemStackB.total;
      }
    }),
  SIZE_DESC("amount", "↓", new Comparator<BlockList.WrappedItemStack>() {
      public int compare(BlockList.WrappedItemStack wrappedItemStackA, BlockList.WrappedItemStack wrappedItemStackB) {
        return wrappedItemStackB.total - wrappedItemStackA.total;
      }
    });
  
  private final Comparator<BlockList.WrappedItemStack> comparator;
  
  public final String label;
  
  public final String glyph;
  
  ItemStackSortType(String label, String glyph, Comparator<BlockList.WrappedItemStack> comparator) {
    this.label = label;
    this.glyph = glyph;
    this.comparator = comparator;
  }
  
  public void sort(List<BlockList.WrappedItemStack> blockList) {
    try {
      Collections.sort(blockList, this.comparator);
    } catch (Exception e) {
      Reference.logger.error("Could not sort the block list!", e);
    } 
  }
  
  public ItemStackSortType next() {
    ItemStackSortType[] values = values();
    return values[(ordinal() + 1) % values.length];
  }
  
  public static ItemStackSortType fromString(String name) {
    try {
      return valueOf(name);
    } catch (Exception exception) {
      return NAME_ASC;
    } 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematic\\util\ItemStackSortType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */