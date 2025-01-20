package co.crystaldev.client.gui.buttons.settings;

import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.util.RenderUtils;
import net.minecraft.util.ResourceLocation;

public class SelectorMenuResourceButton extends SelectorMenuButton {
    private final ResourceLocation resource;

    private final int resourceSize;

    public SelectorMenuResourceButton(int id, int x, int y, int width, int height, String displayText, String currentValue, String[] values, ResourceLocation resource, int size) {
        super(id, x, y, width, height, displayText, currentValue, values);
        this.resource = resource;
        this.resourceSize = size;
    }

    public void drawButton(int mouseX, int mouseY, boolean hovered) {
        Screen.scissorStart(this.scissorPane);
        hovered = (hovered && this.enabled);
        boolean menuButtonHovered = hovered && (this.entireButtonHitBox || mouseX <= this.x + this.width / 2) && !this.previous.isHovered(mouseX, mouseY) && !this.next.isHovered(mouseX, mouseY);
        this.background.fade(menuButtonHovered);
        this.textColor.fade(menuButtonHovered);
        this.valueTextColor.fade(hovered);
        RenderUtils.drawRoundedRect(this.x, this.y, (this.x + this.width), (this.y + this.height), 9.0D, this.background.getCurrentColor().getRGB());
        RenderUtils.setGlColor(this.textColor.getCurrentColor());
        RenderUtils.drawCustomSizedResource(this.resource, this.x + 4, this.y + this.height / 2 - this.resourceSize / 2, this.resourceSize, this.resourceSize);
        this.fontRenderer.drawString(this.displayText, this.x + 8 + this.resourceSize, this.y + this.height / 2 - this.fontRenderer.getStringHeight() / 2, this.textColor.getCurrentColor().getRGB());
        Fonts.NUNITO_SEMI_BOLD_16.drawCenteredString(this.currentValue, this.x + this.width - this.width / 4, this.y + this.height / 2, this.valueTextColor.getCurrentColor().getRGB());
        this.previous.drawButton(mouseX, mouseY, (this.previous.isHovered(mouseX, mouseY) && hovered && this.enabled));
        this.next.drawButton(mouseX, mouseY, (this.next.isHovered(mouseX, mouseY) && hovered && this.enabled));
        Screen.scissorEnd(this.scissorPane);
    }
}
