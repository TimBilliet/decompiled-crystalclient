package mapwriter.region;

import co.crystaldev.client.Resources;
import mapwriter.util.Logging;
import mapwriter.util.Render;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class BlockColors {
  public static final int MAX_BIOMES = 256;
  
  private final int[] waterMultiplierArray = new int[256];
  
  private final int[] grassMultiplierArray = new int[256];
  
  private final int[] foliageMultiplierArray = new int[256];
  
  private final LinkedHashMap<String, BlockData> bcMap = new LinkedHashMap<>();
  
  public BlockColors() {
    Arrays.fill(this.waterMultiplierArray, 16777215);
    Arrays.fill(this.grassMultiplierArray, 16777215);
    Arrays.fill(this.foliageMultiplierArray, 16777215);
  }
  
  public String combineBlockMeta(String BlockName, int meta) {
    return BlockName + " " + meta;
  }
  
  public String combineBlockMeta(String BlockName, String meta) {
    return BlockName + " " + meta;
  }
  
  public int getColour(String blockName, int meta) {
    BlockData data;
    String blockAndMeta = combineBlockMeta(blockName, meta);
    String blockAndWildcard = combineBlockMeta(blockName, "*");
    if (this.bcMap.containsKey(blockAndMeta)) {
      data = this.bcMap.get(blockAndMeta);
    } else if (this.bcMap.containsKey(blockAndWildcard)) {
      data = this.bcMap.get(blockAndWildcard);
    } else {
      data = new BlockData();
      this.bcMap.put(blockAndMeta, data);
    } 
    return data.color;
  }
  
  public int getColour(int blockAndMeta) {
    int meta = blockAndMeta & 0xF;
    int id = blockAndMeta >> 4;
    if (id == 0)
      return 0; 
    return getColour(((ResourceLocation)Block.blockRegistry.getNameForObject(Block.getBlockById(id))).toString(), meta);
  }
  
  public void setColor(String blockName, String meta, int color) {
    String blockAndMeta = combineBlockMeta(blockName, meta);
    if (meta.equals("*"))
      for (int i = 0; i < 16; i++)
        setColor(blockName, String.valueOf(i), color);  
    if (this.bcMap.containsKey(blockAndMeta)) {
      BlockData data = this.bcMap.get(blockAndMeta);
      data.color = color;
    } else {
      BlockData data = new BlockData();
      data.color = color;
      this.bcMap.put(blockAndMeta, data);
    } 
  }
  
  private int getGrassColourMultiplier(int biome) {
    return (biome >= 0 && biome < this.grassMultiplierArray.length) ? this.grassMultiplierArray[biome] : 16777215;
  }
  
  private int getFoliageColourMultiplier(int biome) {
    return (biome >= 0 && biome < this.foliageMultiplierArray.length) ? this.foliageMultiplierArray[biome] : 16777215;
  }
  
  public int getBiomeColour(int blockAndMeta, int biome) {
    Block block = Block.getBlockById(blockAndMeta >> 4);
    int meta = blockAndMeta & 0xF;
    return getBiomeColour(((ResourceLocation)Block.blockRegistry.getNameForObject(block)).toString(), meta, biome);
  }
  
  public int getBiomeColour(String blockName, int meta, int biome) {
    int colourMultiplier = 16777215;
    String combined = combineBlockMeta(blockName, meta);
    for (Map.Entry<String, BlockData> entry : this.bcMap.entrySet()) {
      if (((String)entry.getKey()).equals(combined)) {
        switch (((BlockData)entry.getValue()).type) {
          case GRASS:
            colourMultiplier = getGrassColourMultiplier(biome);
            break;
          case LEAVES:
          case FOLIAGE:
            colourMultiplier = getFoliageColourMultiplier(biome);
            break;
        } 
        return colourMultiplier;
      } 
    } 
    return colourMultiplier;
  }
  
  public void setBiomeWaterShading(int biomeID, int colour) {
    this.waterMultiplierArray[biomeID & 0xFF] = colour;
  }
  
  public void setBiomeGrassShading(int biomeID, int colour) {
    this.grassMultiplierArray[biomeID & 0xFF] = colour;
  }
  
  public void setBiomeFoliageShading(int biomeID, int colour) {
    this.foliageMultiplierArray[biomeID & 0xFF] = colour;
  }
  
  private static BlockType getBlockTypeFromString(String typeString) {
    BlockType blockType = BlockType.NORMAL;
    if (typeString.equalsIgnoreCase("grass")) {
      blockType = BlockType.GRASS;
    } else if (typeString.equalsIgnoreCase("leaves")) {
      blockType = BlockType.LEAVES;
    } else if (typeString.equalsIgnoreCase("foliage")) {
      blockType = BlockType.FOLIAGE;
    } else if (typeString.equalsIgnoreCase("water")) {
      blockType = BlockType.WATER;
    } else if (typeString.equalsIgnoreCase("opaque")) {
      blockType = BlockType.OPAQUE;
    } else {
      Logging.logWarning("unknown block type '%s'", new Object[] { typeString });
    } 
    return blockType;
  }
  
  public void setBlockType(String blockName, String meta, BlockType type) {
    String blockAndMeta = combineBlockMeta(blockName, meta);
    if (meta.equals("*")) {
      for (int i = 0; i < 16; i++)
        setBlockType(blockName, String.valueOf(i), type); 
      return;
    } 
    if (this.bcMap.containsKey(blockAndMeta)) {
      BlockData data = this.bcMap.get(blockAndMeta);
      data.type = type;
      data.color = adjustBlockColourFromType(blockName, meta, type, data.color);
    } else {
      BlockData data = new BlockData();
      data.type = type;
      this.bcMap.put(blockAndMeta, data);
    } 
  }
  
  private static int adjustBlockColourFromType(String BlockName, String meta, BlockType type, int blockColour) {
    Block block = Block.getBlockFromName(BlockName);
    switch (type) {
      case OPAQUE:
        blockColour |= 0xFF000000;
      case NORMAL:
        try {
          ;
          //int renderColour = block.func_180644_h(block.getStateFromMeta(Integer.parseInt(meta) & 0xF));
          int renderColour = block.getRenderColor(block.getStateFromMeta(Integer.parseInt(meta) & 0xF));
          if (renderColour != 16777215)
            blockColour = Render.multiplyColours(blockColour, 0xFF000000 | renderColour); 
        } catch (RuntimeException runtimeException) {}
        break;
      case LEAVES:
        blockColour |= 0xFF000000;
        break;
      case GRASS:
        blockColour = -6579301;
        break;
    } 
    return blockColour;
  }
  
  public static int getColorFromString(String s) {
    return (int)(Long.parseLong(s, 16) & 0xFFFFFFFFL);
  }
  
  private void loadBiomeLine(String[] split) {
    try {
      int startBiomeId = 0;
      int endBiomeId = 256;
      if (!split[1].equals("*")) {
        startBiomeId = Integer.parseInt(split[1]);
        endBiomeId = startBiomeId + 1;
      } 
      if (startBiomeId >= 0 && startBiomeId < 256) {
        int waterMultiplier = getColorFromString(split[2]) & 0xFFFFFF;
        int grassMultiplier = getColorFromString(split[3]) & 0xFFFFFF;
        int foliageMultiplier = getColorFromString(split[4]) & 0xFFFFFF;
        for (int biomeId = startBiomeId; biomeId < endBiomeId; biomeId++) {
          setBiomeWaterShading(biomeId, waterMultiplier);
          setBiomeGrassShading(biomeId, grassMultiplier);
          setBiomeFoliageShading(biomeId, foliageMultiplier);
        } 
      } else {
        Logging.logWarning("biome ID '%d' out of range", new Object[] { Integer.valueOf(startBiomeId) });
      } 
    } catch (NumberFormatException e) {
      Logging.logWarning("invalid biome colour line '%s %s %s %s %s'", new Object[] { split[0], split[1], split[2], split[3], split[4] });
    } 
  }
  
  private void loadBlockLine(String[] split) {
    try {
      int color = getColorFromString(split[3]);
      setColor(split[1], split[2], color);
    } catch (NumberFormatException e) {
      Logging.logWarning("invalid block color line '%s %s %s %s'", new Object[] { split[0], split[1], split[2], split[3] });
    } 
  }
  
  private void loadBlockTypeLine(String[] split) {
    try {
      BlockType type = getBlockTypeFromString(split[3]);
      setBlockType(split[1], split[2], type);
    } catch (NumberFormatException e) {
      Logging.logWarning("invalid block color line '%s %s %s %s'", new Object[] { split[0], split[1], split[2], split[3] });
    } 
  }
  
  public void loadFromFile() {
    ResourceLocation bc = Resources.BLOCK_COLORS;
    try (BufferedReader fin = new BufferedReader(new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(bc).getInputStream()))) {
      String l;
      while ((l = fin.readLine()) != null) {
        String line = l.split("#")[0].trim();
        if (line.length() > 0) {
          String[] lineSplit = line.split(" ");
          if (lineSplit[0].equals("biome") && lineSplit.length == 5) {
            loadBiomeLine(lineSplit);
            continue;
          } 
          if (lineSplit[0].equals("block") && lineSplit.length == 4) {
            loadBlockLine(lineSplit);
            continue;
          } 
          if (lineSplit[0].equals("blocktype") && lineSplit.length == 4) {
            loadBlockTypeLine(lineSplit);
            continue;
          } 
          Logging.logWarning("invalid map colour line '%s'", new Object[] { line });
        } 
      } 
    } catch (IOException ex) {
      Logging.log("Error reading BlockColors", new Object[] { ex });
    } 
  }
  
  public enum BlockType {
    NORMAL, GRASS, LEAVES, FOLIAGE, WATER, OPAQUE;
  }
  
  public static class BlockData {
    public int color = 0;
    
    public BlockType type = BlockType.NORMAL;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwriter\region\BlockColors.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */