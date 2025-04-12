package com.github.timmekeclient.mixin.net.minecraft.client.gui;

import com.github.timmekeclient.Client;
import com.github.timmekeclient.handler.ModuleHandler;
import com.github.timmekeclient.mixin.accessor.net.minecraft.world.MixinWorld;
import com.github.timmekeclient.util.enums.ChatColor;
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
        List<String> values = ((com.github.timmekeclient.mixin.accessor.net.minecraft.client.gui.MixinGuiOverlayDebug) instance).invokeCall();
        values.add("");
        values.add(ChatColor.translate(String.format("&b%s Debug Info", "Timmeke_ Client")));
        values.add("Client Version: " + Client.getMinecraftVersion().getVersionString() + "/" + "1.4.0");
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