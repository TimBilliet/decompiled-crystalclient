package co.crystaldev.client.mixin.accessor.net.minecraft.client.renderer;

import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({ViewFrustum.class})
public interface MixinViewFrustum {
    @Invoker("getRenderChunk")
    RenderChunk callGetRenderChunk(BlockPos paramBlockPos);
}
