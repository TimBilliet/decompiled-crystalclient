package com.github.lunatrius.schematica.world.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

import java.io.File;

public class SaveHandlerSchematic implements ISaveHandler {
    public WorldInfo loadWorldInfo() {
        return null;
    }

    public void checkSessionLock() throws MinecraftException {
    }

    public IChunkLoader getChunkLoader(WorldProvider provider) {
        return null;
    }

    public void saveWorldInfoWithPlayer(WorldInfo info, NBTTagCompound compound) {
    }

    public void saveWorldInfo(WorldInfo info) {
    }

    //@Override
    public IPlayerFileData getPlayerNBTManager() {
        return null;
    }

    public IPlayerFileData getSaveHandler() {
        return null;
    }

    public void flush() {
    }

    public File getWorldDirectory() {
        return null;
    }

    public File getMapFileFromName(String name) {
        return null;
    }

    public String getWorldDirectoryName() {
        return null;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\world\storage\SaveHandlerSchematic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */