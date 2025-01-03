package co.crystaldev.client.gui.screens.screen_overlay;

import co.crystaldev.client.Resources;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.buttons.MenuButton;
import co.crystaldev.client.gui.buttons.ResourceButton;
import co.crystaldev.client.gui.buttons.TextInputField;
import org.lwjgl.input.Keyboard;

public class OverlayJoinGroup extends ScreenOverlay {
  private TextInputField inviteInput;

  public OverlayJoinGroup(int x, int y, int w) {
    super(x, y, w, 10, "Join a group");
    Keyboard.enableRepeatEvents(true);
  }

  public void init() {
    int x = this.pane.x + 5;
    int y = this.pane.y + 24;
    int w = this.pane.width - 10;
    int h = 18;
    addButton((Button)new ResourceButton(-1, this.pane.x + 5, this.pane.y + 5, Fonts.NUNITO_SEMI_BOLD_18.getStringHeight() + 2, Fonts.NUNITO_SEMI_BOLD_18
          .getStringHeight() + 2, Resources.CHEVRON_LEFT) {

        });
    addButton((Button)(this.inviteInput = new TextInputField(-1, x, y, w, h, "CC-XXXXXXXXXXXX") {

        }));
    y += h + 5;
    addButton((Button)new MenuButton(-1, x, y, w, h, "Join Group") {

        });
    while (this.pane.y + this.pane.height < y + h + 5)
      this.pane.height++;
    center();
  }

  public void onGuiClosed() {
    Keyboard.enableRepeatEvents(false);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\screens\screen_overlay\OverlayJoinGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */