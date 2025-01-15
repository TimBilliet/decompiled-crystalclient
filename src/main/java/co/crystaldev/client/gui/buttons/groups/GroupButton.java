package co.crystaldev.client.gui.buttons.groups;

import co.crystaldev.client.Client;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.group.objects.Group;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.gui.screens.screen_overlay.OverlayGroupInteraction;
import co.crystaldev.client.network.Packet;
import co.crystaldev.client.network.socket.client.group.PacketGroupUpdate;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.FadingColor;

public class GroupButton extends Button {
    private static long lastClickTime = 0L;

    private final FadingColor backgroundColor;

    private final FadingColor outlineColor1;

    private final FadingColor outlineColor2;

    private final FadingColor textColor;

    private final FadingColor textColor1;

    private final Group group;

    public Group getGroup() {
        return this.group;
    }

    private long animationStart = System.currentTimeMillis() - 5000L;

    private final int expandedWidth;

    private int targetWidth = -1;

    private int currentWidth = -1;

    private boolean wasHovered = false;

    public GroupButton(Group group, int x, int y, int width, int height) {
        super(-1, x, y, width, height, "");
        this.fontRenderer = Fonts.NUNITO_SEMI_BOLD_18;
        this.group = group;
        for (String str : group.getName().split(" ")) {
            for (String s : str.split("")) {
                if ("abcdefghijklmnopqrstuvwxyz0123456789".contains(s.toLowerCase())) {
                    this.displayText += s;
                    break;
                }
            }
            if (this.displayText.length() > 2)
                break;
        }
        this.expandedWidth = Math.max(this.fontRenderer.getStringWidth(group.getName()) + 16, this.width);
        this.backgroundColor = new FadingColor(this.opts.neutralButtonBackground, this.opts.hoveredButtonBackground);
        this.outlineColor1 = new FadingColor(this.opts.mainDisabled, this.opts.mainEnabled);
        this.outlineColor2 = new FadingColor(this.opts.secondaryDisabled, this.opts.secondaryEnabled);
        this.textColor = new FadingColor(this.opts.getColor(this.opts.neutralTextColor, 0), this.opts.hoveredTextColor);
        this.textColor1 = new FadingColor(this.opts.getColor(this.opts.neutralTextColor, 0), this.opts.hoveredTextColor);
    }

    public void drawButton(int mouseX, int mouseY, boolean hovered) {
        Screen.scissorStart(this.scissorPane);
        Group sel = GroupManager.getSelectedGroup();
        boolean groupSelected = (sel != null && sel.getId().equals(this.group.getId()));
        if (this.targetWidth == -1)
            this.targetWidth = this.currentWidth = hovered ? this.expandedWidth : this.width;
        this

                .currentWidth = (this.targetWidth < this.currentWidth) ? (int) Math.max(this.currentWidth - (System.currentTimeMillis() - this.animationStart) / 30L, this.targetWidth) : (int) Math.min(this.currentWidth + (System.currentTimeMillis() - this.animationStart) / 30L, this.targetWidth);
        this.targetWidth = hovered ? this.expandedWidth : this.width;
        if (this.wasHovered != hovered) {
            this.wasHovered = hovered;
            this.animationStart = System.currentTimeMillis();
        }
        this.backgroundColor.fade(hovered);
        this.outlineColor1.fade(groupSelected);
        this.outlineColor2.fade(groupSelected);
        this.textColor.fade(!hovered);
        this.textColor1.fade(hovered);
        RenderUtils.drawRoundedRectWithGradientBorder(this.x, this.y, (this.x + this.currentWidth), (this.y + this.height), 20.0D, 1.7F, this.outlineColor1
                .getCurrentColor().getRGB(), this.outlineColor2.getCurrentColor().getRGB(), this.backgroundColor.getCurrentColor().getRGB());
        if (this.textColor.getCurrentColor().getAlpha() > 4)
            this.fontRenderer.drawCenteredString(this.displayText, this.x + this.width / 2, this.y + this.height / 2, this.textColor
                    .getCurrentColor().getRGB());
        if (this.textColor1.getCurrentColor().getAlpha() > 4)
            this.fontRenderer.drawCenteredString(this.group.getName(), this.x + this.expandedWidth / 2, this.y + this.height / 2, this.textColor1
                    .getCurrentColor().getRGB());
        Screen.scissorEnd(this.scissorPane);
    }

    public void onInteract(int mouseX, int mouseY, int mouseButton) {
        super.onInteract(mouseX, mouseY, mouseButton);
        if (mouseButton == 1) {
            ((Screen) this.mc.currentScreen).addOverlay((Screen) new OverlayGroupInteraction(this.group, mouseX, mouseY, 100));
        } else {
            Group sel = GroupManager.getSelectedGroup();
            if (System.currentTimeMillis() - lastClickTime > 3000L) {
                String newId = this.group.getId();
                if (sel != null && sel.getId().equals(this.group.getId()))
                    newId = null;
                lastClickTime = System.currentTimeMillis();
                PacketGroupUpdate packet = new PacketGroupUpdate(newId);
                Client.sendPacket((Packet) packet);
            }
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\buttons\groups\GroupButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */