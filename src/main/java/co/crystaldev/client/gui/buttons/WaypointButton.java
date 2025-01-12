package co.crystaldev.client.gui.buttons;

import co.crystaldev.client.Resources;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.gui.screens.ScreenBase;
import co.crystaldev.client.gui.screens.ScreenCosmetics;
import co.crystaldev.client.gui.screens.ScreenMacros;
import co.crystaldev.client.gui.screens.ScreenWaypoints;
import co.crystaldev.client.gui.screens.schematica.ScreenSchematicControl;
import co.crystaldev.client.gui.screens.screen_overlay.OverlayEditWaypoint;
import co.crystaldev.client.handler.WaypointHandler;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.FadingColor;
import co.crystaldev.client.util.objects.Waypoint;

public class WaypointButton extends Button {
  private final Waypoint waypoint;

  private final FadingColor background;

  private final FadingColor text;

  private final FadingColor outline;

  private final FadingColor outline1;

  private final ResourceButton edit;

  private final ResourceButton remove;

  public WaypointButton(Waypoint waypoint, int x, int y, int width, int height) {
    super(-1, x, y, width, height);
    this.waypoint = waypoint;
    this.background = new FadingColor(this.opts.neutralButtonBackground, this.opts.hoveredButtonBackground);
    this.text = new FadingColor(this.opts.neutralTextColor, this.opts.hoveredTextColor);
    this.outline = new FadingColor(this.opts.mainDisabled, this.opts.mainEnabled);
    this.outline1 = new FadingColor(this.opts.secondaryDisabled, this.opts.secondaryEnabled);
    int bSize = this.height - 6;

    this.remove = new ResourceButton(-1, this.x + this.width - 3 - this.height + 4, this.y + this.height / 2 - bSize / 2, bSize, bSize, Resources.CLOSE);
//    this.remove = new ResourceButton(-1,this.x + this.width - 3 - bSize - this.height - bSize + 4, this.y + this.height / 2 - bSize / 2, bSize, bSize, Resources.CLOSE);
    this.edit = new ResourceButton(-1, this.x + this.width - 3 - this.height + 4 - 3 - bSize, this.y + this.height / 2 - bSize / 2, bSize, bSize, Resources.COG);
//    this.edit = new ResourceButton(-1, this.x + this.width - 3 - bSize - this.height - bSize + 4 - 3 - bSize, this.y + this.height / 2 - bSize / 2, bSize, bSize, Resources.COG);
    this.fontRenderer = Fonts.NUNITO_SEMI_BOLD_18;
  }

  public void onUpdate() {
    int bSize = this.height - 6;
    this.edit.y = this.y + this.height / 2 - bSize / 2;
  }

  public void drawButton(int mouseX, int mouseY, boolean hovered) {
    Screen.scissorStart(this.scissorPane);
    boolean waypointVisible = this.waypoint.isVisible();
    this.background.fade(hovered);
    this.text.fade(hovered);
    this.outline.fade(waypointVisible);
    this.outline1.fade(waypointVisible);
    RenderUtils.drawRoundedRectWithGradientBorder(this.x, this.y, (this.x + this.width), (this.y + this.height), 9.0D, 2.5F, this.outline
        .getCurrentColor().getRGB(), this.outline1.getCurrentColor().getRGB(), this.background
        .getCurrentColor().getRGB());
    this.fontRenderer.drawString(this.waypoint.getName(), this.x + 5, this.y + this.height / 2 - this.fontRenderer.getStringHeight() / 2, this.text
        .getCurrentColor().getRGB());
    this.remove.drawButton(mouseX, mouseY, this.remove.isHovered(mouseX, mouseY));
    this.edit.drawButton(mouseX, mouseY, this.edit.isHovered(mouseX, mouseY));
    Screen.scissorEnd(this.scissorPane);
  }

  public void onInteract(int mouseX, int mouseY, int mouseButton) {
    super.onInteract(mouseX, mouseY, mouseButton);
    if (this.edit.isHovered(mouseX, mouseY)) {
      ((Screen)this.mc.currentScreen).addOverlay(new OverlayEditWaypoint(this.waypoint));
    } else if (this.remove.isHovered(mouseX, mouseY)) {
      WaypointHandler.getInstance().removeWaypoint(this.waypoint);
    } else {
      this.waypoint.setVisible(!this.waypoint.isVisible());
    }
  }


}