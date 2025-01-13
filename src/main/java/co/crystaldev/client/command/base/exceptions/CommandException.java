package co.crystaldev.client.command.base.exceptions;

public class CommandException extends Exception {
    public CommandException(String message, Object... args) {
        super(String.format(message, args));
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\command\base\exceptions\CommandException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */