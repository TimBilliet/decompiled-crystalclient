package co.crystaldev.client.gui.buttons.settings;

import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.FadingColor;

public class SelectorMenuButton extends SelectorButton {
    protected final FadingColor background;

    protected final FadingColor valueTextColor;

    protected boolean enabled = true;

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    protected boolean entireButtonHitBox = false;

    public boolean isEntireButtonHitBox() {
        return this.entireButtonHitBox;
    }

    public void setEntireButtonHitBox(boolean entireButtonHitBox) {
        this.entireButtonHitBox = entireButtonHitBox;
    }

    private Runnable onClick = null;

    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }

    public SelectorMenuButton(int id, int x, int y, int width, int height, String displayText, String currentValue, String[] values) {
        super(id, x, y, width, height, displayText, currentValue, values);
        this.background = new FadingColor(this.opts.neutralButtonBackground, this.opts.hoveredButtonBackground);
        this.valueTextColor = new FadingColor(this.opts.neutralTextColor, this.opts.hoveredTextColor);
    }

    public void drawButton(int mouseX, int mouseY, boolean hovered) {
        Screen.scissorStart(this.scissorPane);
        hovered = (hovered && this.enabled);
        boolean menuButtonHovered = (hovered && this.enabled && (this.entireButtonHitBox || mouseX <= this.x + this.width / 2) && !this.previous.isHovered(mouseX, mouseY) && !this.next.isHovered(mouseX, mouseY));
        this.background.fade(menuButtonHovered);
        this.textColor.fade(menuButtonHovered);
        this.valueTextColor.fade(hovered);
        RenderUtils.drawRoundedRect(this.x, this.y, (this.x + this.width), (this.y + this.height), 9.0D, this.background.getCurrentColor().getRGB());
        this.fontRenderer.drawString(this.displayText, this.x + 4, this.y + this.height / 2 - this.fontRenderer.getStringHeight() / 2, this.textColor.getCurrentColor().getRGB());
        Fonts.NUNITO_SEMI_BOLD_16.drawCenteredString(this.currentValue, this.x + this.width - this.width / 4, this.y + this.height / 2, this.valueTextColor.getCurrentColor().getRGB());
        this.previous.drawButton(mouseX, mouseY, (this.previous.isHovered(mouseX, mouseY) && hovered && this.enabled));
        this.next.drawButton(mouseX, mouseY, (this.next.isHovered(mouseX, mouseY) && hovered && this.enabled));
        Screen.scissorEnd(this.scissorPane);
    }

    public void onInteract(int mouseX, int mouseY, int mouseButton) {
        if (!this.enabled)
            return;
        if ((this.entireButtonHitBox || mouseX <= this.x + this.width / 2) && !this.previous.isHovered(mouseX, mouseY) && !this.next.isHovered(mouseX, mouseY)) {
            if (this.onClick != null)
                this.onClick.run();
        } else {
            super.onInteract(mouseX, mouseY, mouseButton);
        }
    }
}
