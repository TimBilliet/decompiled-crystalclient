package com.github.timmekeclient.gui.buttons;

import com.github.timmekeclient.font.FontRenderer;
import com.github.timmekeclient.font.Fonts;
import com.github.timmekeclient.gui.Button;
import com.github.timmekeclient.gui.Screen;
import com.github.timmekeclient.util.RenderUtils;
import org.lwjgl.opengl.GL11;

public class Divider extends Button {
    private final int color;

    private final int lineColor;

    public Divider(int x, int y, String text, FontRenderer fontRenderer) {
        super(-1, x, y, 0, 0, text);
        this.fontRenderer = fontRenderer;
        this.color = this.opts.hoveredTextColor.getRGB();
        this.lineColor = this.opts.mainColor.getRGB();
    }

    public Divider(int x, int y, String text) {
        this(x, y, text, Fonts.NUNITO_REGULAR_20);
    }

    public void drawButton(int mouseX, int mouseY, boolean hovered) {
        Screen.scissorStart(this.scissorPane);
        this.fontRenderer.drawStringWithShadow(this.displayText, this.x + 4, this.y, this.color);
        int y = this.y + this.fontRenderer.getStringHeight() + 4;
        RenderUtils.glColor(this.lineColor);
        RenderUtils.drawLine(2.0F, (this.x + 4), y, (this.x + 20), y);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Screen.scissorEnd(this.scissorPane);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\buttons\Divider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */