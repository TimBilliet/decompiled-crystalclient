package co.crystaldev.client.mixin.accessor.net.minecraft.client.renderer;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({RenderGlobal.class})
public interface MixinRenderGlobal {
    @Accessor("renderDispatcher")
    ChunkRenderDispatcher getRenderDispatcher();
}
