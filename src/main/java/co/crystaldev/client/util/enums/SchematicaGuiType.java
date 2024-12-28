package co.crystaldev.client.util.enums;

public enum SchematicaGuiType {
  LOAD_SCHEMATIC("Load"),
  CONTROL_SCHEMATIC("Control"),
  SAVE_SCHEMATIC("Save");
  
  SchematicaGuiType(String displayText) {
    this.displayText = displayText;
  }
  
  private final String displayText;
  
  public String getDisplayText() {
    return this.displayText;
  }
  
  public String toString() {
    return this.displayText;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\enums\SchematicaGuiType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */