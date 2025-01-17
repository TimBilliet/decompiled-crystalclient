package co.crystaldev.client.gui.buttons.settings;

import co.crystaldev.client.Resources;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.gui.buttons.ResourceButton;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.FadingColor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SelectorButton extends SettingButton<String> {
    private final List<String> values;

    protected final FadingColor textColor;

    protected final ResourceButton previous;

    protected final ResourceButton next;

    public SelectorButton(int id, int x, int y, int width, int height, String displayText, String currentValue, String[] values) {
        super(id, x, y, width, height, displayText, currentValue);
        this.values = new LinkedList<>(Arrays.asList(values));
        this.textColor = new FadingColor(this.opts.neutralTextColor, this.opts.hoveredTextColor);
        int boxSize = this.height - 6;
        this.previous = new ResourceButton(-1, this.x + this.width / 2 - boxSize / 2, this.y + this.height / 2 - boxSize / 2, boxSize, boxSize, Resources.CHEVRON_LEFT) {
            {
                setFadingColor(new FadingColor(opts.getColor(opts.mainColor, 100), opts.getColor(opts.mainColor, 180)));
                setIconColor(new FadingColor(opts.neutralTextColor, opts.hoveredTextColor));
                setRadius(6);
            }
        };
        this.next = new ResourceButton(-1, this.x + this.width - 3 - boxSize, this.y + this.height / 2 - boxSize / 2, boxSize, boxSize, Resources.CHEVRON_RIGHT) {
            {
                setFadingColor(new FadingColor(opts.getColor(opts.mainColor, 100), opts.getColor(opts.mainColor, 180)));
                setIconColor(new FadingColor(opts.neutralTextColor, opts.hoveredTextColor));
                setRadius(6);
            }
        };
    }

    public void onUpdate() {
        int boxSize = this.height - 6;
        this.next.x = this.x + this.width - 3 - boxSize;
        this.previous.y = this.next.y = this.y + this.height / 2 - boxSize / 2;
    }

    public void drawButton(int mouseX, int mouseY, boolean hovered) {
        Screen.scissorStart(this.scissorPane);
        this.textColor.fade(hovered);
        RenderUtils.drawRoundedRect(this.x, this.y, (this.x + this.width), (this.y + this.height), 9.0D, this.opts.neutralButtonBackground.getRGB());
        this.fontRenderer.drawString(this.displayText, this.x + 4, this.y + this.height / 2 - this.fontRenderer.getStringHeight() / 2, this.textColor.getCurrentColor().getRGB());
        Fonts.NUNITO_SEMI_BOLD_16.drawCenteredString(this.currentValue, this.x + this.width - this.width / 4, this.y + this.height / 2, this.textColor.getCurrentColor().getRGB());
        this.previous.drawButton(mouseX, mouseY, (this.previous.isHovered(mouseX, mouseY) && hovered));
        this.next.drawButton(mouseX, mouseY, (this.next.isHovered(mouseX, mouseY) && hovered));
        Screen.scissorEnd(this.scissorPane);
    }

    public boolean shouldOverlayBeRendered(int mouseX, int mouseY) {
        return (mouseX < this.x + this.width / 2);
    }

    public void onInteract(int mouseX, int mouseY, int mouseButton) {
        super.onInteract(mouseX, mouseY, mouseButton);
        int index = this.values.indexOf(this.currentValue);
        if (index == -1)
            index = 0;
        if (this.previous.isHovered(mouseX, mouseY)) {
            if (index == 0)
                index = this.values.size();
            setValue(this.values.get(--index));
        } else if (this.next.isHovered(mouseX, mouseY)) {
            if (index == this.values.size() - 1)
                index = -1;
            setValue(this.values.get(++index));
        }
    }
}