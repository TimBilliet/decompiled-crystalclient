package co.crystaldev.client.command;

import co.crystaldev.client.Client;
import co.crystaldev.client.command.base.AbstractCommand;
import co.crystaldev.client.command.base.CommandInfo;
import co.crystaldev.client.command.base.args.CommandArguments;
import co.crystaldev.client.command.base.exceptions.CommandException;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C01PacketChatMessage;

@CommandInfo(name = "crystalsudo", aliases = {"csudo"}, usage = {"crystalsudo [command]"}, description = "Send a command directly to the server, bypassing the client command handler.", minimumArguments = 1)
public class SudoCommand extends AbstractCommand {
    public void execute(ICommandSender sender, CommandArguments arguments) throws CommandException {
        String cmd = arguments.joinArgs(0);
        cmd = cmd.startsWith("/") ? cmd : ("/" + cmd);
        Client.sendMessage("Running command " + cmd, true);
        (Minecraft.getMinecraft()).thePlayer.sendQueue.addToSendQueue((Packet) new C01PacketChatMessage(cmd));
    }
}