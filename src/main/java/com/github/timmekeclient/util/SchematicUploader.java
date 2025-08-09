package com.github.timmekeclient.util;

import com.github.timmekeclient.Client;
import com.github.timmekeclient.Reference;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class SchematicUploader {
    private static final String DEFAULT_UPLOAD_URL = "http://athion.net/fawe/";

    public static void upload(File schematic) throws IOException {
        ServerData serverData = Minecraft.getMinecraft().getCurrentServerData();
        UUID id = UUID.randomUUID();
        String url = DEFAULT_UPLOAD_URL + "upload.php?" + id;
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

}
