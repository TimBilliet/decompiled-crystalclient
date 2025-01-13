package co.crystaldev.client.gui.buttons;

import co.crystaldev.client.gui.Button;
import co.crystaldev.client.util.ColorUtils;
import co.crystaldev.client.util.RenderUtils;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class SimpleColorPicker extends Button {
    private Color currentColor;

    private int hue;

    public Color getCurrentColor() {
        return this.currentColor;
    }

    private boolean selecting = false;

    private final int colorWidth;

    public SimpleColorPicker(int x, int y, int width, int height) {
        this(x, y, width, height, Color.RED);
    }

    public SimpleColorPicker(int x, int y, int width, int height, Color startingColor) {
        super(-1, x, y, width, height);
        this.currentColor = startingColor;
        this.colorWidth = this.width - 12;
        int[] hsb = ColorUtils.rgbToHsb(this.currentColor.getRGB());
        this.hue = hsb[0];
    }

    public void drawButton(int mouseX, int mouseY, boolean hovered) {
        RenderUtils.resetColor();
        int s = this.colorWidth / 7;
        for (int i = 0; i < 64; i += 8) {
            int color1 = ColorUtils.hsbToRgb(i * 360 / 64, 100, 100) | 0xFF000000;
            int color2 = ColorUtils.hsbToRgb((i + 8) * 360 / 64, 100, 100) | 0xFF000000;
            RenderUtils.drawHorizontalGradientRect(this.x + i / 8 * s, this.y, Math.min(this.x + this.colorWidth, this.x + i / 8 * s + s), this.y + this.height, color1, color2);
        }
        int otherWidth = this.width - this.colorWidth;
        RenderUtils.drawGradientRect(this.x + this.colorWidth, this.y, this.x + this.colorWidth + otherWidth / 2, this.y + this.height, -1L, -1L);
        RenderUtils.drawGradientRect(this.x + this.colorWidth + otherWidth / 2, this.y, this.x + this.width / 2, this.y + this.height, 255L, 255L);
        RenderUtils.setGlColor(getCurrentColor().getRGB() ^ 0xFFFFFF);
        RenderUtils.drawLine(1.5F, this.x + this.width * this.hue / 360.0F, this.y, this.x + this.width * this.hue / 360.0F, (this.y + this.height));
        RenderUtils.drawRoundedBorder(this.x - 0.5D, this.y - 0.5D, (this.x + this.width) + 0.5D, (this.y + this.height) + 0.5D, 4.0D, 1.5F, Color.WHITE
                .getRGB());
        if (hovered && this.selecting)
            if (mouseX >= this.x + this.colorWidth) {
                if (mouseX >= this.x + this.colorWidth + otherWidth / 2) {
                    this.currentColor = new Color(0, 0, 0, 255);
                    this.hue = MathHelper.clamp_int((this.x + this.colorWidth + otherWidth / 2 + otherWidth / 4 - this.x) * 360 / this.width, 0, 360);
                } else {
                    this.currentColor = new Color(255, 255, 255, 255);
                    this.hue = MathHelper.clamp_int((this.x + this.colorWidth + otherWidth / 4 - this.x) * 360 / this.width, 0, 360);
                }
            } else {
                this.hue = MathHelper.clamp_int((mouseX - this.x) * 360 / this.width, 0, 360);
                this.currentColor = new Color(ColorUtils.hsbToRgb(this.hue, 100, 100) | 0xFF000000);
            }
        if (!Mouse.isButtonDown(0) && this.selecting)
            this.selecting = false;
    }

    public void onInteract(int mouseX, int mouseY, int mouseButton) {
        super.onInteract(mouseX, mouseY, mouseButton);
        if (mouseButton == 0)
            this.selecting = true;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\buttons\SimpleColorPicker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */