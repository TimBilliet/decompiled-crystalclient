package com.github.lunatrius.schematica.client.renderer.chunk.overlay;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public interface ISchematicRenderChunkFactory extends IRenderChunkFactory {
  RenderOverlay makeRenderOverlay(World paramWorld, RenderGlobal paramRenderGlobal, BlockPos paramBlockPos, int paramInt);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\renderer\chunk\overlay\ISchematicRenderChunkFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */