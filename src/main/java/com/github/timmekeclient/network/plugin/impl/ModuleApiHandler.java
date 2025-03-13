package com.github.timmekeclient.network.plugin.impl;

import com.github.timmekeclient.Reference;
import com.github.timmekeclient.handler.ModuleHandler;
import com.github.timmekeclient.network.plugin.MessageHandler;
import com.github.timmekeclient.util.ByteBufUtils;
import com.github.timmekeclient.util.objects.ModuleAPI;
import com.google.gson.JsonSyntaxException;
import io.netty.buffer.ByteBuf;

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