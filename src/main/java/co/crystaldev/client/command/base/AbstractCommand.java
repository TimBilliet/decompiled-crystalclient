package co.crystaldev.client.command.base;

import co.crystaldev.client.Client;
import co.crystaldev.client.command.base.args.CommandArguments;
import co.crystaldev.client.command.base.exceptions.CommandException;
import co.crystaldev.client.util.enums.ChatColor;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractCommand extends CommandBase {
  protected Minecraft mc = Minecraft.getMinecraft();
  
  private final String commandName;
  
  private final String description;
  
  private final List<String> commandUsage;
  
  private final List<String> commandAliases;
  
  private final int minArgs;
  
  private final int maxArgs;
  
  private final int requiredArgs;
  
  public String getCommandName() {
    return this.commandName;
  }
  
  public String getDescription() {
    return this.description;
  }
  
  public List<String> getCommandUsage() {
    return this.commandUsage;
  }
  
  public List<String> getCommandAliases() {
    return this.commandAliases;
  }
  
  public int getMinArgs() {
    return this.minArgs;
  }
  
  public int getMaxArgs() {
    return this.maxArgs;
  }
  
  public int getRequiredArgs() {
    return this.requiredArgs;
  }
  
  public AbstractCommand() {
    CommandInfo commandInfo = getClass().<CommandInfo>getAnnotation(CommandInfo.class);
    this.commandName = commandInfo.name();
    this.description = commandInfo.description();
    this.commandUsage = Arrays.asList(commandInfo.usage());
    this.commandAliases = Arrays.asList(commandInfo.aliases());
    this.minArgs = commandInfo.minimumArguments();
    this.maxArgs = commandInfo.maximumArguments();
    this.requiredArgs = commandInfo.requiredArguments();
  }
  
  public String getErrorMessage(String msg) {
    return ChatColor.translate(String.format("%s &cError:&r %s", new Object[] { Client.getErrorPrefix(), msg }));
  }
  
  public String getErrorMessage(CommandException ex) {
    return getErrorMessage(ex.getMessage());
  }
  
  public void sendErrorMessage(String msg) {
    Client.sendMessage(getErrorMessage(msg), false);
  }
  
  public void sendErrorMessage(CommandException ex) {
    sendErrorMessage(ex.getMessage());
  }
  
  public boolean canCommandSenderUseCommand(ICommandSender obj) {
    return true;
  }
  
  public void processCommand(ICommandSender sender, String[] args) {
    try {
      CommandArguments arguments = new CommandArguments(this, args);
      execute(sender, arguments);
    } catch (CommandException ex) {
      sendErrorMessage(ex);
    } 
  }
  
  public String getCommandUsage(ICommandSender sender) {
    if (this.commandUsage.size() <= 1) {
      String usage = this.commandUsage.isEmpty() ? this.commandName : this.commandUsage.get(0);
      return ChatColor.translate(String.format("%s &bUsage: &r/%s", new Object[] { Client.getPrefix(), usage }));
    } 
    String header = String.format("&8&m--------------&8[ &b&lCommand: &r%s &8]&8&m--------------&r", new Object[] { this.commandName });
    String footer = "&8&m-";
    int topLength = this.mc.fontRendererObj.getStringWidth(ChatColor.translate(header));
    for (; this.mc.fontRendererObj.getStringWidth(ChatColor.translate(footer)) < topLength; footer = footer + "-");
    return ChatColor.translate(header + "\n" + String.join("\n", (Iterable)this.commandUsage) + "\n" + footer);
  }
  
  public boolean isUsernameIndex(String[] strings, int i) {
    return false;
  }
  
  public int compareTo(ICommand obj) {
    return 0;
  }
  
  public abstract void execute(ICommandSender paramICommandSender, CommandArguments paramCommandArguments) throws CommandException;
}
