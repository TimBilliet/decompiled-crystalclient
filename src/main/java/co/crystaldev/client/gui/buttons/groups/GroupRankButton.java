package co.crystaldev.client.gui.buttons.groups;

import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.group.objects.enums.Rank;
import co.crystaldev.client.gui.buttons.MenuButton;
import co.crystaldev.client.handler.NotificationHandler;

public class GroupRankButton extends MenuButton {
  private final Rank rank;

  public Rank getRank() {
    return this.rank;
  }

  public GroupRankButton(Rank rank, int x, int y, int width, int height, boolean selected) {
    super(-1, x, y, width, height, rank.getDisplayText());
    this.rank = rank;
    setSelected(selected);
    if (GroupManager.getSelectedGroup() == null || !GroupManager.getSelectedGroup().hasPermission(11)) {
      setEnabled(false);
      this.onClick = (() -> NotificationHandler.addNotification("You do not have permission to interact with permissions in this group"));
    }
  }

  public void setSelected(boolean selected) {
    super.setSelected(selected);
    if (this.selected) {
      this.outlineColor = this.opts.mainColor.getRGB();
    } else {
      this.outlineColor = -1;
    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\buttons\groups\GroupRankButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */