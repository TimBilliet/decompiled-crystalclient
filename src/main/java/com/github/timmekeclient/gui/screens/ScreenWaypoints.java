package com.github.timmekeclient.gui.screens;

import com.github.timmekeclient.gui.Pane;
import com.github.timmekeclient.gui.buttons.WaypointButton;
import com.github.timmekeclient.handler.WaypointHandler;
import com.github.timmekeclient.util.objects.Waypoint;

public class ScreenWaypoints extends ScreenBase {
    public void init() {
        super.init();
        this.content.setScrollIf(b -> b.hasAttribute("waypoint_button"));
        initWaypoints();
    }

    public void draw(int mouseX, int mouseY, float partialTicks) {
        super.draw(mouseX, mouseY, partialTicks);
        this.content.scroll(this, mouseX, mouseY);
    }

    public void initWaypoints() {
        removeButton(b -> b.hasAttribute("waypoint_button"));
        int w = this.content.width / 2 - 15;
        int h = 20;
        int x = this.content.x + 10;
        int y = this.content.y + 5;
        final Pane scissor = this.content.scale(getScaledScreen());
        int index = 0;
        for (Waypoint waypoint : WaypointHandler.getInstance().getRegisteredWaypoints()) {
            if (!waypoint.isCanBeDeleted() || waypoint.getDuration() > -1L)
                continue;
            if (waypoint.isSameServer() && waypoint.isSameWorld()) {
                addButton(new WaypointButton(waypoint, (index % 2 == 0) ? x : (x + w + 10), y, w, h), b -> {
                    b.addAttribute("waypoint_button");
                    b.setScissorPane(scissor);
                });
                if (index % 2 != 0)
                    y += h + 5;
                index++;
            }
        }
        if (index == 0)
            addScreenMessage("There are currently no waypoints created for this server");
        this.content.updateMaxScroll(this, 0);
        this.content.addScrollbarToScreen(this);
    }
}