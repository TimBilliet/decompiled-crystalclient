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
import net.minecraft.util.*;

import java.util.List;

public class RotationHelper {
    public static final RotationHelper INSTANCE = new RotationHelper();

    private static final RegistryNamespacedDefaultedByKey<ResourceLocation, Block> BLOCK_REGISTRY = Block.blockRegistry;

    private static final EnumFacing[][] FACINGS = new EnumFacing[(MixinEnumFacing.getValues()).length][];

    private static final EnumFacing.Axis[][] AXISES = new EnumFacing.Axis[(EnumFacing.Axis.values()).length][];

    private static final BlockLog.EnumAxis[][] AXISES_LOG = new BlockLog.EnumAxis[(EnumFacing.Axis.values()).length][];

    private static final BlockQuartz.EnumType[][] AXISES_QUARTZ = new BlockQuartz.EnumType[(EnumFacing.Axis.values()).length][];

    public boolean rotate(SchematicWorld world, EnumFacing axis, boolean forced) {
        if (world == null)
            return false;
        try {
            ISchematic schematic = world.getSchematic();
            Schematic schematicRotated = rotate(schematic, axis, forced);
            updatePosition(world, axis);
            world.setSchematic((ISchematic) schematicRotated);
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
        throw new RotationException("'%s' is not a valid axis!", new Object[]{axis.getName()});
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
        throw new RotationException("'%s' is not a valid axis!", new Object[]{axis.getName()});
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
            Reference.logger.error("'{}': found 'facing' property with unknown type {}", new Object[]{BLOCK_REGISTRY.getNameForObject(blockState.getBlock()), propertyFacing.getClass().getSimpleName()});
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
            Reference.logger.error("'{}': found 'axis' property with unknown type {}", new Object[]{BLOCK_REGISTRY.getNameForObject(blockState.getBlock()), propertyAxis.getClass().getSimpleName()});
        }
        IProperty propertyVariant = BlockStateHelper.getProperty(blockState, "variant");
        if (propertyVariant instanceof net.minecraft.block.properties.PropertyEnum &&
                BlockQuartz.EnumType.class.isAssignableFrom(propertyVariant.getValueClass())) {
            BlockQuartz.EnumType type = (BlockQuartz.EnumType) blockState.getValue(propertyVariant);
            BlockQuartz.EnumType typeRotated = getRotatedQuartzType(axisRotation, type);
            return blockState.withProperty(propertyVariant, (Comparable) typeRotated);
        }
        if (!forced && (propertyFacing != null || propertyAxis != null))
            throw new RotationException("'%s' cannot be rotated around '%s'", new Object[]{BLOCK_REGISTRY.getNameForObject(blockState.getBlock()), axisRotation});
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
        (new EnumFacing[6])[0] = EnumFacing.DOWN;
        (new EnumFacing[6])[1] = EnumFacing.UP;
        (new EnumFacing[6])[2] = EnumFacing.WEST;
        (new EnumFacing[6])[3] = EnumFacing.EAST;
        (new EnumFacing[6])[4] = EnumFacing.SOUTH;
        (new EnumFacing[6])[5] = EnumFacing.NORTH;
        FACINGS[EnumFacing.DOWN.ordinal()] = new EnumFacing[6];
        (new EnumFacing[6])[0] = EnumFacing.DOWN;
        (new EnumFacing[6])[1] = EnumFacing.UP;
        (new EnumFacing[6])[2] = EnumFacing.EAST;
        (new EnumFacing[6])[3] = EnumFacing.WEST;
        (new EnumFacing[6])[4] = EnumFacing.NORTH;
        (new EnumFacing[6])[5] = EnumFacing.SOUTH;
        FACINGS[EnumFacing.UP.ordinal()] = new EnumFacing[6];
        (new EnumFacing[6])[0] = EnumFacing.EAST;
        (new EnumFacing[6])[1] = EnumFacing.WEST;
        (new EnumFacing[6])[2] = EnumFacing.NORTH;
        (new EnumFacing[6])[3] = EnumFacing.SOUTH;
        (new EnumFacing[6])[4] = EnumFacing.DOWN;
        (new EnumFacing[6])[5] = EnumFacing.UP;
        FACINGS[EnumFacing.NORTH.ordinal()] = new EnumFacing[6];
        (new EnumFacing[6])[0] = EnumFacing.WEST;
        (new EnumFacing[6])[1] = EnumFacing.EAST;
        (new EnumFacing[6])[2] = EnumFacing.NORTH;
        (new EnumFacing[6])[3] = EnumFacing.SOUTH;
        (new EnumFacing[6])[4] = EnumFacing.UP;
        (new EnumFacing[6])[5] = EnumFacing.DOWN;
        FACINGS[EnumFacing.SOUTH.ordinal()] = new EnumFacing[6];
        (new EnumFacing[6])[0] = EnumFacing.NORTH;
        (new EnumFacing[6])[1] = EnumFacing.SOUTH;
        (new EnumFacing[6])[2] = EnumFacing.UP;
        (new EnumFacing[6])[3] = EnumFacing.DOWN;
        (new EnumFacing[6])[4] = EnumFacing.WEST;
        (new EnumFacing[6])[5] = EnumFacing.EAST;
        FACINGS[EnumFacing.WEST.ordinal()] = new EnumFacing[6];
        (new EnumFacing[6])[0] = EnumFacing.SOUTH;
        (new EnumFacing[6])[1] = EnumFacing.NORTH;
        (new EnumFacing[6])[2] = EnumFacing.DOWN;
        (new EnumFacing[6])[3] = EnumFacing.UP;
        (new EnumFacing[6])[4] = EnumFacing.WEST;
        (new EnumFacing[6])[5] = EnumFacing.EAST;
        FACINGS[EnumFacing.EAST.ordinal()] = new EnumFacing[6];
        (new EnumFacing.Axis[3])[0] = EnumFacing.Axis.X;
        (new EnumFacing.Axis[3])[1] = EnumFacing.Axis.Z;
        (new EnumFacing.Axis[3])[2] = EnumFacing.Axis.Y;
        AXISES[EnumFacing.Axis.X.ordinal()] = new EnumFacing.Axis[3];
        (new EnumFacing.Axis[3])[0] = EnumFacing.Axis.Z;
        (new EnumFacing.Axis[3])[1] = EnumFacing.Axis.Y;
        (new EnumFacing.Axis[3])[2] = EnumFacing.Axis.X;
        AXISES[EnumFacing.Axis.Y.ordinal()] = new EnumFacing.Axis[3];
        (new EnumFacing.Axis[3])[0] = EnumFacing.Axis.Y;
        (new EnumFacing.Axis[3])[1] = EnumFacing.Axis.X;
        (new EnumFacing.Axis[3])[2] = EnumFacing.Axis.Z;
        AXISES[EnumFacing.Axis.Z.ordinal()] = new EnumFacing.Axis[3];
        (new BlockLog.EnumAxis[4])[0] = BlockLog.EnumAxis.X;
        (new BlockLog.EnumAxis[4])[1] = BlockLog.EnumAxis.Z;
        (new BlockLog.EnumAxis[4])[2] = BlockLog.EnumAxis.Y;
        (new BlockLog.EnumAxis[4])[3] = BlockLog.EnumAxis.NONE;
        AXISES_LOG[EnumFacing.Axis.X.ordinal()] = new BlockLog.EnumAxis[4];
        (new BlockLog.EnumAxis[4])[0] = BlockLog.EnumAxis.Z;
        (new BlockLog.EnumAxis[4])[1] = BlockLog.EnumAxis.Y;
        (new BlockLog.EnumAxis[4])[2] = BlockLog.EnumAxis.X;
        (new BlockLog.EnumAxis[4])[3] = BlockLog.EnumAxis.NONE;
        AXISES_LOG[EnumFacing.Axis.Y.ordinal()] = new BlockLog.EnumAxis[4];
        (new BlockLog.EnumAxis[4])[0] = BlockLog.EnumAxis.Y;
        (new BlockLog.EnumAxis[4])[1] = BlockLog.EnumAxis.X;
        (new BlockLog.EnumAxis[4])[2] = BlockLog.EnumAxis.Z;
        (new BlockLog.EnumAxis[4])[3] = BlockLog.EnumAxis.NONE;
        AXISES_LOG[EnumFacing.Axis.Z.ordinal()] = new BlockLog.EnumAxis[4];
        (new BlockQuartz.EnumType[5])[0] = BlockQuartz.EnumType.DEFAULT;
        (new BlockQuartz.EnumType[5])[1] = BlockQuartz.EnumType.CHISELED;
        (new BlockQuartz.EnumType[5])[2] = BlockQuartz.EnumType.LINES_Z;
        (new BlockQuartz.EnumType[5])[3] = BlockQuartz.EnumType.LINES_X;
        (new BlockQuartz.EnumType[5])[4] = BlockQuartz.EnumType.LINES_Y;
        AXISES_QUARTZ[EnumFacing.Axis.X.ordinal()] = new BlockQuartz.EnumType[5];
        (new BlockQuartz.EnumType[5])[0] = BlockQuartz.EnumType.DEFAULT;
        (new BlockQuartz.EnumType[5])[1] = BlockQuartz.EnumType.CHISELED;
        (new BlockQuartz.EnumType[5])[2] = BlockQuartz.EnumType.LINES_Y;
        (new BlockQuartz.EnumType[5])[3] = BlockQuartz.EnumType.LINES_Z;
        (new BlockQuartz.EnumType[5])[4] = BlockQuartz.EnumType.LINES_X;
        AXISES_QUARTZ[EnumFacing.Axis.Y.ordinal()] = new BlockQuartz.EnumType[5];
        (new BlockQuartz.EnumType[5])[0] = BlockQuartz.EnumType.DEFAULT;
        (new BlockQuartz.EnumType[5])[1] = BlockQuartz.EnumType.CHISELED;
        (new BlockQuartz.EnumType[5])[2] = BlockQuartz.EnumType.LINES_X;
        (new BlockQuartz.EnumType[5])[3] = BlockQuartz.EnumType.LINES_Y;
        (new BlockQuartz.EnumType[5])[4] = BlockQuartz.EnumType.LINES_Z;
        AXISES_QUARTZ[EnumFacing.Axis.Z.ordinal()] = new BlockQuartz.EnumType[5];
    }

    public static class RotationException extends Exception {
        public RotationException(String message, Object... args) {
            super(String.format(message, args));
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\clien\\util\RotationHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */