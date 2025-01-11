package co.crystaldev.client.gui.screens.screen_overlay;

import co.crystaldev.client.Client;
import co.crystaldev.client.feature.impl.factions.AdjustHelper;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.buttons.MenuButton;
import co.crystaldev.client.gui.buttons.NumberInputField;
import co.crystaldev.client.gui.buttons.TextInputField;
import co.crystaldev.client.gui.buttons.settings.ColorPicker;
import co.crystaldev.client.handler.WaypointHandler;
import co.crystaldev.client.util.ColorObject;
import co.crystaldev.client.util.objects.Waypoint;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

import java.util.Random;

public class OverlayEditWaypoint extends ScreenOverlay {
  private Waypoint waypoint = null;

  private TextInputField name;

  private NumberInputField x;

  private NumberInputField y;

  private NumberInputField z;

  private ColorPicker color;

  public OverlayEditWaypoint() {
    super(0, 0, 300, 10, "Create Waypoint");
    this.overlay = false;
  }

  public OverlayEditWaypoint(Waypoint waypoint) {
    super(0, 0, 300, 10, "Modify Waypoint");
    this.waypoint = waypoint;
  }

  public void init() {
    System.out.println("init overlayeditpayoiuit");
    super.init();
    center();
    int x = this.pane.x + 5;
    int y = this.pane.y + 24;
    int w = this.pane.width - 10;
    int w1 = (this.pane.width - 20) / 3;
    int h = 18;
    addButton((this.name = new TextInputField(-1, x, y, w, h, "Enter Waypoint Name") {

        }));
    y += h + 5;
    addButton((this.x = new NumberInputField(-1, x, y, w1, h, (this.waypoint == null) ? MathHelper.floor_double(this.mc.thePlayer.posX) : this.waypoint.getPos().getX())));
    addButton((this.y = new NumberInputField(-1, x + w1 + 5, y, w1, h, (this.waypoint == null) ? (int)this.mc.thePlayer.posY : this.waypoint.getPos().getY())));
    addButton((this.z = new NumberInputField(-1, x + 10 + w1 * 2, y, w1, h, (this.waypoint == null) ? MathHelper.floor_double(this.mc.thePlayer.posZ) : this.waypoint.getPos().getZ())));
    y += h + 5;
    Random r = new Random();
    ColorObject c = (this.waypoint == null) ? new ColorObject(r.nextInt(255), r.nextInt(255), r.nextInt(255), 180) : this.waypoint.getColor();
    addButton((this.color = new ColorPicker(-1, x, y, w, h, "Color", c, false) {

        }));
    y += this.color.height + 5;
    addButton(new MenuButton(-1, x, y, w, h, (this.waypoint == null) ? "Create" : "Apply Edits"), b -> b.setOnClick(new Runnable() {
      @Override
      public void run() {
        System.out.println("TESTRUNNABLE");
//        System.out.println(x);
//        if(waypoint == null) {
//          Waypoint waypoint;
//          OverlayEditWaypoint.
//          waypoint = new Waypoint(name.getText(), Client.formatConnectedServerIp(), new BlockPos(), color);
//        } else {
//
//        }
        closeOverlay();
        onGuiClosed();
      }
    }));
    while (this.pane.y + this.pane.height < y + h + 5)
      this.pane.height++;
    center();
    this.color.onUpdate();
  }

  public void onButtonInteract(Button button, int mouseX, int mouseY, int mouseButton) {
//    System.out.println("onbuttoninteract overlayeditwaypoint");
    super.onButtonInteract(button, mouseX, mouseY, mouseButton);
    if(button != null && button.displayText != null) {
      if(button.displayText.equals("Create")) {
        System.out.println("CREATE PRESSED");

          Waypoint waypoint;
          waypoint = new Waypoint(name.getText(), Client.formatConnectedServerIp(), new BlockPos(x.getValue(),y.getValue(),z.getValue()), color.getCurrentValue());
        WaypointHandler.getInstance().addWaypoint(waypoint.setWorld(Client.getCurrentWorldName()));
//        this.closeOverlay();
//        super.closeOverlay();
      } else if(button.displayText.equals("Apply Edits")) {
        super.closeOverlay();
      }
    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\screens\screen_overlay\OverlayEditWaypoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */