package com.github.lunatrius.schematica.client.renderer;

import com.github.lunatrius.schematica.proxy.ClientProxy;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RegionRenderCache;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3i;
import net.minecraft.world.World;

public class SchematicRenderCache extends RegionRenderCache {
  private final Minecraft minecraft = Minecraft.getMinecraft();
  
  public SchematicRenderCache(World world, BlockPos from, BlockPos to, int subtract) {
    super(world, from, to, subtract);
  }
  
  public IBlockState getBlockState(BlockPos pos) {
    if (ClientProxy.currentSchematic.schematic == null)
      return Blocks.air.getDefaultState(); 
    BlockPos realPos = pos.add((Vec3i)ClientProxy.currentSchematic.schematic.position);
    WorldClient worldClient = this.minecraft.theWorld;
    if (!worldClient.isAirBlock(realPos))
      return Blocks.air.getDefaultState(); 
    return super.getBlockState(pos);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\renderer\SchematicRenderCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */