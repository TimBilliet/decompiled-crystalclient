package co.crystaldev.client.gui.buttons.settings;

import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.FadingColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

public class KeybindButton extends SettingButton<KeyBinding> {
    private boolean selecting = false;

    private final FadingColor backgroundColor;

    private final FadingColor textColor;

    private final FadingColor keyColor;

    public boolean isSelecting() {
        return this.selecting;
    }

    public void setSelecting(boolean selecting) {
        this.selecting = selecting;
    }

    public KeybindButton(int id, int x, int y, int width, int height, String displayText, KeyBinding currentValue) {
        super(id, x, y, width, height, displayText, currentValue);
        this.backgroundColor = new FadingColor(this.opts.neutralButtonBackground, this.opts.hoveredButtonBackground);
        this.textColor = new FadingColor(this.opts.neutralTextColor, this.opts.hoveredTextColor);
        this.keyColor = new FadingColor(this.opts.getColor(this.opts.mainColor, 100), this.opts.getColor(this.opts.mainColor, 180));
    }

    public void drawButton(int mouseX, int mouseY, boolean hovered) {
        Screen.scissorStart(this.scissorPane);
        this.backgroundColor.fade(hovered);
        this.textColor.fade(hovered);
        this.keyColor.fade(hovered);
        RenderUtils.drawRoundedRect(this.x, this.y, (this.x + this.width), (this.y + this.height), 9.0D, this.backgroundColor.getCurrentColor().getRGB());
        this.fontRenderer.drawString(this.displayText, this.x + 4, this.y + this.height / 2 - this.fontRenderer.getStringHeight() / 2, this.textColor.getCurrentColor().getRGB());
        String s = this.selecting ? "..." : ((this.currentValue.getKeyCode() == 0) ? "NONE" : GameSettings.getKeyDisplayString(this.currentValue.getKeyCode()));
        int sw = Fonts.NUNITO_SEMI_BOLD_16.getStringWidth(s);
        RenderUtils.drawRoundedRect((this.x + this.width - 9 - sw), (this.y + 3), (this.x + this.width - 3), (this.y + this.height - 3), 6.0D, this.keyColor
                .getCurrentColor().getRGB());
        Fonts.NUNITO_SEMI_BOLD_16.drawCenteredString(s, this.x + this.width - 6 - sw / 2, this.y + this.height / 2, this.textColor
                .getCurrentColor().getRGB());
        Screen.scissorEnd(this.scissorPane);
    }

    public void onInteract(int mouseX, int mouseY, int mouseButton) {
        super.onInteract(mouseX, mouseY, mouseButton);
        if (mouseButton == 0)
            this.selecting = !this.selecting;
    }

    public void mouseDown(Screen currentScreen, int mouseX, int mouseY, int mouseButton) {
        if (this.selecting && mouseButton != 0 && mouseButton != 1) {
            (Minecraft.getMinecraft()).gameSettings.setOptionKeyBinding(this.currentValue, -100 + mouseButton);
            KeyBinding.resetKeyBindingArrayAndHash();
            this.selecting = false;
        }
    }

    public boolean onKeyTyped(char key, int keyCode) {
        if (this.selecting) {
            keyCode = (keyCode == 1) ? 0 : keyCode;
            (Minecraft.getMinecraft()).gameSettings.setOptionKeyBinding(this.currentValue, keyCode);
            KeyBinding.resetKeyBindingArrayAndHash();
            this.selecting = false;
            return false;
        }
        return true;
    }

    public boolean shouldOverlayBeRendered(int mouseX, int mouseY) {
        return !this.selecting;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\buttons\settings\KeybindButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */