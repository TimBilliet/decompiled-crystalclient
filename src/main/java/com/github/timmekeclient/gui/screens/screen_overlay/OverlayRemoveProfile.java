package com.github.timmekeclient.gui.screens.screen_overlay;

import com.github.timmekeclient.font.FontRenderer;
import com.github.timmekeclient.font.Fonts;
import com.github.timmekeclient.gui.buttons.Label;
import com.github.timmekeclient.gui.buttons.MenuButton;
import com.github.timmekeclient.gui.screens.ScreenProfiles;
import com.github.timmekeclient.handler.ProfileHandler;
import com.github.timmekeclient.util.objects.FadingColor;
import com.github.timmekeclient.util.objects.profiles.Profile;
import org.apache.commons.lang3.text.WordUtils;

public class OverlayRemoveProfile extends ScreenOverlay {
    private static final FontRenderer fr = Fonts.NUNITO_REGULAR_16;

    private final Profile profile;

    public OverlayRemoveProfile(Profile profile) {
        super(0, 0, 200, 10, "Remove Profile");
        this.profile = profile;
    }

    public void init() {
        String desc = String.format("Are you sure you wish to remove profile '%s'? Removing this profile is permanent and cannot be undone.", this.profile
                .getName());
        int y = this.pane.y + 28;
        for (String str : WordUtils.wrap(desc, 45).split("\n")) {
            addButton(new Label(this.pane.x + this.pane.width / 2, y, str, this.opts.neutralTextColor.getRGB(), fr));
            y += fr.getStringHeight();
        }
        y += 2;
        addButton(new MenuButton(-1, this.pane.x + 5, y, this.pane.width / 2 - 7, 18, "Cancel") {
            {
                onClick = () -> {
                    closeOverlay();
                };
            }
        });
        addButton(new MenuButton(-1, this.pane.x + this.pane.width / 2 + 2, y, this.pane.width / 2 - 7, 18, "Delete Profile") {
            {
                onClick = () -> {
                    ProfileHandler.getInstance().removeProfile(profile);
                    closeOverlay();
                    if (this.mc.currentScreen instanceof ScreenProfiles) {
                        ((ScreenProfiles) this.mc.currentScreen).initProfiles();
                    }
                };
                setTextColor(new FadingColor(opts.secondaryRed, opts.mainRed));
            }
        });
        while (this.pane.y + this.pane.height < y + 18 + 5)
            this.pane.height++;
        center();
    }
}