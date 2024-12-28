package co.crystaldev.client.gui.screens.groups;

import co.crystaldev.client.cache.UsernameCache;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.group.objects.GroupMember;
import co.crystaldev.client.group.objects.enums.Rank;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Pane;
import co.crystaldev.client.gui.buttons.Label;
import co.crystaldev.client.gui.buttons.groups.GroupMemberLargeButton;

import java.util.*;

public class SectionMembers extends GroupSection {
  protected SectionMembers(Pane pane) {
    super(pane);
  }

  public void init() {
    super.init();
    initGroupMembers();
  }

  public void initGroupMembers() {
    removeButton(b -> b.hasAttribute("groupSection"));
    if (!GroupManager.isInGroup())
      return;
    int x = this.pane.x + 20;
    int y = this.pane.y + 10;
    int w = this.pane.width - 40;
    int h = 18;
    final Pane scissor = this.pane.scale(getScaledScreen());
    Map<Rank, List<GroupMember>> sorted = new HashMap<>();
    for (GroupMember mem : GroupManager.getSelectedGroup().getMembers())
      ((List<GroupMember>)sorted.computeIfAbsent(mem.getRank(), r -> new ArrayList())).add(mem);
    List<GroupMember> pending = new ArrayList<>(GroupManager.getSelectedGroup().getPendingMembers());
    pending.sort(Comparator.comparing(GroupMember::getUuid));
    if (!pending.isEmpty()) {
      addButton((Button)new Label(x + w / 2, y + h / 2, "Pending - " + pending
            .size(), 16777215, Fonts.NUNITO_SEMI_BOLD_16) {

          });
      y += h;
      for (GroupMember member : pending) {
        if (member == null)
          continue;
        addButton((Button)new GroupMemberLargeButton(member, x, y, w, h, true) {

            });
        y += h + 5;
      }
    }
    for (Map.Entry<Rank, List<GroupMember>> entry : (new TreeMap<>(sorted)).entrySet()) {
      List<GroupMember> members = entry.getValue();
      members.sort(Comparator.comparing(m -> UsernameCache.getInstance().getUsername(m.getUuid()).toLowerCase()));
      addButton((Button)new Label(x + w / 2, y + h / 2, ((Rank)entry
            .getKey()).getDisplayText() + " - " + members.size(), 16777215, Fonts.NUNITO_SEMI_BOLD_16) {

          });
      y += h;
      for (GroupMember member : members) {
        if (member == null)
          continue;
        addButton((Button)new GroupMemberLargeButton(member, x, y, w, h, false) {

            });
        y += h + 5;
      }
    }
    this.pane.updateMaxScroll(this, -10);
    this.pane.addScrollbarToScreen(this);
  }

  public void draw(int mouseX, int mouseY, float partialTicks) {}
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\screens\groups\SectionMembers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */