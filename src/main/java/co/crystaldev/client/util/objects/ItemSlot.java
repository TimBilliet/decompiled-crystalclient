package co.crystaldev.client.util.objects;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class ItemSlot {
  private static final Minecraft mc = Minecraft.getMinecraft();
  
  private final Type type;
  
  private final int slot;
  
  private final boolean dynamic;
  
  public Type getType() {
    return this.type;
  }
  
  public int getSlot() {
    return this.slot;
  }
  
  public boolean isDynamic() {
    return this.dynamic;
  }
  
  public ItemSlot(Type type, int slot) {
    this.type = type;
    this.slot = slot;
    this.dynamic = false;
  }
  
  public ItemSlot() {
    this.type = null;
    this.slot = -1;
    this.dynamic = true;
  }
  
  public boolean isPresent() {
    if (mc.thePlayer == null)
      return false; 
    if (this.dynamic)
      return (mc.thePlayer.getHeldItem() != null); 
    return (((this.type == Type.ARMOR) ? mc.thePlayer.inventory.armorInventory : mc.thePlayer.inventory.mainInventory)[this.slot] != null);
  }
  
  public ItemStack getItemStack() {
    if (this.dynamic)
      return mc.thePlayer.getHeldItem(); 
    return ((this.type == Type.ARMOR) ? mc.thePlayer.inventory.armorInventory : mc.thePlayer.inventory.mainInventory)[this.slot];
  }
  
  public enum Type {
    ARMOR, REGULAR;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\objects\ItemSlot.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */