package co.crystaldev.client.gui.buttons;

import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.enums.ChatColor;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class MenuResourceButton extends MenuButton {
    private final ResourceLocation resource;

    private final int size;

    private Color resourceColor = Color.WHITE;

    public Color getResourceColor() {
        return this.resourceColor;
    }

    public void setResourceColor(Color resourceColor) {
        this.resourceColor = resourceColor;
    }

    public MenuResourceButton(int id, int x, int y, int width, int height, String displayText, ResourceLocation resource, int size) {
        super(id, x, y, width, height, displayText);
        this.resource = resource;
        this.size = size;
    }

    public MenuResourceButton(int id, int x, int y, int width, int height, String displayText, int outline, ResourceLocation resource, int size) {
        super(id, x, y, width, height, displayText, outline);
        this.resource = resource;
        this.size = size;
    }

    public void drawButton(int mouseX, int mouseY, boolean hovered) {
        int x;
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
        String text = ChatColor.translate(this.displayText);
        if (this.useMinecraftFR) {
            x = this.x + this.width / 2 - this.mc.fontRendererObj.getStringWidth(text) / 2;
            RenderUtils.drawString(text, x + this.size / 2 + 2, this.y + this.height / 2 - this.mc.fontRendererObj.FONT_HEIGHT / 2, this.textColor
                    .getCurrentColor().getRGB());
        } else {
            x = this.x + this.width / 2 - this.fontRenderer.getStringWidth(text) / 2;
            this.fontRenderer.drawString(text, x + this.size / 2 + 2, this.y + this.height / 2 - this.fontRenderer.getStringHeight() / 2, this.textColor
                    .getCurrentColor().getRGB());
        }
        RenderUtils.setGlColor(this.resourceColor);
        RenderUtils.setGlColor(this.textColor.getCurrentColor());
        RenderUtils.drawCustomSizedResource(this.resource, x - 2 - this.size / 2, this.y + this.height / 2 - this.size / 2, this.size, this.size);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Screen.scissorEnd(this.scissorPane);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\buttons\MenuResourceButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */