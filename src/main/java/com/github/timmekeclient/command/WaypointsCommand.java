package com.github.timmekeclient.command;

import com.github.timmekeclient.Client;
import com.github.timmekeclient.command.base.AbstractCommand;
import com.github.timmekeclient.command.base.CommandInfo;
import com.github.timmekeclient.command.base.args.CommandArguments;
import com.github.timmekeclient.command.base.exceptions.CommandException;
import com.github.timmekeclient.handler.WaypointHandler;
import com.github.timmekeclient.util.ColorObject;
import com.github.timmekeclient.util.objects.Waypoint;
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