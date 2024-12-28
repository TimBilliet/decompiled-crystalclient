package com.github.lunatrius.schematica.api.event;

import co.crystaldev.client.event.Event;
import com.github.lunatrius.schematica.api.ISchematic;

public class PostSchematicCaptureEvent extends Event {
  public final ISchematic schematic;
  
  public PostSchematicCaptureEvent(ISchematic schematic) {
    this.schematic = schematic;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\api\event\PostSchematicCaptureEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */