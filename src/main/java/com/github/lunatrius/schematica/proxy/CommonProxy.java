package com.github.lunatrius.schematica.proxy;

import co.crystaldev.client.feature.impl.factions.Schematica;
import com.github.lunatrius.core.util.MBlockPos;
import com.github.lunatrius.schematica.api.ISchematic;
import com.github.lunatrius.schematica.handler.ConfigurationHandler;
import com.github.lunatrius.schematica.handler.QueueTickHandler;
import com.github.lunatrius.schematica.nbt.NBTConversionException;
import com.github.lunatrius.schematica.nbt.NBTHelper;
import com.github.lunatrius.schematica.world.chunk.SchematicContainer;
import com.github.lunatrius.schematica.world.schematic.SchematicUtil;
import com.github.lunatrius.schematica.world.storage.Schematic;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import com.github.lunatrius.schematica.reference.Reference;
import java.io.File;
import java.io.IOException;
import java.util.List;

public abstract class CommonProxy {
  public boolean isSaveEnabled = true;
  
  public boolean isLoadEnabled = true;
  
  public void init() {
    Reference.logger = co.crystaldev.client.Reference.LOGGER;
  }
  
  public void createFolders() {
    if (!ConfigurationHandler.schematicDirectory.exists() && 
      !ConfigurationHandler.schematicDirectory.mkdirs())
      Reference.logger.warn("Could not create schematic directory [{}]!", ConfigurationHandler.schematicDirectory.getAbsolutePath());
  }
  
  public abstract File getDataDirectory();
  
  public File getDirectory(String directory) {
    File dataDirectory = getDataDirectory();
    File subDirectory = new File(dataDirectory, directory);
    if (!subDirectory.exists() && 
      !subDirectory.mkdirs())
      Reference.logger.error("Could not create directory [{}]!", subDirectory.getAbsolutePath());
    try {
      return subDirectory.getCanonicalFile();
    } catch (IOException e) {
      e.printStackTrace();
      return subDirectory;
    } 
  }
  
  public void resetSettings() {
    this.isSaveEnabled = true;
    this.isLoadEnabled = true;
  }
  
  public void unloadSchematic() {}
  
  public void copyChunkToSchematic(ISchematic schematic, World world, int chunkX, int chunkZ, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
    MBlockPos pos = new MBlockPos();
    MBlockPos localPos = new MBlockPos();
    int localMinX = (minX < chunkX << 4) ? 0 : (minX & 0xF);
    int localMaxX = (maxX > (chunkX << 4) + 15) ? 15 : (maxX & 0xF);
    int localMinZ = (minZ < chunkZ << 4) ? 0 : (minZ & 0xF);
    int localMaxZ = (maxZ > (chunkZ << 4) + 15) ? 15 : (maxZ & 0xF);
    for (int chunkLocalX = localMinX; chunkLocalX <= localMaxX; chunkLocalX++) {
      for (int chunkLocalZ = localMinZ; chunkLocalZ <= localMaxZ; chunkLocalZ++) {
        for (int y = minY; y <= maxY; y++) {
          int x = chunkLocalX | chunkX << 4;
          int z = chunkLocalZ | chunkZ << 4;
          int localX = x - minX;
          int localY = y - minY;
          int localZ = z - minZ;
          pos.set(x, y, z);
          localPos.set(localX, localY, localZ);
          try {
            IBlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();
            if (block instanceof BlockDispenser && (Schematica.getInstance()).enabled && (Schematica.getInstance()).dispenserMetaFix) {
              BlockDispenser dispenser = (BlockDispenser)block;
              int meta = dispenser.getMetaFromState(blockState);
              if (meta > 8)
                meta -= 8; 
              blockState = dispenser.getStateFromMeta(meta);
            } 
            boolean success = schematic.setBlockState(localPos, blockState);
            if (success && block.hasTileEntity()) {
              TileEntity tileEntity = world.getTileEntity(pos);
              if (tileEntity != null)
                try {
                  TileEntity reloadedTileEntity = NBTHelper.reloadTileEntity(tileEntity, minX, minY, minZ);
                  schematic.setTileEntity(localPos, reloadedTileEntity);
                } catch (NBTConversionException nce) {
                  Reference.logger.error("Error while trying to save tile entity '{}'!", new Object[] { tileEntity, nce });
                  schematic.setBlockState(localPos, Blocks.bedrock.getDefaultState());
                }  
            } 
          } catch (Exception e) {
            Reference.logger.error("Something went wrong!", e);
          } 
        } 
      } 
    } 
    int minX1 = localMinX | chunkX << 4;
    int minZ1 = localMinZ | chunkZ << 4;
    int maxX1 = localMaxX | chunkX << 4;
    int maxZ1 = localMaxZ | chunkZ << 4;
//    AxisAlignedBB bb = AxisAlignedBB.func_178781_a(minX1, minY, minZ1, (maxX1 + 1), (maxY + 1), (maxZ1 + 1));
    AxisAlignedBB bb = AxisAlignedBB.fromBounds(minX1, minY, minZ1, (maxX1 + 1), (maxY + 1), (maxZ1 + 1));
    List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, bb);
    for (Entity entity : entities) {
      try {
        Entity reloadedEntity = NBTHelper.reloadEntity(entity, minX, minY, minZ);
        schematic.addEntity(reloadedEntity);
      } catch (NBTConversionException nce) {
        Reference.logger.error("Error while trying to save entity '{}'!", entity, nce);
      } 
    } 
  }
  
  public boolean saveSchematic(EntityPlayer player, File directory, String filename, World world, BlockPos from, BlockPos to) {
    try {
      String iconName = "";
      try {
        String[] parts = filename.split(";");
        if (parts.length == 2) {
          iconName = parts[0];
          filename = parts[1];
        } 
      } catch (Exception e) {
        Reference.logger.error("Failed to parse icon data!", e);
      } 
      int minX = Math.min(from.getX(), to.getX());
      int maxX = Math.max(from.getX(), to.getX());
      int minY = Math.min(from.getY(), to.getY());
      int maxY = Math.max(from.getY(), to.getY());
      int minZ = Math.min(from.getZ(), to.getZ());
      int maxZ = Math.max(from.getZ(), to.getZ());
      short width = (short)(Math.abs(maxX - minX) + 1);
      short height = (short)(Math.abs(maxY - minY) + 1);
      short length = (short)(Math.abs(maxZ - minZ) + 1);
      Schematic schematic = new Schematic(SchematicUtil.getIconFromName(iconName), width, height, length);
      SchematicContainer container = new SchematicContainer((ISchematic)schematic, player, world, new File(directory, filename), minX, maxX, minY, maxY, minZ, maxZ);
      QueueTickHandler.INSTANCE.queueSchematic(container);
      return true;
    } catch (Exception e) {
      Reference.logger.error("Failed to save schematic!", e);
      return false;
    } 
  }
  
  public abstract boolean loadSchematic(EntityPlayer paramEntityPlayer, File paramFile, String paramString);
  
  public abstract boolean isPlayerQuotaExceeded(EntityPlayer paramEntityPlayer);
  
  public abstract File getPlayerSchematicDirectory(EntityPlayer paramEntityPlayer, boolean paramBoolean);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\proxy\CommonProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */