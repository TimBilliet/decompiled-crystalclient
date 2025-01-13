package com.github.lunatrius.schematica.world;

import net.minecraft.world.WorldProvider;

public class WorldProviderSchematic extends WorldProvider {
    public String getDimensionName() {
        return "Schematic";
    }

    public String getInternalNameSuffix() {
        return "_schematic";
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\world\WorldProviderSchematic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */