package co.crystaldev.client.mixin.net.minecraft.client.gui;

import co.crystaldev.client.Client;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiDisconnected.class})
public abstract class MixinGuiDisconnected {
    @Inject(method = {"<init>"}, at = {@At("RETURN")})
    public void constructor(GuiScreen screen, String reasonLocalizationKey, IChatComponent chatComp, CallbackInfo ci) {
        Client.setCurrentServerIp(null);
    }
}

