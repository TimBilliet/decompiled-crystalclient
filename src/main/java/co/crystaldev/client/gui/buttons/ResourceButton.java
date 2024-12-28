package co.crystaldev.client.gui.buttons;

import co.crystaldev.client.gui.Button;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.FadingColor;
import net.minecraft.util.ResourceLocation;

public class ResourceButton extends Button {
  protected FadingColor fadingColor;

  protected FadingColor iconColor;

  public FadingColor getFadingColor() {
    return this.fadingColor;
  }

  public void setFadingColor(FadingColor fadingColor) {
    this.fadingColor = fadingColor;
  }

  public FadingColor getIconColor() {
    return this.iconColor;
  }

  public void setIconColor(FadingColor iconColor) {
    this.iconColor = iconColor;
  }

  protected boolean drawBackground = true;

  public void setDrawBackground(boolean drawBackground) {
    this.drawBackground = drawBackground;
  }

  protected boolean resourceFade = true;

  protected final ResourceLocation resourceLocation;

  public void setResourceFade(boolean resourceFade) {
    this.resourceFade = resourceFade;
  }

  protected int radius = 9;

  protected int iconSize;

  public int getRadius() {
    return this.radius;
  }

  public void setRadius(int radius) {
    this.radius = radius;
  }

  public int getIconSize() {
    return this.iconSize;
  }

  public void setIconSize(int iconSize) {
    this.iconSize = iconSize;
  }

  protected boolean enabled = true;

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  protected Runnable onClick = null;

  public void setOnClick(Runnable onClick) {
    this.onClick = onClick;
  }

  public ResourceButton(int id, int x, int y, int width, int height, ResourceLocation resourceLocation) {
    super(id, x, y, width, height);
    this.resourceLocation = resourceLocation;
    this.fadingColor = new FadingColor(this.opts.neutralButtonBackground, this.opts.hoveredButtonBackground);
    this.iconColor = new FadingColor(this.opts.neutralTextColor, this.opts.hoveredTextColor);
    this.iconSize = this.width - 6;
  }

  public void drawButton(int mouseX, int mouseY, boolean hovered) {
    this.fadingColor.fade((hovered && this.enabled));
    this.iconColor.fade((hovered && this.enabled));
    if (this.drawBackground)
      RenderUtils.drawRoundedRect(this.x, this.y, (this.x + this.width), (this.y + this.height), this.radius, this.fadingColor
          .getCurrentColor().getRGB());
    RenderUtils.glColor(this.iconColor.getCurrentColor().getRGB());
    RenderUtils.drawCustomSizedResource(this.resourceLocation, this.x + this.width / 2 - this.iconSize / 2, this.y + this.height / 2 - this.iconSize / 2, this.iconSize, this.iconSize);
  }

  public void onInteract(int mouseX, int mouseY, int mouseButton) {
    super.onInteract(mouseX, mouseY, mouseButton);
    if (this.onClick != null && this.enabled)
      this.onClick.run();
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\buttons\ResourceButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */