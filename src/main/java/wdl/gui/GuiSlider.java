package wdl.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.MathHelper;

class GuiSlider extends GuiButton {
    private float sliderValue;

    private boolean dragging;

    private final String text;

    private final int max;

    public GuiSlider(int id, int x, int y, int width, int height, String text, int value, int max) {
        super(id, x, y, width, height, text);
        this.text = text;
        this.max = max;
        setValue(value);
    }

    protected int getHoverState(boolean mouseOver) {
        return 0;
    }

    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            if (this.dragging) {
                this.sliderValue = (mouseX - this.xPosition + 4) / (this.width - 8);
                this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
                this.dragging = true;
                this.displayString = I18n.format(this.text, new Object[]{Integer.valueOf(getValue())});
            }
            mc.getTextureManager().bindTexture(buttonTextures);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            if (this.enabled) {
                drawTexturedModalRect(this.xPosition + (int) (this.sliderValue * (this.width - 8)), this.yPosition, 0, 66, 4, 20);
                drawTexturedModalRect(this.xPosition + (int) (this.sliderValue * (this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
            } else {
                drawTexturedModalRect(this.xPosition + (int) (this.sliderValue * (this.width - 8)), this.yPosition, 0, 46, 4, 20);
                drawTexturedModalRect(this.xPosition + (int) (this.sliderValue * (this.width - 8)) + 4, this.yPosition, 196, 46, 4, 20);
            }
        }
    }

    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            this.sliderValue = (mouseX - this.xPosition + 4) / (this.width - 8);
            this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
            this.displayString = I18n.format(this.text, new Object[]{Integer.valueOf(getValue())});
            this.dragging = true;
            return true;
        }
        return false;
    }

    public int getValue() {
        return (int) (this.sliderValue * this.max);
    }

    public void setValue(int value) {
        this.sliderValue = value / this.max;
        this.displayString = I18n.format(this.text, new Object[]{Integer.valueOf(getValue())});
    }

    public void mouseReleased(int mouseX, int mouseY) {
        this.dragging = false;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\gui\GuiSlider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */