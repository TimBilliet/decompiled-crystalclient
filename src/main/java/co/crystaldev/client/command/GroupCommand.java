package co.crystaldev.client.command;

import co.crystaldev.client.Client;
import co.crystaldev.client.cache.UsernameCache;
import co.crystaldev.client.command.base.AbstractCommand;
import co.crystaldev.client.command.base.CommandInfo;
import co.crystaldev.client.command.base.args.CommandArguments;
import co.crystaldev.client.command.base.exceptions.CommandException;
import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.group.objects.GroupMember;
import co.crystaldev.client.network.Packet;
import co.crystaldev.client.network.socket.client.group.PacketFocusPlayer;
import co.crystaldev.client.network.socket.client.group.PacketGroupChat;
import co.crystaldev.client.network.socket.client.group.PacketGroupPrivateMessage;
import co.crystaldev.client.network.socket.client.group.PacketPingLocation;
import co.crystaldev.client.util.UsernameUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.MathHelper;

import java.util.UUID;

@CommandInfo(name = "group", aliases = {"g", "groups"}, description = "Quickly access many of your groups features.", usage = {"/g &7- Displays this help message.", "/g loc &7- Broadcasts your location to your selected group.", "/g focus [player] &7- Marks a player for your selected group.", "/g unfocus &7- Removes the focused player currently displayed.", "/g message [name] [message] &7- Privately message members of your group.", "/g reply [message] &7- Privately reply to the last private message.", "/g [message] &7- Sends a message to your selected group."}, minimumArguments = 1)
public class GroupCommand extends AbstractCommand {
    private static UUID lastSender = null;

    public static void setLastSender(UUID lastSender) {
        GroupCommand.lastSender = lastSender;
    }

    public void execute(ICommandSender sender, CommandArguments arguments) throws CommandException {
        if (GroupManager.getSelectedGroup() == null) {
            sendErrorMessage("You are not currently in a group");
            return;
        }
        switch (arguments.getString(0).toLowerCase()) {
            case "message":
            case "msg":
            case "tell":
            case "m":
            case "t":
                if (!arguments.ensureArguments(2, -1, -1, "You need to specify a group member!"))
                    return;
                if (!arguments.ensureArguments(3, -1, -1, "You need to specify a private message to send!"))
                    return;
                for (GroupMember member : GroupManager.getSelectedGroup().getMembers()) {
                    UUID id = member.getUuid();
                    String name = UsernameCache.getInstance().getUsername(id);
                    if (name.equalsIgnoreCase(arguments.getString(1))) {
                        String msg = arguments.joinArgs(2);
                        PacketGroupPrivateMessage packetGroupPrivateMessage = new PacketGroupPrivateMessage(id, msg);
                        Client.sendPacket((Packet) packetGroupPrivateMessage);
                        return;
                    }
                }
                sendErrorMessage("No group member was found by that username.");
                return;
            case "reply":
            case "r":
                if (arguments.ensureArguments(2, -1, -1, "You need to specify a message!"))
                    if (lastSender != null) {
                        String msg = arguments.joinArgs(1);
                        PacketGroupPrivateMessage packetGroupPrivateMessage = new PacketGroupPrivateMessage(lastSender, msg);
                        Client.sendPacket((Packet) packetGroupPrivateMessage);
                    } else {
                        sendErrorMessage("You don't have a recipient to reply to!");
                    }
                return;
            case "loc":
                if ((Minecraft.getMinecraft()).thePlayer != null) {
                    int x = MathHelper.floor_double((Minecraft.getMinecraft()).thePlayer.posX);
                    int y = MathHelper.floor_double((Minecraft.getMinecraft()).thePlayer.posY);
                    int z = MathHelper.floor_double((Minecraft.getMinecraft()).thePlayer.posZ);
                    PacketPingLocation packetPingLocation = new PacketPingLocation(x, y, z);
                    Client.sendPacket((Packet) packetPingLocation);
                }
                return;
            case "focus":
            case "foc":
                if (arguments.ensureArguments(-1, -1, 2, "Please provide a player's IGN to focus!")) {
                    String username = arguments.getString(1);
                    UUID uuid = UsernameUtils.usernameToUUID(username);
                    if (uuid != null) {
                        PacketFocusPlayer packetFocusPlayer = new PacketFocusPlayer(uuid);
                        Client.sendPacket((Packet) packetFocusPlayer);
                    } else {
                        sendErrorMessage("Couldn't find a player by that name!");
                    }
                }
                return;
            case "unfocus":
            case "unfoc":
                if (GroupManager.getSelectedGroup() != null) {
                    if (GroupManager.getSelectedGroup().getFocusedId() != null) {
                        GroupManager.getSelectedGroup().setFocusedId(null);
                        return;
                    }
                    sendErrorMessage("Your group doesn't have any focused players!");
                    return;
                }
                return;
        }
        PacketGroupChat packet = new PacketGroupChat(arguments.joinArgs(0));
        Client.sendPacket((Packet) packet);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\command\GroupCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */