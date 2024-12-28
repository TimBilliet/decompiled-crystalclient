package co.crystaldev.client.feature.impl.hud;

import co.crystaldev.client.feature.annotations.ConfigurableSize;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.HudModuleBackground;
import co.crystaldev.client.util.type.Tuple;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

@ConfigurableSize
@ModuleInfo(name = "Obsidian Count", description = "Displays the amount of Obsidian in your inventory onscreen", category = Category.HUD)
public class ObsidianCount extends HudModuleBackground {
  public String getDisplayText() {
    return getTotalObsidian() + " obby";
  }
  
  public Tuple<String, String> getInfoHud() {
    return new Tuple("Obsidian", Integer.toString(getTotalObsidian()));
  }
  
  private int getTotalObsidian() {
    if (this.mc.thePlayer == null)
      throw new NullPointerException("The current player has not yet been set"); 
    int count = 0;
    for (int i = 1; i < 45; i++) {
      if (this.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
        ItemStack is = this.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
        if (Block.getIdFromBlock(Block.getBlockFromItem(is.getItem())) == 49)
          count += is.stackSize; 
      } 
    } 
    return count;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\hud\ObsidianCount.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */