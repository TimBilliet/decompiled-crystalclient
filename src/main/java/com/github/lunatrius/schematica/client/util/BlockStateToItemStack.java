package com.github.lunatrius.schematica.client.util;

import co.crystaldev.client.util.BlockUtils;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.reference.Reference;
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
      ItemStack itemStack = BlockUtils.getPickBlock(block, movingObjectPosition, (World)world, pos);
      if (itemStack != null)
        return itemStack; 
    } catch (Exception e) {
      Reference.logger.debug("Could not get the pick block for: {}", new Object[] { blockState, e });
    } 
    return null;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\clien\\util\BlockStateToItemStack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */