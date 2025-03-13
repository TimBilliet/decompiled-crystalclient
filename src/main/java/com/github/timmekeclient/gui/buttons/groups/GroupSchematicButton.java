package com.github.timmekeclient.gui.buttons.groups;

import com.github.timmekeclient.Client;
import com.github.timmekeclient.font.Fonts;
import com.github.timmekeclient.group.GroupManager;
import com.github.timmekeclient.group.objects.GroupSchematic;
import com.github.timmekeclient.gui.Button;
import com.github.timmekeclient.gui.Screen;
import com.github.timmekeclient.gui.buttons.MenuButton;
import com.github.timmekeclient.handler.SchematicHandler;
import com.github.timmekeclient.network.socket.client.group.PacketGroupSchematicAction;
import com.github.timmekeclient.util.RenderUtils;
import com.github.timmekeclient.util.enums.EnumActionShift;
import com.github.timmekeclient.util.objects.FadingColor;

import java.awt.*;

public class GroupSchematicButton extends Button {
    private final GroupSchematic schematic;

    private final MenuButton removeButton;

    private final FadingColor fadingColor;

    private final FadingColor textColor;

    public GroupSchematicButton(GroupSchematic schematic, int x, int y, int width, int height) {
        super(-1, x, y, width, height, schematic.getName());
        this.schematic = schematic;
        if (GroupManager.getSelectedGroup().hasPermission(10)) {
            this.removeButton = new MenuButton(-1, this.x + this.width - 5 - 25, this.y + 2, 25, this.height - 4, "Delete");
            this.removeButton.setDrawBackground(false);
            this.removeButton.setFontRenderer(Fonts.NUNITO_SEMI_BOLD_16);
            this.removeButton.getTextColor().setColor2(Color.RED);
        } else {
            this.removeButton = null;
        }
        this.fadingColor = new FadingColor(this.opts.neutralButtonBackground, this.opts.hoveredButtonBackground);
        this.textColor = new FadingColor(this.opts.neutralTextColor, this.opts.hoveredTextColor);
        this.fontRenderer = Fonts.NUNITO_SEMI_BOLD_20;
    }

    public void onUpdate() {
        if (this.removeButton != null)
            this.removeButton.y = this.y + 2;
    }

    public void drawButton(int mouseX, int mouseY, boolean hovered) {
        Screen.scissorStart(this.scissorPane);
        this.fadingColor.fade(hovered);
        this.textColor.fade(hovered);
        RenderUtils.drawRoundedRect(this.x, this.y, (this.x + this.width), (this.y + this.height), 6.0D, this.fadingColor
                .getCurrentColor().getRGB());
        this.fontRenderer.drawString(this.displayText, this.x + 5, this.y + this.height / 2 - this.fontRenderer.getStringHeight() / 2, this.textColor
                .getCurrentColor().getRGB());
        if (hovered && this.removeButton != null)
            this.removeButton.drawButton(mouseX, mouseY, this.removeButton.isHovered(mouseX, mouseY));
        Screen.scissorEnd(this.scissorPane);
    }

    public void onInteract(int mouseX, int mouseY, int mouseButton) {
        if (this.removeButton != null && this.removeButton.isHovered(mouseX, mouseY)) {
            PacketGroupSchematicAction packet = new PacketGroupSchematicAction(this.schematic, EnumActionShift.REMOVE);
            Client.sendPacket(packet);
        } else {
            SchematicHandler.getInstance().loadSchematic(this.schematic.getDir(), this.schematic.getId());
        }
    }
}
