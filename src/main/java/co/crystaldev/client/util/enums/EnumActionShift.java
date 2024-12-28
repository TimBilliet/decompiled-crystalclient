package co.crystaldev.client.util.enums;

public enum EnumActionShift {
  ADD("ADD"),
  REMOVE("REMOVE");
  
  EnumActionShift(String serializationString) {
    this.serializationString = serializationString;
  }
  
  private final String serializationString;
  
  public String getSerializationString() {
    return this.serializationString;
  }
  
  public String toString() {
    return this.serializationString;
  }
  
  public static EnumActionShift fromString(String name) {
    for (EnumActionShift action : values()) {
      if (action.getSerializationString().equalsIgnoreCase(name))
        return action; 
    } 
    return null;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\enums\EnumActionShift.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */