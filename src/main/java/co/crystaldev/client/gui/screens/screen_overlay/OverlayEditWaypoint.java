package co.crystaldev.client.gui.screens.screen_overlay;

import co.crystaldev.client.Client;
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
        super.init();
        center();
        int x = this.pane.x + 5;
        int y = this.pane.y + 24;
        int w = this.pane.width - 10;
        int w1 = (this.pane.width - 20) / 3;
        int h = 18;

        addButton((this.name = new TextInputField(-1, x, y, w, h, "Enter Waypoint Name") {

        }));
        if (waypoint != null) {
            name.setText(waypoint.getName());
        }
        y += h + 5;
        addButton((this.x = new NumberInputField(-1, x, y, w1, h, (this.waypoint == null) ? MathHelper.floor_double(this.mc.thePlayer.posX) : this.waypoint.getPos().getX())));
        addButton((this.y = new NumberInputField(-1, x + w1 + 5, y, w1, h, (this.waypoint == null) ? (int) this.mc.thePlayer.posY : this.waypoint.getPos().getY())));
        addButton((this.z = new NumberInputField(-1, x + 10 + w1 * 2, y, w1, h, (this.waypoint == null) ? MathHelper.floor_double(this.mc.thePlayer.posZ) : this.waypoint.getPos().getZ())));
        y += h + 5;
        Random r = new Random();
        ColorObject c = (this.waypoint == null) ? new ColorObject(r.nextInt(255), r.nextInt(255), r.nextInt(255), 180) : this.waypoint.getColor();
        addButton((this.color = new ColorPicker(-1, x, y, w, h, "Color", c, false) {

        }));
        this.color.setCanBeExpanded(false);
        this.color.setShouldCollapseButtonsBelow(false);
        this.color.invertExpandedState();
        this.color.setBackgroundColorToNonHovering();

        y += this.color.height + 5;
        addButton(new MenuButton(-1, x, y, w, h, (this.waypoint == null) ? "Create" : "Apply Edits") { {
                onClick = () ->{
                    OverlayEditWaypoint instance = OverlayEditWaypoint.this;
                    if (!instance.name.getText().isEmpty()) {
                        if (OverlayEditWaypoint.this.waypoint != null) {
                            OverlayEditWaypoint.this.waypoint.setName(instance.name.getText());
                            OverlayEditWaypoint.this.waypoint.setColor(instance.color.getCurrentValue());
                            new BlockPos(instance.x.getValue(), instance.y.getValue(), instance.z.getValue());
                            OverlayEditWaypoint.this.closeOverlay();
                        } else {
                            WaypointHandler.getInstance().addWaypoint(instance.name.getText(), Client.formatConnectedServerIp(), new BlockPos(instance.x.getValue(), instance.y.getValue(), instance.z.getValue()), (ColorObject)instance.color.getCurrentValue());
                            this.mc.displayGuiScreen(null);
                        }
                    }
                };
            }
        });
        while (this.pane.y + this.pane.height < y + h + 5)
            this.pane.height++;
        center();
        this.color.onUpdate();
    }
}