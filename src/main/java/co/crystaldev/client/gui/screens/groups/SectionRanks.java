package co.crystaldev.client.gui.screens.groups;

import co.crystaldev.client.Reference;
import co.crystaldev.client.cache.UsernameCache;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.group.annotations.Permission;
import co.crystaldev.client.group.objects.GroupMember;
import co.crystaldev.client.group.objects.Permissions;
import co.crystaldev.client.group.objects.enums.Rank;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Pane;
import co.crystaldev.client.gui.ScrollPane;
import co.crystaldev.client.gui.buttons.Label;
import co.crystaldev.client.gui.buttons.groups.GroupMemberSmallButton;
import co.crystaldev.client.gui.buttons.groups.GroupPermissionButton;
import co.crystaldev.client.gui.buttons.groups.GroupRankButton;
import co.crystaldev.client.util.RenderUtils;

import java.lang.reflect.Field;
import java.util.*;

public class SectionRanks extends GroupSection {
    private Rank selectedRank;

    private ScrollPane permissions;

    protected SectionRanks(Pane pane, Pane members) {
        super(pane);
        this.members = new ScrollPane(members);
        this.memberContent = new ScrollPane((Pane) this.members);
        this.memberContent.y += 20;
        this.memberContent.height -= 20;
        this.memberContent.setScrollIf(b -> b.hasAttribute("groupSection#memberButton"));
        initGroupMembers();
        addGroupMemberAddButtonString("groupSection");
    }

    public void init() {
        super.init();
        this.selectedRank = Rank.LEADER;
        int x = this.pane.x + 20;
        int x1 = this.pane.x + this.pane.width / 2 + 10;
        int y = this.pane.y + 10;
        int w = this.pane.width / 2 - 30;
        int h = 18;
        int index = 0;
        for (Rank rank : Rank.values()) {
            addButton((Button) new GroupRankButton(rank, (index == 0) ? x : x1, y, w, h, (rank == this.selectedRank)) {
                {
                    addAttribute("groupSection");
                }
            });
            index++;
            if (index == 2) {
                y += h + 5;
                index = 0;
            }
        }
        if (index != 0)
            y += h + 5;
        this.permissions = new ScrollPane(this.pane.x, y, this.pane.width, this.pane.height - (y - this.pane.y));
        this.permissions.setScrollIf(b -> b.hasAttribute("groupSection#permissionButton"));
        initPermissions();
    }

    public void onButtonInteract(Button button, int mouseX, int mouseY, int mouseButton) {
        super.onButtonInteract(button, mouseX, mouseY, mouseButton);
        if (button instanceof GroupRankButton) {
            this.selectedRank = ((GroupRankButton) button).getRank();
            for (Button b : this.buttons) {
                if (b instanceof GroupRankButton) {
                    ((GroupRankButton) b).setSelected((((GroupRankButton) b).getRank() == this.selectedRank));
                    continue;
                }
                if (b instanceof GroupPermissionButton)
                    ((GroupPermissionButton) b).setRank(this.selectedRank);
            }
        }
    }

    public void draw(int mouseX, int mouseY, float partialTicks) {
        RenderUtils.drawRoundedRect(this.members.x, this.members.y, (this.members.x + this.members.width), (this.members.y + this.members.height), 22.0D, this.opts.sidebarBackground
                .getRGB());
        Fonts.NUNITO_SEMI_BOLD_18.drawCenteredString("Members", this.members.x + this.members.width / 2, this.members.y + 5 + Fonts.NUNITO_SEMI_BOLD_18
                .getStringHeight() / 2, -1);
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
            sorted.computeIfAbsent(mem.getRank(), r -> new ArrayList<>()).add(mem);
        for (Map.Entry<Rank, List<GroupMember>> entry : (new TreeMap<>(sorted)).entrySet()) {
            List<GroupMember> members = entry.getValue();
            members.sort(Comparator.comparing(m -> UsernameCache.getInstance().getUsername(m.getUuid()).toLowerCase()));
            addButton((Button) new Label(x + w / 2, y + h / 2, ((Rank) entry
                    .getKey()).getDisplayText() + " - " + members.size(), 16777215, Fonts.NUNITO_SEMI_BOLD_16) {
                {
                    {
                        addAttribute("groupSection#memberButton");
                        setScissorPane(scissor);
                    }
                }
            });
            y += h;
            for (GroupMember member : members) {
                if (member == null)
                    continue;
                addButton((Button) new GroupMemberSmallButton(member, x, y, w, h) {
                    {
                        addAttribute("groupSection#memberButton");
                        setScissorPane(scissor);
                    }
                });
                y += h + 5;
            }
        }
        this.memberContent.updateMaxScroll(this, 0);
        this.memberContent.addScrollbarToScreen(this, "groupSection#memberScrollBar");
    }

    public void initPermissions() {
        removeButton(b -> b.hasAttribute("groupSection#permissionButton"));
        int x = this.permissions.x + 20;
        int y = this.permissions.y;
        int w = this.permissions.width - 40;
        int h = 18;
        final Pane scissor = this.permissions.scale(getScaledScreen());
        for (Field field : Permissions.class.getDeclaredFields()) {
            System.out.println(field);
            Permission annotation = field.getAnnotation(Permission.class);
            if (annotation != null)
                try {
                    addButton(new GroupPermissionButton(this.selectedRank, field.getInt(null), x, y, w, h, annotation.label()) {
                        {
                            addAttribute("groupSection#permissionButton");
                            setScissorPane(scissor);
                        }
                    });
                    y += h + 5;
                } catch (IllegalAccessException ex) {
                    Reference.LOGGER.error("Unable to retrieve static field value", ex);
                }
        }
        this.permissions.updateMaxScroll(this, 0);
        this.permissions.updateButtons(this.buttons);
        this.permissions.addScrollbarToScreen(this, "groupSection");
    }

    public void scroll(int dwheel, int mouseX, int mouseY) {
        super.scroll(dwheel, mouseX, mouseY);
        if (this.permissions != null)
            this.permissions.scroll(this, mouseX, mouseY, dwheel);
    }
}