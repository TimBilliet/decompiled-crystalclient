package com.github.lunatrius.schematica.client.printer.registry;

import com.github.lunatrius.schematica.block.state.BlockStateHelper;
import net.minecraft.block.*;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

import java.util.*;

public class PlacementRegistry {
    public static final PlacementRegistry INSTANCE = new PlacementRegistry();

    private final Map<Class<? extends Block>, PlacementData> classPlacementMap = new LinkedHashMap<>();

    private final Map<Block, PlacementData> blockPlacementMap = new HashMap<>();

    private final Map<Item, PlacementData> itemPlacementMap = new HashMap<>();

    private void populateMappings() {
        this.classPlacementMap.clear();
        this.blockPlacementMap.clear();
        this.itemPlacementMap.clear();
        IValidPlayerFacing playerFacingEntity = (blockState, player, pos, world) -> {
            EnumFacing facing = (EnumFacing) BlockStateHelper.getPropertyValue(blockState, "facing");
            return (facing == player.getHorizontalFacing());
        };
        IValidPlayerFacing playerFacingEntityOpposite = (blockState, player, pos, world) -> {
            EnumFacing facing = (EnumFacing) BlockStateHelper.getPropertyValue(blockState, "facing");
            return (facing == player.getHorizontalFacing().getOpposite());
        };
        IValidPlayerFacing playerFacingPiston = (blockState, player, pos, world) -> {
            EnumFacing facing = (EnumFacing) BlockStateHelper.getPropertyValue(blockState, "facing");
//        return (facing == BlockPistonBase.func_180695_a(world, pos, (EntityLivingBase)player));
            return (facing == BlockPistonBase.getFacingFromEntity(world, pos, (EntityLivingBase) player));
        };
        IValidPlayerFacing playerFacingRotateY = (blockState, player, pos, world) -> {
            EnumFacing facing = (EnumFacing) BlockStateHelper.getPropertyValue(blockState, "facing");
            return (facing == player.getHorizontalFacing().rotateY());
        };
        IValidPlayerFacing playerFacingLever = (blockState, player, pos, world) -> {
            BlockLever.EnumOrientation value = (BlockLever.EnumOrientation) blockState.getValue((IProperty) BlockLever.FACING);
            return (!value.getFacing().getAxis().isVertical() || BlockLever.EnumOrientation.forFacings(value.getFacing(), player.getHorizontalFacing()) == value);
        };
        IValidPlayerFacing playerFacingStandingSign = (blockState, player, pos, world) -> {
            int value = ((Integer) blockState.getValue((IProperty) BlockStandingSign.ROTATION)).intValue();
            int facing = MathHelper.floor_double((player.rotationYaw + 180.0D) * 16.0D / 360.0D + 0.5D) & 0xF;
            return (value == facing);
        };
        IValidPlayerFacing playerFacingIgnore = (blockState, player, pos, world) -> false;
        IOffset offsetSlab = blockState -> {
            if (!((BlockSlab) blockState.getBlock()).isDouble()) {
                BlockSlab.EnumBlockHalf half = (BlockSlab.EnumBlockHalf) blockState.getValue((IProperty) BlockSlab.HALF);
                return (half == BlockSlab.EnumBlockHalf.TOP) ? 1.0F : 0.0F;
            }
            return 0.0F;
        };
        IOffset offsetStairs = blockState -> {
            BlockStairs.EnumHalf half = (BlockStairs.EnumHalf) blockState.getValue((IProperty) BlockStairs.HALF);
            return (half == BlockStairs.EnumHalf.TOP) ? 1.0F : 0.0F;
        };
        IOffset offsetTrapDoor = blockState -> {
            BlockTrapDoor.DoorHalf half = (BlockTrapDoor.DoorHalf) blockState.getValue((IProperty) BlockTrapDoor.HALF);
            return (half == BlockTrapDoor.DoorHalf.TOP) ? 1.0F : 0.0F;
        };
        IValidBlockFacing blockFacingLog = (solidSides, blockState) -> {
            List<EnumFacing> list = new ArrayList<>();
            BlockLog.EnumAxis axis = (BlockLog.EnumAxis) blockState.getValue((IProperty) BlockLog.LOG_AXIS);
            for (EnumFacing side : solidSides) {
                if (axis != BlockLog.EnumAxis.fromFacingAxis(side.getAxis()))
                    continue;
                list.add(side);
            }
            return list;
        };
        IValidBlockFacing blockFacingPillar = (solidSides, blockState) -> {
            List<EnumFacing> list = new ArrayList<>();
            EnumFacing.Axis axis = (EnumFacing.Axis) blockState.getValue((IProperty) BlockRotatedPillar.AXIS);
            for (EnumFacing side : solidSides) {
                if (axis != side.getAxis())
                    continue;
                list.add(side);
            }
            return list;
        };
        IValidBlockFacing blockFacingOpposite = (solidSides, blockState) -> {
            List<EnumFacing> list = new ArrayList<>();
            IProperty propertyFacing = BlockStateHelper.getProperty(blockState, "facing");
            if (propertyFacing != null && propertyFacing.getValueClass().equals(EnumFacing.class)) {
                EnumFacing facing = (EnumFacing) blockState.getValue(propertyFacing);
                for (EnumFacing side : solidSides) {
                    if (facing.getOpposite() != side)
                        continue;
                    list.add(side);
                }
            }
            return list;
        };
        IValidBlockFacing blockFacingSame = (solidSides, blockState) -> {
            List<EnumFacing> list = new ArrayList<>();
            IProperty propertyFacing = BlockStateHelper.getProperty(blockState, "facing");
            if (propertyFacing != null && propertyFacing.getValueClass().equals(EnumFacing.class)) {
                EnumFacing facing = (EnumFacing) blockState.getValue(propertyFacing);
                for (EnumFacing side : solidSides) {
                    if (facing != side)
                        continue;
                    list.add(side);
                }
            }
            return list;
        };
        IValidBlockFacing blockFacingHopper = (solidSides, blockState) -> {
            List<EnumFacing> list = new ArrayList<>();
            EnumFacing facing = (EnumFacing) blockState.getValue((IProperty) BlockHopper.FACING);
            for (EnumFacing side : solidSides) {
                if (facing != side)
                    continue;
                list.add(side);
            }
            return list;
        };
        IValidBlockFacing blockFacingLever = (solidSides, blockState) -> {
            List<EnumFacing> list = new ArrayList<>();
            BlockLever.EnumOrientation facing = (BlockLever.EnumOrientation) blockState.getValue((IProperty) BlockLever.FACING);
            for (EnumFacing side : solidSides) {
                if (facing.getFacing().getOpposite() != side)
                    continue;
                list.add(side);
            }
            return list;
        };
        IValidBlockFacing blockFacingQuartz = (solidSides, blockState) -> {
            List<EnumFacing> list = new ArrayList<>();
            BlockQuartz.EnumType variant = (BlockQuartz.EnumType) blockState.getValue((IProperty) BlockQuartz.VARIANT);
            for (EnumFacing side : solidSides) {
                if (variant == BlockQuartz.EnumType.LINES_X && side.getAxis() != EnumFacing.Axis.X)
                    continue;
                if (variant == BlockQuartz.EnumType.LINES_Y && side.getAxis() != EnumFacing.Axis.Y)
                    continue;
                if (variant == BlockQuartz.EnumType.LINES_Z && side.getAxis() != EnumFacing.Axis.Z)
                    continue;
                list.add(side);
            }
            return list;
        };
        IExtraClick extraClickDoubleSlab = blockState -> ((BlockSlab) blockState.getBlock()).isDouble() ? 1 : 0;
        addPlacementMapping((Class) BlockLog.class, new PlacementData(blockFacingLog));
        addPlacementMapping((Class) BlockButton.class, new PlacementData(blockFacingOpposite));
        addPlacementMapping((Class) BlockChest.class, new PlacementData(playerFacingEntityOpposite));
        addPlacementMapping((Class) BlockDispenser.class, new PlacementData(playerFacingPiston));
        addPlacementMapping((Class) BlockDoor.class, new PlacementData(playerFacingEntity));
        addPlacementMapping((Class) BlockEnderChest.class, new PlacementData(playerFacingEntityOpposite));
        addPlacementMapping((Class) BlockFenceGate.class, new PlacementData(playerFacingEntity));
        addPlacementMapping((Class) BlockFurnace.class, new PlacementData(playerFacingEntityOpposite));
        addPlacementMapping((Class) BlockHopper.class, new PlacementData(blockFacingHopper));
        addPlacementMapping((Class) BlockPistonBase.class, new PlacementData(playerFacingPiston));
        addPlacementMapping((Class) BlockPumpkin.class, new PlacementData(playerFacingEntityOpposite));
        addPlacementMapping((Class) BlockRotatedPillar.class, new PlacementData(blockFacingPillar));
        addPlacementMapping((Class) BlockSlab.class, (new PlacementData()).setOffsetY(offsetSlab).setExtraClick(extraClickDoubleSlab));
        addPlacementMapping((Class) BlockStairs.class, (new PlacementData(playerFacingEntity)).setOffsetY(offsetStairs));
        addPlacementMapping((Class) BlockTorch.class, new PlacementData(blockFacingOpposite));
        addPlacementMapping((Class) BlockTrapDoor.class, (new PlacementData(blockFacingOpposite)).setOffsetY(offsetTrapDoor));
        addPlacementMapping(Blocks.anvil, new PlacementData(playerFacingRotateY));
        addPlacementMapping(Blocks.cocoa, new PlacementData(blockFacingSame));
        addPlacementMapping(Blocks.end_portal_frame, new PlacementData(playerFacingEntityOpposite));
        addPlacementMapping(Blocks.ladder, new PlacementData(blockFacingOpposite));
        addPlacementMapping(Blocks.lever, new PlacementData(playerFacingLever, blockFacingLever));
        addPlacementMapping(Blocks.quartz_block, new PlacementData(blockFacingQuartz));
        addPlacementMapping(Blocks.standing_sign, new PlacementData(playerFacingStandingSign));
        addPlacementMapping((Block) Blocks.tripwire_hook, new PlacementData(blockFacingOpposite));
        addPlacementMapping(Blocks.wall_sign, new PlacementData(blockFacingOpposite));
        addPlacementMapping(Items.comparator, new PlacementData(playerFacingEntityOpposite));
        addPlacementMapping(Items.repeater, new PlacementData(playerFacingEntityOpposite));
        addPlacementMapping(Blocks.bed, new PlacementData(playerFacingIgnore));
        addPlacementMapping(Blocks.end_portal, new PlacementData(playerFacingIgnore));
        addPlacementMapping((Block) Blocks.piston_extension, new PlacementData(playerFacingIgnore));
        addPlacementMapping((Block) Blocks.piston_head, new PlacementData(playerFacingIgnore));
        addPlacementMapping((Block) Blocks.portal, new PlacementData(playerFacingIgnore));
        addPlacementMapping((Block) Blocks.skull, new PlacementData(playerFacingIgnore));
    }

    private PlacementData addPlacementMapping(Class<? extends Block> clazz, PlacementData data) {
        if (clazz == null || data == null)
            return null;
        return this.classPlacementMap.put(clazz, data);
    }

    private PlacementData addPlacementMapping(Block block, PlacementData data) {
        if (block == null || data == null)
            return null;
        return this.blockPlacementMap.put(block, data);
    }

    private PlacementData addPlacementMapping(Item item, PlacementData data) {
        if (item == null || data == null)
            return null;
        return this.itemPlacementMap.put(item, data);
    }

    public PlacementData getPlacementData(IBlockState blockState, ItemStack itemStack) {
        Item item = itemStack.getItem();
        PlacementData placementDataItem = this.itemPlacementMap.get(item);
        if (placementDataItem != null)
            return placementDataItem;
        Block block = blockState.getBlock();
        PlacementData placementDataBlock = this.blockPlacementMap.get(block);
        if (placementDataBlock != null)
            return placementDataBlock;
        for (Class<? extends Block> clazz : this.classPlacementMap.keySet()) {
            if (clazz.isInstance(block))
                return this.classPlacementMap.get(clazz);
        }
        return null;
    }

    static {
        INSTANCE.populateMappings();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\printer\registry\PlacementRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */