package com.github.lunatrius.core.entity;

import com.github.lunatrius.core.util.vector.Vector3f;
import com.github.lunatrius.core.util.vector.Vector3i;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class EntityHelper {
    public static final int WILDMARK = -1;

    public static int getItemCountInInventory(IInventory inventory, Item item) {
        return getItemCountInInventory(inventory, item, -1);
    }

    public static int getItemCountInInventory(IInventory inventory, Item item, int itemDamage) {
        int inventorySize = inventory.getSizeInventory();
        int count = 0;
        for (int slot = 0; slot < inventorySize; slot++) {
            ItemStack itemStack = inventory.getStackInSlot(slot);
//      if (itemStack != null && itemStack.getItem() == item && (itemDamage == -1 || itemDamage == itemStack.getItemDamageForDisplay()))
            if (itemStack != null && itemStack.getItem() == item && (itemDamage == -1 || itemDamage == itemStack.getItemDamage()))

                count += itemStack.stackSize;
        }
        return count;
    }

    public static Vector3f getVector3fFromEntity(Entity entity) {
        return new Vector3f((float) entity.posX, (float) entity.posY, (float) entity.posZ);
    }

    public static Vector3f getVector3fFromEntity(Entity entity, Vector3f vec) {
        return vec.set((float) entity.posX, (float) entity.posY, (float) entity.posZ);
    }

    public static Vector3i getVector3iFromEntity(Entity entity) {
        return new Vector3i((int) Math.floor(entity.posX), (int) Math.floor(entity.posY), (int) Math.floor(entity.posZ));
    }

    public static Vector3i getVector3iFromEntity(Entity entity, Vector3i vec) {
        return vec.set((int) Math.floor(entity.posX), (int) Math.floor(entity.posY), (int) Math.floor(entity.posZ));
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\core\entity\EntityHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */