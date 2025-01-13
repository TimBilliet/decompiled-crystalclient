package co.crystaldev.client.gui.buttons;

import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.enums.ChatColor;
import co.crystaldev.client.util.objects.FadingColor;

import java.awt.*;

public class MenuButton extends Button {
    protected FadingColor fadingColor;

    protected FadingColor textColor;

    public FadingColor getFadingColor() {
        return this.fadingColor;
    }

    public void setFadingColor(FadingColor fadingColor) {
        this.fadingColor = fadingColor;
    }

    public FadingColor getTextColor() {
        return this.textColor;
    }

    public void setTextColor(FadingColor textColor) {
        this.textColor = textColor;
    }

    protected boolean selected = false;

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    protected boolean drawBackground = true;

    public void setDrawBackground(boolean drawBackground) {
        this.drawBackground = drawBackground;
    }

    protected boolean useMinecraftFR = false;

    public void setUseMinecraftFR(boolean useMinecraftFR) {
        this.useMinecraftFR = useMinecraftFR;
    }

    protected boolean enabled = true;

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Runnable onClick = null;

    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }

    protected long outlineColor = -1;

    public void setOutlineColor(long outlineColor) {
        this.outlineColor = outlineColor;
    }

    public MenuButton(int id, int x, int y, int width, int height, String displayText) {
        super(id, x, y, width, height, displayText);
        this.fontRenderer = Fonts.NUNITO_SEMI_BOLD_20;
        this.fadingColor = new FadingColor(this.opts.neutralButtonBackground, this.opts.hoveredButtonBackground);
        this.textColor = new FadingColor(this.opts.neutralTextColor, this.opts.hoveredTextColor);
    }

    public MenuButton(int id, int x, int y, int width, int height, String displayText, long outline) {
        this(id, x, y, width, height, displayText);
        this.outlineColor = outline;
    }

    public void drawButton(int mouseX, int mouseY, boolean hovered) {
        Screen.scissorStart(this.scissorPane);
        hovered = (this.enabled && hovered);
        this.fadingColor.fade(hovered);
        this.textColor.fade((hovered || this.selected));
        if (this.drawBackground)
            if (this.outlineColor == -1) {
                RenderUtils.drawRoundedRect(this.x, this.y, (this.x + this.width), (this.y + this.height), 9.0D, this.fadingColor
                        .getCurrentColor().getRGB());
            } else {
                RenderUtils.drawRoundedRectWithBorder(this.x, this.y, (this.x + this.width), (this.y + this.height), 9.0D, 2.0F, this.outlineColor, this.fadingColor
                        .getCurrentColor().getRGB());
            }
        if (this.useMinecraftFR) {
            RenderUtils.drawCenteredString(ChatColor.translate(this.displayText), this.x + this.width / 2, this.y + this.height / 2, this.textColor
                    .getCurrentColor().getRGB());
        } else {
            this.fontRenderer.drawCenteredString(ChatColor.translate(this.displayText), this.x + this.width / 2, this.y + this.height / 2, this.textColor
                    .getCurrentColor().getRGB());
        }
        Screen.scissorEnd(this.scissorPane);
    }

    public void onInteract(int mouseX, int mouseY, int mouseButton) {
        if (this.onClick != null && this.enabled)
            this.onClick.run();
    }

    public Color getBackgroundColor() {
        return this.fadingColor.getCurrentColor();
    }

    public void setBackgroundColor(Color color) {
        this.fadingColor = new FadingColor(color, 100, 175);
    }

    public void setMinAlpha(int minAlpha) {
        this.fadingColor.setMinAlpha(minAlpha);
    }

    public void setMaxAlpha(int maxAlpha) {
        this.fadingColor.setMaxAlpha(maxAlpha);
    }
}
