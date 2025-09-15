package com.github.timmekeclient.command;

import com.github.timmekeclient.Client;
import com.github.timmekeclient.command.base.AbstractCommand;
import com.github.timmekeclient.command.base.CommandInfo;
import com.github.timmekeclient.command.base.args.CommandArguments;
import com.github.timmekeclient.command.base.exceptions.CommandException;
import com.github.timmekeclient.feature.impl.all.ClickSettings;
import net.minecraft.command.ICommandSender;

import java.util.Locale;

@CommandInfo(name = "click", description = "Quickly access to many Click Settings features.", usage = {"click [right | left] &7- Holds right | left click."}, minimumArguments = 1)

public class ClickCommand extends AbstractCommand {
    @Override
    public void execute(ICommandSender sender, CommandArguments arguments) throws CommandException {
        ClickSettings clickSettings  = ClickSettings.getInstance();
        if (!clickSettings.enabled) {
            sendErrorMessage("This command cannot be used while the module is disabled.");
            return;
        }
        switch (arguments.getString(0).toLowerCase(Locale.ROOT)){
            case "right":
                clickSettings.holdRightClick = !clickSettings.holdRightClick;
                Client.sendMessage(clickSettings.holdRightClick ? "Holding right click" : "Stopped holding right click", true);
                return;
            case "left":
                clickSettings.holdLeftClick = !clickSettings.holdLeftClick;
                Client.sendMessage(clickSettings.holdLeftClick ? "Holding left click" : "Stopped holding left click", true);
                return;
        }
        Client.sendMessage(getCommandUsage(sender), false);

    }
}