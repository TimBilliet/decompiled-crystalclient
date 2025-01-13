package co.crystaldev.client.network.socket.client.group;

import co.crystaldev.client.Client;
import co.crystaldev.client.Reference;
import co.crystaldev.client.network.ByteBufWrapper;
import co.crystaldev.client.network.INetHandler;
import co.crystaldev.client.network.Packet;
import co.crystaldev.client.util.enums.ChatColor;
import com.google.gson.JsonObject;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;


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
        String topMsg = String.format("&8&l❙  &b&l%s&b has shared a schematic with you!", new Object[]{this.username});
        String bottomMsg = "&8&l❙  &7&l - &rDownload by clicking this message!";
        String cmd = String.format("/schemshare %s %s", new Object[]{this.schemDir, this.schemId});

        ChatComponentText ch = new ChatComponentText(ChatColor.translate(topMsg));
//        ch.func_150256_b().func_150241_a(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
        ch.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));

        ChatComponentText ch1 = new ChatComponentText(ChatColor.translate(bottomMsg));
        ch1.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));

        Client.sendMessage("&8&l❙", false);
        (Minecraft.getMinecraft()).ingameGUI.getChatGUI().printChatMessage(ch);
        (Minecraft.getMinecraft()).ingameGUI.getChatGUI().printChatMessage(ch1);
        Client.sendMessage("&8&l❙", false);
    }
}