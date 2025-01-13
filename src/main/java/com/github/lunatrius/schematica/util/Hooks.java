package com.github.lunatrius.schematica.util;

import co.crystaldev.client.util.BlockUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class Hooks {
    public static boolean onPickBlock(MovingObjectPosition target, EntityPlayer player, World world) {
        ItemStack result = null;
        boolean isCreative = player.capabilities.isCreativeMode;
        TileEntity te = null;
        if (target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            IBlockState state = world.getBlockState(target.getBlockPos());
            if (state.getBlock().getMaterial() == Material.air)
                return false;
            if (isCreative && GuiScreen.isCtrlKeyDown())
                te = world.getTileEntity(target.getBlockPos());
            result = BlockUtils.getPickBlock(state.getBlock(), target, world, target.getBlockPos(), player);
        }
        if (result == null)
            return false;
        if (te != null) {
            NBTTagCompound nbt = new NBTTagCompound();
            te.writeToNBT(nbt);
            result.setTagInfo("BlockEntityTag", (NBTBase) nbt);
            NBTTagCompound display = new NBTTagCompound();
            result.setTagInfo("display", (NBTBase) display);
            NBTTagList lore = new NBTTagList();
            display.setTag("Lore", (NBTBase) lore);
            lore.appendTag((NBTBase) new NBTTagString("(+NBT)"));
        }
        int slot;
        for (slot = 0; slot < 9; slot++) {
            ItemStack stack = player.inventory.getStackInSlot(slot);
            if (stack != null && stack.isItemEqual(result) && ItemStack.areItemStackTagsEqual(stack, result)) {
                player.inventory.currentItem = slot;
                return true;
            }
        }
        if (!isCreative)
            return false;
        slot = player.inventory.getFirstEmptyStack();
        if (slot < 0 || slot >= 9)
            slot = player.inventory.currentItem;
        player.inventory.setInventorySlotContents(slot, result);
        player.inventory.currentItem = slot;
        return true;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematic\\util\Hooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */