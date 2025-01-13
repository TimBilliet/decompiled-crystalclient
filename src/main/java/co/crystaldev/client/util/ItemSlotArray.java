package co.crystaldev.client.util;

import co.crystaldev.client.util.objects.ItemSlot;

public class ItemSlotArray {
    private final ItemSlot[] slots;

    private final ItemSlot[] reversed;

    public ItemSlotArray(ItemSlot... slots) {
        this.slots = slots;
        this.reversed = new ItemSlot[slots.length];
        for (int i = 0; i < slots.length; i++)
            this.reversed[slots.length - 1 - i] = slots[i];
    }

    public ItemSlot[] get() {
        return this.slots;
    }

    public ItemSlot[] getReversed() {
        return this.reversed;
    }

    public boolean anyPresent() {
        for (ItemSlot slot : this.slots) {
            if (slot.isPresent())
                return true;
        }
        return false;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\ItemSlotArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */