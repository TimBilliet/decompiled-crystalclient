package com.github.lunatrius.schematica.client.renderer.chunk.proxy;

import co.crystaldev.client.mixin.accessor.net.minecraft.client.renderer.chunk.MixinRenderChunk;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.ListedRenderChunk;
import net.minecraft.client.renderer.chunk.SetVisibility;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class SchematicRenderChunkList extends ListedRenderChunk {
    public SchematicRenderChunkList(World world, RenderGlobal renderGlobal, BlockPos pos, int index) {
        super(world, renderGlobal, pos, index);
    }

    public void rebuildChunk(float x, float y, float z, ChunkCompileTaskGenerator generator) {
        generator.getLock().lock();
        try {
            if (generator.getStatus() == ChunkCompileTaskGenerator.Status.COMPILING) {
                BlockPos from = getPosition();
                SchematicWorld schematic = (SchematicWorld) ((MixinRenderChunk) this).getWorld();
                if (from.getX() < 0 || from.getZ() < 0 || from.getX() >= schematic.getWidth() || from.getZ() >= schematic.getLength()) {
                    SetVisibility visibility = new SetVisibility();
                    visibility.setAllVisible(true);
                    CompiledChunk dummy = new CompiledChunk();
                    dummy.setVisibility(visibility);
                    generator.setCompiledChunk(dummy);
                    return;
                }
            }
        } finally {
            generator.getLock().unlock();
        }
        super.rebuildChunk(x, y, z, generator);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\renderer\chunk\proxy\SchematicRenderChunkList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */