package com.github.lunatrius.schematica.world;

import com.github.lunatrius.schematica.world.storage.SaveHandlerSchematic;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

public class WorldDummy extends World {
    private static WorldDummy instance;

    protected WorldDummy(ISaveHandler saveHandler, WorldInfo worldInfo, WorldProvider worldProvider, Profiler profiler, boolean client) {
        super(saveHandler, worldInfo, worldProvider, profiler, client);
    }

    protected IChunkProvider createChunkProvider() {
        return null;
    }


    protected int getRenderDistanceChunks() {
        return 0;
    }

    public static WorldDummy instance() {
        if (instance == null) {
            WorldSettings worldSettings = new WorldSettings(0L, WorldSettings.GameType.CREATIVE, false, false, WorldType.FLAT);
            WorldInfo worldInfo = new WorldInfo(worldSettings, "FakeWorld");
            instance = new WorldDummy((ISaveHandler) new SaveHandlerSchematic(), worldInfo, new WorldProviderSchematic(), new Profiler(), false);
        }
        return instance;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\world\WorldDummy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */