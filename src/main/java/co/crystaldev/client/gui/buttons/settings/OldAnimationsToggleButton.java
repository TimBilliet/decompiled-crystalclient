package co.crystaldev.client.gui.buttons.settings;

import co.crystaldev.client.font.FontRenderer;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.gui.ease.Animation;
import co.crystaldev.client.gui.ease.Easing;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.FadingColor;

public class OldAnimationsToggleButton extends ToggleButton {
  private Animation stateAnimation;

  private final FadingColor stateColor;

  public OldAnimationsToggleButton(int id, int x, int y, int width, int height, String displayText, boolean state) {
    super(id, x, y, width, height, displayText, state);
    this.stateAnimation = new Animation(1L, state ? 1.0F : 0.0F, state ? 1.0F : 0.0F, Easing.IN_OUT_CUBIC);
    this.stateColor = new FadingColor(this.opts.mainDisabled, this.opts.mainColor, 200L, Easing.IN_OUT_CUBIC);
  }

  public void drawButton(int mouseX, int mouseY, boolean hovered) {
    Screen.scissorStart(this.scissorPane);
    hovered = (hovered && this.enabled);
    this.backgroundColor.fade(hovered);
    this.textColor.fade(hovered);
    this.stateColor.fade(this.currentValue);
    RenderUtils.drawRoundedRect(this.x, this.y, (this.x + this.width), (this.y + this.height), 9.0D, this.backgroundColor.getCurrentColor().getRGB());
    this.fontRenderer.drawString(this.displayText, this.x + 4, this.y + this.height / 2 - this.fontRenderer.getStringHeight() / 2, this.textColor.getCurrentColor().getRGB());
    int boxSize = this.height - 6;
    RenderUtils.drawRoundedRect((this.x + this.width - 3 - boxSize * 3), (this.y + 3), (this.x + this.width - 3), (this.y + this.height - 3), 6.0D, this.opts.neutralButtonBackground
        .getRGB());
    FontRenderer fr = Fonts.NUNITO_SEMI_BOLD_12;
    fr.drawString("1.7", this.x + this.width - 3 - boxSize * 3 + 3, this.y + this.height / 2 - fr.getStringHeight() / 2, this.textColor.getCurrentColor().getRGB());
    fr.drawString("1.8", this.x + this.width - 6 - fr.getStringWidth("1.7"), this.y + this.height / 2 - fr.getStringHeight() / 2, this.textColor.getCurrentColor().getRGB());
    RenderUtils.resetColor();
    int switchSize = (int)((boxSize * 3) * 0.5D), offset = (int)((boxSize * 3 - switchSize) * this.stateAnimation.getValue());
    RenderUtils.drawRoundedRectWithBorder((offset + this.x + this.width - 3 - boxSize * 3 + 1), (this.y + 3 + 1), (offset + this.x + this.width - 3 - boxSize * 3 + switchSize - 1), (this.y + this.height - 3 - 1), 6.0D, 2.5F, this.stateColor
        .getCurrentColor().getRGB(), this.stateColor.getCurrentColor().darker().getRGB());
    Screen.scissorEnd(this.scissorPane);
  }

  public void onInteract(int mouseX, int mouseY, int mouseButton) {
    super.onInteract(mouseX, mouseY, mouseButton);
    if (this.enabled)
      this.stateAnimation = new Animation(0.2F, this.stateAnimation.getValue(), this.currentValue ? 1.0F : 0.0F, this.stateAnimation.getEasing());
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\buttons\settings\OldAnimationsToggleButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */