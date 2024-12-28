package com.github.lunatrius.schematica.client.gui.buttons;

import co.crystaldev.client.util.objects.Schematic;
import net.minecraft.client.gui.GuiButton;

public class SchematicHistoryButton extends GuiButton {
  public final Schematic schematic;
  
  public SchematicHistoryButton(Schematic schematic, int x, int y, int w, int h) {
    super(-1, x, y, w, h, schematic.getFile().getName().replace(".schematic", ""));
    this.schematic = schematic;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\gui\buttons\SchematicHistoryButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */