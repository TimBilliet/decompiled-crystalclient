package co.crystaldev.client.util;

import co.crystaldev.client.Client;
import co.crystaldev.client.Reference;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SchematicUploader {
    private static final String DEFAULT_UPLOAD_URL = "http://athion.net/fawe/";

    private static final Map<String, String> CUSTOM_UPLOAD_URLS = (Map<String, String>) ImmutableMap.of("crystaldev", "https://upload.crystaldev.co/fawe/", "thearchon", "https://www.buildersrefuge.com/schematics/");

    public static void upload(File schematic) throws IOException {
        ServerData serverData = Minecraft.getMinecraft().getCurrentServerData();
        String serverIp = (serverData == null) ? "" : serverData.serverIP;
        UUID id = UUID.randomUUID();
        String url = getUploadUrl(serverIp) + "upload.php?" + id;
        Client.sendMessage("&fUploading schematic...", true);
        MultipartUploader uploader = new MultipartUploader(url, true);
        uploader.addPart("schematicFile", schematic);
        List<String> response = uploader.finish();
        if (response.contains("Success!")) {
            try {
                String loadCommand = "/schematic load url:" + id;
                (Minecraft.getMinecraft()).thePlayer.sendChatMessage(loadCommand);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(new StringSelection(loadCommand), null);
            } finally {
                Client.sendMessage("&aFinished uploading schematic!", true);
            }
        } else {
            Client.sendMessage("&cFailed to upload schematic.", true);
            Reference.LOGGER.error("Error uploading schematic. Response:\n\n" + Arrays.toString(response.toArray()));
        }
    }

    private static String getUploadUrl(String serverIp) {
        for (String key : CUSTOM_UPLOAD_URLS.keySet()) {
            if (serverIp.toLowerCase().contains(key))
                return CUSTOM_UPLOAD_URLS.get(key);
        }
        return "http://athion.net/fawe/";
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\SchematicUploader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */