package co.crystaldev.client.mixin.net.minecraft.client.gui;

import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({GuiControls.class})
public abstract class MixinGuiControls extends GuiScreen {
    @Shadow
    private GameSettings options;

    public void onGuiClosed() {
        super.onGuiClosed();
        this.options.saveOptions();
    }
}
