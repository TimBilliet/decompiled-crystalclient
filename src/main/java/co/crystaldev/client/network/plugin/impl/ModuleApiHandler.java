package co.crystaldev.client.network.plugin.impl;

import co.crystaldev.client.Reference;
import co.crystaldev.client.handler.ModuleHandler;
import co.crystaldev.client.handler.NotificationHandler;
import co.crystaldev.client.network.plugin.MessageHandler;
import co.crystaldev.client.util.ByteBufUtils;
import co.crystaldev.client.util.objects.ModuleAPI;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.netty.buffer.ByteBuf;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Map;

public class ModuleApiHandler extends MessageHandler {
    public String json;

    public void fromBytes(ByteBuf buf) {
        this.json = ByteBufUtils.readUTF8String(buf);
        if (!this.json.startsWith("{"))
            return;
        try {
            ModuleHandler.setModuleApi((ModuleAPI) Reference.GSON.fromJson(this.json, ModuleAPI.class));
        } catch (JsonSyntaxException ex) {
            Reference.LOGGER.error("Received invalid JSON from module api ({})", this.json, ex);
        }
    }

    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.json);
    }

    public void onMessage() {
        Reference.LOGGER.info("Received message from Module API: " + this.json);
    }
}