package com.github.lunatrius.schematica.util;

import co.crystaldev.client.util.objects.Transformation;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import net.minecraft.util.EnumFacing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadedSchematic {
  private static final AtomicInteger COUNTER = new AtomicInteger();
  
  private final UUID id = UUID.randomUUID();
  
  public List<Transformation> transformations = new ArrayList<>();
  
  public final Stack<SchematicReplaceAction> replaceHistory = new Stack<>();
  
  public final String name = "Schematic " + COUNTER.incrementAndGet();
  
  public File currentFile = null;
  
  public SchematicWorld schematic = null;
  
  public EnumFacing axisFlip = EnumFacing.UP;
  
  public EnumFacing axisRotation = EnumFacing.UP;
  
  public int totalBlocks = 0;
  
  public boolean equals(Object obj) {
    if (!(obj instanceof LoadedSchematic))
      return false; 
    return ((LoadedSchematic)obj).id.equals(this.id);
  }
  
  public String toString() {
    return this.name;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematic\\util\LoadedSchematic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */