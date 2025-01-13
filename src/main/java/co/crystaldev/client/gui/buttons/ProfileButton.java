package co.crystaldev.client.gui.buttons;

import co.crystaldev.client.Resources;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.gui.screens.screen_overlay.OverlayModifyProfile;
import co.crystaldev.client.gui.screens.screen_overlay.OverlayRemoveProfile;
import co.crystaldev.client.handler.ProfileHandler;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.FadingColor;
import co.crystaldev.client.util.objects.profiles.Profile;

public class ProfileButton extends Button {
    private final ProfileHandler handler = ProfileHandler.getInstance();

    private final Profile profile;

    private final FadingColor background;

    private final FadingColor text;

    private final FadingColor outline;

    private final FadingColor outline1;

    private final ResourceButton remove;

    private final ResourceButton settings;

    public ProfileButton(Profile profile, int x, int y, int width, int height) {
        super(-1, x, y, width, height);
        this.profile = profile;
        this.fontRenderer = Fonts.NUNITO_SEMI_BOLD_24;
        this.background = new FadingColor(this.opts.neutralButtonBackground, this.opts.hoveredButtonBackground);
        this.text = new FadingColor(this.profile.getColor().darker(), this.profile.getColor());
        this.outline = new FadingColor(this.opts.mainDisabled, this.profile.getColor().darker());
        this.outline1 = new FadingColor(this.opts.secondaryDisabled, this.profile.getColor());
        int bSize = 16;
        this.remove = new ResourceButton(-1, this.x + this.width - bSize - this.height - bSize + 4, this.y + this.height / 2 - bSize / 2, bSize, bSize, Resources.CLOSE);
        this.settings = new ResourceButton(-1, this.x + this.width - bSize - this.height - bSize + 4 - 5 - bSize, this.y + this.height / 2 - bSize / 2, bSize, bSize, Resources.COG);
    }

    public void onUpdate() {
        int bSize = 16;
        this.settings.y = this.y + this.height / 2 - bSize / 2;
    }

    public void drawButton(int mouseX, int mouseY, boolean hovered) {
        Screen.scissorStart(this.scissorPane);
        boolean selected = this.profile.equals(this.handler.getSelectedProfile());
        setProfileColor();
        this.background.fade(hovered);
        this.text.fade(hovered);
        this.outline.fade(selected);
        this.outline1.fade(selected);
        this.text.setForceUpdate(false);
        this.outline.setForceUpdate(false);
        this.outline1.setForceUpdate(false);
        RenderUtils.drawRoundedRectWithGradientBorder(this.x, this.y, (this.x + this.width), (this.y + this.height), 9.0D, 3.5F, this.outline
                .getCurrentColor().getRGB(), this.outline1.getCurrentColor().getRGB(), this.background.getCurrentColor().getRGB());
        this.fontRenderer.drawString(this.profile.getName(), this.x + 8, this.y + this.height / 2 - this.fontRenderer.getStringHeight() / 2, this.text
                .getCurrentColor().getRGB());
        this.remove.drawButton(mouseX, mouseY, this.remove.isHovered(mouseX, mouseY));
        this.settings.drawButton(mouseX, mouseY, this.settings.isHovered(mouseX, mouseY));
        Screen.scissorEnd(this.scissorPane);
    }

    public void onInteract(int mouseX, int mouseY, int mouseButton) {
        super.onInteract(mouseX, mouseY, mouseButton);
        if (this.settings.isHovered(mouseX, mouseY)) {
            ((Screen) this.mc.currentScreen).addOverlay((Screen) new OverlayModifyProfile(this.profile));
        } else if (this.remove.isHovered(mouseX, mouseY)) {
            ((Screen) this.mc.currentScreen).addOverlay((Screen) new OverlayRemoveProfile(this.profile));
        } else if (mouseButton == 0) {
            boolean selected = this.profile.equals(this.handler.getSelectedProfile());
            if (selected) {
                this.handler.swapToProfile((Profile) null);
            } else {
                this.handler.swapToProfile(this.profile);
            }
            this.handler.unsetLastProfile();
        }
    }

    private void setProfileColor() {
        if (!this.text.getColor2().equals(this.profile.getColor())) {
            this.text.setColor1(this.profile.getColor().darker());
            this.text.setColor2(this.profile.getColor());
            this.text.setForceUpdate(true);
        }
        if (!this.outline1.getColor2().equals(this.profile.getColor())) {
            this.outline.setColor2(this.profile.getColor().darker());
            this.outline1.setColor2(this.profile.getColor());
            this.outline.setForceUpdate(true);
            this.outline1.setForceUpdate(true);
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\buttons\ProfileButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */