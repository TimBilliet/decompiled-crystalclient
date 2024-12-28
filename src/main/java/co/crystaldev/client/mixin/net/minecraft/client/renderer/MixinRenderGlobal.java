package co.crystaldev.client.mixin.net.minecraft.client.renderer;

import co.crystaldev.client.duck.RenderGlobalExt;
import net.minecraft.client.renderer.RenderGlobal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({RenderGlobal.class})
public abstract class MixinRenderGlobal implements RenderGlobalExt {
  @Shadow
  private int countEntitiesRendered;

  @Shadow
  private int countEntitiesTotal;

  public String getHudEntityCount() {
    return String.format("%s/%s", this.countEntitiesRendered, this.countEntitiesTotal);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\renderer\MixinRenderGlobal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */