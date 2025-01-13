package co.crystaldev.client.gui.buttons.groups;

import co.crystaldev.client.Client;
import co.crystaldev.client.cache.SkinCache;
import co.crystaldev.client.cache.UsernameCache;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.group.objects.GroupMember;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.gui.buttons.MenuButton;
import co.crystaldev.client.gui.screens.screen_overlay.OverlayGroupMemberInteraction;
import co.crystaldev.client.network.Packet;
import co.crystaldev.client.network.socket.client.group.PacketPendingGroupMemberAction;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.FadingColor;
import co.crystaldev.client.util.objects.resources.CachedSkin;
import org.lwjgl.opengl.GL11;

public class GroupMemberLargeButton extends Button {
    private final GroupMember member;

    private final CachedSkin skin;

    private final FadingColor fadingColor;

    private final FadingColor textColor;

    private final MenuButton acceptButton;

    private final MenuButton denyButton;

    private final boolean pending;

    public GroupMemberLargeButton(GroupMember member, int x, int y, int width, int height, boolean pending) {
        super(-1, x, y, width, height);
        this.member = member;
        this.fadingColor = new FadingColor(this.opts.neutralButtonBackground, this.opts.hoveredButtonBackground);
        this.textColor = new FadingColor(this.opts.neutralTextColor, this.opts.hoveredTextColor);
        this.fontRenderer = Fonts.NUNITO_SEMI_BOLD_16;
        this.skin = SkinCache.getInstance().getCachedSkin(this.member.getUuid());
        this.pending = pending;
        if (this.pending && GroupManager.getSelectedGroup() != null && GroupManager.getSelectedGroup().hasPermission(8)) {
            this.denyButton = new MenuButton(-1, this.x + this.width - 5 - 40, this.y + 2, 40, this.height - 4, "Deny") {

            };
            this.acceptButton = new MenuButton(-1, this.x + this.width - 10 - 80, this.y + 2, 40, this.height - 4, "Accept") {

            };
        } else {
            this.acceptButton = this.denyButton = null;
        }
    }

    public void onUpdate() {
        if (this.denyButton != null)
            this.denyButton.y = this.y + 2;
        if (this.acceptButton != null)
            this.acceptButton.y = this.y + 2;
    }

    public void drawButton(int mouseX, int mouseY, boolean hovered) {
        Screen.scissorStart(this.scissorPane);
        this.fadingColor.fade(hovered);
        this.textColor.fade(hovered);
        if (this.member == null)
            return;
        RenderUtils.drawRoundedRect(this.x, this.y, (this.x + this.width), (this.y + this.height), 8.0D, this.fadingColor
                .getCurrentColor().getRGB());
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderUtils.drawCustomSizedResource(this.skin.getResourceLocation(), this.x + 4, this.y + 4, this.height - 8, this.height - 8);
        String username = UsernameCache.getInstance().getUsername(this.member.getUuid());
        this.fontRenderer.drawString(username, this.x + 8 + this.height - 8, this.y + this.height / 2 - this.fontRenderer.getStringHeight() / 2, this.textColor
                .getCurrentColor().getRGB());
        if (this.pending && this.acceptButton != null && this.denyButton != null) {
            this.acceptButton.drawButton(mouseX, mouseY, this.acceptButton.isHovered(mouseX, mouseY));
            this.denyButton.drawButton(mouseX, mouseY, this.denyButton.isHovered(mouseX, mouseY));
        } else {
            RenderUtils.drawCircle((this.x + this.width - 6), this.y + this.height / 2.0F, 8.0F,
                    this.member.isOnline() ? this.opts.mainColor.getRGB() : this.opts.mainDisabled.getRGB());
        }
        Screen.scissorEnd(this.scissorPane);
    }

    public void onInteract(int mouseX, int mouseY, int mouseButton) {
        super.onInteract(mouseX, mouseY, mouseButton);
        if (this.pending && this.acceptButton.isHovered(mouseX, mouseY)) {
            PacketPendingGroupMemberAction packet = new PacketPendingGroupMemberAction(this.member.getUuid(), PacketPendingGroupMemberAction.Action.ACCEPT);
            Client.sendPacket((Packet) packet);
        } else if (this.pending && this.denyButton.isHovered(mouseX, mouseY)) {
            PacketPendingGroupMemberAction packet = new PacketPendingGroupMemberAction(this.member.getUuid(), PacketPendingGroupMemberAction.Action.DENY);
            Client.sendPacket((Packet) packet);
        } else if (mouseButton == 1) {
            ((Screen) this.mc.currentScreen).addOverlay((Screen) new OverlayGroupMemberInteraction(this.member, mouseX, mouseY, 150));
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\buttons\groups\GroupMemberLargeButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */