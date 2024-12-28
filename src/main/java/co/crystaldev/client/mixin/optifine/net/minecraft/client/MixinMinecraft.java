package co.crystaldev.client.mixin.optifine.net.minecraft.client;

import co.crystaldev.client.feature.impl.all.Zoom;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({Minecraft.class})
public abstract class MixinMinecraft {
  @Redirect(method = {"runTick"}, at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;getEventDWheel()I"))
  private int cancelScrollForZoom() {
    int wheel = Mouse.getEventDWheel();
    if (Zoom.getInstance() != null && (Zoom.getInstance()).scrollToZoom && (Zoom.getInstance()).zoomed)
      return 0;
    return wheel;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\optifine\net\minecraft\client\MixinMinecraft.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */