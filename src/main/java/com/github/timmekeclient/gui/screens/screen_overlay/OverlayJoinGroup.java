package com.github.timmekeclient.gui.screens.screen_overlay;

import com.github.timmekeclient.Client;
import com.github.timmekeclient.Resources;
import com.github.timmekeclient.font.Fonts;
import com.github.timmekeclient.gui.Button;
import com.github.timmekeclient.gui.Screen;
import com.github.timmekeclient.gui.buttons.MenuButton;
import com.github.timmekeclient.gui.buttons.ResourceButton;
import com.github.timmekeclient.gui.buttons.TextInputField;
import com.github.timmekeclient.handler.NotificationHandler;
import com.github.timmekeclient.network.socket.client.group.PacketGroupInvitationAction;
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
        addButton((Button) new ResourceButton(-1, this.pane.x + 5, this.pane.y + 5, Fonts.NUNITO_SEMI_BOLD_18.getStringHeight() + 2, Fonts.NUNITO_SEMI_BOLD_18
                .getStringHeight() + 2, Resources.CHEVRON_LEFT) {
            {
                setOnClick(() -> {
                    ((Screen) this.mc.currentScreen).addOverlay(new OverlayCreateGroup(pane.x, pane.y, pane.width));
                    closeOverlay();
                });
            }

        });
        addButton((Button) (this.inviteInput = new TextInputField(-1, x, y, w, h, "CC-XXXXXXXXXXXX") {
            {
                setMaxLength(15);
            }
        }));
        y += h + 5;
        addButton((Button) new MenuButton(-1, x, y, w, h, "Join Group") {
            {
                onClick = () -> {
                    if (inviteInput.getText().length() == inviteInput.getMaxLength()) {
                        closeOverlay();
                        PacketGroupInvitationAction packet = new PacketGroupInvitationAction(inviteInput.getText(), PacketGroupInvitationAction.Action.REQUEST_JOIN);
                        Client.sendPacket(packet);
                    } else {
                        NotificationHandler.addNotification("You must give a valid invite code");
                    }
                };
            }
        });
        while (this.pane.y + this.pane.height < y + h + 5)
            this.pane.height++;
        center();
    }

    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
}
