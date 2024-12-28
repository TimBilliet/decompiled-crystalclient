package co.crystaldev.client.command;

import co.crystaldev.client.Client;
import co.crystaldev.client.command.base.AbstractCommand;
import co.crystaldev.client.command.base.CommandInfo;
import co.crystaldev.client.command.base.args.CommandArguments;
import co.crystaldev.client.command.base.exceptions.CommandException;
import co.crystaldev.client.feature.annotations.properties.Slider;
import co.crystaldev.client.feature.impl.mechanic.ToggleSneak;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.MathHelper;

import java.lang.reflect.Field;

@CommandInfo(name = "flyboost", aliases = {"boost", "fb"}, usage = {"flyboost [amount | toggle]"}, description = "Modify your flyboost speed.", requiredArguments = 1)
public class FlyboostCommand extends AbstractCommand {
  public void execute(ICommandSender sender, CommandArguments arguments) throws CommandException {
    if (arguments.getString(0).equalsIgnoreCase("toggle")) {
      (ToggleSneak.getInstance()).flyBoost = !(ToggleSneak.getInstance()).flyBoost;
      Client.sendMessage(ToggleSneak.getInstance().getToggleMessage("Flyboost", (ToggleSneak.getInstance()).flyBoost), true);
    } else {
      Field field = ToggleSneak.getInstance().getFieldFromOption("Fly Boost Multiplier");
      Slider slider = field.<Slider>getAnnotation(Slider.class);
      double newFlySpeed = MathHelper.clamp_double(arguments.getDouble(0), slider.minimum(), slider.maximum());
      (ToggleSneak.getInstance()).flyBoostMultiplier = newFlySpeed;
      Client.sendMessage(String.format("Flyboost speed modified to &b%.2fx", new Object[] { Double.valueOf(newFlySpeed) }), true);
    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\command\FlyboostCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */