package co.crystaldev.client.command;

import co.crystaldev.client.Client;
import co.crystaldev.client.command.base.AbstractCommand;
import co.crystaldev.client.command.base.CommandInfo;
import co.crystaldev.client.command.base.args.CommandArguments;
import co.crystaldev.client.command.base.exceptions.CommandException;
import co.crystaldev.client.handler.WaypointHandler;
import co.crystaldev.client.util.ColorObject;
import co.crystaldev.client.util.objects.Waypoint;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

import java.util.Random;

@CommandInfo(name = "waypoints", usage = {"waypoints create <x> <y> <z> <name>"}, description = "Create waypoints through use of a command.", minimumArguments = 5)
public class WaypointsCommand extends AbstractCommand {
    public void execute(ICommandSender sender, CommandArguments arguments) throws CommandException {
        Waypoint waypoint;
        int x = arguments.getInt(1), y = arguments.getInt(2), z = arguments.getInt(3);
        String name = arguments.joinArgs(4);
        if ("create".equalsIgnoreCase(arguments.getString(0))) {
            Random r = new Random();
            ColorObject c = new ColorObject(r.nextInt(255), r.nextInt(255), r.nextInt(255), 180);
            waypoint = new Waypoint(name, Client.formatConnectedServerIp(), new BlockPos(x, y, z), c);
        } else {
            waypoint = new Waypoint(name, Client.formatConnectedServerIp(), new BlockPos(x, y, z));
            waypoint.setDuration(30000L).setServerSided(true);
        }
        WaypointHandler.getInstance().addWaypoint(waypoint.setWorld(Client.getCurrentWorldName()));
        Client.sendMessage(String.format("Waypoint '&b%s&r' has been created!", waypoint.getName()), true);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\command\WaypointsCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */