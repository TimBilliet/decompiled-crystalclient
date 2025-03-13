package com.github.timmekeclient.gui.screens.screen_overlay;

import com.github.timmekeclient.gui.buttons.MenuButton;
import com.github.timmekeclient.gui.buttons.TextInputField;

public class OverlayOfflineAccount extends ScreenOverlay {
    public OverlayOfflineAccount() {
        super(5, 5, 300, 20, "Offline Accounts");
    }

    public void init() {
        super.init();
        int x = this.pane.x + 5;
        int y = this.pane.y + 24;
        int w = this.pane.width - 10;
        int h = 18;
        TextInputField field;
        addButton(field = new TextInputField(0, x, y, w, h, "Offline Account"), b -> {
            b.setOnTextInput(a -> {
            });
        });
        y += h + 5;
        addButton(new MenuButton(0, x, y, w, h, "Add"), b -> b.setOnClick(() -> {

        }));
        while (this.pane.y + this.pane.height < y + h + 5)
            this.pane.height++;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\screens\screen_overlay\OverlayOfflineAccount.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */