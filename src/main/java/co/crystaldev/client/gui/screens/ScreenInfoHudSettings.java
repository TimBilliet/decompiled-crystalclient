package co.crystaldev.client.gui.screens;

import co.crystaldev.client.feature.base.Module;
import co.crystaldev.client.feature.impl.hud.InfoHud;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Pane;
import co.crystaldev.client.gui.buttons.InfoHudButton;
import co.crystaldev.client.gui.buttons.Label;
import co.crystaldev.client.util.enums.ChatColor;
import net.minecraft.client.gui.GuiScreen;

public class ScreenInfoHudSettings extends ScreenSettings {
    public ScreenInfoHudSettings(GuiScreen parent) {
        super(InfoHud.getInstance(), parent);
    }

    public void initSettings() {
        removeButton(b -> b.hasAttribute("config_option"));
        this.x = this.content.x + 14;
        this.x1 = this.content.x + this.content.width / 2 + 7;
        this.y = this.content.y + 5;
        this.w = this.content.width - 28;
        this.w1 = this.w / 2 - 7;
        this.h = 18;
        Pane scissor = this.content.scale(getScaledScreen());
        addButton((Button) new InfoHudButton(this.x, this.y, this.w), b -> {
            b.addAttribute("config_option");
            b.setScissorPane(scissor);
            this.y += b.height + 4;
        });
        addButton((Button) new Label(this.x + this.w / 2, this.y + this.h / 2, ChatColor.translate("&lHINT:&r Drag module to reorder, right-click to toggle Info HUD"), 16777215, Fonts.NUNITO_SEMI_BOLD_16), b -> {
            b.addAttribute("config_option");
            b.setScissorPane(scissor);
        });
        this.y += this.h + 4;
        addSettingsButtons();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\screens\ScreenInfoHudSettings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */