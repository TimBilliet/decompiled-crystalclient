package co.crystaldev.client.handler;

import co.crystaldev.client.Reference;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.player.PlayerChatEvent;
import co.crystaldev.client.util.enums.ChatColor;
import net.minecraft.client.Minecraft;
import net.minecraft.command.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.*;

public class ClientCommandHandler implements ICommandManager, IRegistrable {
  private static final ClientCommandHandler INSTANCE = new ClientCommandHandler();
  
  public String[] latestAutoComplete = null;
  
  private final Map<String, ICommand> commandMap = new HashMap<>();
  
  private final Set<ICommand> commandSet = new HashSet<>();
  
  public void onPlayerChat(PlayerChatEvent event) {
    int code = executeCommand((ICommandSender)(Minecraft.getMinecraft()).thePlayer, event.message);
    if (code != 0 && event.isCancellable())
      event.setCancelled(true); 
  }
  
  public int executeCommand(ICommandSender sender, String rawCommand) {
    rawCommand = rawCommand.trim();
    if (!rawCommand.startsWith("/"))
      return 0; 
    rawCommand = rawCommand.substring(1);
    String[] temp = rawCommand.split(" ");
    String[] args = new String[temp.length - 1];
    String name = temp[0];
    System.arraycopy(temp, 1, args, 0, args.length);
    ICommand cmd = this.commandMap.get(name.toLowerCase());
    try {
      if (cmd == null)
        return 0; 
      if (cmd.canCommandSenderUseCommand(sender)) {
        cmd.processCommand(sender, args);
        return 1;
      } 
      sender.addChatMessage((IChatComponent)format(EnumChatFormatting.RED, "commands.generic.permission", new Object[0]));
    } catch (WrongUsageException ex) {
      sender.addChatMessage((IChatComponent)format(EnumChatFormatting.RED, "commands.generic.usage", new Object[] { format(EnumChatFormatting.RED, ex.getMessage(), ex.getErrorObjects()) }));
    } catch (CommandException ex) {
      sender.addChatMessage((IChatComponent)format(EnumChatFormatting.RED, ex.getMessage(), ex.getErrorObjects()));
    } catch (Exception ex) {
      sender.addChatMessage((IChatComponent)format(EnumChatFormatting.RED, "commands.generic.exception", new Object[0]));
      Reference.LOGGER.error("Error executing command", ex);
    } 
    return -1;
  }
  
  public ICommand registerCommand(ICommand command) {
    this.commandMap.put(command.getCommandName(), command);
    this.commandSet.add(command);
    for (String s : command.getCommandAliases()) {
      ICommand cmd = this.commandMap.get(s);
      if (cmd == null || !cmd.getCommandName().equals(s))
        this.commandMap.put(s, command); 
    } 
    return command;
  }
  
  public List<String> getTabCompletionOptions(ICommandSender sender, String input, BlockPos pos) {
    String[] astring = input.split(" ", -1);
    String s = astring[0];
    if (astring.length == 1) {
      List<String> list = new ArrayList<>();
      for (Map.Entry<String, ICommand> entry : this.commandMap.entrySet()) {
        if (CommandBase.doesStringStartWith(s, entry.getKey()) && ((ICommand)entry.getValue()).canCommandSenderUseCommand(sender))
          list.add(entry.getKey()); 
      } 
      return list;
    } 
    ICommand cmd = this.commandMap.get(s);
    if (cmd != null && cmd.canCommandSenderUseCommand(sender))
      return cmd.addTabCompletionOptions(sender, dropFirstString(astring), pos);
      //return cmd.func_180525_a(sender, dropFirstString(astring), pos);
    return null;
  }
  
  public List<ICommand> getPossibleCommands(ICommandSender sender) {
    List<ICommand> list = new ArrayList<>();
    for (ICommand cmd : this.commandSet) {
      if (cmd.canCommandSenderUseCommand(sender))
        list.add(cmd); 
    } 
    return list;
  }
  
  public Map<String, ICommand> getCommands() {
    return this.commandMap;
  }
  
  private static String[] dropFirstString(String[] input) {
    String[] astring = new String[input.length - 1];
    System.arraycopy(input, 1, astring, 0, input.length - 1);
    return astring;
  }
  
  private ChatComponentTranslation format(EnumChatFormatting color, String str, Object... args) {
    ChatComponentTranslation ret = new ChatComponentTranslation(str, args);
    ret.getChatStyle().setColor(color);
    return ret;
  }
  
  public void autoComplete(String leftOfCursor, String full) {
    this.latestAutoComplete = null;
    if (leftOfCursor.charAt(0) == '/') {
      leftOfCursor = leftOfCursor.substring(1);
      Minecraft mc = Minecraft.getMinecraft();
      if (mc.currentScreen instanceof net.minecraft.client.gui.GuiChat) {
        List<String> commands = getTabCompletionOptions((ICommandSender)mc.thePlayer, leftOfCursor, mc.thePlayer.getPosition());
        if (commands != null && !commands.isEmpty()) {
          if (leftOfCursor.indexOf(' ') == -1) {
            for (int i = 0; i < commands.size(); i++) {
              commands.set(i, ChatColor.translate(String.format("&7/%s&r", new Object[] { commands.get(i) })));
            } 
          } else {
            for (int i = 0; i < commands.size(); i++) {
              commands.set(i, ChatColor.translate(String.format("&7%s&r", new Object[] { commands.get(i) })));
            } 
          } 
          this.latestAutoComplete = commands.<String>toArray(new String[commands.size()]);
        } 
      } 
    } 
  }
  
  public static ClientCommandHandler getInstance() {
    return INSTANCE;
  }
  
  public void registerEvents() {
    EventBus.register(this, PlayerChatEvent.class, this::onPlayerChat);
  }
}
