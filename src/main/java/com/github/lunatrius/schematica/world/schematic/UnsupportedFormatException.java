package com.github.lunatrius.schematica.world.schematic;

public class UnsupportedFormatException extends Exception {
  public UnsupportedFormatException(String format) {
    super(String.format("Unsupported format: %s", new Object[] { format }));
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\world\schematic\UnsupportedFormatException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */