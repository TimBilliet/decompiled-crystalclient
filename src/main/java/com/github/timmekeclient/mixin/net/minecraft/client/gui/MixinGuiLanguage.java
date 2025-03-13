package com.github.timmekeclient.mixin.net.minecraft.client.gui;

import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({GuiLanguage.class})
public abstract class MixinGuiLanguage extends GuiScreen {
    public void onGuiClosed() {
        this.mc.ingameGUI.getChatGUI().refreshChat();
    }
}
