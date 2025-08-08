package com.github.timmekeclient.command;

import com.github.timmekeclient.Client;
import com.github.timmekeclient.command.base.AbstractCommand;
import com.github.timmekeclient.command.base.CommandInfo;
import com.github.timmekeclient.command.base.args.CommandArguments;
import com.github.timmekeclient.command.base.exceptions.CommandException;
import com.github.timmekeclient.feature.impl.factions.Patchcrumbs;
import net.minecraft.command.ICommandSender;


@CommandInfo(name = "exclude" , description = "Set bounds to ignore patchcrumbs in", usage = {"/exclude pos1 &7- Defines the block you're looking at as the first position of the exclusion area", "/exclude pos2 &7- Defines the block you're looking at as the second position of the exclusion area"}, minimumArguments = 1, maximumArguments = 1)
public class PatchcrumbsCommand extends AbstractCommand {
    @Override
    public void execute(ICommandSender sender, CommandArguments arguments) throws CommandException {
        Patchcrumbs patchcrumbs =  Patchcrumbs.getInstance();
        if (!patchcrumbs.enabled || !patchcrumbs.exclusionZone) {
            sendErrorMessage("This command cannot be used while the module or setting is disabled.");
            return;
        }
        switch (arguments.getString(0).toLowerCase()){
            case "pos1":
                patchcrumbs.setExclusionZonePos(true);
                return;
            case "pos2":
                patchcrumbs.setExclusionZonePos(false);
                return;
        }
        Client.sendMessage(getCommandUsage(sender), false);

    }
}
