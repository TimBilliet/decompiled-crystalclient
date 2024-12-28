package co.crystaldev.client.mixin.net.minecraft.block;

import co.crystaldev.client.feature.impl.mechanic.NoLag;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BlockSlab.class})
public abstract class MixinBlockSlab extends Block {
  public MixinBlockSlab(Material blockMaterialIn, MapColor blockMapColorIn) {
    super(blockMaterialIn, blockMapColorIn);
  }
  
  @Shadow
  protected static boolean isSlab(Block blockIn) {
    return false;
  }
//  protected static boolean func_150003_a(Block blockIn) {
//    return false;
//  }
  
  @Inject(method = {"shouldSideBeRendered"}, cancellable = true, at = {@At("HEAD")})
  private void disableSlabRendering$shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side, CallbackInfoReturnable<Boolean> cir) {
    if (NoLag.isEnabled((NoLag.getInstance()).disableSlabRendering) && isSlab(worldIn.getBlockState(pos).getBlock()) && isSlab(worldIn.getBlockState(pos.up()).getBlock()))
      cir.setReturnValue(shouldSideBeRendered(worldIn, pos, side));
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\block\MixinBlockSlab.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */