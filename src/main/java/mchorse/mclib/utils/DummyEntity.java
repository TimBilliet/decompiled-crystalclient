package mchorse.mclib.utils;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DummyEntity extends EntityLivingBase {
  private final ItemStack[] held;
  
  public ItemStack right;
  
  public ItemStack left;
  
  public DummyEntity(World worldIn) {
    super(worldIn);
    this.right = new ItemStack(Items.diamond_sword);
    this.left = new ItemStack(Items.golden_sword);
    this.held = new ItemStack[] { null, null, null, null, null, null };
  }
  
  public void setItems(ItemStack left, ItemStack right) {
    this.left = left;
    this.right = right;
  }
  
  public void toggleItems(boolean toggle) {
    if (toggle) {
      this.held[0] = this.right;
    } else {
      this.held[0] = null;
    } 
  }
  
  public ItemStack getHeldItem() {
    return this.held[0];
  }
  
  public ItemStack getEquipmentInSlot(int slotIn) {
    return this.held[slotIn];
  }
  
  public ItemStack getCurrentArmor(int slotIn) {
    return this.held[1 + slotIn];
  }
  
  public void setCurrentItemOrArmor(int slotIn, ItemStack stack) {
    this.held[slotIn] = stack;
  }

  @Override
  public ItemStack[] getInventory() {
    return new ItemStack[0];
  }

  public ItemStack[] getLastActiveItems() {
    return this.held;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\DummyEntity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */