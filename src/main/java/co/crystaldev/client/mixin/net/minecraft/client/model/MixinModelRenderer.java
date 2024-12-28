package co.crystaldev.client.mixin.net.minecraft.client.model;

import co.crystaldev.client.feature.impl.mechanic.NoLag;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ModelRenderer.class})
public abstract class MixinModelRenderer {
  @Shadow
  private boolean compiled;
  
  @Unique
  private boolean compiledState;
  
  @Inject(method = {"render"}, at = {@At("HEAD")})
  private void resetCompiled(float j, CallbackInfo ci) {
    if (this.compiledState != NoLag.isEnabled((NoLag.getInstance()).batchModelRendering))
      this.compiled = false;
  }
  
  @Inject(method = {"compileDisplayList"}, at = {@At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/renderer/Tessellator;getWorldRenderer()Lnet/minecraft/client/renderer/WorldRenderer;")})
  private void beginRendering(CallbackInfo ci) {
    this.compiledState = NoLag.isEnabled((NoLag.getInstance()).batchModelRendering);
    if (this.compiledState)
      Tessellator.getInstance().getWorldRenderer().begin(7, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);
  }
  
  @Inject(method = {"compileDisplayList"}, at = {@At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glEndList()V", remap = false)})
  private void draw(CallbackInfo ci) {
    if (NoLag.isEnabled((NoLag.getInstance()).batchModelRendering))
      Tessellator.getInstance().draw();
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\model\MixinModelRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */