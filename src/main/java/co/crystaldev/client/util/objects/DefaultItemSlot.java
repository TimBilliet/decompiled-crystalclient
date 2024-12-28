package co.crystaldev.client.util.objects;

import net.minecraft.item.ItemStack;

public class DefaultItemSlot extends ItemSlot {
  private final ItemStack itemStack;
  
  public ItemStack getItemStack() {
    return this.itemStack;
  }
  
  public DefaultItemSlot(Type type, ItemStack itemStack) {
    super(type, -1);
    this.itemStack = itemStack;
  }
  
  public boolean isPresent() {
    return (this.itemStack != null);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\objects\DefaultItemSlot.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */