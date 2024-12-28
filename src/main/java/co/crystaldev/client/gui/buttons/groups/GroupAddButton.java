package co.crystaldev.client.gui.buttons.groups;

import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.FadingColor;

public class GroupAddButton extends Button {
  private final FadingColor backgroundColor;

  private final FadingColor textColor;

  public GroupAddButton(int x, int y, int width, int height) {
    super(-1, x, y, width, height, "+");
    this.backgroundColor = new FadingColor(this.opts.neutralButtonBackground, this.opts.hoveredButtonBackground);
    this.textColor = new FadingColor(this.opts.neutralTextColor, this.opts.hoveredTextColor);
  }

  public void drawButton(int mouseX, int mouseY, boolean hovered) {
    Screen.scissorStart(this.scissorPane);
    this.backgroundColor.fade(hovered);
    this.textColor.fade(hovered);
    RenderUtils.drawRoundedRectWithGradientBorder(this.x, this.y, (this.x + this.width), (this.y + this.height), 20.0D, 1.7F, this.opts.mainDisabled
        .getRGB(), this.opts.secondaryDisabled.getRGB(), this.backgroundColor.getCurrentColor().getRGB());
    if (this.textColor.getCurrentColor().getAlpha() > 4)
      this.fontRenderer.drawCenteredString(this.displayText, this.x + this.width / 2, this.y + this.height / 2, this.textColor
          .getCurrentColor().getRGB());
    Screen.scissorEnd(this.scissorPane);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\buttons\groups\GroupAddButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */