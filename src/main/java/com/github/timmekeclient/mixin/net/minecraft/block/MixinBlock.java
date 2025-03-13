package com.github.timmekeclient.mixin.net.minecraft.block;

import com.github.timmekeclient.feature.impl.mechanic.NoLag;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Block.class})
public abstract class MixinBlock {
    @Inject(method = {"shouldSideBeRendered"}, cancellable = true, at = {@At("HEAD")})
    private void shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side, CallbackInfoReturnable<Boolean> cir) {
        if (NoLag.isEnabled((NoLag.getInstance()).disableSlabRendering) && checkSlab(worldIn, pos))
            cir.setReturnValue(Boolean.FALSE);
    }

    private boolean checkSlab(IBlockAccess world, BlockPos pos) {
        return (NoLag.isSlab(world.getBlockState(pos).getBlock()) && NoLag.isSlab(world.getBlockState(pos.up()).getBlock()));
    }
}
