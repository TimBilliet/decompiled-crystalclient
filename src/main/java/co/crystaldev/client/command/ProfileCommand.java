package co.crystaldev.client.command;

import co.crystaldev.client.Client;
import co.crystaldev.client.command.base.AbstractCommand;
import co.crystaldev.client.command.base.CommandInfo;
import co.crystaldev.client.command.base.args.CommandArguments;
import co.crystaldev.client.command.base.exceptions.CommandException;
import co.crystaldev.client.handler.ProfileHandler;
import co.crystaldev.client.util.enums.ChatColor;
import co.crystaldev.client.util.objects.profiles.Profile;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.util.List;
import java.util.UUID;

@CommandInfo(name = "profile", aliases = {"profiles"}, usage = {"/profile list &7- List all available profiles.", "/profile create <name> [auto use on server] &7- Create a new profile.", "/profile switch <name> &7- Switch to a different profile."}, description = "Manage and create profiles through a command interface.", minimumArguments = 1)
public class ProfileCommand extends AbstractCommand {
    public void execute(ICommandSender sender, CommandArguments arguments) throws CommandException {
        List<Profile> profiles;
        Profile selected;
        switch (arguments.getString(0).toLowerCase()) {
            case "create":
                if (arguments.ensureArguments(-1, -1, 3, "You need a name for this profile!")) {
                    ProfileHandler.getInstance().createNewProfile(arguments.getString(1), arguments.getBoolean(2));
                    Client.sendMessage("Created a new profile", true);
                }
                return;
            case "switch":
                try {
                    UUID uuid = arguments.getUUID(1);
                    ProfileHandler.getInstance().swapToProfile(uuid);
                    if (ProfileHandler.getInstance().getSelectedProfile() == null) {
                        sendErrorMessage("No profile was found by that name/ID");
                    } else {
                        Client.sendMessage("Profile was switched to &b" + ProfileHandler.getInstance().getSelectedProfile().getName(), true);
                    }
                } catch (Exception ex) {
                    for (Profile profile : ProfileHandler.getInstance().getProfiles()) {
                        if (profile.getName().equals(arguments.getString(1))) {
                            ProfileHandler.getInstance().swapToProfile(profile);
                            Client.sendMessage("Profile was switched to &b" + ProfileHandler.getInstance().getSelectedProfile().getName(), true);
                            return;
                        }
                    }
                }
                sendErrorMessage("No profile was found.");
                return;
            case "list":
                profiles = ProfileHandler.getInstance().getProfiles();
                selected = ProfileHandler.getInstance().getSelectedProfile();
                if (profiles.isEmpty()) {
                    sendErrorMessage("There are no profiles");
                } else {
                    Client.sendMessage("Available Profiles &8[&7" + profiles.size() + " loaded&8]", true);
                    for (Profile profile : profiles) {
                        ChatComponentText ch;
                        if (selected != null && profile.getId().equals(selected.getId())) {
                            ch = new ChatComponentText(ChatColor.translate("&7 - &b&lSelected Profile:&r " + profile.getName()));
                        } else {
                            ch = new ChatComponentText(ChatColor.translate("&7 - &r&lProfile:&r " + profile.getName()));
                            ch.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/profile switch " + profile
                                    .getId()));
                            ch.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (IChatComponent) new ChatComponentText(
                                    ChatColor.translate("&rClick to select"))));
                        }
                        this.mc.ingameGUI.getChatGUI().printChatMessage((IChatComponent) ch);
                    }
                }
                return;
        }
        Client.sendMessage(getCommandUsage(null), false);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\command\ProfileCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */