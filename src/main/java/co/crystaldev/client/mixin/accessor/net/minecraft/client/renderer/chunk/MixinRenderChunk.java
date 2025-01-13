package co.crystaldev.client.mixin.accessor.net.minecraft.client.renderer.chunk;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({RenderChunk.class})
public interface MixinRenderChunk {
    @Accessor("world")
    World getWorld();

    @Invoker("preRenderBlocks")
    void callPreRenderBlocks(WorldRenderer paramWorldRenderer, BlockPos paramBlockPos);

    @Invoker("postRenderBlocks")
    void callPostRenderBlocks(EnumWorldBlockLayer paramEnumWorldBlockLayer, float paramFloat1, float paramFloat2, float paramFloat3, WorldRenderer paramWorldRenderer, CompiledChunk paramCompiledChunk);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\accessor\net\minecraft\client\renderer\chunk\MixinRenderChunk.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */