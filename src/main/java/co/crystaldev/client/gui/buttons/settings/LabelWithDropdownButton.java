package co.crystaldev.client.gui.buttons.settings;

import co.crystaldev.client.feature.base.Dropdown;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.gui.ScrollPane;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.FadingColor;
import org.lwjgl.opengl.GL11;

public class LabelWithDropdownButton<T> extends SettingButton<Dropdown<T>> {
    private final FadingColor textColor;

    private final DropdownButton<T> dropdown;

    public LabelWithDropdownButton(int id, int x, int y, int width, int height, String label, String placeholderText, Dropdown<T> dropdown) {
        super(id, x, y, width, height, label, dropdown);
        this.textColor = new FadingColor(this.opts.neutralTextColor, this.opts.hoveredTextColor);
        this.dropdown = new DropdownButton<T>(-1, this.x + this.width / 2 - 2, this.y, this.width / 2, this.height, placeholderText, dropdown) {

        };
    }

    public LabelWithDropdownButton(int id, int x, int y, int width, int height, String label, Dropdown<T> dropdown) {
        this(id, x, y, width, height, label, "No item selected", dropdown);
    }

    public void drawButton(int mouseX, int mouseY, boolean hovered) {
        if (this.dropdown.getScissorPane() == null)
            this.dropdown.setScissorPane(this.scissorPane);
        Screen.scissorStart(this.scissorPane);
        this.textColor.fade((hovered || this.dropdown.isExpanded()));
        RenderUtils.drawRoundedRect(this.x, this.y, (this.x + this.width), (this.y + this.height), 9.0D, this.opts.neutralButtonBackground.getRGB());
        this.fontRenderer.drawString(this.displayText, this.x + 4, this.y + this.height / 2 - this.fontRenderer.getStringHeight() / 2, this.textColor.getCurrentColor().getRGB());
        Screen.scissorEnd(this.scissorPane);
        GL11.glPushMatrix();
        GL11.glTranslated(0.0D, 0.0D, 1.0D);
        this.dropdown.drawButton(mouseX, mouseY, this.dropdown.isHovered(mouseX, mouseY));
        GL11.glPopMatrix();
    }

    public void onUpdate() {
        this.dropdown.y = this.y;
        this.dropdown.onUpdate();
    }

    public void mouseDown(Screen screen, int mouseX, int mouseY, int mouseButton) {
        super.mouseDown(screen, mouseX, mouseY, mouseButton);
        this.dropdown.mouseDown(screen, mouseX, mouseY, mouseButton);
    }

    public void onInteract(int mouseX, int mouseY, int mouseButton) {
        this.dropdown.onInteract(mouseX, mouseY, mouseButton);
        if (this.dropdown.wasUpdated())
            setValue(this.dropdown.getCurrentValue());
    }

    public boolean isHovered(int mouseX, int mouseY) {
        return (super.isHovered(mouseX, mouseY) || this.dropdown.isHovered(mouseX, mouseY));
    }

    public boolean onScroll(ScrollPane pane, int mouseX, int mouseY, int dwheel) {
        return (super.onScroll(pane, mouseX, mouseY, dwheel) || this.dropdown.onScroll(pane, mouseX, mouseY, dwheel));
    }

    public boolean shouldOverlayBeRendered(int mouseX, int mouseY) {
        return (mouseX < this.dropdown.x);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\buttons\settings\LabelWithDropdownButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */