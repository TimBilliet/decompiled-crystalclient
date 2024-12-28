package co.crystaldev.client.gui.buttons;

import co.crystaldev.client.font.FontRenderer;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.util.RenderUtils;

public class Label extends Button {
  protected final FontRenderer fontRenderer;
  
  protected final int color;
  
  protected boolean centered = true;
  
  public void setCentered(boolean centered) {
    this.centered = centered;
  }
  
  public Label(int x, int y, String text, int color, FontRenderer fontRenderer) {
    super(-1, x, y, 0, 0, text);
    this.fontRenderer = fontRenderer;
    this.color = color;
  }
  
  public Label(int x, int y, String text, int color) {
    this(x, y, text, color, Fonts.NUNITO_REGULAR_20);
  }
  
  public void drawButton(int mouseX, int mouseY, boolean hovered) {
    Screen.scissorStart(this.scissorPane);
    RenderUtils.resetColor();
    if (this.centered) {
      this.fontRenderer.drawCenteredStringWithShadow(this.displayText, this.x, this.y, this.color);
    } else {
      this.fontRenderer.drawStringWithShadow(this.displayText, this.x, this.y, this.color);
    } 
    Screen.scissorEnd(this.scissorPane);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\buttons\Label.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */