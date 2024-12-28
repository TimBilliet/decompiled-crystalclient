package co.crystaldev.client.util;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class TileEntityUtils {
  public static AxisAlignedBB getRenderBoundingBox(TileEntity tileEntityIn) {
    AxisAlignedBB bb = BoundingBox.INFINITE_EXTENT_AABB;
    BlockPos pos = tileEntityIn.getPos();
    Block type = tileEntityIn.getBlockType();
    if (type == Blocks.enchanting_table) {
      bb = new AxisAlignedBB(pos, pos.add(1, 1, 1));
    } else if (type == Blocks.chest || type == Blocks.trapped_chest) {
      bb = new AxisAlignedBB(pos.add(-1, 0, -1), pos.add(2, 2, 2));
    } else if (type != null && type != Blocks.beacon) {
      AxisAlignedBB cbb;
      try {
//        cbb = type.getSelectedBoundingBox(tileEntityIn.getWorld(), pos, tileEntityIn.getWorld().getBlockState(pos));
        cbb = type.getSelectedBoundingBox(tileEntityIn.getWorld(), pos);
//        type.getCollisionBoundingBox()//mss
      } catch (Exception ex) {
        cbb = new AxisAlignedBB(pos.add(-1, 0, -1), pos.add(1, 1, 1));
      }
      if (cbb != null)
        bb = cbb;
    }
    return bb;
  }

  public static boolean shouldRenderInPass(int pass) {
    return (pass == 0);
  }
}