package co.crystaldev.client.mixin.net.minecraft.client.gui;

import co.crystaldev.client.Reference;
import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.multiplayer.ServerData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({ServerListEntryNormal.class})
public abstract class MixinServerListEntryNormal {
    @Shadow
    @Final
    private ServerData server;

    @Shadow
    protected abstract void prepareServerIcon();

    @Redirect(method = {"drawEntry"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ServerListEntryNormal;prepareServerIcon()V"))
    private void crashFix(ServerListEntryNormal serverListEntryNormal) {
        try {
            prepareServerIcon();
        } catch (Exception ex) {
            this.server.setBase64EncodedIconData(null);
            Reference.LOGGER.error("Server icon is invalid", ex);
        }
    }
}
