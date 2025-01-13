package co.crystaldev.client.network.socket.client.group;

import co.crystaldev.client.Reference;
import co.crystaldev.client.event.impl.network.ChatReceivedEvent;
import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.network.ByteBufWrapper;
import co.crystaldev.client.network.INetHandler;
import co.crystaldev.client.network.Packet;
import co.crystaldev.client.util.enums.ChatColor;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.io.IOException;

public class PacketGroupChat extends Packet {
    private String ign;

    private String message;

    private String serverIp;

    public PacketGroupChat() {
    }

    public PacketGroupChat(String message) {
        this.message = message;
    }

    public void write(ByteBufWrapper out) throws IOException {
        out.writeString(this.message);
    }

    public void read(ByteBufWrapper in) throws IOException {
        JsonObject obj = (JsonObject) Reference.GSON.fromJson(in.readString(), JsonObject.class);
        this.ign = obj.get("ign").getAsString();
        this.serverIp = obj.get("serverIp").getAsString();
        this.message = obj.get("message").getAsString();
    }

    public void process(INetHandler handler) {
        if ((Minecraft.getMinecraft()).theWorld == null)
            return;
        ChatComponentText ch = new ChatComponentText(ChatColor.translate('&', "&8[&b&lGroup Chat&8] &r" + this.ign + ": "));
        ChatComponentText ch1 = new ChatComponentText(ChatColor.translate('&', "&f" + this.message));
        ChatComponentText ch2 = new ChatComponentText(ChatColor.translate(String.format("&bServer: &r%s\n&bGroup: &r%s", new Object[]{this.serverIp,
                GroupManager.getSelectedGroup().getName()})));
        ch.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (IChatComponent) ch2));
        ch1.getChatStyle().setChatHoverEvent(null);
        IChatComponent all = (new ChatComponentText("")).appendSibling((IChatComponent) ch).appendSibling((IChatComponent) ch1);
        ChatReceivedEvent event = new ChatReceivedEvent(all, (byte) 0);
        event.call();
        if (!event.isCancelled() && event.message != null)
            (Minecraft.getMinecraft()).ingameGUI.getChatGUI().printChatMessage(event.message);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\socket\client\group\PacketGroupChat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */