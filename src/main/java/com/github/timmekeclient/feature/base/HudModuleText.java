package com.github.timmekeclient.feature.base;

import com.github.timmekeclient.feature.annotations.Hidden;
import com.github.timmekeclient.feature.annotations.properties.Colour;
import com.github.timmekeclient.feature.annotations.properties.Toggle;
import com.github.timmekeclient.util.ColorObject;
import com.github.timmekeclient.util.RenderUtils;
import com.github.timmekeclient.util.type.Tuple;

public abstract class HudModuleText extends HudModule {
    @Colour(label = "Text Color", isTextRender = true)
    public ColorObject textColor = new ColorObject(255, 255, 255, 255);

    @Toggle(label = "Info Hud Enabled")
    @Hidden
    public boolean infoHudEnabled = true;

    protected boolean dynamicSize = false;

    public boolean awaitingInfoHudRender = false;

    public boolean drawingDefaultText = false;

    public boolean hasInfoHud = false;

    public abstract String getDisplayText();

    public String getDefaultDisplayText() {
        return getDisplayText();
    }

    public Tuple<String, String> getInfoHud() {
        return null;
    }

    public void draw() {
        String display = this.drawingDefaultText ? getDefaultDisplayText() : getDisplayText();
        if (display != null && !display.isEmpty()) {
            display = display.trim();
            display = (!display.startsWith("[") ? "[" : "") + display + (!display.endsWith("]") ? "]" : "");
            if (this.dynamicSize)
                this.width = this.mc.fontRendererObj.getStringWidth(display);
            RenderUtils.drawCenteredString(display, getRenderX() + this.width / 2, getRenderY() + this.height / 2, this.textColor);
        }
    }
}
