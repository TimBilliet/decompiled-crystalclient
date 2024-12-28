package co.crystaldev.client.command;

import co.crystaldev.client.command.base.AbstractCommand;
import co.crystaldev.client.command.base.CommandInfo;
import co.crystaldev.client.command.base.args.CommandArguments;
import co.crystaldev.client.command.base.exceptions.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

@CommandInfo(name = "dupe", description = "Easily dupe on servers lol", minimumArguments = 2, maximumArguments = 3)
public class DupeCommand extends AbstractCommand {
  public void execute(ICommandSender sender, CommandArguments arguments) throws CommandException {
    int blockId = arguments.getInt(0);
    int itemCount = arguments.getInt(1);
    if (!this.mc.playerController.isInCreativeMode())
      throw new CommandException("You must be in creative mode!", new Object[0]); 
    Item item = Item.getItemById(blockId);
    if (item == null) {
      sender.addChatMessage((IChatComponent)new ChatComponentText("The given item is invalid."));
    } else {
      ItemStack stack = new ItemStack(item, itemCount);
      sender.addChatMessage((IChatComponent)new ChatComponentText(String.format("Dropping %dx %s...", new Object[] { Integer.valueOf(itemCount), item.getItemStackDisplayName(stack) })));
      stack.stackSize = itemCount;
      stack.setItemDamage(stack.getMaxDamage() - 1);
      if (arguments.size() > 2)
        System.out.println("Cock spawner sharpness");
        stack.addEnchantment(Enchantment.sharpness, arguments.getInt(2));
        //stack.addEnchantment(Enchantment.field_180314_l, arguments.getInt(2));
      this.mc.getNetHandler().addToSendQueue((Packet)new C10PacketCreativeInventoryAction(3, stack));
    } 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\command\DupeCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */