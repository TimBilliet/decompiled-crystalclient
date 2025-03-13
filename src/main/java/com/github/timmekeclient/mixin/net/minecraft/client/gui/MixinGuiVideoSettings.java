package com.github.timmekeclient.mixin.net.minecraft.client.gui;

import com.github.timmekeclient.duck.GameSettingsExt;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiVideoSettings;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({GuiVideoSettings.class})
public abstract class MixinGuiVideoSettings extends GuiScreen {
    public void onGuiClosed() {
        super.onGuiClosed();
        ((GameSettingsExt) this.mc.gameSettings).onSettingsGuiClosed();
    }
}
