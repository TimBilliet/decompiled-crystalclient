package com.github.timmekeclient.command;

import com.github.timmekeclient.Client;
import com.github.timmekeclient.command.base.AbstractCommand;
import com.github.timmekeclient.command.base.CommandInfo;
import com.github.timmekeclient.command.base.args.CommandArguments;
import com.github.timmekeclient.command.base.exceptions.CommandException;
import com.github.timmekeclient.feature.impl.factions.FloatFinder;
import net.minecraft.command.ICommandSender;

@CommandInfo(name = "floatfinder", aliases = {"float"}, description = "Quickly access to some float finder features.", usage = {"/float setbarrel &7- Defines the block you're looking at as barrel block", "/float setpower &7- Defines the block above the one you're looking at as power block", "/float reset &7- Reset all positions and overlays", "/float find &7- Try and find the float position"}, minimumArguments = 1, maximumArguments = 1)
public class FloatFinderCommand extends AbstractCommand {
    public void execute(ICommandSender sender, CommandArguments arguments) throws CommandException {
        FloatFinder floatInst = FloatFinder.getInstance();
        if (!floatInst.enabled) {
            sendErrorMessage("This command cannot be used while the module is disabled.");
            return;
        }
        switch (arguments.getString(0).toLowerCase()) {
            case "setbarrel":
                floatInst.selectBarrelBlock();
                return;
            case "setpower":
                floatInst.selectPowerBlock();
                return;
            case "find":
                floatInst.findFloatOnce();
                return;
            case "reset":
                floatInst.barrelBlockPos = null;
                floatInst.horizontal = null;
                floatInst.vertical = null;
                floatInst.powerBlockPos = null;
                Client.sendMessage("Float finder positions have been reset", true);
                return;
        }
        Client.sendMessage(getCommandUsage(sender), false);
    }
}
