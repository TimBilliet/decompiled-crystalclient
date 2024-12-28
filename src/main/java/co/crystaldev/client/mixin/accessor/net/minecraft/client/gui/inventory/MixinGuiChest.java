package co.crystaldev.client.mixin.accessor.net.minecraft.client.gui.inventory;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.IInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({GuiChest.class})
public interface MixinGuiChest {
  @Accessor("lowerChestInventory")
  IInventory getLowerChestInventory();
  
  @Accessor("upperChestInventory")
  IInventory getUpperChestInventory();
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\accessor\net\minecraft\client\gui\inventory\MixinGuiChest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */