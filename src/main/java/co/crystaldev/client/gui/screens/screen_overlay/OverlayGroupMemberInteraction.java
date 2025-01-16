package co.crystaldev.client.gui.screens.screen_overlay;

import co.crystaldev.client.Client;
import co.crystaldev.client.cache.UsernameCache;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.group.objects.Group;
import co.crystaldev.client.group.objects.GroupMember;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Pane;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.gui.buttons.MenuButton;
import co.crystaldev.client.handler.NotificationHandler;
import co.crystaldev.client.network.socket.client.group.PacketGroupMemberAction;
import co.crystaldev.client.util.objects.FadingColor;
import net.minecraft.client.gui.GuiChat;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.UUID;

public class OverlayGroupMemberInteraction extends ScreenOverlay {
    private final GroupMember member;

    public OverlayGroupMemberInteraction(GroupMember member, int x, int y, int w) {
        super(x, y, w, 10, UsernameCache.getInstance().getUsername(member.getUuid()));
        this.member = member;
        this.dimBackground = false;
    }

    public void init() {
        this.pane.width = Math.max(this.pane.width, Fonts.NUNITO_SEMI_BOLD_18.getStringWidth(this.headerText) + 6);
        Pane pane = ((Screen) this.mc.currentScreen).pane;
        while (this.pane.x + this.pane.width > pane.x + pane.width - 5)
            this.pane.x--;
        int x = this.pane.x + 5;
        int y = this.pane.y + 23;
        int w = this.pane.width - 10;
        int h = 18;
        Group sg = GroupManager.getSelectedGroup();
        UUID player = Client.getUniqueID();
        addButton((Button) new MenuButton(-1, x, y, w, h, "Copy UUID") {
            {
                onClick = () -> {
                    StringSelection selection = new StringSelection(member.getUuid().toString());
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
                    NotificationHandler.addNotification("UUID copied to clipboard!");
                    closeOverlay();
                };
            }
        });
        y += h + 5;
        addButton((Button) new MenuButton(-1, x, y, w, h, "Send Message") {
            {
                onClick = () -> {
                    mc.displayGuiScreen(new GuiChat(String.format("/g msg %s ", OverlayGroupMemberInteraction.this.headerText)));
                };
            }
        });
        if (sg.hasPermission(6) && sg.compareRanks(player, this.member.getUuid()) && sg.canRankBeUpdated(player, this.member.getRank().promote())) {
            y += h + 5;
            addButton((Button) new MenuButton(-1, x, y, w, h, "Promote") {
                {
                    onClick = () -> {
                        PacketGroupMemberAction packet = new PacketGroupMemberAction(member.getUuid(), PacketGroupMemberAction.Action.PROMOTE);
                        Client.sendPacket(packet);
                        closeOverlay();
                    };
                }
            });
        }
        if (sg.hasPermission(7) && sg.compareRanks(player, this.member.getUuid()) && sg.canRankBeUpdated(player, this.member.getRank().demote())) {
            y += h + 5;
            addButton((Button) new MenuButton(-1, x, y, w, h, "Demote") {
                {
                    onClick = () -> {
                        PacketGroupMemberAction packet = new PacketGroupMemberAction(member.getUuid(), PacketGroupMemberAction.Action.DEMOTE);
                        Client.sendPacket(packet);
                        closeOverlay();
                    };
                }
            });
        }
        if (sg.hasPermission(5) && sg.compareRanks(player, this.member.getUuid())) {
            y += h + 5;
            addButton((Button) new MenuButton(-1, x, y, w, h, "Remove from group") {
                {
                    setTextColor(new FadingColor(opts.secondaryRed, opts.mainRed));
                    onClick = () -> {
                        PacketGroupMemberAction packet = new PacketGroupMemberAction(member.getUuid(), PacketGroupMemberAction.Action.REMOVE);
                        Client.sendPacket(packet);
                        closeOverlay();
                    };
                }
            });
        }
        while (this.pane.y + this.pane.height < y + h + 5)
            this.pane.height++;
        while (this.pane.y + this.pane.height > pane.y + pane.height - 5) {
            this.pane.y--;
            for (Button button : this.buttons)
                button.y--;
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\screens\screen_overlay\OverlayGroupMemberInteraction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */