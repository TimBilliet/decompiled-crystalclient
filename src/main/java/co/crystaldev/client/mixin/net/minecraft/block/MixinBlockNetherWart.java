package co.crystaldev.client.mixin.net.minecraft.block;

import co.crystaldev.client.patcher.hook.CropUtilities;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({BlockNetherWart.class})
public abstract class MixinBlockNetherWart extends BlockBush {
//  public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos) {
public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
    CropUtilities.updateWartMaxY(worldIn, pos, worldIn.getBlockState(pos).getBlock());
    return super.getCollisionBoundingBox(worldIn, pos, state);
//    return super.getCollisionBoundingBox(worldIn, pos);
  }
  
  public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 start, Vec3 end) {
    CropUtilities.updateWartMaxY(worldIn, pos, worldIn.getBlockState(pos).getBlock());
    return super.collisionRayTrace(worldIn, pos, start, end);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\block\MixinBlockNetherWart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */