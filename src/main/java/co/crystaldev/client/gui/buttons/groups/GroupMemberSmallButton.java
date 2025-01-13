package co.crystaldev.client.gui.buttons.groups;

import co.crystaldev.client.cache.SkinCache;
import co.crystaldev.client.cache.UsernameCache;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.group.objects.GroupMember;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.gui.screens.screen_overlay.OverlayGroupMemberInteraction;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.FadingColor;
import co.crystaldev.client.util.objects.resources.CachedSkin;
import org.lwjgl.opengl.GL11;

public class GroupMemberSmallButton extends Button {
    private final GroupMember member;

    private final CachedSkin skin;

    private final FadingColor fadingColor;

    private final FadingColor textColor;

    public GroupMemberSmallButton(GroupMember member, int x, int y, int width, int height) {
        super(-1, x, y, width, height);
        this.member = member;
        this.fadingColor = new FadingColor(this.opts.neutralButtonBackground, this.opts.hoveredButtonBackground);
        this.textColor = new FadingColor(this.opts.neutralTextColor, this.opts.hoveredTextColor);
        this.fontRenderer = Fonts.NUNITO_SEMI_BOLD_16;
        this.skin = SkinCache.getInstance().getCachedSkin(this.member.getUuid());
    }

    public void drawButton(int mouseX, int mouseY, boolean hovered) {
        Screen.scissorStart(this.scissorPane);
        this.fadingColor.fade(hovered);
        this.textColor.fade(hovered);
        RenderUtils.drawRoundedRect(this.x, this.y, (this.x + this.width), (this.y + this.height), 8.0D, this.fadingColor
                .getCurrentColor().getRGB());
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderUtils.drawCustomSizedResource(this.skin.getResourceLocation(), this.x + 4, this.y + 4, this.height - 8, this.height - 8);
        String username = UsernameCache.getInstance().getUsername(this.member.getUuid());
        this.fontRenderer.drawString(username, this.x + 8 + this.height - 8, this.y + this.height / 2 - this.fontRenderer.getStringHeight() / 2, this.textColor
                .getCurrentColor().getRGB());
        RenderUtils.drawCircle((this.x + this.width - 6), this.y + this.height / 2.0F, 8.0F,
                this.member.isOnline() ? this.opts.mainColor.getRGB() : this.opts.mainDisabled.getRGB());
        Screen.scissorEnd(this.scissorPane);
    }

    public void onInteract(int mouseX, int mouseY, int mouseButton) {
        super.onInteract(mouseX, mouseY, mouseButton);
        if (mouseButton == 1)
            ((Screen) this.mc.currentScreen).addOverlay((Screen) new OverlayGroupMemberInteraction(this.member, mouseX, mouseY, 150));
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\buttons\groups\GroupMemberSmallButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */