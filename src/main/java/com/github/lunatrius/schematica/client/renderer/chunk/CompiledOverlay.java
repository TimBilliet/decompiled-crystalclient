package com.github.lunatrius.schematica.client.renderer.chunk;

import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.util.EnumWorldBlockLayer;

public class CompiledOverlay extends CompiledChunk {
  public void setLayerStarted(EnumWorldBlockLayer layer) {
    if (layer == EnumWorldBlockLayer.TRANSLUCENT)
      super.setLayerStarted(layer); 
  }
  
  public void setLayerUsed(EnumWorldBlockLayer layer) {
    if (layer == EnumWorldBlockLayer.TRANSLUCENT)
      super.setLayerUsed(layer); 
  }
  
  public boolean isLayerStarted(EnumWorldBlockLayer layer) {
    return (layer == EnumWorldBlockLayer.TRANSLUCENT && super.isLayerStarted(layer));
  }
  
  public boolean isLayerEmpty(EnumWorldBlockLayer layer) {
    return (layer == EnumWorldBlockLayer.TRANSLUCENT && super.isLayerEmpty(layer));
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\renderer\chunk\CompiledOverlay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */