package com.github.lunatrius.schematica.client.world;

import co.crystaldev.client.util.type.Tuple;
import com.github.lunatrius.core.util.BlockPosHelper;
import com.github.lunatrius.core.util.MBlockPos;
import com.github.lunatrius.schematica.api.ISchematic;
import com.github.lunatrius.schematica.block.state.pattern.BlockStateReplacer;
import com.github.lunatrius.schematica.client.world.chunk.ChunkProviderSchematic;
import com.github.lunatrius.schematica.reference.Reference;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockStateHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3i;
import net.minecraft.world.*;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SchematicWorld extends WorldClient {
  private static final WorldSettings WORLD_SETTINGS = new WorldSettings(0L, WorldSettings.GameType.CREATIVE, false, false, WorldType.FLAT);
  
  private ISchematic schematic;
  
  public final MBlockPos position = new MBlockPos();
  
  public boolean isRendering;
  
  public boolean isRenderingLayer;
  
  public int renderingLayer;
  
  public SchematicWorld(ISchematic schematic) {
    super(null, WORLD_SETTINGS, 0, EnumDifficulty.PEACEFUL, (Minecraft.getMinecraft()).mcProfiler);
    this.schematic = schematic;
    for (TileEntity tileEntity : schematic.getTileEntities())
      initializeTileEntity(tileEntity); 
    this.isRendering = false;
    this.isRenderingLayer = false;
    this.renderingLayer = 0;
  }
  
  public IBlockState getBlockState(BlockPos pos) {
    if (this.isRenderingLayer && this.renderingLayer != pos.getY())
      return Blocks.air.getDefaultState(); 
    return this.schematic.getBlockState(pos);
  }
  
  public boolean setBlockState(BlockPos pos, IBlockState state, int flags) {
    return this.schematic.setBlockState(pos, state);
  }
  
  public TileEntity getTileEntity(BlockPos pos) {
    if (this.isRenderingLayer && this.renderingLayer != pos.getY())
      return null; 
    return this.schematic.getTileEntity(pos);
  }
  
  public void setTileEntity(BlockPos pos, TileEntity tileEntity) {
    this.schematic.setTileEntity(pos, tileEntity);
    initializeTileEntity(tileEntity);
  }
  
  public void removeTileEntity(BlockPos pos) {
    this.schematic.removeTileEntity(pos);
  }
  
  public int getLightFromNeighborsFor(EnumSkyBlock type, BlockPos pos) {
    return 15;
  }
  
  public float getLightBrightness(BlockPos pos) {
    return 1.0F;
  }
  
  public boolean isBlockNormalCube(BlockPos pos, boolean _default) {
    return getBlockState(pos).getBlock().isNormalCube();
  }
  
  public void calculateInitialSkylight() {}
  
  protected void calculateInitialWeather() {}
  
  public void setSpawnPoint(BlockPos pos) {}
  
  protected int func_152379_p() {
    return 0;
  }
  
  public boolean isAirBlock(BlockPos pos) {
    return (getBlockState(pos).getBlock().getMaterial() == Material.air);
  }
  
  public BiomeGenBase getBiomeGenForCoords(BlockPos pos) {
    return BiomeGenBase.jungle;
  }
  
  public int getWidth() {
    return this.schematic.getWidth();
  }
  
  public int getLength() {
    return this.schematic.getLength();
  }
  
  public int getHeight() {
    return this.schematic.getHeight();
  }
  
  public boolean extendedLevelsInChunkCache() {
    return false;
  }
  
  protected IChunkProvider createChunkProvider() {
    return (IChunkProvider)new ChunkProviderSchematic(this);
  }
  
  public Entity getEntityByID(int id) {
    return null;
  }
  
  public void setSchematic(ISchematic schematic) {
    this.schematic = schematic;
  }
  
  public ISchematic getSchematic() {
    return this.schematic;
  }
  
  public void initializeTileEntity(TileEntity tileEntity) {
    tileEntity.setWorldObj((World)this);
    tileEntity.getBlockType();
    try {
      tileEntity.invalidate();
      tileEntity.validate();
    } catch (Exception e) {
      Reference.logger.error("TileEntity validation for {} failed!", new Object[] { tileEntity.getClass(), e });
    } 
  }
  
  public void setIcon(ItemStack icon) {
    this.schematic.setIcon(icon);
  }
  
  public ItemStack getIcon() {
    return this.schematic.getIcon();
  }
  
  public List<TileEntity> getTileEntities() {
    return this.schematic.getTileEntities();
  }
  
  public boolean toggleRendering() {
    this.isRendering = !this.isRendering;
    return this.isRendering;
  }
  
  public int replaceBlocks(List<MBlockPos> positions, BlockStateReplacer replacer, Map<IProperty, Comparable> properties) {
    int count = 0;
    for (MBlockPos pos : positions) {
      IBlockState blockState = this.schematic.getBlockState((BlockPos)pos);
      if (blockState.getBlock().hasTileEntity())
        continue; 
      IBlockState replacement = replacer.getReplacement(blockState, properties);
      if (replacement.getBlock().hasTileEntity())
        continue; 
      if (this.schematic.setBlockState((BlockPos)pos, replacement)) {
        markBlockForUpdate((BlockPos)pos.add((Vec3i)this.position));
        count++;
      } 
    } 
    return count;
  }
  
  public Tuple<Integer, List<MBlockPos>> replaceBlock(BlockStateHelper matcher, BlockStateReplacer replacer, Map<IProperty, Comparable> properties) {
    int count = 0;
    List<MBlockPos> replaced = new ArrayList<>();
    for (MBlockPos pos : BlockPosHelper.getAllInBox(0, 0, 0, getWidth(), getHeight(), getLength())) {
      IBlockState blockState = this.schematic.getBlockState((BlockPos)pos);
      if (blockState.getBlock().hasTileEntity())
        continue; 
      if (matcher.apply(blockState)) {
        IBlockState replacement = replacer.getReplacement(blockState, properties);
        if (replacement.getBlock().hasTileEntity())
          continue; 
        if (this.schematic.setBlockState((BlockPos)pos, replacement)) {
          replaced.add(new MBlockPos((Vec3i)pos));
          markBlockForUpdate((BlockPos)pos.add((Vec3i)this.position));
          count++;
        } 
      } 
    } 
    return new Tuple(Integer.valueOf(count), replaced);
  }
  
  public boolean isInside(BlockPos pos) {
    int x = pos.getX();
    int y = pos.getY();
    int z = pos.getZ();
    return (x >= 0 && y >= 0 && z >= 0 && x < getWidth() && y < getHeight() && z < getLength());
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\world\SchematicWorld.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */