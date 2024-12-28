package co.crystaldev.client.mixin.accessor.net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin({RenderManager.class})
public interface MixinRenderManager {
  @Accessor("skinMap")
  Map<String, RenderPlayer> getSkinMap();
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\accessor\net\minecraft\client\renderer\entity\MixinRenderManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */