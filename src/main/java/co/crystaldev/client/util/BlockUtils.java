package co.crystaldev.client.util;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class BlockUtils {
    public static ItemStack getPickBlock(Block blockIn, MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
        return getPickBlock(blockIn, target, world, pos);
    }

    public static ItemStack getPickBlock(Block blockIn, MovingObjectPosition target, World world, BlockPos pos) {
        Item item = blockIn.getItem(world, pos);
        if (item == null)
            return null;
        Block block = (item instanceof net.minecraft.item.ItemBlock && !blockIn.isFlowerPot()) ? Block.getBlockFromItem(item) : blockIn;
        return new ItemStack(item, 1, block.getDamageValue(world, pos));
    }
}