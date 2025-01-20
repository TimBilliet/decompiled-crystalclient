package co.crystaldev.client.feature.impl.hud;

import co.crystaldev.client.feature.annotations.ConfigurableSize;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.HudModuleBackground;
import co.crystaldev.client.util.type.Tuple;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

@ConfigurableSize
@ModuleInfo(name = "Potion Count", description = "Displays the amount of Obsidian in your inventory onscreen", category = Category.HUD)
public class PotionCount extends HudModuleBackground {
    public String getDisplayText() {
        return getPotionCount((EntityPlayer) this.mc.thePlayer) + " pots";
    }

    public Tuple<String, String> getInfoHud() {
        return new Tuple("Potions", Integer.toString(getPotionCount((EntityPlayer) this.mc.thePlayer)));
    }

    public static int getPotionCount(EntityPlayer player) {
        if (player == null)
            throw new NullPointerException("The current player has not yet been set");
        int count = 0;
        for (int i = 1; i < 45; i++) {
            if (player.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = player.inventoryContainer.getSlot(i).getStack();
                Item item = is.getItem();
                if (item instanceof ItemPotion) {
                    ItemPotion pot = (ItemPotion) item;
                    if (pot.getEffects(is) != null)
                        for (PotionEffect effect : pot.getEffects(is)) {
                            if (effect.getPotionID() == Potion.heal.getId() && ItemPotion.isSplash(is.getItemDamage()))
                                count++;
                        }
                }
            }
        }
        return count;
    }
}
