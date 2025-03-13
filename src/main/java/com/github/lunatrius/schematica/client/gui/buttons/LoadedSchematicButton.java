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
