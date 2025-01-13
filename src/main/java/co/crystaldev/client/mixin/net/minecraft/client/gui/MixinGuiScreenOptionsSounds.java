package co.crystaldev.client.mixin.net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = {"net.minecraft.client.gui.GuiScreenOptionsSounds$Button"})
public abstract class MixinGuiScreenOptionsSounds {
    @Redirect(method = {"mouseDragged(Lnet/minecraft/client/Minecraft;II)V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/GameSettings;saveOptions()V"))
    private void cancelSaving(GameSettings instance) {
    }

    @Inject(method = {"mouseReleased(II)V"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/audio/SoundHandler;playSound(Lnet/minecraft/client/audio/ISound;)V")})
    private void saveOptions(int mouseX, int mouseY, CallbackInfo ci) {
        (Minecraft.getMinecraft()).gameSettings.saveOptions();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\gui\MixinGuiScreenOptionsSounds.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */