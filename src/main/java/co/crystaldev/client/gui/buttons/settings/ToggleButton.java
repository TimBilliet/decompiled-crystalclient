package co.crystaldev.client.gui.buttons.settings;

import co.crystaldev.client.Resources;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.FadingColor;

public class ToggleButton extends SettingButton<Boolean> {
    protected final FadingColor backgroundColor;

    protected final FadingColor textColor;

    protected final FadingColor stateColor;

    protected final FadingColor glyphColor;

    protected boolean enabled = true;

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ToggleButton(int id, int x, int y, int width, int height, String displayText, boolean state) {
        super(id, x, y, width, height, displayText, state);
        this.backgroundColor = new FadingColor(this.opts.neutralButtonBackground, this.opts.hoveredButtonBackground);
        this.textColor = new FadingColor(this.opts.neutralTextColor, this.opts.hoveredTextColor);
        this.stateColor = new FadingColor(this.opts.neutralButtonBackground, this.opts.getColor(this.opts.mainColor, 180));
        this.glyphColor = new FadingColor(this.opts.getColor(this.opts.hoveredTextColor, 0), this.opts.getColor(this.opts.hoveredTextColor, 140));
    }

    public void drawButton(int mouseX, int mouseY, boolean hovered) {
        Screen.scissorStart(this.scissorPane);
        hovered = (hovered && this.enabled);
        this.backgroundColor.fade(hovered);
        this.textColor.fade(hovered);
        this.stateColor.fade(this.currentValue);
        this.glyphColor.fade(this.currentValue);
        RenderUtils.drawRoundedRect(this.x, this.y, (this.x + this.width), (this.y + this.height), 9.0D, this.backgroundColor.getCurrentColor().getRGB());
        this.fontRenderer.drawString(this.displayText, this.x + 4, this.y + this.height / 2 - this.fontRenderer.getStringHeight() / 2, this.textColor.getCurrentColor().getRGB());
        int boxSize = this.height - 6;
        RenderUtils.drawRoundedRect((this.x + this.width - 3 - boxSize), (this.y + 3), (this.x + this.width - 3), (this.y + this.height - 3), 6.0D, this.stateColor
                .getCurrentColor().getRGB());
        RenderUtils.setGlColor(this.glyphColor.getCurrentColor());
        RenderUtils.drawCustomSizedResource(Resources.CHECK, this.x + this.width - 1 - boxSize, this.y + 5, boxSize - 4, boxSize - 4);
        RenderUtils.resetColor();
        Screen.scissorEnd(this.scissorPane);
    }

    public void onInteract(int mouseX, int mouseY, int mouseButton) {
        super.onInteract(mouseX, mouseY, mouseButton);
        if (this.enabled)
            setValue(!this.currentValue);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\buttons\settings\ToggleButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */