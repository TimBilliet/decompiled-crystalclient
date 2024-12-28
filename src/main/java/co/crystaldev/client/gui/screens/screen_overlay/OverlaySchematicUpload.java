package co.crystaldev.client.gui.screens.screen_overlay;

import co.crystaldev.client.Client;
import co.crystaldev.client.Reference;
import co.crystaldev.client.feature.impl.factions.Schematica;
import co.crystaldev.client.group.objects.GroupSchematic;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.buttons.MenuButton;
import co.crystaldev.client.gui.buttons.TextInputField;
import co.crystaldev.client.handler.NotificationHandler;
import co.crystaldev.client.network.Packet;
import co.crystaldev.client.network.socket.client.group.PacketGroupSchematicAction;
import co.crystaldev.client.util.MultipartUploader;
import co.crystaldev.client.util.enums.EnumActionShift;
import co.crystaldev.client.util.objects.Transformation;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

public class OverlaySchematicUpload extends ScreenOverlay {
  private TextInputField schematicNameField;

  private MenuButton uploadSchematic;

  public OverlaySchematicUpload() {
    super(0, 0, 300, 10, "Group Schematic Upload");
  }

  public void init() {
    super.init();
    int x = this.pane.x + 5;
    int y = this.pane.y + 26;
    int w = this.pane.width - 10;
    int h = 18;
    addButton(
        (Button)(this.schematicNameField = new TextInputField(0, x, y, w, h, ClientProxy.currentSchematic.currentFile.getName().replaceAll("\\..+", ""))));
    y += h + 5;
    addButton((Button)(this.uploadSchematic = new MenuButton(1, x, y, w, h, "Upload Schematic")));
    while (this.pane.y + this.pane.height < y + h + 5)
      this.pane.height++;
    center();
  }

  public void onButtonInteract(Button button, int mouseX, int mouseY, int mouseButton) {
    super.onButtonInteract(button, mouseX, mouseY, mouseButton);
    if (button.equals(this.uploadSchematic) &&
      ClientProxy.currentSchematic.schematic != null) {
      String name = this.schematicNameField.getText().isEmpty() ? this.schematicNameField.getPlaceholderText() : this.schematicNameField.getText();
      Schematica.getInstance().getWorkerThread().execute(() -> {
            try {
              NotificationHandler.addNotification("Starting upload of schematic");
              JsonArray transformations = new JsonArray();
              for (Transformation t : ClientProxy.currentSchematic.transformations) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("type", t.getType().toString());
                jsonObject.addProperty("direction", t.getDirection().toString());
                jsonObject.addProperty("x", Integer.valueOf(t.getX()));
                jsonObject.addProperty("y", Integer.valueOf(t.getY()));
                jsonObject.addProperty("z", Integer.valueOf(t.getZ()));
                transformations.add((JsonElement)jsonObject);
              }
              JsonObject obj = new JsonObject();
              obj.addProperty("name", name);
              obj.addProperty("uploadedAt", Long.valueOf(System.currentTimeMillis()));
              obj.addProperty("x", Integer.valueOf(ClientProxy.currentSchematic.schematic.position.getX()));
              obj.addProperty("y", Integer.valueOf(ClientProxy.currentSchematic.schematic.position.getY()));
              obj.addProperty("z", Integer.valueOf(ClientProxy.currentSchematic.schematic.position.getZ()));
              obj.add("transformations", (JsonElement)transformations);
              MultipartUploader uploader = new MultipartUploader("https://cdn.crystalclient.net/schemUpload");
              uploader.addPart("", ClientProxy.currentSchematic.currentFile);
              uploader.addField("body", Reference.GSON.toJson(obj, JsonObject.class));
              for (String str : uploader.finish()) {
                if (!str.startsWith("{")) {
                  String dir = str.split("/")[0];
                  String id = str.split("/")[1];
                  PacketGroupSchematicAction packet = new PacketGroupSchematicAction(new GroupSchematic(name, dir, id), EnumActionShift.ADD);
                  Client.sendPacket((Packet)packet);
                  closeOverlay();
                  continue;
                }
                NotificationHandler.addNotification("Error uploading schematic");
              }
              NotificationHandler.addNotification("Schematic has been uploaded!");
            } catch (IOException ex) {
              NotificationHandler.addNotification("Error uploading schematic");
              Reference.LOGGER.error("Error uploading schematic", ex);
            }
          });
    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\screens\screen_overlay\OverlaySchematicUpload.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */