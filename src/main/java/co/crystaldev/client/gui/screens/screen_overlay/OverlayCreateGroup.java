package co.crystaldev.client.gui.screens.screen_overlay;

import co.crystaldev.client.Client;
import co.crystaldev.client.font.FontRenderer;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.gui.buttons.Label;
import co.crystaldev.client.gui.buttons.MenuButton;
import co.crystaldev.client.gui.buttons.TextInputField;
import co.crystaldev.client.handler.NotificationHandler;
import co.crystaldev.client.network.socket.client.group.PacketCreateGroup;
import org.apache.commons.lang3.text.WordUtils;
import org.lwjgl.input.Keyboard;

public class OverlayCreateGroup extends ScreenOverlay {
    private static final FontRenderer fr = Fonts.NUNITO_REGULAR_16;

    private final String description = "Groups are the simple and superior way to strategize, communicate, and benefit your team.";

    private TextInputField nameInput;

    public OverlayCreateGroup(int x, int y, int w) {
        super(x, y, w, 10, "Create a group");
        Keyboard.enableRepeatEvents(true);
    }

    public void init() {
        int x = this.pane.x + 5;
        int y = this.pane.y + 28;
        int w = this.pane.width - 10;
        int h = 18;
        for (String str : WordUtils.wrap("Groups are the simple and superior way to strategize, communicate, and benefit your team.", 50).split("\n")) {
            addButton((Button) new Label(x + w / 2, y, str, this.opts.neutralTextColor.getRGB(), fr));
            y += fr.getStringHeight();
        }
        y += 2;
        addButton((Button) (this.nameInput = new TextInputField(-1, x, y, w, h, "Enter your desired group name") {
            {
                setMaxLength(20);
            }
        }));
        y += h + 5;
        addButton((Button) new MenuButton(-1, x, y, w, h, "Create Group") {
            {
                onClick = () -> {
                    if (nameInput.getText().length() > 3) {
                        if (GroupManager.getGroups().size() < 10) {
                            PacketCreateGroup packet = new PacketCreateGroup(nameInput.getText());
                            Client.sendPacket(packet);
                            closeOverlay();
                            NotificationHandler.addNotification("Creating your group!");

                        } else {
                            NotificationHandler.addNotification("You are currently at the group limit");

                        }
                    } else {
                        NotificationHandler.addNotification("Group name must be atleast 3 letters long");

                    }
                };
            }
        });
        y += h + 9;
        addButton((Button) new Label(x + w / 2, y, "Already invited to a group?", -1, fr));
        y += 11;
        addButton((Button) new MenuButton(-1, x, y, w, h, "Join Group") {
            {
                onClick = () -> {
                    ((Screen) this.mc.currentScreen).addOverlay(new OverlayJoinGroup(pane.x, pane.y, pane.width));
                    closeOverlay();
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