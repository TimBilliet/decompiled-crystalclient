package com.github.lunatrius.schematica.nbt;

import com.github.lunatrius.schematica.world.WorldDummy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class NBTHelper {
    public static List<TileEntity> readTileEntitiesFromCompound(NBTTagCompound compound) {
        return readTileEntitiesFromCompound(compound, new ArrayList<>());
    }

    public static List<TileEntity> readTileEntitiesFromCompound(NBTTagCompound compound, List<TileEntity> tileEntities) {
        NBTTagList tagList = compound.getTagList("TileEntities", 10);
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound tileEntityCompound = tagList.getCompoundTagAt(i);
            TileEntity tileEntity = readTileEntityFromCompound(tileEntityCompound);
            tileEntities.add(tileEntity);
        }
        return tileEntities;
    }

    public static NBTTagCompound writeTileEntitiesToCompound(List<TileEntity> tileEntities) {
        return writeTileEntitiesToCompound(tileEntities, new NBTTagCompound());
    }

    public static NBTTagCompound writeTileEntitiesToCompound(List<TileEntity> tileEntities, NBTTagCompound compound) {
        NBTTagList tagList = new NBTTagList();
        for (TileEntity tileEntity : tileEntities) {
            NBTTagCompound tileEntityCompound = writeTileEntityToCompound(tileEntity);
            tagList.appendTag((NBTBase) tileEntityCompound);
        }
        compound.setTag("TileEntities", (NBTBase) tagList);
        return compound;
    }

    public static List<Entity> readEntitiesFromCompound(NBTTagCompound compound) {
        return readEntitiesFromCompound(compound, null, new ArrayList<>());
    }

    public static List<Entity> readEntitiesFromCompound(NBTTagCompound compound, World world) {
        return readEntitiesFromCompound(compound, world, new ArrayList<>());
    }

    public static List<Entity> readEntitiesFromCompound(NBTTagCompound compound, List<Entity> entities) {
        return readEntitiesFromCompound(compound, null, entities);
    }

    public static List<Entity> readEntitiesFromCompound(NBTTagCompound compound, World world, List<Entity> entities) {
        NBTTagList tagList = compound.getTagList("Entities", 10);
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound entityCompound = tagList.getCompoundTagAt(i);
            Entity entity = readEntityFromCompound(entityCompound, world);
            if (entity != null)
                entities.add(entity);
        }
        return entities;
    }

    public static NBTTagCompound writeEntitiesToCompound(List<Entity> entities) {
        return writeEntitiesToCompound(entities, new NBTTagCompound());
    }

    public static NBTTagCompound writeEntitiesToCompound(List<Entity> entities, NBTTagCompound compound) {
        NBTTagList tagList = new NBTTagList();
        for (Entity entity : entities) {
            NBTTagCompound entityCompound = new NBTTagCompound();
            entity.writeToNBT(entityCompound);
            tagList.appendTag((NBTBase) entityCompound);
        }
        compound.setTag("Entities", (NBTBase) tagList);
        return compound;
    }

    public static TileEntity reloadTileEntity(TileEntity tileEntity) throws NBTConversionException {
        return reloadTileEntity(tileEntity, 0, 0, 0);
    }

    public static TileEntity reloadTileEntity(TileEntity tileEntity, int offsetX, int offsetY, int offsetZ) throws NBTConversionException {
        if (tileEntity == null)
            return null;
        try {
            NBTTagCompound tileEntityCompound = writeTileEntityToCompound(tileEntity);
            tileEntity = readTileEntityFromCompound(tileEntityCompound);
            BlockPos pos = tileEntity.getPos();
            tileEntity.setPos(pos.add(-offsetX, -offsetY, -offsetZ));
        } catch (Throwable t) {
            throw new NBTConversionException(tileEntity, t);
        }
        return tileEntity;
    }

    public static Entity reloadEntity(Entity entity) throws NBTConversionException {
        return reloadEntity(entity, 0, 0, 0);
    }

    public static Entity reloadEntity(Entity entity, int offsetX, int offsetY, int offsetZ) throws NBTConversionException {
        if (entity == null)
            return null;
        try {
            NBTTagCompound entityCompound = writeEntityToCompound(entity);
            if (entityCompound != null) {
                entity = readEntityFromCompound(entityCompound, (World) WorldDummy.instance());
                if (entity != null) {
                    entity.posX -= offsetX;
                    entity.posY -= offsetY;
                    entity.posZ -= offsetZ;
                }
            }
        } catch (Throwable t) {
            throw new NBTConversionException(entity, t);
        }
        return entity;
    }

    public static NBTTagCompound writeTileEntityToCompound(TileEntity tileEntity) {
        NBTTagCompound tileEntityCompound = new NBTTagCompound();
        tileEntity.writeToNBT(tileEntityCompound);
        return tileEntityCompound;
    }

    public static TileEntity readTileEntityFromCompound(NBTTagCompound tileEntityCompound) {
        return TileEntity.createAndLoadEntity(tileEntityCompound);
    }

    public static NBTTagCompound writeEntityToCompound(Entity entity) {
        NBTTagCompound entityCompound = new NBTTagCompound();
        if (entity.writeToNBTOptional(entityCompound))
            return entityCompound;
        return null;
    }

    public static Entity readEntityFromCompound(NBTTagCompound nbtTagCompound, World world) {
        return EntityList.createEntityFromNBT(nbtTagCompound, world);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\nbt\NBTHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */