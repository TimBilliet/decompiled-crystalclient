package co.crystaldev.client.command.base.exceptions;

public class ArgumentFormatException extends CommandException {
    public ArgumentFormatException(String message, Object... args) {
        super(message, args);
    }
}
