package com.github.lunatrius.schematica.client.gui.buttons;

import com.github.timmekeclient.util.objects.Schematic;
import net.minecraft.client.gui.GuiButton;

public class SchematicHistoryButton extends GuiButton {
    public final Schematic schematic;

    public SchematicHistoryButton(Schematic schematic, int x, int y, int w, int h) {
        super(-1, x, y, w, h, schematic.getFile().getName().replace(".schematic", ""));
        this.schematic = schematic;
    }
}
