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
            throw new CommandException("You must be in creative mode!");
        Item item = Item.getItemById(blockId);
        if (item == null) {
            sender.addChatMessage(new ChatComponentText("The given item is invalid."));
        } else {
            ItemStack stack = new ItemStack(item, itemCount);
            sender.addChatMessage(new ChatComponentText(String.format("Dropping %dx %s...", itemCount, item.getItemStackDisplayName(stack))));
            stack.stackSize = itemCount;
            stack.setItemDamage(stack.getMaxDamage() - 1);
            if (arguments.size() > 2)
                stack.addEnchantment(Enchantment.sharpness, arguments.getInt(2));
            this.mc.getNetHandler().addToSendQueue(new C10PacketCreativeInventoryAction(3, stack));
        }
    }
}