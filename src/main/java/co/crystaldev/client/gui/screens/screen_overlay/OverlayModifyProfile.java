package co.crystaldev.client.gui.screens.screen_overlay;

import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.buttons.Label;
import co.crystaldev.client.gui.buttons.MenuButton;
import co.crystaldev.client.gui.buttons.SimpleColorPicker;
import co.crystaldev.client.gui.buttons.TextInputField;
import co.crystaldev.client.util.objects.profiles.Profile;

public class OverlayModifyProfile extends ScreenOverlay {
    private final Profile profile;

    private TextInputField name;

    private TextInputField autoSelect;

    private SimpleColorPicker color;

    public OverlayModifyProfile(Profile profile) {
        super(0, 0, 200, 10, "Modify Profile");
        this.profile = profile;
    }

    public void init() {
        super.init();
        int x = this.pane.x + 5;
        int y = this.pane.y + 24;
        int w = this.pane.width - 10;
        int h = 18;
        addButton(this.name = new TextInputField(-1, x + w / 2, y, w / 2, h, "Profile Name") {
            {
                setText(profile.getName());
                setMaxLength(32);
            }
        });
        addButton(new Label(x + 5, y + h / 2 - Fonts.NUNITO_REGULAR_20.getStringHeight() / 2, "Name", -1) {
            {
                setCentered(false);
            }
        });
        y += h + 5;
        addButton(this.autoSelect = new TextInputField(-1, x + w / 2, y, w / 2, h, "Server IP") {
            {
                setText(profile.getAutoUseServer() == null ? "" : profile.getAutoUseServer());
            }
        });
        addButton(new Label(x + 5, y + h / 2 - Fonts.NUNITO_REGULAR_20.getStringHeight() / 2, "Auto-Select", -1) {
            {
                setCentered(false);
            }
        });
        y += h + 7;
        addButton(this.color = new SimpleColorPicker(x + w / 2 + 3, y, w / 2 - 6, h - 6, this.profile.getColor()));
        addButton(new Label(x + 5, y + (h - 6) / 2 - Fonts.NUNITO_REGULAR_20.getStringHeight() / 2, "Color", -1) {
            {
                setCentered(false);
            }
        });
        y += h - 6 + 5;
        addButton(new MenuButton(-1, x, y, w, h, "Save Profile") {
            {
                onClick = () -> {
                    if (!name.getText().isEmpty()) {
                        profile.setName(name.getText());
                    }
                    profile.setAutoUseServer(autoSelect.getText().isEmpty() ? null : autoSelect.getText());
                    profile.setColor(color.getCurrentColor());
                    closeOverlay();
                };
            }
        });
        while (this.pane.y + this.pane.height < y + h + 5)
            this.pane.height++;
        center();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\screens\screen_overlay\OverlayModifyProfile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */