package co.crystaldev.client.mixin.net.minecraft.client.renderer.chunk;

import net.minecraft.client.renderer.chunk.SetVisibility;
import net.minecraft.client.renderer.chunk.VisGraph;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({VisGraph.class})
public abstract class MixinVisGraph {
  @Shadow
  private int field_178611_f;
  
  @Inject(method = {"computeVisibility"}, cancellable = true, at = {@At("HEAD")})
  public void computeVisibility(CallbackInfoReturnable<SetVisibility> ci) {
    if (4097 - this.field_178611_f < 256) {
      SetVisibility setVisibility = new SetVisibility();
      setVisibility.setAllVisible(true);
      ci.setReturnValue(setVisibility);
    } 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\renderer\chunk\MixinVisGraph.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */