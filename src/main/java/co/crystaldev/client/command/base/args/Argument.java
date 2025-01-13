package co.crystaldev.client.command.base.args;

import co.crystaldev.client.command.base.exceptions.ArgumentFormatException;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.util.MathHelper;

import java.util.UUID;

public class Argument {
    private final String argument;

    public Argument(String argument) {
        this.argument = argument;
    }

    public String getAsString() {
        return this.argument;
    }

    public int getAsInt() throws ArgumentFormatException {
        try {
            return Integer.parseInt(this.argument);
        } catch (NumberFormatException ex) {
            throw new ArgumentFormatException(String.format("Argument '%s' is required to be an integer", new Object[]{this.argument}), new Object[0]);
        }
    }

    public int getAsInt(int min, int max) throws ArgumentFormatException {
        return MathHelper.clamp_int(getAsInt(), min, max);
    }

    public float getAsFloat() throws ArgumentFormatException {
        try {
            return Float.parseFloat(this.argument);
        } catch (NumberFormatException ex) {
            throw new ArgumentFormatException(String.format("Argument '%s' is required to be a float", new Object[]{this.argument}), new Object[0]);
        }
    }

    public float getAsFloat(float min, float max) throws ArgumentFormatException {
        return MathHelper.clamp_float(getAsFloat(), min, max);
    }

    public double getAsDouble() throws ArgumentFormatException {
        try {
            return Double.parseDouble(this.argument);
        } catch (NumberFormatException ex) {
            throw new ArgumentFormatException(String.format("Argument '%s' is required to be a double", new Object[]{this.argument}), new Object[0]);
        }
    }

    public double getAsDouble(double min, double max) throws ArgumentFormatException {
        return MathHelper.clamp_double(getAsDouble(), min, max);
    }

    public boolean getBoolean() {
        return (this.argument.equalsIgnoreCase("true") || this.argument.equalsIgnoreCase("yes"));
    }

    public UUID getAsUUID() throws ArgumentFormatException {
        try {
            if (this.argument.contains("-"))
                return UUID.fromString(this.argument);
            return UUIDTypeAdapter.fromString(this.argument);
        } catch (IllegalArgumentException ex) {
            throw new ArgumentFormatException("Argument '%s' is required to be an UUID", new Object[0]);
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\command\base\args\Argument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */