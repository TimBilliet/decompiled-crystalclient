package com.github.lunatrius.schematica.api.event;

import com.github.lunatrius.schematica.api.ISchematic;
import com.github.timmekeclient.event.Event;

public class PostSchematicCaptureEvent extends Event {
    public final ISchematic schematic;

    public PostSchematicCaptureEvent(ISchematic schematic) {
        this.schematic = schematic;
    }
}
