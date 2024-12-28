package co.crystaldev.client.gui.screens;

import co.crystaldev.client.feature.impl.factions.AdjustHelper;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.buttons.Label;
import co.crystaldev.client.gui.buttons.MenuButton;
import co.crystaldev.client.gui.buttons.NumberInputField;
import net.minecraft.util.BlockPos;

import java.awt.*;

public class ScreenAdjHelper extends ScreenBase {
  private NumberInputField pos1X;

  private NumberInputField pos1Y;

  private NumberInputField pos1Z;

  private MenuButton pos1MoveHere;

  private NumberInputField pos2X;

  private NumberInputField pos2Y;

  private NumberInputField pos2Z;

  private MenuButton pos2MoveHere;

  public void init() {
    super.init();
    int x = this.content.x + this.content.width / 2;
    int y = this.content.y + 60;
    int w = this.content.y / 3;
    int h = 20;
    int id = 500;
    addButton(new Label(x - w / 2 - 15, y - 10, "Position 1", 16777215));
    addButton((this.pos1X = new NumberInputField(++id, x - w - 15, y, w, h, (AdjustHelper.getInstance()).pos1.getX(), -30000000, 30000000)));
    addButton(new Label(x - w - 25, y + h / 2, "X:", 16777215));
    y += h + 5;
    addButton((this.pos1Y = new NumberInputField(++id, x - w - 15, y, w, h, (AdjustHelper.getInstance()).pos1.getY(), 0, 256)));
    addButton(new Label(x - w - 25, y + h / 2, "Y:", 16777215));
    y += h + 5;
    addButton((this.pos1Z = new NumberInputField(++id, x - w - 15, y, w, h, (AdjustHelper.getInstance()).pos1.getZ(), -30000000, 30000000)));
    addButton(new Label(x - w - 25, y + h / 2, "Z:", 16777215));
    y += h + 5;
    addButton((this.pos1MoveHere = new MenuButton(++id, x - w - 15, y, w, h, "Move Here")));
    y = this.content.y + 60;
    addButton(new Label(x + 15 + w / 2, y - 10, "Position 2", 16777215));
    addButton((this.pos2X = new NumberInputField(++id, x + 15, y, w, h, (AdjustHelper.getInstance()).pos2.getX(), -30000000, 30000000)));
    addButton(new Label(x + 5, y + h / 2, "X:", 16777215));
    y += h + 5;
    addButton((this.pos2Y = new NumberInputField(++id, x + 15, y, w, h, (AdjustHelper.getInstance()).pos2.getY(), 0, 256)));
    addButton(new Label(x + 5, y + h / 2, "Y:", 16777215));
    y += h + 5;
    addButton((this.pos2Z = new NumberInputField(++id, x + 15, y, w, h, (AdjustHelper.getInstance()).pos2.getZ(), -30000000, 30000000)));
    addButton(new Label(x + 5, y + h / 2, "Z:", 16777215));
    y += h + 5;
    addButton((this.pos2MoveHere = new MenuButton(++id, x + 15, y, w, h, "Move Here")));
  }

  public void draw(int mouseX, int mouseY, float partialTicks) {
    super.draw(mouseX, mouseY, partialTicks);
    int size = Fonts.NUNITO_SEMI_BOLD_20.getStringHeight() + Fonts.NUNITO_SEMI_BOLD_18.getStringHeight();
    int half = this.header.height / 2 - Fonts.NUNITO_SEMI_BOLD_20.getStringHeight() / 2;
    int y = this.header.y + this.header.height / 2 - size / 2;
    Fonts.NUNITO_SEMI_BOLD_20.drawString(
        (AdjustHelper.getInstance()).name, this.header.x + this.header.width - Fonts.NUNITO_SEMI_BOLD_20
        .getStringWidth((AdjustHelper.getInstance()).name) - half, y, Color.WHITE
        .getRGB());
    Fonts.NUNITO_SEMI_BOLD_18.drawString(
        (AdjustHelper.getInstance()).description, this.header.x + this.header.width - Fonts.NUNITO_SEMI_BOLD_18
        .getStringWidth((AdjustHelper.getInstance()).description) - half, y + Fonts.NUNITO_SEMI_BOLD_20
        .getStringHeight(), Color.WHITE.getRGB());
  }

  public void keyTyped(char keyTyped, int keyCode) {
    super.keyTyped(keyTyped, keyCode);
    try {
      (AdjustHelper.getInstance()).pos1 = new BlockPos(this.pos1X.getValue(), this.pos1Y.getValue(), this.pos1Z.getValue());
      (AdjustHelper.getInstance()).pos2 = new BlockPos(this.pos2X.getValue(), this.pos2Y.getValue(), this.pos2Z.getValue());
    } catch (NumberFormatException numberFormatException) {

    }
  }

  public void onButtonInteract(Button button, int mouseX, int mouseY, int mouseButton) {
    super.onButtonInteract(button, mouseX, mouseY, mouseButton);
    if (button.id == this.pos1MoveHere.id) {
      BlockPos pos = this.mc.thePlayer.getPosition();
      (AdjustHelper.getInstance()).pos1 = pos;
      this.pos1X.setValue(pos.getX());
      this.pos1Y.setValue(pos.getY());
      this.pos1Z.setValue(pos.getZ());
    } else if (button.id == this.pos2MoveHere.id) {
      BlockPos pos = this.mc.thePlayer.getPosition();
      (AdjustHelper.getInstance()).pos2 = pos;
      this.pos2X.setValue(pos.getX());
      this.pos2Y.setValue(pos.getY());
      this.pos2Z.setValue(pos.getZ());
    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\screens\ScreenAdjHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */