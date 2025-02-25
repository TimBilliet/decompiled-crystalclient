package com.github.lunatrius.schematica.client.world.chunk;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class ChunkSchematic extends Chunk {
    private final World world;

    public ChunkSchematic(World world, int x, int z) {
        super(world, x, z);
        this.world = world;
    }

    protected void generateHeightMap() {
    }

    public void generateSkylightMap() {
    }

    public IBlockState getBlockState(BlockPos pos) {
        return this.world.getBlockState(pos);
    }

    public boolean getAreLevelsEmpty(int startY, int endY) {
        return false;
    }

    public TileEntity getTileEntity(BlockPos pos, EnumCreateEntityType createEntityType) {
        return this.world.getTileEntity(pos);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\world\chunk\ChunkSchematic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */