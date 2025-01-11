package com.github.lunatrius.schematica.client.util;

import co.crystaldev.client.util.BlockUtils;
import com.github.lunatrius.core.util.BlockPosHelper;
import com.github.lunatrius.core.util.MBlockPos;
import com.github.lunatrius.schematica.block.state.BlockStateHelper;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.reference.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3i;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class BlockList {
  public List<WrappedItemStack> getList(EntityPlayer player, SchematicWorld world, World mcWorld) {
    List<WrappedItemStack> blockList = new ArrayList<>();
    if (world == null)
      return blockList; 
    MovingObjectPosition movingObjectPosition = new MovingObjectPosition(player);
    MBlockPos mcPos = new MBlockPos();
    for (MBlockPos pos : BlockPosHelper.getAllInBox(BlockPos.ORIGIN, new BlockPos(world.getWidth() - 1, world.getHeight() - 1, world.getLength() - 1))) {
      if (world.isRenderingLayer && pos.getY() != world.renderingLayer)
        continue; 
      IBlockState blockState = world.getBlockState(pos);
      Block block = blockState.getBlock();
      if (block == Blocks.air || world.isAirBlock(pos))
        continue; 
      mcPos.set(world.position.add(pos));
      IBlockState mcBlockState = mcWorld.getBlockState(mcPos);
      boolean isPlaced = BlockStateHelper.areBlockStatesEqual(blockState, mcBlockState);
      ItemStack stack = null;
      try {
        stack = BlockUtils.getPickBlock(block, movingObjectPosition, world, pos, player);
      } catch (Exception e) {
        Reference.logger.debug("Could not get the pick block for: {}", blockState, e);
      } 
      if (stack == null || stack.getItem() == null) {
        Reference.logger.debug("Could not find the item for: {}", blockState);
        continue;
      } 
      WrappedItemStack wrappedItemStack = findOrCreateWrappedItemStackFor(blockList, stack);
      if (isPlaced) {
        wrappedItemStack.placed++;
      } else {
        wrappedItemStack.positions.add(new BlockPos(mcPos.x, mcPos.y, mcPos.z));
      } 
      wrappedItemStack.total++;
    } 
    return blockList;
  }
  
  private WrappedItemStack findOrCreateWrappedItemStackFor(List<WrappedItemStack> blockList, ItemStack itemStack) {
    for (WrappedItemStack wrappedItemStack1 : blockList) {
      if (wrappedItemStack1.itemStack.isItemEqual(itemStack))
        return wrappedItemStack1; 
    } 
    WrappedItemStack wrappedItemStack = new WrappedItemStack(itemStack.copy());
    blockList.add(wrappedItemStack);
    return wrappedItemStack;
  }
  
  public static class WrappedItemStack {
    public ItemStack itemStack;
    
    public int placed;
    
    public int total;
    
    public ArrayList<BlockPos> positions;
    
    public WrappedItemStack(ItemStack itemStack) {
      this(itemStack, 0, 0);
    }
    
    public WrappedItemStack(ItemStack itemStack, int placed, int total) {
      this.itemStack = itemStack;
      this.placed = placed;
      this.total = total;
      this.positions = new ArrayList<>();
    }
    
    public String getItemStackDisplayName() {
      return this.itemStack.getItem().getItemStackDisplayName(this.itemStack);
    }
    
    public String getFormattedAmount() {
      char color = (this.placed < this.total) ? 'c' : 'a';
      return String.format("\u00A7%c%d\u00A7r/%d", color, this.placed, this.total);
    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\clien\\util\BlockList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */