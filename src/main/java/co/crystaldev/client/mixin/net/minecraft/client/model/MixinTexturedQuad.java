package co.crystaldev.client.mixin.net.minecraft.client.model;

import co.crystaldev.client.feature.impl.mechanic.NoLag;
import co.crystaldev.client.mixin.accessor.net.minecraft.client.renderer.MixinWorldRenderer;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({TexturedQuad.class})
public abstract class MixinTexturedQuad {
  @Unique
  private boolean drawOnSelf;
  
  @Redirect(method = {"draw"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldRenderer;begin(ILnet/minecraft/client/renderer/vertex/VertexFormat;)V"))
  private void beginDraw(WorldRenderer renderer, int glMode, VertexFormat format) {
    this.drawOnSelf = !((MixinWorldRenderer)renderer).isDrawing();
    if (this.drawOnSelf || !NoLag.isEnabled((NoLag.getInstance()).batchModelRendering))
      renderer.begin(glMode, DefaultVertexFormats.POSITION_TEX_NORMAL);
  }
  
  @Redirect(method = {"draw"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Tessellator;draw()V"))
  private void endDraw(Tessellator tessellator) {
    if (this.drawOnSelf || !NoLag.isEnabled((NoLag.getInstance()).batchModelRendering))
      tessellator.draw();
  }
}
