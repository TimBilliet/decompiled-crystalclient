package com.github.lunatrius.schematica.client.renderer.chunk.container;

import com.github.lunatrius.schematica.client.renderer.chunk.overlay.RenderOverlay;
import com.github.lunatrius.schematica.client.renderer.chunk.overlay.RenderOverlayList;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.chunk.ListedRenderChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.EnumWorldBlockLayer;
import org.lwjgl.opengl.GL11;

public class SchematicChunkRenderContainerList extends SchematicChunkRenderContainer {
    public void renderChunkLayer(EnumWorldBlockLayer layer) {
        if (this.initialized) {
            for (RenderChunk renderchunk : this.renderChunks) {
                ListedRenderChunk listedRenderChunk = (ListedRenderChunk) renderchunk;
                GlStateManager.pushMatrix();
                preRenderChunk(renderchunk);
                GL11.glCallList(listedRenderChunk.getDisplayList(layer, listedRenderChunk.getCompiledChunk()));
                GlStateManager.popMatrix();
            }
            GlStateManager.resetColor();
            this.renderChunks.clear();
        }
    }

    public void renderOverlay() {
        if (this.initialized)
            for (RenderOverlay renderOverlay : this.renderOverlays) {
                RenderOverlayList renderOverlayList = (RenderOverlayList) renderOverlay;
                GlStateManager.pushMatrix();
                preRenderChunk((RenderChunk) renderOverlay);
                GL11.glCallList(renderOverlayList.getDisplayList(EnumWorldBlockLayer.TRANSLUCENT, renderOverlayList.getCompiledChunk()));
                GlStateManager.popMatrix();
            }
        GlStateManager.resetColor();
        this.renderOverlays.clear();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\renderer\chunk\container\SchematicChunkRenderContainerList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */