package com.github.lunatrius.schematica.client.renderer.chunk.container;

import com.github.lunatrius.schematica.client.renderer.chunk.overlay.RenderOverlay;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.EnumWorldBlockLayer;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class SchematicChunkRenderContainerVbo extends SchematicChunkRenderContainer {
  public void renderChunkLayer(EnumWorldBlockLayer layer) {
    preRenderChunk();
    if (this.initialized) {
      for (RenderChunk renderChunk : this.renderChunks) {
        VertexBuffer vertexbuffer = renderChunk.getVertexBufferByLayer(layer.ordinal());
        GlStateManager.pushMatrix();
        preRenderChunk(renderChunk);
        renderChunk.multModelviewMatrix();
        vertexbuffer.bindBuffer();
        setupArrayPointers();
        vertexbuffer.drawArrays(7);
        GlStateManager.popMatrix();
      } 
      OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
      GlStateManager.resetColor();
      this.renderChunks.clear();
    } 
    postRenderChunk();
  }
  
  private void preRenderChunk() {
    GL11.glEnableClientState(32884);
    OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
    GL11.glEnableClientState(32888);
    OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
    GL11.glEnableClientState(32888);
    OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
    GL11.glEnableClientState(32886);
  }
  
  private void postRenderChunk() {
    List<VertexFormatElement> elements = DefaultVertexFormats.BLOCK.getElements();
    for (VertexFormatElement element : elements) {
      VertexFormatElement.EnumUsage usage = element.getUsage();
      int index = element.getIndex();
      switch (usage) {
        case POSITION:
          GL11.glDisableClientState(32884);
        case UV:
          OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + index);
          GL11.glDisableClientState(32888);
          OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
        case COLOR:
          GL11.glDisableClientState(32886);
          GlStateManager.resetColor();
      } 
    } 
  }
  
  private void setupArrayPointers() {
    GL11.glVertexPointer(3, 5126, 28, 0L);
    GL11.glColorPointer(4, 5121, 28, 12L);
    GL11.glTexCoordPointer(2, 5126, 28, 16L);
    OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
    GL11.glTexCoordPointer(2, 5122, 28, 24L);
    OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
  }
  
  public void renderOverlay() {
    if (this.initialized) {
      preRenderOverlay();
      for (RenderOverlay renderOverlay : this.renderOverlays) {
        VertexBuffer vertexBuffer = renderOverlay.getVertexBufferByLayer(EnumWorldBlockLayer.TRANSLUCENT.ordinal());
        GlStateManager.pushMatrix();
        preRenderChunk((RenderChunk)renderOverlay);
        renderOverlay.multModelviewMatrix();
        vertexBuffer.bindBuffer();
        setupArrayPointersOverlay();
        vertexBuffer.drawArrays(7);
        GlStateManager.popMatrix();
      } 
      OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
      GlStateManager.resetColor();
      this.renderOverlays.clear();
      postRenderOverlay();
    } 
  }
  
  private void preRenderOverlay() {
    GL11.glEnableClientState(32884);
    GL11.glEnableClientState(32886);
  }
  
  private void postRenderOverlay() {
    GL11.glDisableClientState(32886);
    GL11.glDisableClientState(32884);
  }
  
  private void setupArrayPointersOverlay() {
    GL11.glVertexPointer(3, 5126, 16, 0L);
    GL11.glColorPointer(4, 5121, 16, 12L);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\renderer\chunk\container\SchematicChunkRenderContainerVbo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */