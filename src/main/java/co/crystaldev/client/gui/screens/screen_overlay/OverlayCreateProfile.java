package co.crystaldev.client.gui.screens.screen_overlay;

import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.buttons.Label;
import co.crystaldev.client.gui.buttons.MenuButton;
import co.crystaldev.client.gui.buttons.SimpleColorPicker;
import co.crystaldev.client.gui.buttons.TextInputField;
import co.crystaldev.client.gui.buttons.settings.ToggleButton;

import java.awt.*;

public class OverlayCreateProfile extends ScreenOverlay {
  private TextInputField name;

  private ToggleButton autoSelect;

  private SimpleColorPicker color;

  private MenuButton create;

  public OverlayCreateProfile() {
    super(0, 0, 200, 10, "Create Profile");
  }

  public void init() {
    super.init();
    int x = this.pane.x + 5;
    int y = this.pane.y + 24;
    int w = this.pane.width - 10;
    int h = 18;
    addButton((Button)(this.name = new TextInputField(-1, x + w / 2, y, w / 2, h, "Profile Name") {

        }));
    addButton((Button)new Label(x + 5, y + h / 2 - Fonts.NUNITO_REGULAR_20.getStringHeight() / 2, "Name", -1) {

        });
    y += h + 5;
    addButton((Button)(this.color = new SimpleColorPicker(x + w / 2 + 3, y, w / 2 - 6, h - 6, Color.WHITE)));
    addButton((Button)new Label(x + 5, y + (h - 6) / 2 - Fonts.NUNITO_REGULAR_20.getStringHeight() / 2, "Color", -1) {

        });
    y += h - 6 + 5;
    addButton((Button)(this.autoSelect = new ToggleButton(-1, x, y, w, h, "Auto-Select on this " + (this.mc.isSingleplayer() ? "World" : "Server"), false)));
    y += h + 7;
    addButton((Button)(this.create = new MenuButton(-1, x, y, w, h, "Create Profile") {

        }));
    while (this.pane.y + this.pane.height < y + h + 5)
      this.pane.height++;
    center();
  }

  public void draw(int mouseX, int mouseY, float partialTicks) {
    super.draw(mouseX, mouseY, partialTicks);
    this.create.setEnabled(!this.name.getText().isEmpty());
  }
}
