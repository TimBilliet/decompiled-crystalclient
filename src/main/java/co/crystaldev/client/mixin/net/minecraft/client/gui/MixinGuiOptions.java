package co.crystaldev.client.mixin.net.minecraft.client.gui;

import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({GuiOptions.class})
public abstract class MixinGuiOptions extends GuiScreen {
    public void onGuiClosed() {
        this.mc.gameSettings.saveOptions();
    }
}
