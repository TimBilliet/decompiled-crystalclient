package co.crystaldev.client.gui.screens.screen_overlay;

import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Pane;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.util.RenderUtils;

public class ScreenOverlay extends Screen {
    protected final String headerText;

    protected boolean dimBackground = true;

    public boolean isDimBackground() {
        return this.dimBackground;
    }

    public void setDimBackground(boolean dimBackground) {
        this.dimBackground = dimBackground;
    }

    public ScreenOverlay(int x, int y, int w, int h, String headerText) {
        this.pane = new Pane(x, y, w, h);
        this.headerText = headerText;
        this.overlay = true;
    }

    public void draw(int mouseX, int mouseY, float partialTicks) {
        RenderUtils.drawRoundedRect(this.pane.x, this.pane.y, (this.pane.x + this.pane.width), (this.pane.y + this.pane.height), 14.0D, this.opts.backgroundColor
                .getRGB());
        RenderUtils.drawRoundedRect(this.pane.x, this.pane.y, (this.pane.x + this.pane.width), (this.pane.y + this.pane.height), 14.0D, this.opts.backgroundColor1
                .getRGB());
        Fonts.SOURCE_SANS_20.drawCenteredString(this.headerText, this.pane.x + this.pane.width / 2, this.pane.y + 6 + Fonts.SOURCE_SANS_20
                .getStringHeight() / 2, 16777215);
    }

    public void closeOverlay() {
        if (this.mc.currentScreen instanceof Screen) {
            Screen screen = (Screen) this.mc.currentScreen;
            screen.removeOverlay(this);
        }
    }

    public void center() {
        int newX, newY;
        if (!(this.mc.currentScreen instanceof Screen)) {
            newX = this.mc.displayWidth / 4 - this.pane.width / 2;
            newY = this.mc.displayHeight / 4 - this.pane.height / 2;
        } else {
            Pane pane = ((Screen) this.mc.currentScreen).pane;
            newX = pane.x + pane.width / 2 - this.pane.width / 2;
            newY = pane.y + pane.height / 2 - this.pane.height / 2;
        }
        int diffX = newX - this.pane.x, diffY = newY - this.pane.y;
        for (Button button : this.buttons) {
            button.x += diffX;
            button.y += diffY;
        }
        this.pane.x = newX;
        this.pane.y = newY;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\screens\screen_overlay\ScreenOverlay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */