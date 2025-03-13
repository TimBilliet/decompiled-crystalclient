package com.github.timmekeclient.command;

import com.github.timmekeclient.Client;
import com.github.timmekeclient.command.base.AbstractCommand;
import com.github.timmekeclient.command.base.CommandInfo;
import com.github.timmekeclient.command.base.args.CommandArguments;
import com.github.timmekeclient.command.base.exceptions.CommandException;
import com.github.timmekeclient.feature.annotations.properties.Slider;
import com.github.timmekeclient.feature.impl.mechanic.ToggleSneak;
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
            Client.sendMessage(String.format("Flyboost speed modified to &b%.2fx", new Object[]{Double.valueOf(newFlySpeed)}), true);
        }
    }
}