package mapwriter.region;

import co.crystaldev.client.Reference;
import mapwriter.util.Logging;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.NibbleArray;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MwChunk implements IChunk {
  public static final int SIZE = 16;
  
  public final int x;
  
  public final int z;
  
  public final int dimension;
  
  char[][] dataArray;
  
  public final Map<BlockPos, TileEntity> tileentityMap;
  
  public final byte[] biomeArray;
  
  public final int maxY;
  
  public MwChunk(int x, int z, int dimension, char[][] data, byte[] biomeArray, Map<BlockPos, TileEntity> TileEntityMap) {
    this.x = x;
    this.z = z;
    this.dimension = dimension;
    this.biomeArray = biomeArray;
    this.tileentityMap = TileEntityMap;
    this.dataArray = data;
    int maxY = 255;
    for (int y = 0; y < 16; y++) {
      if (data[y] != null)
        maxY = (y << 4) + 15; 
    } 
    this.maxY = maxY;
  }
  
  public String toString() {
    return String.format("(%d, %d) dim%d", this.x, Integer.valueOf(this.z), this.dimension);
  }
  
  public static MwChunk read(int x, int z, int dimension, RegionFileCache regionFileCache) {
    byte[] biomeArray = null;
    byte[][] lsbArray = new byte[16][];
    char[][] data = new char[16][];
    byte[][] lightingArray = new byte[16][];
    Map<BlockPos, TileEntity> TileEntityMap = new HashMap<>();
    DataInputStream dis = null;
    RegionFile regionFile = regionFileCache.getRegionFile(x << 4, z << 4, dimension);
    if (!regionFile.isOpen() && 
      regionFile.exists())
      regionFile.open(); 
    if (regionFile.isOpen())
      dis = regionFile.getChunkDataInputStream(x & 0x1F, z & 0x1F); 
    if (dis != null)
      try {
        NBTTagCompound nbttagcompound = CompressedStreamTools.read(dis);
        NBTTagCompound level = nbttagcompound.getCompoundTag("Level");
        int xNbt = level.getInteger("xPos");
        int zNbt = level.getInteger("zPos");
        if (xNbt != x || zNbt != z)
          Logging.logWarning("chunk (%d, %d) has NBT coords (%d, %d)", new Object[] {x, Integer.valueOf(z), Integer.valueOf(xNbt), Integer.valueOf(zNbt) });
        NBTTagList sections = level.getTagList("Sections", 10);
        for (int k = 0; k < sections.tagCount(); k++) {
          NBTTagCompound section = sections.getCompoundTagAt(k);
          int y = section.getByte("Y");
          lsbArray[y & 0xF] = section.getByteArray("Blocks");
          NibbleArray nibblearray = new NibbleArray(section.getByteArray("Data"));
          NibbleArray nibblearray1 = section.hasKey("Add", 7) ? new NibbleArray(section.getByteArray("Add")) : null;
          data[y & 0xF] = new char[(lsbArray[y]).length];
          for (int l = 0; l < (data[y & 0xF]).length; l++) {
            int i1 = l & 0xF;
            int j1 = l >> 8 & 0xF;
            int k1 = l >> 4 & 0xF;
            int l1 = (nibblearray1 != null) ? nibblearray1.get(i1, j1, k1) : 0;
            data[y & 0xF][l] = (char)(l1 << 12 | (lsbArray[y][l] & 0xFF) << 4 | nibblearray.get(i1, j1, k1));
          } 
        } 
        biomeArray = level.getByteArray("Biomes");
        NBTTagList nbttaglist2 = level.getTagList("TileEntities", 10);
        if (nbttaglist2 != null)
          for (int i1 = 0; i1 < nbttaglist2.tagCount(); i1++) {
            NBTTagCompound nbttagcompound4 = nbttaglist2.getCompoundTagAt(i1);
            TileEntity tileentity = TileEntity.createAndLoadEntity(nbttagcompound4);
            if (tileentity != null)
              TileEntityMap.put(tileentity.getPos(), tileentity); 
          }  
      } catch (IOException e) {
        Logging.logError("%s: could not read chunk (%d, %d) from region file\n", new Object[] { e, Integer.valueOf(x), Integer.valueOf(z) });
      } finally {
        try {
          dis.close();
        } catch (IOException e) {
          Logging.logError("MwChunk.read: %s while closing input stream", new Object[] { e });
        } 
      }  
    return new MwChunk(x, z, dimension, data, biomeArray, TileEntityMap);
  }
  
  public boolean isEmpty() {
    return (this.maxY <= 0);
  }
  
  public int getBiome(int x, int z) {
    return (this.biomeArray != null) ? (this.biomeArray[(z & 0xF) << 4 | x & 0xF] & 0xFF) : 0;
  }
  
  public int getLightValue(int x, int y, int z) {
    return 15;
  }
  
  public int getMaxY() {
    return this.maxY;
  }
  
  public int getBlockAndMetadata(int x, int y, int z) {
    int yi = y >> 4 & 0xF;
    int offset = (y & 0xF) << 8 | (z & 0xF) << 4 | x & 0xF;
    return (this.dataArray != null && this.dataArray[yi] != null && (this.dataArray[yi]).length != 0) ? this.dataArray[yi][offset] : 0;
  }
  
  private NBTTagCompound writeChunkToNBT() {
    NBTTagCompound nbttagcompound = new NBTTagCompound();
    NBTTagCompound nbttagcompound1 = new NBTTagCompound();
    nbttagcompound.setTag("Level", (NBTBase)nbttagcompound1);
    nbttagcompound1.setInteger("xPos", this.x);
    nbttagcompound1.setInteger("zPos", this.z);
    NBTTagList nbttaglist = new NBTTagList();
    for (int y = 0; y < this.dataArray.length; y++) {
      if (this.dataArray[y] != null) {
        byte[] abyte = new byte[(this.dataArray[y]).length];
        NibbleArray nibblearray = new NibbleArray();
        NibbleArray nibblearray1 = null;
        for (int k = 0; k < (this.dataArray[y]).length; k++) {
          char c0 = this.dataArray[y][k];
          int l = k & 0xF;
          int i1 = k >> 8 & 0xF;
          int j1 = k >> 4 & 0xF;
          if (c0 >> 12 != 0) {
            if (nibblearray1 == null)
              nibblearray1 = new NibbleArray(); 
            nibblearray1.set(l, i1, j1, c0 >> 12);
          } 
          abyte[k] = (byte)(c0 >> 4 & 0xFF);
          nibblearray.set(l, i1, j1, c0 & 0xF);
        } 
        NBTTagCompound nbttagcompound2 = new NBTTagCompound();
        nbttagcompound2.setByte("Y", (byte)y);
        nbttagcompound2.setByteArray("Blocks", abyte);
        if (nibblearray1 != null)
          nbttagcompound2.setByteArray("Add", nibblearray1.getData()); 
        nbttagcompound2.setByteArray("Data", nibblearray.getData());
        nbttaglist.appendTag((NBTBase)nbttagcompound2);
      } 
      nbttagcompound1.setTag("Sections", (NBTBase)nbttaglist);
    } 
    nbttagcompound1.setByteArray("Biomes", this.biomeArray);
    NBTTagList nbttaglist3 = new NBTTagList();
    for (TileEntity tileentity : this.tileentityMap.values()) {
      NBTTagCompound nbttagcompound2 = new NBTTagCompound();
      try {
        nbttaglist3.appendTag((NBTBase)nbttagcompound2);
      } catch (Exception ex) {
        Reference.LOGGER.error("TileEntity ({}) has raised an exception attempting to write state", new Object[] { tileentity.getClass().getSimpleName(), ex });
      } 
    } 
    nbttagcompound1.setTag("TileEntities", (NBTBase)nbttaglist3);
    return nbttagcompound;
  }
  
  public synchronized boolean write(RegionFileCache regionFileCache) {
    boolean error = false;
    RegionFile regionFile = regionFileCache.getRegionFile(this.x << 4, this.z << 4, this.dimension);
    if (!regionFile.isOpen())
      error = regionFile.open(); 
    if (!error) {
      DataOutputStream dos = regionFile.getChunkDataOutputStream(this.x & 0x1F, this.z & 0x1F);
      if (dos != null) {
        try {
          CompressedStreamTools.write(writeChunkToNBT(), dos);
        } catch (IOException e) {
          Logging.logError("%s: could not write chunk (%d, %d) to region file", new Object[] { e, Integer.valueOf(this.x), Integer.valueOf(this.z) });
          error = true;
        } finally {
          try {
            dos.close();
          } catch (IOException e) {
            Logging.logError("%s while closing chunk data output stream", new Object[] { e });
          } 
        } 
      } else {
        Logging.logError("error: could not get output stream for chunk (%d, %d)", new Object[] { Integer.valueOf(this.x), Integer.valueOf(this.z) });
      } 
    } else {
      Logging.logError("error: could not open region file for chunk (%d, %d)", new Object[] { Integer.valueOf(this.x), Integer.valueOf(this.z) });
    } 
    return error;
  }
  
  public Long getCoordIntPair() {
    return Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(this.x, this.z));
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwriter\region\MwChunk.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */