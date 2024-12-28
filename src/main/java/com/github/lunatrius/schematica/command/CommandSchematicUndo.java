package com.github.lunatrius.schematica.command;

import com.github.lunatrius.schematica.block.state.pattern.BlockStateReplacer;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import com.github.lunatrius.schematica.reference.Reference;
import com.github.lunatrius.schematica.util.SchematicReplaceAction;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.util.Arrays;
import java.util.List;

public class CommandSchematicUndo extends CommandSchematicaBase {
  public String getCommandName() {
    return "schematicaUndo";
  }
  
  public String getCommandUsage(ICommandSender sender) {
    return I18n.format("/schematicaUndo", new Object[0]);
  }
  
  public List<String> func_180525_a(ICommandSender sender, String[] args, BlockPos pos) {
    return null;
  }
  
  public List<String> getCommandAliases() {
    return Arrays.asList(new String[] { "schematicaundo", "schematicundo", "schematicUndo" });
  }
  
  public boolean canCommandSenderUseCommand(ICommandSender obj) {
    return true;
  }
  
  public boolean isUsernameIndex(String[] strings, int i) {
    return false;
  }
  
  public int compareTo(ICommand obj) {
    return 0;
  }
  
  public void processCommand(ICommandSender sender, String[] args) throws CommandException {
    SchematicWorld schematic = ClientProxy.currentSchematic.schematic;
    if (schematic == null)
      throw new CommandException(I18n.format("schematica.command.replace.noSchematic", new Object[0]), new Object[0]); 
    if (ClientProxy.currentSchematic.replaceHistory.isEmpty())
      throw new CommandException(I18n.format("There was nothing to undo."));
    SchematicReplaceAction action = ClientProxy.currentSchematic.replaceHistory.pop();
    if (action == null)
      throw new CommandException(I18n.format("There was nothing to undo."));
    try {
      BlockStateReplacer.BlockStateInfo replacementInfo = action.getPrevious();
      BlockStateReplacer replacer = BlockStateReplacer.forBlockState(replacementInfo.block.getDefaultState());
      int count = schematic.replaceBlocks(action.getUpdatedPositions(), replacer, replacementInfo.stateData);
      sender.addChatMessage((IChatComponent)new ChatComponentText("Undid replacement of " + count + " blocks."));
    } catch (Exception e) {
      Reference.logger.error("Something went wrong!", e);
      throw new CommandException(e.getMessage(), new Object[0]);
    } 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\command\CommandSchematicUndo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */