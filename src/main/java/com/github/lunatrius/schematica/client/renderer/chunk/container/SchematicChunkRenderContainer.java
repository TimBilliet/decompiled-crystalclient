package com.github.lunatrius.schematica.client.renderer.chunk.container;

import com.github.lunatrius.schematica.client.renderer.chunk.overlay.RenderOverlay;
import com.google.common.collect.Lists;
import net.minecraft.client.renderer.ChunkRenderContainer;

import java.util.List;

public abstract class SchematicChunkRenderContainer extends ChunkRenderContainer {
    protected List<RenderOverlay> renderOverlays = Lists.newArrayListWithCapacity(17424);

    public void initialize(double viewEntityX, double viewEntityY, double viewEntityZ) {
        super.initialize(viewEntityX, viewEntityY, viewEntityZ);
        this.renderOverlays.clear();
    }

    public void addRenderOverlay(RenderOverlay renderOverlay) {
        this.renderOverlays.add(renderOverlay);
    }

    public abstract void renderOverlay();
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\renderer\chunk\container\SchematicChunkRenderContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */