package co.crystaldev.client.feature.base;

import co.crystaldev.client.feature.annotations.properties.Colour;
import co.crystaldev.client.feature.annotations.properties.PageBreak;
import co.crystaldev.client.feature.annotations.properties.Toggle;
import co.crystaldev.client.gui.GuiOptions;
import co.crystaldev.client.util.ColorObject;
import co.crystaldev.client.util.RenderUtils;

import java.awt.*;

public abstract class HudModuleBackground extends HudModuleText {
    @PageBreak(label = "HUD Module Settings")
    @Toggle(label = "Draw Background")
    public boolean drawBackground = true;

    @Toggle(label = "Draw Border")
    public boolean drawBorder = false;

    @Colour(label = "Border Color")
    public ColorObject borderColor = ColorObject.fromColor((GuiOptions.getInstance()).mainColor);

    @Colour(label = "Background Color")
    public ColorObject backgroundColor = new ColorObject(0, 0, 0, 100);

    public void configPostInit() {
        super.configPostInit();
        setOptionVisibility("Draw Border", f -> this.drawBackground);
        setOptionVisibility("Border Color", f -> (this.drawBackground && this.drawBorder));
        setOptionVisibility("Background Color", f -> this.drawBackground);
    }

    public void draw() {
        if (!this.drawBackground) {
            super.draw();
            return;
        }
        String display = this.drawingDefaultText ? getDefaultDisplayText() : getDisplayText();
        if (display != null) {
            int x = getRenderX();
            int y = getRenderY();
            if (this.dynamicSize)
                this.width = this.mc.fontRendererObj.getStringWidth(display) + 6;
            drawBackground(x, y, x + this.width, y + this.height);
            RenderUtils.drawCenteredString(display, x + this.width / 2, y + this.height / 2, this.textColor);
        }
    }

    protected void drawBackground(int x, int y, int x1, int y1) {
        if (this.drawBackground)
            if (this.drawBorder) {
                RenderUtils.drawBorderedRect((x + 1), y, (x1 - 1), y1, 1.0F, this.borderColor, this.backgroundColor);
            } else {
                RenderUtils.drawFastRect(x, y, x1, y1, (Color) this.backgroundColor);
            }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\base\HudModuleBackground.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */