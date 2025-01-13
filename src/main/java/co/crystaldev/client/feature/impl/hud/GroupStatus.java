package co.crystaldev.client.feature.impl.hud;

import co.crystaldev.client.feature.annotations.properties.Colour;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.Toggle;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.HudModule;
import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.group.objects.GroupMember;
import co.crystaldev.client.group.objects.PlayerStatusUpdate;
import co.crystaldev.client.gui.GuiOptions;
import co.crystaldev.client.util.ColorObject;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.enums.AnchorRegion;
import co.crystaldev.client.util.enums.ChatColor;
import co.crystaldev.client.util.objects.ModulePosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.util.MathHelper;

@ModuleInfo(name = "Group Status", description = "View the status of all your teammates onscreen", category = Category.HUD)
public class GroupStatus
        extends HudModule {
    @Toggle(label = "Use Boots")
    public boolean showBoots = false;
    @Colour(label = "Header Color", isTextRender = true)
    public ColorObject headerColor = ColorObject.fromColor((GuiOptions.getInstance()).mainColor).setBold(true).setUnderline(true);
    @Colour(label = "Text Color", isTextRender = true)
    public ColorObject textColor = new ColorObject(255, 255, 255, 255);


    private static GroupStatus INSTANCE;


    private final List<PlayerStatusUpdate> displayed = new ArrayList<>();
    private long lastUpdate = 0L;

    public GroupStatus() {
        this.enabled = false;
        this.position = new ModulePosition(AnchorRegion.CENTER_RIGHT, 5.0F, 120.0F);
        this.width = this.mc.fontRendererObj.getStringWidth(ChatColor.translate("&b&lMember&r  &b&lPots&r  &b&lHelmet&r  &b&lHealth&r"));
        this.height = this.mc.fontRendererObj.FONT_HEIGHT * 3;
        INSTANCE = this;
    }


    public void draw() {
        if (GroupManager.getSelectedGroup() == null) {
            return;
        }

        if (System.currentTimeMillis() - this.lastUpdate > 2500L) {
            this.displayed.clear();
            this.displayed.addAll((Collection<? extends PlayerStatusUpdate>) GroupManager.getSelectedGroup().getMembers().stream()
                    .filter(GroupMember::isOnline)
                    .filter(GroupMember::hasStatus)
                    .map(GroupMember::getStatus)
                    .collect(Collectors.toList()));
            Collections.sort(this.displayed);


            this.displayed.forEach(PlayerStatusUpdate::updateUsername);


            while (this.displayed.size() > 5) {
                this.displayed.remove(this.displayed.size() - 1);
            }
            this.lastUpdate = System.currentTimeMillis();
        }

        if (this.displayed.isEmpty()) {
            return;
        }
        int renderX = getRenderX();
        int renderY = getRenderY();
        int x = renderX, nextX = renderX;
        int y = renderY;

        nextX = Math.max(nextX, RenderUtils.drawString("Member", x, y, this.headerColor) + 3);
        y += this.mc.fontRendererObj.FONT_HEIGHT + 2;
        for (PlayerStatusUpdate s : this.displayed) {
            nextX = Math.max(nextX, RenderUtils.drawString(s.getUsername(), x, y, this.textColor) + 3);
            y += this.mc.fontRendererObj.FONT_HEIGHT;
        }
        x = nextX;
        y = renderY;

        nextX = Math.max(nextX, RenderUtils.drawString("Pots", x, y, this.headerColor) + 3);
        y += this.mc.fontRendererObj.FONT_HEIGHT + 2;
        for (PlayerStatusUpdate s : this.displayed) {
            nextX = Math.max(nextX, RenderUtils.drawString(Integer.toString(s.getPots()), x, y, this.textColor) + 3);
            y += this.mc.fontRendererObj.FONT_HEIGHT;
        }
        x = nextX;
        y = renderY;

        nextX = Math.max(nextX, RenderUtils.drawString(this.showBoots ? "Boots" : "Helmet", x, y, this.headerColor) + 3);
        y += this.mc.fontRendererObj.FONT_HEIGHT + 2;
        for (PlayerStatusUpdate s : this.displayed) {
            nextX = Math.max(nextX, RenderUtils.drawString(String.format("%.1f", new Object[]{Float.valueOf(MathHelper.clamp_float((this.showBoots ? s.getBoots() : s.getHelmet()) * 100.0F, 0.0F, 100.0F))}) + '%', x, y, this.textColor) + 3);
            y += this.mc.fontRendererObj.FONT_HEIGHT;
        }
        x = nextX;
        y = renderY;

        nextX = Math.max(nextX, RenderUtils.drawString("Health", x, y, this.headerColor) + 3);
        y += this.mc.fontRendererObj.FONT_HEIGHT + 2;
        for (PlayerStatusUpdate s : this.displayed) {
            nextX = Math.max(nextX, RenderUtils.drawString(String.format("❤ %d", new Object[]{Integer.valueOf(s.getHealth())}), x, y, this.textColor) + 3);
            y += this.mc.fontRendererObj.FONT_HEIGHT;
        }
        this.width = nextX - renderX;
        this.height = y - renderY;
    }

    public static GroupStatus getInstance() {
        return INSTANCE;
    }
}
