package com.github.lunatrius.schematica.client.gui.buttons;

import com.github.lunatrius.schematica.util.LoadedSchematic;
import net.minecraft.client.gui.GuiButton;

public class LoadedSchematicButton extends GuiButton {
    public final LoadedSchematic schematic;

    public LoadedSchematicButton(LoadedSchematic schematic, String name, int x, int y, int w, int h) {
        super(-1, x, y, w, h, name);
        this.schematic = schematic;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\gui\buttons\LoadedSchematicButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */