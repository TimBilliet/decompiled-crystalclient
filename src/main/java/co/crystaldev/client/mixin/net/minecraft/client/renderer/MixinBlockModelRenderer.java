package co.crystaldev.client.mixin.net.minecraft.client.renderer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BlockModelRenderer.class})
public abstract class MixinBlockModelRenderer {
    @Inject(method = {"renderModel(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/resources/model/IBakedModel;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/BlockPos;Lnet/minecraft/client/renderer/WorldRenderer;Z)Z"}, cancellable = true, at = {@At(value = "INVOKE", target = "Lnet/minecraft/crash/CrashReport;makeCrashReport(Ljava/lang/Throwable;Ljava/lang/String;)Lnet/minecraft/crash/CrashReport;", shift = At.Shift.BEFORE)})
    private void renderModel(IBlockAccess blockAccessIn, IBakedModel modelIn, IBlockState blockStateIn, BlockPos blockPosIn, WorldRenderer worldRendererIn, boolean checkSides, CallbackInfoReturnable<Boolean> ci) {
        ci.setReturnValue(Boolean.FALSE);
    }
}
