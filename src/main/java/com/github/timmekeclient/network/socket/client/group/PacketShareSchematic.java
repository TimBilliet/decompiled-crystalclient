package com.github.timmekeclient.network.socket.client.group;

import com.github.timmekeclient.Client;
import com.github.timmekeclient.Reference;
import com.github.timmekeclient.network.ByteBufWrapper;
import com.github.timmekeclient.network.INetHandler;
import com.github.timmekeclient.network.Packet;
import com.github.timmekeclient.util.enums.ChatColor;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;

import java.io.IOException;


public class PacketShareSchematic
        extends Packet {
    private String username;
    private String schemDir;
    private String schemId;

    public PacketShareSchematic() {
    }

    public PacketShareSchematic(String schemDir, String schemId) {
        this.schemDir = schemDir;
        this.schemId = schemId;
    }


    public void write(ByteBufWrapper out) throws IOException {
        JsonObject obj = new JsonObject();
        obj.addProperty("dir", this.schemDir);
        obj.addProperty("id", this.schemId);

        out.writeString(Reference.GSON.toJson(obj, JsonObject.class));
    }


    public void read(ByteBufWrapper in) throws IOException {
        JsonObject obj = (JsonObject) Reference.GSON.fromJson(in.readString(), JsonObject.class);
        this.username = obj.get("ign").getAsString();
        this.schemDir = obj.get("dir").getAsString();
        this.schemId = obj.get("id").getAsString();
    }


    public void process(INetHandler handler) {
        String topMsg = String.format("&8&l❙  &b&l%s&b has shared a schematic with you!", this.username);
        String bottomMsg = "&8&l❙  &7&l - &rDownload by clicking this message!";
        String cmd = String.format("/schemshare %s %s", this.schemDir, this.schemId);

        ChatComponentText ch = new ChatComponentText(ChatColor.translate(topMsg));
        ch.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));

        ChatComponentText ch1 = new ChatComponentText(ChatColor.translate(bottomMsg));
        ch1.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));

        Client.sendMessage("&8&l❙", false);
        (Minecraft.getMinecraft()).ingameGUI.getChatGUI().printChatMessage(ch);
        (Minecraft.getMinecraft()).ingameGUI.getChatGUI().printChatMessage(ch1);
        Client.sendMessage("&8&l❙", false);
    }
}