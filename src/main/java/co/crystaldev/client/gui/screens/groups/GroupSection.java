package co.crystaldev.client.gui.screens.groups;

import co.crystaldev.client.Config;
import co.crystaldev.client.Resources;
import co.crystaldev.client.feature.settings.GroupOptions;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Pane;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.gui.ScrollPane;
import co.crystaldev.client.gui.buttons.ResourceButton;
import co.crystaldev.client.gui.screens.screen_overlay.OverlayInviteManipulation;

public abstract class GroupSection extends Screen {
  protected final ScrollPane pane;

  protected ScrollPane members;

  protected ScrollPane memberContent;

  protected ResourceButton memberAddButton = null;

  protected GroupSection(Pane pane) {
    this.pane = (pane instanceof ScrollPane) ? (ScrollPane)pane : new ScrollPane(pane);
    this.pane.setScrollIf(b -> b.hasAttribute("groupSection"));
    init();
  }

  public void init() {
    clearSection();
  }

  public void initGroupMembers() {}

  public void drawDefaultBackground() {}

  public void onGuiClosed() {
    Config.getInstance().saveConfig("group_options", GroupOptions.getInstance());
  }

  public void onButtonInteract(Button button, int mouseX, int mouseY, int mouseButton) {
    if (button.equals(this.memberAddButton))
      ((Screen)this.mc.currentScreen).addOverlay((Screen)new OverlayInviteManipulation(button.x - 150, button.y + button.height, 150, 30));
  }

  public void scroll(int dwheel, int mouseX, int mouseY) {
    this.pane.scroll(this, mouseX, mouseY, dwheel);
    if (this.members != null)
      this.members.scroll(this, mouseX, mouseY, dwheel);
    if (this.memberContent != null)
      this.memberContent.scroll(this, mouseX, mouseY, dwheel);
  }

  public void addOverlay(Screen overlay) {
    ((ScreenGroups)this.mc.currentScreen).addOverlay(overlay);
  }

  public void clearSection() {
    removeButton(b -> b.hasAttribute("groupSection"));
    removeButton(b -> b.hasAttribute("groupSection#memberButton"));
  }

  public void addGroupMemberAddButtonString(final String attribute) {
    if (this.members != null && GroupManager.getSelectedGroup() != null && GroupManager.getSelectedGroup().hasPermission(12)) {
      int size = Fonts.NUNITO_SEMI_BOLD_18.getStringHeight() + 2;
      addButton((Button)(this.memberAddButton = new ResourceButton(-1, this.members.x + this.members.width - 5 - size, this.members.y + 5, size, size, Resources.ADD_PERSON) {

          }));
    }
  }
}
