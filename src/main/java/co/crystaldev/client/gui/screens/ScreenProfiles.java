package co.crystaldev.client.gui.screens;

import co.crystaldev.client.Resources;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Pane;
import co.crystaldev.client.gui.buttons.MenuResourceButton;
import co.crystaldev.client.gui.buttons.ProfileButton;
import co.crystaldev.client.gui.buttons.SearchButton;
import co.crystaldev.client.gui.screens.screen_overlay.OverlayCreateProfile;
import co.crystaldev.client.handler.ProfileHandler;
import co.crystaldev.client.util.objects.profiles.Profile;

public class ScreenProfiles extends ScreenBase {
    private SearchButton search;

    public void init() {
        super.init();
        int half = this.header.height / 2 - Fonts.NUNITO_SEMI_BOLD_24.getStringHeight() / 2;
        int h = this.header.height - half * 2;
        addButton((this.search = new SearchButton(this.header.x + this.header.width - half - h, this.header.y + half, h, h * 6, h)));
        this.content.setScrollIf(b -> b.hasAttribute("profile_button"));
        initProfiles();
    }

    public void draw(int mouseX, int mouseY, float partialTicks) {
        super.draw(mouseX, mouseY, mouseX);
        this.content.scroll(this, mouseX, mouseY);
        if (this.search.wasUpdated())
            initProfiles();
    }

    public void initProfiles() {
        removeButton(b -> b.hasAttribute("profile_button"));
        int w = (int) (this.content.width * 0.7D);
        int h = 28;
        int x = this.content.x + this.content.width / 2 - w / 2;
        int y = this.content.y + 5;
        final Pane scissor = this.content.scale(this.getScaledScreen());
        addButton(new MenuResourceButton(-1, x, y, w, h, "Create Profile", Resources.PROFILE_ADD, 16), b-> {
            b.setFontRenderer(Fonts.NUNITO_SEMI_BOLD_24);
            b.addAttribute("profile_button");
            b.onClick = ()->{
              addOverlay(new OverlayCreateProfile());
            };
        } );
        y += h + 8;
        for (Profile profile : ProfileHandler.getInstance().getProfiles()) {
            if (!this.search.matchesQuery(profile.getName()))
                continue;
            addButton(new ProfileButton(profile, x, y, w, h) { {
                setScissorPane(scissor);
                addAttribute("profile_button");
            }
            });
            y += h + 8;
        }
        this.content.updateMaxScroll(this, 5);
    }
}