package com.github.lunatrius.schematica.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public abstract class CommandSchematicaBase extends CommandBase {
  public int getRequiredPermissionLevel() {
    return 0;
  }
  
  public boolean canCommandSenderUseCommand(ICommandSender sender) {
    return (super.canCommandSenderUseCommand(sender) || (sender instanceof net.minecraft.entity.player.EntityPlayerMP && getRequiredPermissionLevel() <= 0));
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\command\CommandSchematicaBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */