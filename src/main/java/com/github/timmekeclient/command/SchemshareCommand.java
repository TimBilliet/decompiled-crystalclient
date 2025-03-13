package com.github.timmekeclient.command;

import com.github.timmekeclient.command.base.AbstractCommand;
import com.github.timmekeclient.command.base.CommandInfo;
import com.github.timmekeclient.command.base.args.CommandArguments;
import com.github.timmekeclient.command.base.exceptions.CommandException;
import com.github.timmekeclient.handler.SchematicHandler;
import net.minecraft.command.ICommandSender;

@CommandInfo(name = "schemshare", usage = {"schemshare <schematic directory> <schematic ID>"}, description = "Download schematics which were shared with you.", requiredArguments = 2)
public class SchemshareCommand extends AbstractCommand {
    public void execute(ICommandSender sender, CommandArguments arguments) throws CommandException {
        String dir = arguments.getString(0);
        String id = arguments.getString(1);
        SchematicHandler.getInstance().loadSchematic(dir, id);
    }
}