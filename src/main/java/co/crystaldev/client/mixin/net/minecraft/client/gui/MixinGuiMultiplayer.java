package co.crystaldev.client.mixin.net.minecraft.client.gui;

import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiMultiplayer.class})
public abstract class MixinGuiMultiplayer extends GuiScreen {
    @Inject(method = {"connectToServer"}, at = {@At("HEAD")})
    private void connectToServer(ServerData server, CallbackInfo ci) {
        if (this.mc.theWorld != null)
            this.mc.theWorld.sendQuittingDisconnectingPacket();
    }
}
