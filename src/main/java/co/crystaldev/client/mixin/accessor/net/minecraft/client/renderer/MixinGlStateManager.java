package co.crystaldev.client.mixin.accessor.net.minecraft.client.renderer;

import net.minecraft.client.renderer.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({GlStateManager.class})
public interface MixinGlStateManager {
  @Accessor("activeTextureUnit")
  static int getActiveTextureUnit() {
    throw new UnsupportedOperationException("Mixin failed to inject!");
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\accessor\net\minecraft\client\renderer\MixinGlStateManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */