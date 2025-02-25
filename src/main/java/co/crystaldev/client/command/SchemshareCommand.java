package co.crystaldev.client.command;

import co.crystaldev.client.command.base.AbstractCommand;
import co.crystaldev.client.command.base.CommandInfo;
import co.crystaldev.client.command.base.args.CommandArguments;
import co.crystaldev.client.command.base.exceptions.CommandException;
import co.crystaldev.client.handler.SchematicHandler;
import net.minecraft.command.ICommandSender;

@CommandInfo(name = "schemshare", usage = {"schemshare <schematic directory> <schematic ID>"}, description = "Download schematics which were shared with you.", requiredArguments = 2)
public class SchemshareCommand extends AbstractCommand {
    public void execute(ICommandSender sender, CommandArguments arguments) throws CommandException {
        String dir = arguments.getString(0);
        String id = arguments.getString(1);
        SchematicHandler.getInstance().loadSchematic(dir, id);
    }
}