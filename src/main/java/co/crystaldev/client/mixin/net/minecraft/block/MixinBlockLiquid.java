package co.crystaldev.client.mixin.net.minecraft.block;

import co.crystaldev.client.feature.impl.mechanic.NoLag;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin({BlockLiquid.class})
public abstract class MixinBlockLiquid extends Block {
    public MixinBlockLiquid() {
        super(Material.air, null);
    }

    @Overwrite
    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
    }

    @Inject(method = {"getRenderType"}, cancellable = true, at = {@At("HEAD")})
    public void getRenderType(CallbackInfoReturnable<Integer> cir) {
        if (NoLag.isEnabled((NoLag.getInstance()).disableLiquids))
            cir.setReturnValue(Integer.valueOf(-1));
    }

    public boolean isReplaceable(World world, BlockPos pos) {
        return true;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\block\MixinBlockLiquid.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */