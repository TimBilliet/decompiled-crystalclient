package co.crystaldev.client.command.base.args;

import co.crystaldev.client.Client;
import co.crystaldev.client.command.base.AbstractCommand;
import co.crystaldev.client.command.base.exceptions.ArgumentFormatException;
import co.crystaldev.client.command.base.exceptions.CommandException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandArguments implements Iterable<Argument> {
  private static final Pattern ARGUMENT_PATTERN = Pattern.compile("\"([^\"]+)\"|'([^']+)'|([^\\s]+)");
  
  private final AbstractCommand command;
  
  private final List<Argument> arguments = new ArrayList<>();
  
  public List<Argument> getArguments() {
    return this.arguments;
  }
  
  public CommandArguments(AbstractCommand command, String[] args) throws CommandException {
    this.command = command;
    Matcher matcher = ARGUMENT_PATTERN.matcher(String.join(" ", (CharSequence[])args));
    while (matcher.find()) {
      for (int i = 0; i < matcher.groupCount(); i++) {
        String group = matcher.group(i);
        if (group != null) {
          if (group.contains(" "))
            for (char character : group.toCharArray()) {
              if (character == '"' || character == '\'') {
                group = group.replace(Character.toString(character), "");
                break;
              } 
            }  
          this.arguments.add(new Argument(group));
          break;
        } 
      } 
    } 
    ensureArguments(this.command.getMinArgs(), this.command.getMaxArgs(), this.command.getRequiredArgs());
  }
  
  public Argument get(int index) {
    return this.arguments.get(index);
  }
  
  public String getString(int index) {
    return get(index).getAsString();
  }
  
  public String joinArgs(int startingIndex) {
    startingIndex = Math.min(startingIndex, size() - 1);
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = startingIndex; i < this.arguments.size(); i++)
      stringBuilder.append(getString(i)).append(" "); 
    return stringBuilder.toString().trim();
  }
  
  public int getInt(int index) throws ArgumentFormatException {
    return get(index).getAsInt();
  }
  
  public int getInt(int index, int min, int max) throws ArgumentFormatException {
    return get(index).getAsInt(min, max);
  }
  
  public float getFloat(int index) throws ArgumentFormatException {
    return get(index).getAsFloat();
  }
  
  public float getFloat(int index, float min, float max) throws ArgumentFormatException {
    return get(index).getAsFloat(min, max);
  }
  
  public double getDouble(int index) throws ArgumentFormatException {
    return get(index).getAsDouble();
  }
  
  public double getDouble(int index, double min, double max) throws ArgumentFormatException {
    return get(index).getAsDouble(min, max);
  }
  
  public boolean getBoolean(int index) {
    return get(index).getBoolean();
  }
  
  public UUID getUUID(int index) throws ArgumentFormatException {
    return get(index).getAsUUID();
  }
  
  public boolean argumentExistsAtIndex(int index) {
    return (index >= 0 && index < size());
  }
  
  public void ensureArguments(int minArgs, int maxArgs, int requiredArgs) throws CommandException {
    CommandException ex = null;
    if (requiredArgs >= 0 && this.arguments.size() != requiredArgs) {
      ex = new CommandException("Argument count does not match required count. Expected %d argument(s), arguments given: %d", new Object[] { Integer.valueOf(requiredArgs), Integer.valueOf(this.arguments.size()) });
    } else if (minArgs >= 0 && this.arguments.size() < minArgs) {
      ex = new CommandException("Argument count is too small. Expected at least %d argument(s), arguments given: %d", new Object[] { Integer.valueOf(minArgs), Integer.valueOf(this.arguments.size()) });
    } else if (maxArgs >= 0 && this.arguments.size() > maxArgs) {
      ex = new CommandException("Argument count is too large. Expected at most %d argument(s), arguments given: %d", new Object[] { Integer.valueOf(minArgs), Integer.valueOf(this.arguments.size()) });
    } 
    if (ex != null) {
      if (this.arguments.isEmpty())
        Client.sendMessage(this.command.getCommandUsage(null), false); 
      throw ex;
    } 
  }
  
  public boolean ensureArguments(int minArgs, int maxArgs, int requiredArgs, String errorMessage) {
    try {
      ensureArguments(minArgs, maxArgs, requiredArgs);
      return true;
    } catch (CommandException ex) {
      this.command.sendErrorMessage(errorMessage);
      return false;
    } 
  }
  
  public boolean isEmpty() {
    return this.arguments.isEmpty();
  }
  
  public int size() {
    return this.arguments.size();
  }
  
  public Iterator<Argument> iterator() {
    return this.arguments.iterator();
  }
  
  public String toString() {
    StringBuilder elements = new StringBuilder(isEmpty() ? "[]" : "[");
    for (Argument argument : this)
      elements.append("\n\t\t").append(argument.getAsString()); 
    if (!elements.toString().endsWith("]"))
      elements.append("\n\t").append("]").append('\n'); 
    return String.format("CommandArguments {\n\tsize = %d,\n\telements = %s}", new Object[] { Integer.valueOf(size()), elements });
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\command\base\args\CommandArguments.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */