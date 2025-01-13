package co.crystaldev.client.network.socket.client.group;

import co.crystaldev.client.Client;
import co.crystaldev.client.Reference;
//import co.crystaldev.client.command.GroupCommand;
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
import java.util.UUID;

public class PacketGroupPrivateMessage extends Packet {
    private static final char ARROW = '\u27a5';

    private UUID to;

    private UUID senderId;

    private String sender;

    private String receiver;

    private String message;

    private String server;

    public PacketGroupPrivateMessage() {
    }

    public PacketGroupPrivateMessage(UUID to, String message) {
        this.to = to;
        this.message = message;
    }

    public void write(ByteBufWrapper out) throws IOException {
        out.writeUUID(this.to);
        out.writeString(this.message);
    }

    public void read(ByteBufWrapper in) throws IOException {
        JsonObject obj = (JsonObject) Reference.GSON.fromJson(in.readString(), JsonObject.class);
        this.sender = obj.get("sender").getAsString();
        this.receiver = obj.get("receiver").getAsString();
        this.message = obj.get("message").getAsString();
        this.server = obj.get("serverIp").getAsString();
        this.senderId = in.readUUID();
    }

    public void process(INetHandler handler) {
        if ((Minecraft.getMinecraft()).theWorld == null)
            return;
        String prefix = String.format("%s&8 [&f%s &7%c %s&8] ", new Object[]{Client.getPrefix(), this.sender, Character.valueOf('\u27a5'), this.receiver});
        String lore = String.format("&bServer: &r%s\n&bGroup: &r%s", new Object[]{this.server, GroupManager.getSelectedGroup().getName()});
        ChatComponentText ch = new ChatComponentText(ChatColor.translate(prefix));
        ChatComponentText ch1 = new ChatComponentText(ChatColor.translate("&f" + this.message));
        ChatComponentText ch2 = new ChatComponentText(ChatColor.translate(lore));
        ch.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (IChatComponent) ch2));
        ch1.getChatStyle().setChatHoverEvent(null);
        IChatComponent all = (new ChatComponentText("")).appendSibling((IChatComponent) ch).appendSibling((IChatComponent) ch1);
        ChatReceivedEvent event = new ChatReceivedEvent(all, (byte) 0);
        event.call();
        if (!event.isCancelled() && event.message != null)
            (Minecraft.getMinecraft()).ingameGUI.getChatGUI().printChatMessage(event.message);
//    if (this.senderId != Client.getUniqueID())
//      GroupCommand.setLastSender(this.senderId);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\socket\client\group\PacketGroupPrivateMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */