package com.github.lunatrius.schematica.client.util;

import com.github.lunatrius.core.util.BlockPosHelper;
import com.github.lunatrius.core.util.MBlockPos;
import com.github.lunatrius.schematica.api.ISchematic;
import com.github.lunatrius.schematica.block.state.BlockStateHelper;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.reference.Reference;
import com.github.lunatrius.schematica.world.storage.Schematic;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;

import java.util.List;

public class FlipHelper {
    public static final FlipHelper INSTANCE = new FlipHelper();

    private static final RegistryNamespacedDefaultedByKey<ResourceLocation, Block> BLOCK_REGISTRY = Block.blockRegistry;

    public boolean flip(SchematicWorld world, EnumFacing axis, boolean forced) {
        if (world == null)
            return false;
        try {
            ISchematic schematic = world.getSchematic();
            Schematic schematicFlipped = flip(schematic, axis, forced);
            world.setSchematic((ISchematic) schematicFlipped);
            for (TileEntity tileEntity : world.getTileEntities())
                world.initializeTileEntity(tileEntity);
            return true;
        } catch (FlipException fe) {
            Reference.logger.error(fe.getMessage());
        } catch (Exception e) {
            Reference.logger.fatal("Something went wrong!", e);
        }
        return false;
    }

    public Schematic flip(ISchematic schematic, EnumFacing axis, boolean forced) throws FlipException {
        Vec3i dimensionsFlipped = new Vec3i(schematic.getWidth(), schematic.getHeight(), schematic.getLength());
        Schematic schematicFlipped = new Schematic(schematic.getIcon(), dimensionsFlipped.getX(), dimensionsFlipped.getY(), dimensionsFlipped.getZ());
        MBlockPos tmp = new MBlockPos();
        for (MBlockPos pos : BlockPosHelper.getAllInBox(0, 0, 0, schematic.getWidth() - 1, schematic.getHeight() - 1, schematic.getLength() - 1)) {
            IBlockState blockState = schematic.getBlockState((BlockPos) pos);
            IBlockState blockStateFlipped = flipBlock(blockState, axis, forced);
            schematicFlipped.setBlockState(flipPos((BlockPos) pos, axis, dimensionsFlipped, tmp), blockStateFlipped);
        }
        List<TileEntity> tileEntities = schematic.getTileEntities();
        for (TileEntity tileEntity : tileEntities) {
            BlockPos pos = tileEntity.getPos();
            tileEntity.setPos(new BlockPos((Vec3i) flipPos(pos, axis, dimensionsFlipped, tmp)));
            schematicFlipped.setTileEntity(tileEntity.getPos(), tileEntity);
        }
        return schematicFlipped;
    }

    private BlockPos flipPos(BlockPos pos, EnumFacing axis, Vec3i dimensions, MBlockPos flipped) throws FlipException {
        switch (axis) {
            case DOWN:
            case UP:
                return (BlockPos) flipped.set(pos.getX(), dimensions.getY() - 1 - pos.getY(), pos.getZ());
            case NORTH:
            case SOUTH:
                return (BlockPos) flipped.set(pos.getX(), pos.getY(), dimensions.getZ() - 1 - pos.getZ());
            case WEST:
            case EAST:
                return (BlockPos) flipped.set(dimensions.getX() - 1 - pos.getX(), pos.getY(), pos.getZ());
        }
        throw new FlipException("'%s' is not a valid axis!", new Object[]{axis.getName()});
    }

    private IBlockState flipBlock(IBlockState blockState, EnumFacing axis, boolean forced) throws FlipException {
        IProperty propertyFacing = BlockStateHelper.getProperty(blockState, "facing");
        if (propertyFacing instanceof net.minecraft.block.properties.PropertyDirection) {
            Comparable value = blockState.getValue(propertyFacing);
            if (value instanceof EnumFacing) {
                EnumFacing facing = getFlippedFacing(axis, (EnumFacing) value);
                if (propertyFacing.getAllowedValues().contains(facing))
                    return blockState.withProperty(propertyFacing, (Comparable) facing);
            }
        } else if (propertyFacing instanceof net.minecraft.block.properties.PropertyEnum) {
            if (BlockLever.EnumOrientation.class.isAssignableFrom(propertyFacing.getValueClass())) {
                BlockLever.EnumOrientation orientation = (BlockLever.EnumOrientation) blockState.getValue(propertyFacing);
                BlockLever.EnumOrientation orientationRotated = getFlippedLeverFacing(axis, orientation);
                if (propertyFacing.getAllowedValues().contains(orientationRotated))
                    return blockState.withProperty(propertyFacing, (Comparable) orientationRotated);
            }
        } else if (propertyFacing != null) {
            Reference.logger.error("'{}': found 'facing' property with unknown type {}", new Object[]{BLOCK_REGISTRY.getNameForObject(blockState.getBlock()), propertyFacing.getClass().getSimpleName()});
        }
        if (!forced && propertyFacing != null)
            throw new FlipException("'%s' cannot be flipped across '%s'", new Object[]{BLOCK_REGISTRY.getNameForObject(blockState.getBlock()), axis});
        return blockState;
    }

    private static EnumFacing getFlippedFacing(EnumFacing axis, EnumFacing side) {
        if (axis.getAxis() == side.getAxis())
            return side.getOpposite();
        return side;
    }

    private static BlockLever.EnumOrientation getFlippedLeverFacing(EnumFacing source, BlockLever.EnumOrientation side) {
        EnumFacing facing;
        if (source.getAxis() != side.getFacing().getAxis())
            return side;
        if (side == BlockLever.EnumOrientation.UP_Z || side == BlockLever.EnumOrientation.DOWN_Z) {
            facing = EnumFacing.NORTH;
        } else if (side == BlockLever.EnumOrientation.UP_X || side == BlockLever.EnumOrientation.DOWN_X) {
            facing = EnumFacing.WEST;
        } else {
            facing = side.getFacing();
        }
        EnumFacing facingFlipped = getFlippedFacing(source, side.getFacing());
        return BlockLever.EnumOrientation.forFacings(facingFlipped, facing);
    }

    public static class FlipException extends Exception {
        public FlipException(String message, Object... args) {
            super(String.format(message, args));
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\clien\\util\FlipHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */