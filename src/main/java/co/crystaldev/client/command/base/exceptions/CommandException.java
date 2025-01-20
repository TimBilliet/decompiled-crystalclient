package co.crystaldev.client.command.base.exceptions;

public class CommandException extends Exception {
    public CommandException(String message, Object... args) {
        super(String.format(message, args));
    }
}
