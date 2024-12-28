package co.crystaldev.client.mixin.accessor.net.minecraftforge.client;

import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({GuiIngameForge.class})
public interface MixinGuiIngameForge {
  @Accessor(value = "eventParent", remap = false)
  void setEventParent(RenderGameOverlayEvent paramRenderGameOverlayEvent);
  
  @Accessor(value = "eventParent", remap = false)
  RenderGameOverlayEvent getEventParent();
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\accessor\net\minecraftforge\client\MixinGuiIngameForge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */