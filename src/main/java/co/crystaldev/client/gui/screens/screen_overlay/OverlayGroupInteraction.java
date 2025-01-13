package co.crystaldev.client.gui.screens.screen_overlay;

import co.crystaldev.client.Client;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.group.objects.Group;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Pane;
import co.crystaldev.client.gui.ScrollPane;
import co.crystaldev.client.gui.buttons.MenuButton;
import co.crystaldev.client.gui.screens.ScreenBase;
import co.crystaldev.client.network.Packet;
import co.crystaldev.client.network.socket.client.group.PacketGroupUpdate;
import co.crystaldev.client.util.enums.GroupCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class OverlayGroupInteraction extends ScreenOverlay {
    private final Group group;

    public OverlayGroupInteraction(Group group, int x, int y, int w) {
        super(x, y, w, 10, group.getName());
        this.group = group;
        this.dimBackground = false;
    }

    public void init() {
        this.pane.width = Math.max(this.pane.width, Fonts.NUNITO_SEMI_BOLD_18.getStringWidth(this.group.getName()) + 6);
        int x = this.pane.x + 5;
        int y = this.pane.y + 23;
        int w = this.pane.width - 10;
        int h = 18;
        List<GroupCategory> sorted = new ArrayList<>(Arrays.asList(GroupCategory.values()));
        sorted.sort(Comparator.comparing(c -> c.toString().toLowerCase()));
        for (GroupCategory category : sorted) {
            addButton((Button) new MenuButton(-1, x, y, w, h, category.getTranslationKey()) {

            });
            y += h + 5;
        }
        addButton((Button) new MenuButton(-1, x, y, w, h, "Leave Group") {

        });
        while (this.pane.y + this.pane.height < y + h + 5)
            this.pane.height++;
        if (this.mc.currentScreen instanceof ScreenBase) {
            ScrollPane scrollPane = ((ScreenBase) this.mc.currentScreen).content;
            while (this.pane.y + this.pane.height > ((Pane) scrollPane).y + ((Pane) scrollPane).height - 5) {
                this.pane.y--;
                for (Button button : this.buttons)
                    button.y--;
            }
        }
    }

    private void selectGroup() {
        Group sel = GroupManager.getSelectedGroup();
        if (sel != null && sel.getId().equals(this.group.getId()))
            return;
        PacketGroupUpdate packet = new PacketGroupUpdate(this.group.getId());
        Client.sendPacket((Packet) packet);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\screens\screen_overlay\OverlayGroupInteraction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */