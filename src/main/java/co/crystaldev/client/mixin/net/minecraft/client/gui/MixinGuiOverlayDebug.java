package co.crystaldev.client.mixin.net.minecraft.client.gui;

import co.crystaldev.client.Client;
import co.crystaldev.client.handler.ModuleHandler;
import co.crystaldev.client.mixin.accessor.net.minecraft.world.MixinWorld;
import co.crystaldev.client.util.enums.ChatColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiOverlayDebug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin({GuiOverlayDebug.class})
public abstract class MixinGuiOverlayDebug {
    @Shadow
    @Final
    private Minecraft mc;

    @Redirect(method = {"renderDebugInfoLeft"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiOverlayDebug;call()Ljava/util/List;"))
    private List<String> renderDebugInfoLeft(GuiOverlayDebug instance) {
        List<String> values = ((co.crystaldev.client.mixin.accessor.net.minecraft.client.gui.MixinGuiOverlayDebug) instance).invokeCall();
        values.add("");
        values.add(ChatColor.translate(String.format("&b%s Debug Info", "Crystal Client")));
        values.add("Client Version: " + Client.getMinecraftVersion().getVersionString() + "/" + "1.1.16-projectassfucker");
        values.add("Client brand: " + this.mc.thePlayer.getClientBrand());
        values.add("Server TPS: " + String.format("%.2f", ModuleHandler.getTps()));
        values.add("");
        values.add("World name: " + Client.getCurrentWorldName());
        values.add("Tile Entity Count (Loaded): " + this.mc.theWorld.loadedTileEntityList.size());
        values.add("Tile Entity Count (Tickable): " + this.mc.theWorld.tickableTileEntities.size());
        values.add("Tile Entity Count (For removal): " + ((MixinWorld) this.mc.theWorld).getTileEntitiesToBeRemoved().size());
        return values;
    }
}