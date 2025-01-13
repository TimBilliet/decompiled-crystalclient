package co.crystaldev.client.mixin.optifine.net.minecraft.client.gui;

import net.minecraft.client.gui.GuiOverlayDebug;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin({GuiOverlayDebug.class})
public abstract class MixinGuiOverlayDebug {
    @ModifyArg(method = {"call"}, at = @At(remap = false, value = "INVOKE", target = "Ljava/lang/StringBuffer;insert(ILjava/lang/String;)Ljava/lang/StringBuffer;"))
    @Dynamic("OptiFine")
    private String simplifyFpsCounter(String original) {
        return original.startsWith("/") ? "" : original;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\optifine\net\minecraft\client\gui\MixinGuiOverlayDebug.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */