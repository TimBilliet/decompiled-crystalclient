package co.crystaldev.client.mixin.net.minecraft.block;

import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin({BlockRedstoneDiode.class})
public abstract class MixinBlockRedstoneDiode {
  @Overwrite
  public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
    return (side != EnumFacing.DOWN);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\block\MixinBlockRedstoneDiode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */