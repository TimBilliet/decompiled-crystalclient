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
    Item item = blockIn.getItem(world, pos);////
    if (item == null)
      return null; 
    Block block = (item instanceof net.minecraft.item.ItemBlock && !blockIn.isFlowerPot()) ? Block.getBlockFromItem(item) : blockIn;
//    return new ItemStack(item, 1, block.func_176222_j(world, pos));
    return new ItemStack(item, 1, block.getDamageValue(world, pos));
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\BlockUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */