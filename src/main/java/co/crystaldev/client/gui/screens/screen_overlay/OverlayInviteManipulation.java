package co.crystaldev.client.gui.screens.screen_overlay;

import co.crystaldev.client.Client;
import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.buttons.MenuButton;
import co.crystaldev.client.handler.NotificationHandler;
import co.crystaldev.client.network.socket.client.group.PacketGroupInvitationAction;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class OverlayInviteManipulation extends ScreenOverlay {
    public OverlayInviteManipulation(int x, int y, int w, int h) {
        super(x, y, w, h, "Invite Management");
    }

    public void init() {
        super.init();
        int x = this.pane.x + 5;
        int y = this.pane.y + 26;
        int w = this.pane.width - 10;
        int h = 18;
        addButton((Button) new MenuButton(0, x, y, w, h, "Copy Invite") {
            {
                onClick = () -> {
                    if (GroupManager.getSelectedGroup() != null) {
                        if (GroupManager.getSelectedGroup().getInviteCode() != null) {
                            StringSelection selection = new StringSelection(GroupManager.getSelectedGroup().getInviteCode());
                            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
                            closeOverlay();
                            NotificationHandler.addNotification("Invitation code copied to clipboard!");
                        } else {
                            NotificationHandler.addNotification("Unable to copy invitation code to clipboard. Try resetting the code");
                        }

                    }
                };
            }
        });
        y += h + 5;
        addButton((Button) new MenuButton(1, x, y, w, h, "Reset Invite") {
            {
                onClick = () -> {
                    PacketGroupInvitationAction packet = new PacketGroupInvitationAction("", PacketGroupInvitationAction.Action.REQUEST_RESET);
                    Client.sendPacket(packet);
                    NotificationHandler.addNotification("Invitation code has been reset!");
                };
            }
        });
        while (this.pane.y + this.pane.height < y + h + 5)
            this.pane.height++;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\screens\screen_overlay\OverlayInviteManipulation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */