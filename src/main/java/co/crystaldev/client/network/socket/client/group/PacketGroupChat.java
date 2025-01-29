package co.crystaldev.client.network.socket.client.group;

import co.crystaldev.client.Client;
import co.crystaldev.client.Reference;
import co.crystaldev.client.event.impl.network.ChatReceivedEvent;
import co.crystaldev.client.feature.impl.factions.FloatFinder;
import co.crystaldev.client.feature.impl.factions.Patchcrumbs;
import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.network.ByteBufWrapper;
import co.crystaldev.client.network.INetHandler;
import co.crystaldev.client.network.Packet;
import co.crystaldev.client.util.enums.ChatColor;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.BlockPos;
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
        JsonObject obj = Reference.GSON.fromJson(in.readString(), JsonObject.class);
        this.ign = obj.get("ign").getAsString();
        this.serverIp = obj.get("serverIp").getAsString();
        this.message = obj.get("message").getAsString();
    }

    public void process(INetHandler handler) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null)
            return;
        if (message.startsWith("ZQX_")) {
            FloatFinder floatInst = FloatFinder.getInstance();
            Patchcrumbs patchInst = Patchcrumbs.getInstance();
            if (floatInst.enabled && mc.thePlayer != null && !ign.equals(mc.thePlayer.getName())) {
                if (message.equals("ZQX_V") || message.equals("ZQX_H") || message.equals("ZQX_D")) {
                    floatInst.horizontal = null;
                    floatInst.vertical = null;
                    patchInst.clearCrumbFromFloatFinder();
                    return;
                }
                String[] coords = message.substring(4).split("_");
                if (coords.length == 7) {
                    int hX = Integer.parseInt(coords[0]);
                    int hY = Integer.parseInt(coords[1]);
                    int hZ = Integer.parseInt(coords[2]);
                    int bX = Integer.parseInt(coords[3]);
                    int bY = Integer.parseInt(coords[4]);
                    int bZ = Integer.parseInt(coords[5]);
                    floatInst.horizontal = new BlockPos(hX, hY, hZ);
                    floatInst.barrelBlockPos = new BlockPos(bX, bY, bZ);
                    floatInst.vertical = new BlockPos(bX, hY, bZ);
                    if (patchInst.enabled && patchInst.useFloatFinder) {
                        patchInst.setCrumbsFromFloatFinder(hX, hY, hZ, coords[6]);
                        if (!patchInst.announceReceivedCoords)
                            return;
                    }
                    Client.sendMessage(String.format("&fReceived shared float position at &bx%s y%s z%s.", hX, hY, hZ), true);
                }
            }
            return;
        }

        ChatComponentText ch = new ChatComponentText(ChatColor.translate('&', "&8[&b&lGroup Chat&8] &r" + this.ign + ": "));
        ChatComponentText ch1 = new ChatComponentText(ChatColor.translate('&', "&f" + this.message));
        ChatComponentText ch2 = new ChatComponentText(ChatColor.translate(String.format("&bServer: &r%s\n&bGroup: &r%s", this.serverIp,
                GroupManager.getSelectedGroup().getName())));
        ch.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, ch2));
        ch1.getChatStyle().setChatHoverEvent(null);
        IChatComponent all = (new ChatComponentText("")).appendSibling(ch).appendSibling(ch1);
        ChatReceivedEvent event = new ChatReceivedEvent(all, (byte) 0);
        event.call();
        if (!event.isCancelled() && event.message != null)
            mc.ingameGUI.getChatGUI().printChatMessage(event.message);
    }
}
