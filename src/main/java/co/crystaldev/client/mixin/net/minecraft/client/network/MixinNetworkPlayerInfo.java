package co.crystaldev.client.mixin.net.minecraft.client.network;

import co.crystaldev.client.Resources;
import co.crystaldev.client.command.ThumbnailCommand;
import co.crystaldev.client.duck.NetworkPlayerInfoExt;
import co.crystaldev.client.handler.PlayerHandler;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({NetworkPlayerInfo.class})
public abstract class MixinNetworkPlayerInfo implements NetworkPlayerInfoExt {
    @Shadow
    @Final
    private GameProfile gameProfile;

    @Unique
    private boolean crystal$onlineStatus = false;

    @Inject(method = {"getSkinType"}, cancellable = true, at = {@At("HEAD")})
    private void getSkinType(CallbackInfoReturnable<String> ci) {
        if (ThumbnailCommand.isRendering())
            ci.setReturnValue("default");
    }

    @Inject(method = {"getLocationSkin"}, cancellable = true, at = {@At("HEAD")})
    private void getLocationSkin(CallbackInfoReturnable<ResourceLocation> ci) {
        if (ThumbnailCommand.isRendering())
            ci.setReturnValue(Resources.DEFAULT_SKIN);
    }

    @Inject(method = {"getLocationCape"}, cancellable = true, at = {@At("HEAD")})
    private void getLocationCape(CallbackInfoReturnable<ResourceLocation> ci) {
        if (ThumbnailCommand.isRendering())
            ci.setReturnValue(null);
    }

    @Inject(method = {"<init>(Lcom/mojang/authlib/GameProfile;)V"}, at = {@At("RETURN")})
    public void constructor$1(GameProfile p_i46294_1_, CallbackInfo ci) {
        setOnlineStatus(PlayerHandler.getInstance().getOnlineUsers().contains(this.gameProfile.getId()));
    }

    @Inject(method = {"<init>(Lnet/minecraft/network/play/server/S38PacketPlayerListItem$AddPlayerData;)V"}, at = {@At("RETURN")})
    public void constructor$2(S38PacketPlayerListItem.AddPlayerData p_i46295_1_, CallbackInfo ci) {
        setOnlineStatus(PlayerHandler.getInstance().getOnlineUsers().contains(this.gameProfile.getId()));
    }

    public void setOnlineStatus(boolean online) {
        this.crystal$onlineStatus = online;
    }

    public boolean isOnCrystalClient() {
        return this.crystal$onlineStatus;
    }
}
