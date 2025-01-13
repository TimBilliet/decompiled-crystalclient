package com.github.lunatrius.schematica.world.storage;

import com.github.lunatrius.schematica.api.ISchematic;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.RegistryNamespacedDefaultedByKey;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Schematic implements ISchematic {
    private static final ItemStack DEFAULT_ICON = new ItemStack((Block) Blocks.grass);

    private static final RegistryNamespacedDefaultedByKey<ResourceLocation, Block> BLOCK_REGISTRY = Block.blockRegistry;

    private ItemStack icon;

    private final short[][][] blocks;

    private final byte[][][] metadata;

    private final List<TileEntity> tileEntities = new ArrayList<>();

    private final List<Entity> entities = new ArrayList<>();

    private final int width;

    private final int height;

    private final int length;

    public Schematic(ItemStack icon, int width, int height, int length) {
        this.icon = icon;
        this.blocks = new short[width][height][length];
        this.metadata = new byte[width][height][length];
        this.width = width;
        this.height = height;
        this.length = length;
    }

    public IBlockState getBlockState(BlockPos pos) {
        if (!isValid(pos))
            return Blocks.air.getDefaultState();
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        Block block = (Block) BLOCK_REGISTRY.getObjectById(this.blocks[x][y][z]);
        return block.getStateFromMeta(this.metadata[x][y][z]);
    }

    public boolean setBlockState(BlockPos pos, IBlockState blockState) {
        if (!isValid(pos))
            return false;
        Block block = blockState.getBlock();
        int id = Block.getIdFromBlock(block);
        if (id == -1)
            return false;
        int meta = block.getMetaFromState(blockState);
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        this.blocks[x][y][z] = (short) id;
        this.metadata[x][y][z] = (byte) meta;
        return true;
    }

    public TileEntity getTileEntity(BlockPos pos) {
        for (TileEntity tileEntity : this.tileEntities) {
            if (tileEntity.getPos().equals(pos))
                return tileEntity;
        }
        return null;
    }

    public List<TileEntity> getTileEntities() {
        return this.tileEntities;
    }

    public void setTileEntity(BlockPos pos, TileEntity tileEntity) {
        if (!isValid(pos))
            return;
        removeTileEntity(pos);
        if (tileEntity != null)
            this.tileEntities.add(tileEntity);
    }

    public void removeTileEntity(BlockPos pos) {
        Iterator<TileEntity> iterator = this.tileEntities.iterator();
        while (iterator.hasNext()) {
            TileEntity tileEntity = iterator.next();
            if (tileEntity.getPos().equals(pos))
                iterator.remove();
        }
    }

    public List<Entity> getEntities() {
        return this.entities;
    }

    public void addEntity(Entity entity) {
        if (entity == null || entity.getUniqueID() == null || entity instanceof net.minecraft.entity.player.EntityPlayer)
            return;
        for (Entity e : this.entities) {
            if (entity.getUniqueID().equals(e.getUniqueID()))
                return;
        }
        this.entities.add(entity);
    }

    public void removeEntity(Entity entity) {
        if (entity == null || entity.getUniqueID() == null)
            return;
        Iterator<Entity> iterator = this.entities.iterator();
        while (iterator.hasNext()) {
            Entity e = iterator.next();
            if (entity.getUniqueID().equals(e.getUniqueID()))
                iterator.remove();
        }
    }

    public ItemStack getIcon() {
        return this.icon;
    }

    public void setIcon(ItemStack icon) {
        if (icon != null) {
            this.icon = icon;
        } else {
            this.icon = DEFAULT_ICON.copy();
        }
    }

    public int getWidth() {
        return this.width;
    }

    public int getLength() {
        return this.length;
    }

    public int getHeight() {
        return this.height;
    }

    private boolean isValid(BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        return (x >= 0 && y >= 0 && z >= 0 && x < this.width && y < this.height && z < this.length);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\world\storage\Schematic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */