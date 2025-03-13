package com.github.timmekeclient.gui.screens.screen_overlay;

import com.github.timmekeclient.Client;
import com.github.timmekeclient.font.FontRenderer;
import com.github.timmekeclient.font.Fonts;
import com.github.timmekeclient.group.objects.Group;
import com.github.timmekeclient.gui.Button;
import com.github.timmekeclient.gui.buttons.Label;
import com.github.timmekeclient.gui.buttons.MenuButton;
import com.github.timmekeclient.network.socket.client.group.PacketLeaveGroup;
import com.github.timmekeclient.util.objects.FadingColor;
import org.apache.commons.lang3.text.WordUtils;

public class OverlayLeaveGroup extends ScreenOverlay {
    private static final FontRenderer fr = Fonts.NUNITO_REGULAR_16;

    private final Group group;

    public OverlayLeaveGroup(Group group) {
        super(0, 0, 200, 10, "Leave group");
        this.group = group;
    }

    public void init() {
        String desc = String.format("Are you sure you wish to leave the group %s? You will only be able to rejoin with the group invitation code.", this.group
                .getName());
        int y = this.pane.y + 28;
        for (String str : WordUtils.wrap(desc, 45).split("\n")) {
            addButton((Button) new Label(this.pane.x + this.pane.width / 2, y, str, this.opts.neutralTextColor.getRGB(), fr));
            y += fr.getStringHeight();
        }
        y += 2;
        addButton((Button) new MenuButton(-1, this.pane.x + 5, y, this.pane.width / 2 - 7, 18, "Cancel") {
            {
                onClick = () -> {
                    closeOverlay();
                };
            }
        });
        addButton((Button) new MenuButton(-1, this.pane.x + this.pane.width / 2 + 2, y, this.pane.width / 2 - 7, 18, "Leave Group") {
            {
                onClick = () -> {
                    PacketLeaveGroup packet = new PacketLeaveGroup(group.getId());
                    Client.sendPacket(packet);
                    closeOverlay();
                };
                setTextColor(new FadingColor(opts.secondaryRed, opts.mainRed));
            }
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