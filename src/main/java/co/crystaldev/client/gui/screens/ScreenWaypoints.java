package co.crystaldev.client.gui.screens;

import co.crystaldev.client.gui.Pane;
import co.crystaldev.client.gui.buttons.WaypointButton;
import co.crystaldev.client.handler.WaypointHandler;
import co.crystaldev.client.util.objects.Waypoint;

public class ScreenWaypoints extends ScreenBase {
  public void init() {
    super.init();
    this.content.setScrollIf(b -> b.hasAttribute("waypoint_button"));
    initWaypoints();
  }

  //TODO add scroll functionality (draw & addScrollBarToScreen)

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
        addButton(new WaypointButton(waypoint, (index % 2 == 0) ? x : (x + w + 10), y, w, h), b->{
          b.addAttribute("waypoint_button");
//          b.setScissorPane(scissor);
        });
        if (index % 2 != 0)
          y += h + 5;
        index++;
      }
    }
    if (index == 0)
      addScreenMessage("There are currently no waypoints created for this server");
    this.content.updateMaxScroll(this, 5);
  }
}