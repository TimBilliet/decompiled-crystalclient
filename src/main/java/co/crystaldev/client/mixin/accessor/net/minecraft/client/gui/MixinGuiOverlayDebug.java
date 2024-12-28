package co.crystaldev.client.mixin.accessor.net.minecraft.client.gui;

import net.minecraft.client.gui.GuiOverlayDebug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin({GuiOverlayDebug.class})
public interface MixinGuiOverlayDebug {
  @Invoker("call")
  List<String> invokeCall();
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\accessor\net\minecraft\client\gui\MixinGuiOverlayDebug.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */