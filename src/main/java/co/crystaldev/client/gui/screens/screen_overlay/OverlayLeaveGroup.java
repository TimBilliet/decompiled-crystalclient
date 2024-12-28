package co.crystaldev.client.gui.screens.screen_overlay;

import co.crystaldev.client.font.FontRenderer;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.group.objects.Group;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.buttons.Label;
import co.crystaldev.client.gui.buttons.MenuButton;
import org.apache.commons.lang3.text.WordUtils;

public class OverlayLeaveGroup extends ScreenOverlay {
  private static final FontRenderer fr = Fonts.NUNITO_REGULAR_16;

  private final Group group;

  public OverlayLeaveGroup(Group group) {
    super(0, 0, 200, 10, "Leave group");
    this.group = group;
  }

  public void init() {
    String desc = String.format("Are you sure you wish to leave the group %s? You will only be able to rejoin with the group invitation code.", new Object[] { this.group
          .getName() });
    int y = this.pane.y + 28;
    for (String str : WordUtils.wrap(desc, 45).split("\n")) {
      addButton((Button)new Label(this.pane.x + this.pane.width / 2, y, str, this.opts.neutralTextColor.getRGB(), fr));
      y += fr.getStringHeight();
    }
    y += 2;
    addButton((Button)new MenuButton(-1, this.pane.x + 5, y, this.pane.width / 2 - 7, 18, "Cancel") {

        });
    addButton((Button)new MenuButton(-1, this.pane.x + this.pane.width / 2 + 2, y, this.pane.width / 2 - 7, 18, "Leave Group") {

        });
    while (this.pane.y + this.pane.height < y + 18 + 5)
      this.pane.height++;
    center();
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\screens\screen_overlay\OverlayLeaveGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */