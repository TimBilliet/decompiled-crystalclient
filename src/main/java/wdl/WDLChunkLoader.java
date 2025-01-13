package wdl;

import net.minecraft.block.Block;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.chunk.storage.RegionFileCache;
import net.minecraft.world.storage.SaveHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wdl.api.IEntityEditor;
import wdl.api.ITileEntityEditor;
import wdl.api.ITileEntityImportationIdentifier;
import wdl.api.WDLApi;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

public class WDLChunkLoader extends AnvilChunkLoader {
    private static final Logger logger = LogManager.getLogger();

    private final File chunkSaveLocation;

    public static WDLChunkLoader create(SaveHandler handler, WorldProvider provider) {
        return new WDLChunkLoader(getWorldSaveFolder(handler, provider));
    }

    private static File getWorldSaveFolder(SaveHandler handler, WorldProvider provider) {
        File baseFolder = handler.getWorldDirectory();
        try {
            Method forgeGetSaveFolderMethod = provider.getClass().getMethod("getSaveFolder", new Class[0]);
            String name = (String) forgeGetSaveFolderMethod.invoke(provider, new Object[0]);
            if (name != null) {
                File file = new File(baseFolder, name);
                file.mkdirs();
                return file;
            }
            return baseFolder;
        } catch (Exception e) {
            if (provider instanceof net.minecraft.world.WorldProviderHell) {
                File file = new File(baseFolder, "DIM-1");
                file.mkdirs();
                return file;
            }
            if (provider instanceof net.minecraft.world.WorldProviderEnd) {
                File file = new File(baseFolder, "DIM1");
                file.mkdirs();
                return file;
            }
            return baseFolder;
        }
    }

    public WDLChunkLoader(File file) {
        super(file);
        this.chunkSaveLocation = file;
    }

    public void saveChunk(World world, Chunk chunk) throws MinecraftException, IOException {
        world.checkSessionLock();
        NBTTagCompound levelTag = writeChunkToNBT(chunk, world);
        NBTTagCompound rootTag = new NBTTagCompound();
        rootTag.setTag("Level", (NBTBase) levelTag);
        addChunkToPending(chunk.getChunkCoordIntPair(), rootTag);
    }

    private NBTTagCompound writeChunkToNBT(Chunk chunk, World world) {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setByte("V", (byte) 1);
        compound.setInteger("xPos", chunk.xPosition);
        compound.setInteger("zPos", chunk.zPosition);
        compound.setLong("LastUpdate", world.getTotalWorldTime());
        compound.setIntArray("HeightMap", chunk.getHeightMap());
        compound.setBoolean("TerrainPopulated", chunk.isTerrainPopulated());
        compound.setBoolean("LightPopulated", chunk.isLightPopulated());
        compound.setLong("InhabitedTime", chunk.getInhabitedTime());
        ExtendedBlockStorage[] blockStorageArray = chunk.getBlockStorageArray();
        NBTTagList blockStorageList = new NBTTagList();
        boolean hasNoSky = !world.provider.getHasNoSky();
        for (ExtendedBlockStorage blockStorage : blockStorageArray) {
            if (blockStorage != null) {
                NBTTagCompound blockData = new NBTTagCompound();
                blockData.setByte("Y",
                        (byte) (blockStorage.getYLocation() >> 4 & 0xFF));
//        byte[] var12 = new byte[(blockStorage.func_177487_g()).length];
                byte[] var12 = new byte[(blockStorage.getData()).length];
                NibbleArray var13 = new NibbleArray();
                NibbleArray var14 = null;
                for (int var15 = 0; var15 < (blockStorage.getData()).length; var15++) {
                    char var16 = blockStorage.getData()[var15];
                    int var17 = var15 & 0xF;
                    int var18 = var15 >> 8 & 0xF;
                    int var19 = var15 >> 4 & 0xF;
                    if (var16 >> 12 != 0) {
                        if (var14 == null)
                            var14 = new NibbleArray();
                        var14.set(var17, var18, var19, var16 >> 12);
                    }
                    var12[var15] = (byte) (var16 >> 4 & 0xFF);
                    var13.set(var17, var18, var19, var16 & 0xF);
                }
                blockData.setByteArray("Blocks", var12);
                blockData.setByteArray("Data", var13.getData());
                if (var14 != null)
                    blockData.setByteArray("Add", var14.getData());
                blockData.setByteArray("BlockLight", blockStorage
                        .getBlocklightArray().getData());
                if (hasNoSky) {
                    blockData.setByteArray("SkyLight", blockStorage
                            .getSkylightArray().getData());
                } else {
                    blockData.setByteArray("SkyLight",
                            new byte[(blockStorage.getBlocklightArray().getData()).length]);
                }
                blockStorageList.appendTag((NBTBase) blockData);
            }
        }
        compound.setTag("Sections", (NBTBase) blockStorageList);
        compound.setByteArray("Biomes", chunk.getBiomeArray());
        chunk.setHasEntities(false);
        NBTTagList entityList = getEntityList(chunk);
        compound.setTag("Entities", (NBTBase) entityList);
        NBTTagList tileEntityList = getTileEntityList(chunk);
        compound.setTag("TileEntities", (NBTBase) tileEntityList);
        List<NextTickListEntry> updateList = world.getPendingBlockUpdates(chunk, false);
        if (updateList != null) {
            long worldTime = world.getTotalWorldTime();
            NBTTagList entries = new NBTTagList();
            for (NextTickListEntry entry : updateList) {
                NBTTagCompound entryTag = new NBTTagCompound();
                ResourceLocation location = (ResourceLocation) Block.blockRegistry.getNameForObject(entry.getBlock());
                entryTag.setString("i", (location == null) ? "" : location
                        .toString());
                entryTag.setInteger("x", entry.position.getX());
                entryTag.setInteger("y", entry.position.getY());
                entryTag.setInteger("z", entry.position.getZ());
                entryTag.setInteger("t", (int) (entry.scheduledTime - worldTime));
                entryTag.setInteger("p", entry.priority);
                entries.appendTag((NBTBase) entryTag);
            }
            compound.setTag("TileTicks", (NBTBase) entries);
        }
        return compound;
    }

    public NBTTagList getEntityList(Chunk chunk) {
        NBTTagList entityList = new NBTTagList();
        List<Entity> entities = new ArrayList<>();
        for (ClassInheritanceMultiMap<Entity> map : chunk.getEntityLists())
            entities.addAll((Collection<? extends Entity>) map);
        for (Entity e : WDL.newEntities.get(chunk.getChunkCoordIntPair())) {
            e.isDead = false;
            entities.add(e);
        }
        for (Entity entity : entities) {
            if (entity == null) {
                logger.warn("[WDL] Null entity in chunk at " + chunk
                        .getChunkCoordIntPair());
                continue;
            }
            if (!shouldSaveEntity(entity))
                continue;
            for (WDLApi.ModInfo<IEntityEditor> info : (Iterable<WDLApi.ModInfo<IEntityEditor>>) WDLApi.getImplementingExtensions(IEntityEditor.class)) {
                try {
                    if (((IEntityEditor) info.mod).shouldEdit(entity))
                        ((IEntityEditor) info.mod).editEntity(entity);
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to edit entity " + entity + " for chunk at " + chunk

                            .getChunkCoordIntPair() + " with extension " + info, ex);
                }
            }
            NBTTagCompound entityData = new NBTTagCompound();
            try {
                if (entity.writeToNBTOptional(entityData)) {
                    chunk.setHasEntities(true);
                    entityList.appendTag((NBTBase) entityData);
                }
            } catch (Exception e) {
                WDLMessages.chatMessageTranslated(WDLMessageTypes.ERROR, "wdl.messages.generalError.failedToSaveEntity", new Object[]{entity,

                        Integer.valueOf(chunk.xPosition), Integer.valueOf(chunk.zPosition), e});
                logger.warn("Compound: " + entityData);
                logger.warn("Entity metadata dump:");
                try {
                    List<DataWatcher.WatchableObject> objects = entity.getDataWatcher().getAllWatched();
                    if (objects == null) {
                        logger.warn("No entries (getAllWatched() returned null)");
                    } else {
                        logger.warn(objects);
                        for (DataWatcher.WatchableObject obj : objects) {
                            if (obj != null)
                                logger.warn("WatchableObject [getDataValueId()=" + obj
                                        .getDataValueId() + ", getObject()=" + obj

                                        .getObject() + ", getObjectType()=" + obj

                                        .getObjectType() + ", isWatched()=" + obj

                                        .isWatched() + "]");
                        }
                    }
                } catch (Exception e2) {
                    logger.warn("Failed to complete dump: ", e);
                }
                logger.warn("End entity metadata dump");
            }
        }
        return entityList;
    }

    public static boolean shouldSaveEntity(Entity e) {
        return !(e instanceof net.minecraft.entity.player.EntityPlayer);
    }

    public NBTTagList getTileEntityList(Chunk chunk) {
        NBTTagList tileEntityList = new NBTTagList();
        Map<BlockPos, TileEntity> chunkTEMap = chunk.getTileEntityMap();
        Map<BlockPos, NBTTagCompound> oldTEMap = getOldTileEntities(chunk);
        Map<BlockPos, TileEntity> newTEMap = WDL.newTileEntities.get(chunk.getChunkCoordIntPair());
        if (newTEMap == null)
            newTEMap = new HashMap<>();
        Set<BlockPos> allTELocations = new HashSet<>();
        allTELocations.addAll(chunkTEMap.keySet());
        allTELocations.addAll(oldTEMap.keySet());
        allTELocations.addAll(newTEMap.keySet());
        for (BlockPos pos : allTELocations) {
            if (newTEMap.containsKey(pos)) {
                NBTTagCompound compound = new NBTTagCompound();
                TileEntity te = newTEMap.get(pos);
                try {
                    te.writeToNBT(compound);
                } catch (Exception e) {
                    WDLMessages.chatMessageTranslated(WDLMessageTypes.ERROR, "wdl.messages.generalError.failedToSaveTE", new Object[]{te, pos,

                            Integer.valueOf(chunk.xPosition), Integer.valueOf(chunk.zPosition), e});
                    logger.warn("Compound: " + compound);
                    continue;
                }
                String entityType = compound.getString("id") + " (" + te.getClass().getCanonicalName() + ")";
                WDLMessages.chatMessageTranslated(WDLMessageTypes.LOAD_TILE_ENTITY, "wdl.messages.tileEntity.usingNew", new Object[]{entityType, pos});
                editTileEntity(pos, compound, ITileEntityEditor.TileEntityCreationMode.NEW);
                tileEntityList.appendTag((NBTBase) compound);
                continue;
            }
            if (oldTEMap.containsKey(pos)) {
                NBTTagCompound compound = oldTEMap.get(pos);
                editTileEntity(pos, compound, ITileEntityEditor.TileEntityCreationMode.IMPORTED);
                tileEntityList.appendTag((NBTBase) compound);
                continue;
            }
            if (chunkTEMap.containsKey(pos)) {
                TileEntity te = chunkTEMap.get(pos);
                NBTTagCompound compound = new NBTTagCompound();
                try {
                    te.writeToNBT(compound);
                } catch (Exception e) {
                    WDLMessages.chatMessageTranslated(WDLMessageTypes.ERROR, "wdl.messages.generalError.failedToSaveTE", new Object[]{te, pos,

                            Integer.valueOf(chunk.xPosition), Integer.valueOf(chunk.zPosition), e});
                    logger.warn("Compound: " + compound);
                    continue;
                }
                editTileEntity(pos, compound, ITileEntityEditor.TileEntityCreationMode.EXISTING);
                tileEntityList.appendTag((NBTBase) compound);
            }
        }
        return tileEntityList;
    }

    public Map<BlockPos, NBTTagCompound> getOldTileEntities(Chunk chunk) {
        DataInputStream dis = null;
        Map<BlockPos, NBTTagCompound> returned = new HashMap<>();
        try {
            dis = RegionFileCache.getChunkInputStream(this.chunkSaveLocation, chunk.xPosition, chunk.zPosition);
            if (dis == null)
                return returned;
            NBTTagCompound chunkNBT = CompressedStreamTools.read(dis);
            NBTTagCompound levelNBT = chunkNBT.getCompoundTag("Level");
            NBTTagList oldList = levelNBT.getTagList("TileEntities", 10);
            if (oldList != null)
                for (int i = 0; i < oldList.tagCount(); i++) {
                    NBTTagCompound oldNBT = oldList.getCompoundTagAt(i);
                    String entityID = oldNBT.getString("id");
                    BlockPos pos = new BlockPos(oldNBT.getInteger("x"), oldNBT.getInteger("y"), oldNBT.getInteger("z"));
                    Block block = chunk.getBlock(pos);//func_177428_a
                    if (shouldImportTileEntity(entityID, pos, block, oldNBT, chunk)) {
                        returned.put(pos, oldNBT);
                    } else {
                        WDLMessages.chatMessageTranslated(WDLMessageTypes.LOAD_TILE_ENTITY, "wdl.messages.tileEntity.notImporting", new Object[]{entityID, pos});
                    }
                }
        } catch (Exception e) {
            WDLMessages.chatMessageTranslated(WDLMessageTypes.ERROR, "wdl.messages.generalError.failedToImportTE", new Object[]{Integer.valueOf(chunk.xPosition), Integer.valueOf(chunk.zPosition), e});
        } finally {
            if (dis != null)
                try {
                    dis.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
        }
        return returned;
    }

    public boolean shouldImportTileEntity(String entityID, BlockPos pos, Block block, NBTTagCompound tileEntityNBT, Chunk chunk) {
        if (block instanceof net.minecraft.block.BlockChest && entityID.equals("Chest"))
            return true;
        if (block instanceof net.minecraft.block.BlockDispenser && entityID.equals("Trap"))
            return true;
        if (block instanceof net.minecraft.block.BlockDropper && entityID.equals("Dropper"))
            return true;
        if (block instanceof net.minecraft.block.BlockFurnace && entityID.equals("Furnace"))
            return true;
        if (block instanceof net.minecraft.block.BlockNote && entityID.equals("Music"))
            return true;
        if (block instanceof net.minecraft.block.BlockBrewingStand && entityID
                .equals("Cauldron"))
            return true;
        if (block instanceof net.minecraft.block.BlockHopper && entityID.equals("Hopper"))
            return true;
        if (block instanceof net.minecraft.block.BlockBeacon && entityID.equals("Beacon"))
            return true;
        for (WDLApi.ModInfo<ITileEntityImportationIdentifier> info : (Iterable<WDLApi.ModInfo<ITileEntityImportationIdentifier>>) WDLApi.getImplementingExtensions(ITileEntityImportationIdentifier.class)) {
            if (((ITileEntityImportationIdentifier) info.mod).shouldImportTileEntity(entityID, pos, block, tileEntityNBT, chunk))
                return true;
        }
        return false;
    }

    public static void editTileEntity(BlockPos pos, NBTTagCompound compound, ITileEntityEditor.TileEntityCreationMode creationMode) {
        for (WDLApi.ModInfo<ITileEntityEditor> info : (Iterable<WDLApi.ModInfo<ITileEntityEditor>>) WDLApi.getImplementingExtensions(ITileEntityEditor.class)) {
            try {
                if (((ITileEntityEditor) info.mod).shouldEdit(pos, compound, creationMode)) {
                    ((ITileEntityEditor) info.mod).editTileEntity(pos, compound, creationMode);
                    WDLMessages.chatMessageTranslated(WDLMessageTypes.LOAD_TILE_ENTITY, "wdl.messages.tileEntity.edited", new Object[]{pos, info

                            .getDisplayName()});
                }
            } catch (Exception ex) {
                throw new RuntimeException("Failed to edit tile entity at " + pos + " with extension " + info + "; NBT is now " + compound + " (this may be the initial value, an edited value, or a partially edited value)", ex);
            }
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\WDLChunkLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */