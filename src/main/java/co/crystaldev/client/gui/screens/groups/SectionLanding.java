package co.crystaldev.client.gui.screens.groups;

import co.crystaldev.client.cache.UsernameCache;
import co.crystaldev.client.font.FontRenderer;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.group.objects.Group;
import co.crystaldev.client.group.objects.GroupMember;
import co.crystaldev.client.group.objects.enums.Rank;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Pane;
import co.crystaldev.client.gui.ScrollPane;
import co.crystaldev.client.gui.buttons.Label;
import co.crystaldev.client.gui.buttons.MenuButton;
import co.crystaldev.client.gui.buttons.groups.GroupMemberSmallButton;
import co.crystaldev.client.util.RenderUtils;

import java.util.*;

public class SectionLanding extends GroupSection {
  protected SectionLanding(Pane pane, Pane members) {
    super(pane);
    this.members = new ScrollPane(members);
    this.memberContent = new ScrollPane(members);
    this.memberContent.y += 20;
    this.memberContent.height -= 20;
    this.memberContent.setScrollIf(b -> b.hasAttribute("groupSection#memberButton"));
    initGroupMembers();
    int x = this.pane.x + 20;
    int y = this.pane.y + 60;
    int w = this.pane.width - 40;
    int h = 18;
    addButton((Button)new MenuButton(-1, x, y, w, h, "Ping Location") {

        });
    y += h + 5;
    addButton((Button)new MenuButton(-1, x, y, w, h, "Upload Current Schematic") {

        });
    y += h + 5;
    addButton((Button)new MenuButton(-1, x, y, w, h, "Share Current Schematic") {

        });
    y += h + 5;
    addGroupMemberAddButtonString("groupSection");
  }

  public void draw(int mouseX, int mouseY, float partialTicks) {
    RenderUtils.drawRoundedRect(this.members.x, this.members.y, (this.members.x + this.members.width), (this.members.y + this.members.height), 22.0D, this.opts.sidebarBackground
        .getRGB());
    Fonts.NUNITO_SEMI_BOLD_18.drawCenteredString("Members", this.members.x + this.members.width / 2, this.members.y + 5 + Fonts.NUNITO_SEMI_BOLD_18
        .getStringHeight() / 2, -1);
    Group sel = GroupManager.getSelectedGroup();
    if (sel == null)
      return;
    Fonts.NUNITO_SEMI_BOLD_36.drawCenteredString(sel.getName(), this.pane.x + this.pane.width / 2, this.pane.y + 10 + Fonts.NUNITO_SEMI_BOLD_36
        .getStringHeight() / 2, -1);
    int membersOnline = sel.getOnlineMembers();
    int members = sel.getMemberCount();
    String online = String.format("%d member%s online", new Object[] { Integer.valueOf(membersOnline), (membersOnline == 1) ? "" : "s" });
    String total = String.format("%d total member%s", new Object[] { Integer.valueOf(members), (members == 1) ? "" : "s" });
    FontRenderer fr = Fonts.NUNITO_SEMI_BOLD_12;
    int x = this.pane.x + this.pane.width / 2 - fr.getStringWidth(online) + 1;
    int x1 = this.pane.x + this.pane.width / 2 + 12;
    fr.drawString(online, x, this.members.y + 10 + Fonts.NUNITO_SEMI_BOLD_36.getStringHeight(), -1);
    fr.drawString(total, x1, this.members.y + 10 + Fonts.NUNITO_SEMI_BOLD_36.getStringHeight(), -1);
    RenderUtils.drawCircle((x - 4), (this.members.y + 11 + Fonts.NUNITO_SEMI_BOLD_36.getStringHeight() + (int)(fr.getStringHeight() / 2.0D)), 12.0F, this.opts.mainColor
        .getRGB());
    RenderUtils.drawCircle((x1 - 4), (this.members.y + 11 + Fonts.NUNITO_SEMI_BOLD_36.getStringHeight() + (int)(fr.getStringHeight() / 2.0D)), 12.0F, this.opts.mainDisabled
        .getRGB());
  }

  public void initGroupMembers() {
    removeButton(b -> (b.hasAttribute("groupSection#memberButton") || b.hasAttribute("groupSection#memberScrollBar")));
    if (!GroupManager.isInGroup())
      return;
    int x = this.memberContent.x + 8;
    int y = this.memberContent.y;
    int w = this.memberContent.width - 16;
    int h = 20;
    final Pane scissor = this.memberContent.scale(getScaledScreen());
    Map<Rank, List<GroupMember>> sorted = new HashMap<>();
    for (GroupMember mem : GroupManager.getSelectedGroup().getMembers())
      ((List<GroupMember>)sorted.computeIfAbsent(mem.getRank(), r -> new ArrayList())).add(mem);
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
        addButton((Button)new GroupMemberSmallButton(member, x, y, w, h) {

            });
        y += h + 5;
      }
    }
    this.memberContent.updateMaxScroll(this, 0);
    this.memberContent.addScrollbarToScreen(this, "groupSection#memberScrollBar");
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\screens\groups\SectionLanding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */