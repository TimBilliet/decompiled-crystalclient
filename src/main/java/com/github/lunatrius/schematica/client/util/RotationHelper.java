package com.github.lunatrius.schematica.client.util;

import co.crystaldev.client.handler.NotificationHandler;
import co.crystaldev.client.mixin.accessor.net.minecraft.util.MixinEnumFacing;
import co.crystaldev.client.util.enums.ChatColor;
import com.github.lunatrius.core.util.BlockPosHelper;
import com.github.lunatrius.core.util.MBlockPos;
import com.github.lunatrius.schematica.api.ISchematic;
import com.github.lunatrius.schematica.block.state.BlockStateHelper;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.reference.Reference;
import com.github.lunatrius.schematica.world.storage.Schematic;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import java.util.List;

import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.BlockQuartz.EnumType;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.RegistryNamespacedDefaultedByKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3i;
import net.minecraft.util.EnumFacing.Axis;

public class RotationHelper {
    public static final RotationHelper INSTANCE = new RotationHelper();
    private static final RegistryNamespacedDefaultedByKey<ResourceLocation, Block> BLOCK_REGISTRY;
    private static final EnumFacing[][] FACINGS;
    private static final Axis[][] AXISES;
    private static final EnumAxis[][] AXISES_LOG;
    private static final BlockQuartz.EnumType[][] AXISES_QUARTZ;

    public boolean rotate(SchematicWorld world, EnumFacing axis, boolean forced) {
        if (world == null)
            return false;
        try {
            ISchematic schematic = world.getSchematic();
            Schematic schematicRotated = rotate(schematic, axis, forced);
            updatePosition(world, axis);
            world.setSchematic(schematicRotated);
            for (TileEntity tileEntity : world.getTileEntities())
                world.initializeTileEntity(tileEntity);
            return true;
        } catch (RotationException re) {
            Reference.logger.error(re.getMessage());
        } catch (Exception e) {
            Reference.logger.fatal("Something went wrong!", e);
        }
        return false;
    }

    private void updatePosition(SchematicWorld world, EnumFacing axis) {
        int offset;
        switch (axis) {
            case DOWN:
            case UP:
                offset = (world.getWidth() - world.getLength()) / 2;
                world.position.x += offset;
                world.position.z -= offset;
                break;
            case NORTH:
            case SOUTH:
                offset = (world.getWidth() - world.getHeight()) / 2;
                world.position.x += offset;
                world.position.y -= offset;
                break;
            case WEST:
            case EAST:
                offset = (world.getHeight() - world.getLength()) / 2;
                world.position.y += offset;
                world.position.z -= offset;
                break;
        }
    }

    public Schematic rotate(ISchematic schematic, EnumFacing axis, boolean forced) throws RotationException {
        Vec3i dimensionsRotated = rotateDimensions(axis, schematic.getWidth(), schematic.getHeight(), schematic.getLength());
        Schematic schematicRotated = new Schematic(schematic.getIcon(), dimensionsRotated.getX(), dimensionsRotated.getY(), dimensionsRotated.getZ());
        MBlockPos tmp = new MBlockPos();
        for (MBlockPos pos : BlockPosHelper.getAllInBox(0, 0, 0, schematic.getWidth() - 1, schematic.getHeight() - 1, schematic.getLength() - 1)) {
            try {
                IBlockState blockState = schematic.getBlockState((BlockPos) pos);
                IBlockState blockStateRotated = rotateBlock(blockState, axis, forced);
                schematicRotated.setBlockState(rotatePos((BlockPos) pos, axis, dimensionsRotated, tmp), blockStateRotated);
            } catch (RotationException ex) {
                NotificationHandler.addNotification(ChatColor.translate("&c&lError: &r" + ex.getMessage() + "\n&rHold 'Left Shift' to force rotation."));
                throw ex;
            }
        }
        List<TileEntity> tileEntities = schematic.getTileEntities();
        for (TileEntity tileEntity : tileEntities) {
            BlockPos pos = tileEntity.getPos();
            tileEntity.setPos(new BlockPos((Vec3i) rotatePos(pos, axis, dimensionsRotated, tmp)));
            schematicRotated.setTileEntity(tileEntity.getPos(), tileEntity);
        }
        return schematicRotated;
    }

    private Vec3i rotateDimensions(EnumFacing axis, int width, int height, int length) throws RotationException {
        switch (axis) {
            case DOWN:
            case UP:
                return new Vec3i(length, height, width);
            case NORTH:
            case SOUTH:
                return new Vec3i(height, width, length);
            case WEST:
            case EAST:
                return new Vec3i(width, length, height);
        }
        throw new RotationException("'%s' is not a valid axis!", axis.getName());
    }

    private BlockPos rotatePos(BlockPos pos, EnumFacing axis, Vec3i dimensions, MBlockPos rotated) throws RotationException {
        switch (axis) {
            case DOWN:
                return (BlockPos) rotated.set(pos.getZ(), pos.getY(), dimensions.getZ() - 1 - pos.getX());
            case UP:
                return (BlockPos) rotated.set(dimensions.getX() - 1 - pos.getZ(), pos.getY(), pos.getX());
            case NORTH:
                return (BlockPos) rotated.set(dimensions.getX() - 1 - pos.getY(), pos.getX(), pos.getZ());
            case SOUTH:
                return (BlockPos) rotated.set(pos.getY(), dimensions.getY() - 1 - pos.getX(), pos.getZ());
            case WEST:
                return (BlockPos) rotated.set(pos.getX(), dimensions.getY() - 1 - pos.getZ(), pos.getY());
            case EAST:
                return (BlockPos) rotated.set(pos.getX(), pos.getZ(), dimensions.getZ() - 1 - pos.getY());
        }
        throw new RotationException("'%s' is not a valid axis!", axis.getName());
    }

    private IBlockState rotateBlock(IBlockState blockState, EnumFacing axisRotation, boolean forced) throws RotationException {
        IProperty propertyFacing = BlockStateHelper.getProperty(blockState, "facing");
        if (propertyFacing instanceof net.minecraft.block.properties.PropertyDirection) {
            Comparable value = blockState.getValue(propertyFacing);
            if (value instanceof EnumFacing) {
                EnumFacing facing = getRotatedFacing(axisRotation, (EnumFacing) value);
                if (propertyFacing.getAllowedValues().contains(facing))
                    return blockState.withProperty(propertyFacing, (Comparable) facing);
            }
        } else if (propertyFacing instanceof net.minecraft.block.properties.PropertyEnum) {
            if (BlockLever.EnumOrientation.class.isAssignableFrom(propertyFacing.getValueClass())) {
                BlockLever.EnumOrientation orientation = (BlockLever.EnumOrientation) blockState.getValue(propertyFacing);
                BlockLever.EnumOrientation orientationRotated = getRotatedLeverFacing(axisRotation, orientation);
                if (propertyFacing.getAllowedValues().contains(orientationRotated))
                    return blockState.withProperty(propertyFacing, (Comparable) orientationRotated);
            }
        } else if (propertyFacing != null) {
            Reference.logger.error("'{}': found 'facing' property with unknown type {}", BLOCK_REGISTRY.getNameForObject(blockState.getBlock()), propertyFacing.getClass().getSimpleName());
        }
        IProperty propertyAxis = BlockStateHelper.getProperty(blockState, "axis");
        if (propertyAxis instanceof net.minecraft.block.properties.PropertyEnum) {
            if (EnumFacing.Axis.class.isAssignableFrom(propertyAxis.getValueClass())) {
                EnumFacing.Axis axis = (EnumFacing.Axis) blockState.getValue(propertyAxis);
                EnumFacing.Axis axisRotated = getRotatedAxis(axisRotation, axis);
                return blockState.withProperty(propertyAxis, (Comparable) axisRotated);
            }
            if (BlockLog.EnumAxis.class.isAssignableFrom(propertyAxis.getValueClass())) {
                BlockLog.EnumAxis axis = (BlockLog.EnumAxis) blockState.getValue(propertyAxis);
                BlockLog.EnumAxis axisRotated = getRotatedLogAxis(axisRotation, axis);
                return blockState.withProperty(propertyAxis, (Comparable) axisRotated);
            }
        } else if (propertyAxis != null) {
            Reference.logger.error("'{}': found 'axis' property with unknown type {}", BLOCK_REGISTRY.getNameForObject(blockState.getBlock()), propertyAxis.getClass().getSimpleName());
        }
        IProperty propertyVariant = BlockStateHelper.getProperty(blockState, "variant");
        if (propertyVariant instanceof net.minecraft.block.properties.PropertyEnum &&
                BlockQuartz.EnumType.class.isAssignableFrom(propertyVariant.getValueClass())) {
            BlockQuartz.EnumType type = (BlockQuartz.EnumType) blockState.getValue(propertyVariant);
            BlockQuartz.EnumType typeRotated = getRotatedQuartzType(axisRotation, type);
            return blockState.withProperty(propertyVariant, (Comparable) typeRotated);
        }
        if (!forced && (propertyFacing != null || propertyAxis != null))
            throw new RotationException("'%s' cannot be rotated around '%s'", BLOCK_REGISTRY.getNameForObject(blockState.getBlock()), axisRotation);
        return blockState;
    }

    private static EnumFacing getRotatedFacing(EnumFacing source, EnumFacing side) {
        return FACINGS[source.ordinal()][side.ordinal()];
    }

    private static EnumFacing.Axis getRotatedAxis(EnumFacing source, EnumFacing.Axis axis) {
        return AXISES[source.getAxis().ordinal()][axis.ordinal()];
    }

    private static BlockLog.EnumAxis getRotatedLogAxis(EnumFacing source, BlockLog.EnumAxis axis) {
        return AXISES_LOG[source.getAxis().ordinal()][axis.ordinal()];
    }

    private static BlockQuartz.EnumType getRotatedQuartzType(EnumFacing source, BlockQuartz.EnumType type) {
        return AXISES_QUARTZ[source.getAxis().ordinal()][type.ordinal()];
    }

    private static BlockLever.EnumOrientation getRotatedLeverFacing(EnumFacing source, BlockLever.EnumOrientation side) {
        EnumFacing facing;
        if (source.getAxis().isVertical() && side.getFacing().getAxis().isVertical()) {
            facing = (side == BlockLever.EnumOrientation.UP_X || side == BlockLever.EnumOrientation.DOWN_X) ? EnumFacing.NORTH : EnumFacing.WEST;
        } else {
            facing = side.getFacing();
        }
        EnumFacing facingRotated = getRotatedFacing(source, side.getFacing());
        return BlockLever.EnumOrientation.forFacings(facingRotated, facing);
    }

    static {
        BLOCK_REGISTRY = Block.blockRegistry;
        FACINGS = new EnumFacing[MixinEnumFacing.getValues().length][];
        AXISES = new EnumFacing.Axis[EnumFacing.Axis.values().length][];
        AXISES_LOG = new BlockLog.EnumAxis[EnumFacing.Axis.values().length][];
        AXISES_QUARTZ = new BlockQuartz.EnumType[Axis.values().length][];
        FACINGS[EnumFacing.DOWN.ordinal()] = new EnumFacing[]{EnumFacing.DOWN, EnumFacing.UP, EnumFacing.WEST, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.NORTH};
        FACINGS[EnumFacing.UP.ordinal()] = new EnumFacing[]{EnumFacing.DOWN, EnumFacing.UP, EnumFacing.EAST, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.SOUTH};
        FACINGS[EnumFacing.NORTH.ordinal()] = new EnumFacing[]{EnumFacing.EAST, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.DOWN, EnumFacing.UP};
        FACINGS[EnumFacing.SOUTH.ordinal()] = new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.UP, EnumFacing.DOWN};
        FACINGS[EnumFacing.WEST.ordinal()] = new EnumFacing[]{EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.UP, EnumFacing.DOWN, EnumFacing.WEST, EnumFacing.EAST};
        FACINGS[EnumFacing.EAST.ordinal()] = new EnumFacing[]{EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.DOWN, EnumFacing.UP, EnumFacing.WEST, EnumFacing.EAST};
        AXISES[Axis.X.ordinal()] = new Axis[]{Axis.X, Axis.Z, Axis.Y};
        AXISES[Axis.Y.ordinal()] = new Axis[]{Axis.Z, Axis.Y, Axis.X};
        AXISES[Axis.Z.ordinal()] = new Axis[]{Axis.Y, Axis.X, Axis.Z};
        AXISES_LOG[Axis.X.ordinal()] = new EnumAxis[]{EnumAxis.X, EnumAxis.Z, EnumAxis.Y, EnumAxis.NONE};
        AXISES_LOG[Axis.Y.ordinal()] = new EnumAxis[]{EnumAxis.Z, EnumAxis.Y, EnumAxis.X, EnumAxis.NONE};
        AXISES_LOG[Axis.Z.ordinal()] = new EnumAxis[]{EnumAxis.Y, EnumAxis.X, EnumAxis.Z, EnumAxis.NONE};
        AXISES_QUARTZ[Axis.X.ordinal()] = new EnumType[]{EnumType.DEFAULT, EnumType.CHISELED, EnumType.LINES_Z, EnumType.LINES_X, EnumType.LINES_Y};
        AXISES_QUARTZ[Axis.Y.ordinal()] = new EnumType[]{EnumType.DEFAULT, EnumType.CHISELED, EnumType.LINES_Y, EnumType.LINES_Z, EnumType.LINES_X};
        AXISES_QUARTZ[Axis.Z.ordinal()] = new EnumType[]{EnumType.DEFAULT, EnumType.CHISELED, EnumType.LINES_X, EnumType.LINES_Y, EnumType.LINES_Z};
    }

    public static class RotationException extends Exception {
        public RotationException(String message, Object... args) {
            super(String.format(message, args));
        }
    }
}