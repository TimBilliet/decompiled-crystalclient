package com.github.lunatrius.schematica.client.renderer.chunk.overlay;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;

public class RenderOverlayList extends RenderOverlay {
    private final int displayList = GLAllocation.generateDisplayLists(1);

    public RenderOverlayList(World world, RenderGlobal renderGlobal, BlockPos pos, int index) {
        super(world, renderGlobal, pos, index);
    }

    public int getDisplayList(EnumWorldBlockLayer layer, CompiledChunk compiledChunk) {
        return !compiledChunk.isLayerEmpty(layer) ? this.displayList : -1;
    }

    public void deleteGlResources() {
        super.deleteGlResources();
        GLAllocation.deleteDisplayLists(this.displayList);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\renderer\chunk\overlay\RenderOverlayList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */