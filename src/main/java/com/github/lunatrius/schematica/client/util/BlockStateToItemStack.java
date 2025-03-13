package com.github.lunatrius.schematica.client.util;

import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.reference.Reference;
import com.github.timmekeclient.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class BlockStateToItemStack {
    public static ItemStack getItemStack(IBlockState blockState, MovingObjectPosition movingObjectPosition, SchematicWorld world, BlockPos pos) {
        Block block = blockState.getBlock();
        try {
            ItemStack itemStack = BlockUtils.getPickBlock(block, movingObjectPosition, (World) world, pos);
            if (itemStack != null)
                return itemStack;
        } catch (Exception e) {
            Reference.logger.debug("Could not get the pick block for: {}", new Object[]{blockState, e});
        }
        return null;
    }
}
