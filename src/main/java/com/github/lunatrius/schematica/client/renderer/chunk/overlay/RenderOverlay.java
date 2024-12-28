package com.github.lunatrius.schematica.client.renderer.chunk.overlay;

import co.crystaldev.client.feature.impl.factions.Schematica;
import co.crystaldev.client.mixin.accessor.net.minecraft.client.renderer.chunk.MixinRenderChunk;
import co.crystaldev.client.util.type.Tuple;
import com.github.lunatrius.core.client.renderer.GeometryTessellator;
import com.github.lunatrius.schematica.block.state.BlockStateHelper;
import com.github.lunatrius.schematica.client.renderer.chunk.CompiledOverlay;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.handler.ConfigurationHandler;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import com.github.lunatrius.schematica.reference.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RegionRenderCache;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class RenderOverlay extends RenderChunk {
  private final VertexBuffer vertexBuffer;
  
  public RenderOverlay(World world, RenderGlobal renderGlobal, BlockPos pos, int index) {
    super(world, renderGlobal, pos, index);
    this.vertexBuffer = OpenGlHelper.useVbo() ? new VertexBuffer(DefaultVertexFormats.POSITION_COLOR) : null;
  }
  
  public VertexBuffer getVertexBufferByLayer(int layer) {
    return this.vertexBuffer;
  }
  
  public void rebuildChunk(float x, float y, float z, ChunkCompileTaskGenerator generator) {
    RegionRenderCache regionRenderCache;
    CompiledOverlay compiledOverlay = new CompiledOverlay();
    BlockPos from = getPosition();
    BlockPos to = from.add(15, 15, 15);
    generator.getLock().lock();
    SchematicWorld schematic = (SchematicWorld)((MixinRenderChunk)this).getWorld();
    try {
      if (generator.getStatus() != ChunkCompileTaskGenerator.Status.COMPILING)
        return; 
      if (from.getX() < 0 || from.getZ() < 0 || from.getX() >= schematic.getWidth() || from.getZ() >= schematic.getLength()) {
        generator.setCompiledChunk(CompiledChunk.DUMMY);
        return;
      } 
      regionRenderCache = new RegionRenderCache(((MixinRenderChunk)this).getWorld(), from.add(-1, -1, -1), to.add(1, 1, 1), 1);
      generator.setCompiledChunk((CompiledChunk)compiledOverlay);
    } finally {
      generator.getLock().unlock();
    } 
    VisGraph visgraph = new VisGraph();
    if (!regionRenderCache.extendedLevelsInChunkCache()) {
      renderChunksUpdated++;
      Minecraft mc = Minecraft.getMinecraft();
      WorldClient worldClient = mc.theWorld;
      EnumWorldBlockLayer layer = EnumWorldBlockLayer.TRANSLUCENT;
      WorldRenderer worldRenderer = generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(layer);
      GeometryTessellator.setStaticDelta(ConfigurationHandler.blockDelta);
      Map<BlockPos, Tuple<RenderType, AxisAlignedBB>> tracers = new HashMap<>();
      int cuboids = 0;
      for (BlockPos pos : BlockPos.getAllInBox(from, to)) {
        if ((schematic.isRenderingLayer && schematic.renderingLayer != pos.getY()) || !schematic.isInside(pos))
          continue; 
        boolean render = false;
        int sides = 0;
        int color = 0;
        IBlockState schBlockState = schematic.getBlockState(pos);
        Block schBlock = schBlockState.getBlock();
        if (schBlock.isOpaqueCube())
          visgraph.func_178606_a(pos);
//          visgraph.setOpaqueCube(pos);
        BlockPos mcPos = pos.add((Vec3i)schematic.position);
        IBlockState mcBlockState = worldClient.getBlockState(mcPos);
        Block mcBlock = mcBlockState.getBlock();
        boolean isSchAirBlock = schematic.isAirBlock(pos);
        boolean isMcAirBlock = worldClient.isAirBlock(mcPos);
        if (!isMcAirBlock && isSchAirBlock && (Schematica.getInstance()).highlightAir) {
          render = true;
          color = 12517567;
          sides = getSides(mcBlock, (World)worldClient, mcPos, sides);
        } 
        if (!render) {
          if (ConfigurationHandler.highlight)
            if (!isMcAirBlock) {
              if (schBlock != mcBlock) {
                Material material = mcBlock.getMaterial();
                if (!(Schematica.getInstance()).highlightInLiquid || (material != Material.water && material != Material.lava)) {
                  render = true;
                  color = 16711680;
                  if (!(Schematica.getInstance()).trayMode || mcPos.getY() != 253) {
                    mcBlock.setBlockBoundsBasedOnState((IBlockAccess)worldClient, mcPos);
                    AxisAlignedBB aabb = mcBlock.getCollisionBoundingBox((World)worldClient, mcPos, null).expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D);
                    tracers.put(mcPos, new Tuple(RenderType.INCORRECT_BLOCK, aabb));
                  } 
                } 
              } else if (!BlockStateHelper.areBlockStatesEqual(schBlockState, mcBlockState)) {
                render = true;
                color = 12541696;
                if (!(Schematica.getInstance()).trayMode || mcPos.getY() != 253) {
                  mcBlock.setBlockBoundsBasedOnState((IBlockAccess)worldClient, mcPos);
                  AxisAlignedBB aabb = mcBlock.getCollisionBoundingBox((World)worldClient, mcPos, null).expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D);
                  tracers.put(mcPos, new Tuple(RenderType.WRONG_META, aabb));
                } 
              } 
            } else if (!isSchAirBlock) {
              render = true;
              color = 49151;
            }  
          if (render)
            sides = getSides(schBlock, (World)schematic, pos, sides); 
        } 
        if (render && sides != 0) {
          if (!compiledOverlay.isLayerStarted(layer)) {
            compiledOverlay.setLayerStarted(layer);
            ((MixinRenderChunk)this).callPreRenderBlocks(worldRenderer, from);
          } 
          if (cuboids <= 2700) {
            if (ClientProxy.currentSchematic.totalBlocks > 7000) {
              GeometryTessellator.drawCuboid(worldRenderer, pos, sides, 0x3F000000 | color);
            } else {
              schBlock.setBlockBoundsBasedOnState((IBlockAccess)schematic, pos);
              AxisAlignedBB aabb = schBlock.getCollisionBoundingBox((World)schematic, pos, null).expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D);
              GeometryTessellator.drawCuboid(worldRenderer, aabb, sides, 0x3F000000 | color);
            } 
            cuboids++;
          } 
          compiledOverlay.setLayerUsed(layer);
        } 
      } 
      try {
        for (BlockPos pos : BlockPos.getAllInBox(from, to)) {
          BlockPos mcPos = pos.add((Vec3i)schematic.position);
          if (tracers.containsKey(mcPos)) {
            Tuple<RenderType, AxisAlignedBB> tuple = tracers.get(mcPos);
            Schematica.getInstance().addTracer(mcPos, (AxisAlignedBB)tuple.getItem2(), (RenderType)tuple.getItem1());
            continue;
          } 
          (Schematica.getInstance()).incorrectBlocks.remove(mcPos);
          (Schematica.getInstance()).wrongMetaBlocks.remove(mcPos);
        } 
        if (compiledOverlay.isLayerStarted(layer))
          ((MixinRenderChunk)this).callPostRenderBlocks(layer, x, y, z, worldRenderer, (CompiledChunk)compiledOverlay); 
      } catch (NullPointerException ex) {
        Reference.logger.error("Error batching Schematica chunk render overlay", ex);
      } 
    } 
    compiledOverlay.setVisibility(visgraph.computeVisibility());
  }
  
  private int getSides(Block block, World world, BlockPos pos, int sides) {
    if (block.shouldSideBeRendered((IBlockAccess)world, pos.offset(EnumFacing.DOWN), EnumFacing.DOWN))
      sides |= 0x1; 
    if (block.shouldSideBeRendered((IBlockAccess)world, pos.offset(EnumFacing.UP), EnumFacing.UP))
      sides |= 0x2; 
    if (block.shouldSideBeRendered((IBlockAccess)world, pos.offset(EnumFacing.NORTH), EnumFacing.NORTH))
      sides |= 0x4; 
    if (block.shouldSideBeRendered((IBlockAccess)world, pos.offset(EnumFacing.SOUTH), EnumFacing.SOUTH))
      sides |= 0x8; 
    if (block.shouldSideBeRendered((IBlockAccess)world, pos.offset(EnumFacing.WEST), EnumFacing.WEST))
      sides |= 0x10; 
    if (block.shouldSideBeRendered((IBlockAccess)world, pos.offset(EnumFacing.EAST), EnumFacing.EAST))
      sides |= 0x20; 
    return sides;
  }
  
  public void deleteGlResources() {
    super.deleteGlResources();
    if (this.vertexBuffer != null)
      this.vertexBuffer.deleteGlBuffers(); 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\renderer\chunk\overlay\RenderOverlay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */