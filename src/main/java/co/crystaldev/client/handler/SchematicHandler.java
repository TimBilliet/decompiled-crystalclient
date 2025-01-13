package co.crystaldev.client.handler;

import co.crystaldev.client.Client;
import co.crystaldev.client.Reference;
import co.crystaldev.client.network.Packet;
import co.crystaldev.client.network.socket.client.group.PacketShareSchematic;
import co.crystaldev.client.util.FileUtils;
import co.crystaldev.client.util.MultipartUploader;
import co.crystaldev.client.util.objects.Schematic;
import co.crystaldev.client.util.objects.Schematic;
import co.crystaldev.client.util.objects.Transformation;
import com.github.lunatrius.core.util.MBlockPos;
import com.github.lunatrius.schematica.Schematica;
import com.github.lunatrius.schematica.client.printer.SchematicPrinter;
import com.github.lunatrius.schematica.client.renderer.RenderSchematic;
import com.github.lunatrius.schematica.client.util.FlipHelper;
import com.github.lunatrius.schematica.client.util.RotationHelper;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class SchematicHandler {
    private static SchematicHandler INSTANCE = null;

    private final List<Schematic> loadedSchems = new ArrayList<>();

    public List<Schematic> getLoadedSchems() {
        return this.loadedSchems;
    }

    public void loadSchematic(String dir, String id) {
        for (Schematic schematic : this.loadedSchems) {
            if (schematic.getDir().equals(dir) && schematic.getId().equals(id)) {
                Schematica.proxy.unloadSchematic();
                loadSchematic(schematic);
                return;
            }
        }
        Client.getInstance().getExecutor().submit(() -> {
            try {
                String json = "https://cdn.crystalclient.net/" + dir + "/" + id + ".json";
                String schem = "https://cdn.crystalclient.net/" + dir + "/" + id + ".schematic";
                File schematicDir = new File(getSchematicDirectory(), dir);
                File jsonFile = new File(schematicDir, id + ".json");
                File schemFile = new File(schematicDir, id + ".schematic");
                FileReader reader = null;
                JsonObject obj = null;
                if (!jsonFile.exists() || !schemFile.exists()) {
                    reader = new FileReader(jsonFile);
                    obj = Reference.GSON.fromJson(reader, JsonObject.class);
                    if (!jsonFile.exists() || !(new File(schematicDir, FileUtils.sanitizeFileName(obj.get("name").getAsString()) + ".schematic")).exists()) {
                        Client.sendMessage("Downloading schematic", true);
                        copyURLToFile(new URL(json), jsonFile, 5000, 2000);
                        copyURLToFile(new URL(schem), schemFile, 5000, 2000);
                    }
                }
                if (obj == null) {
                    reader = new FileReader(jsonFile);
                    obj = Reference.GSON.fromJson(reader, JsonObject.class);
                }
                Schematic downloadedSchem = new Schematic(dir, id, obj);
                if (!schemFile.renameTo(new File(schematicDir, FileUtils.sanitizeFileName(downloadedSchem.getName()) + ".schematic"))) {
                    Client.sendErrorMessage("Schematic download failed", true);
                    return;
                }
                this.loadedSchems.add(downloadedSchem);
                Schematica.proxy.unloadSchematic();
                loadSchematic(downloadedSchem);
                reader.close();
            } catch (IOException ex) {
                Client.sendErrorMessage("Schematic download failed, IOException was raised.", true);
                Reference.LOGGER.error("Unable to download schematic", ex);
            }
        });
    }

    private void loadSchematic(Schematic schematic) {
        synchronized (Client.class) {
            File dir = schematic.getFile();
            File schem = new File(dir, FileUtils.sanitizeFileName(schematic.getName()) + ".schematic");
            if (dir.exists() && schem.exists()) {
                ClientProxy proxy = Schematica.proxy;
                proxy.unloadSchematic();
                if (proxy.loadSchematic(null, dir, schem.getName())) {
                    SchematicWorld world = ClientProxy.currentSchematic.schematic;
                    MBlockPos pos = world.position;
                    for (Transformation t : schematic.getTransformations()) {
                        ClientProxy.currentSchematic.transformations.add(t);
                        pos.x = t.getX();
                        pos.y = t.getY();
                        pos.z = t.getZ();
                        if (t.getType() == Transformation.Type.FLIP) {
                            FlipHelper.INSTANCE.flip(world, t.getDirection(), false);
                            continue;
                        }
                        RotationHelper.INSTANCE.rotate(world, t.getDirection(), false);
                    }
                    pos.x = schematic.getX();
                    pos.y = schematic.getY();
                    pos.z = schematic.getZ();
                    ClientProxy.moveToPlayer = false;
                    RenderSchematic.INSTANCE.refresh();
                    SchematicPrinter.INSTANCE.refresh();
                }
            } else {
                this.loadedSchems.remove(schematic);
                loadSchematic(schematic.getDir(), schematic.getId());
            }
        }
    }

    public void copyURLToFile(URL source, File destination, int connectionTimeout, int readTimeout) throws IOException {
        URLConnection connection = source.openConnection();
        connection.setConnectTimeout(connectionTimeout);
        connection.setReadTimeout(readTimeout);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.96 Safari/537.36");
        InputStream input = connection.getInputStream();
//    FileUtils.copyInputStreamToFile(input, destination);
        org.apache.commons.io.FileUtils.copyInputStreamToFile(input, destination);
    }

    public void shareCurrentSchematic() {
        if (ClientProxy.currentSchematic.schematic == null)
            return;
        co.crystaldev.client.feature.impl.factions.Schematica.getInstance().getWorkerThread().execute(() -> {
            try {
                Client.sendMessage("Uploading schematic...", true);
                JsonArray transformations = new JsonArray();
                for (Transformation t : ClientProxy.currentSchematic.transformations) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("type", t.getType().toString());
                    jsonObject.addProperty("direction", t.getDirection().toString());
                    jsonObject.addProperty("x", t.getX());
                    jsonObject.addProperty("y", t.getY());
                    jsonObject.addProperty("z", t.getZ());
                    transformations.add((JsonElement) jsonObject);
                }
                JsonObject obj = new JsonObject();
                obj.addProperty("name", FileUtils.sanitizeFileName(ClientProxy.currentSchematic.currentFile.getName().replace(".schematic", "")));
                obj.addProperty("uploadedAt", System.currentTimeMillis());
                obj.addProperty("x", ClientProxy.currentSchematic.schematic.position.x);
                obj.addProperty("y", ClientProxy.currentSchematic.schematic.position.y);
                obj.addProperty("z", ClientProxy.currentSchematic.schematic.position.z);
                obj.add("transformations", (JsonElement) transformations);
                MultipartUploader uploader = new MultipartUploader("https://cdn.crystalclient.net/schemUpload");
                uploader.addPart("", ClientProxy.currentSchematic.currentFile);
                uploader.addField("body", Reference.GSON.toJson(obj, JsonObject.class));
                for (String str : uploader.finish()) {
                    if (!str.startsWith("{")) {
                        String dir = str.split("/")[0];
                        String id = str.split("/")[1];
                        PacketShareSchematic packet = new PacketShareSchematic(dir, id);
                        Client.sendPacket(packet);
                        continue;
                    }
                    Client.sendErrorMessage("Schematic upload failed", true);
                }
                Client.sendMessage("Schematic has been uploaded!", true);
            } catch (IOException ex) {
                Client.sendErrorMessage("Schematic upload failed", true);
                Reference.LOGGER.error("Error uploading schematic", ex);
            }
        });
    }

    public File getSchematicDirectory() {
        return new File(Client.getClientRunDirectory(), "schematic-cache");
    }

    public static SchematicHandler getInstance() {
        return (INSTANCE == null) ? (INSTANCE = new SchematicHandler()) : INSTANCE;
    }
}