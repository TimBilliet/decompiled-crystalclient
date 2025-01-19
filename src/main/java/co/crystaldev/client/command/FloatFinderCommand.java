package co.crystaldev.client.command;

import co.crystaldev.client.Client;
import co.crystaldev.client.command.base.AbstractCommand;
import co.crystaldev.client.command.base.CommandInfo;
import co.crystaldev.client.command.base.args.CommandArguments;
import co.crystaldev.client.command.base.exceptions.CommandException;
import co.crystaldev.client.feature.impl.factions.FloatFinder;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

@CommandInfo(name = "floatfinder", aliases = {"float"}, description = "Quickly access to some float finder features.", usage = {"/float set &7- Defines the barrel block", "/float reset &7- Resets the barrel block", "/float find &7- Try and find the float"}, minimumArguments = 1)
public class FloatFinderCommand extends AbstractCommand {
    public void execute(ICommandSender sender, CommandArguments arguments) throws CommandException {
        if (!(FloatFinder.getInstance()).enabled) {
            sendErrorMessage("This command cannot be used while the module is disabled.");
            return;
        }
        switch (arguments.getString(0).toLowerCase()) {
            case "set":
                (FloatFinder.getInstance()).selectBarrelBlock();
                return;
            case "find":
                (FloatFinder.getInstance()).findFloatOnce();
                return;
            case "reset":
                (FloatFinder.getInstance()).barrelBlockPos = new BlockPos(0, 0, 0);
                (FloatFinder.getInstance()).horizontal = new BlockPos(0, 0, 0);
                (FloatFinder.getInstance()).vertical = new BlockPos(0, 0, 0);
                Client.sendMessage("Float finder positions have been reset", true);
                return;
        }
        Client.sendMessage(getCommandUsage(sender), false);
    }
}
