package com.github.lunatrius.schematica.client.printer;

import co.crystaldev.client.Reference;
import co.crystaldev.client.feature.impl.factions.Schematica;
import co.crystaldev.client.mixin.accessor.net.minecraft.block.MixinBlockRedstoneDiode;
import co.crystaldev.client.mixin.accessor.net.minecraft.util.MixinEnumFacing;
import com.github.lunatrius.core.util.BlockPosHelper;
import com.github.lunatrius.core.util.MBlockPos;
import com.github.lunatrius.schematica.block.state.BlockStateHelper;
import com.github.lunatrius.schematica.client.printer.nbtsync.NBTSync;
import com.github.lunatrius.schematica.client.printer.nbtsync.SyncRegistry;
import com.github.lunatrius.schematica.client.printer.registry.PlacementData;
import com.github.lunatrius.schematica.client.printer.registry.PlacementRegistry;
import com.github.lunatrius.schematica.client.util.BlockStateToItemStack;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.handler.ConfigurationHandler;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SchematicPrinter {
    public static final SchematicPrinter INSTANCE = new SchematicPrinter();

    public static final PropertyDirection FACING = PropertyDirection.create("facing");

    public static final PropertyBool EXTENDED = PropertyBool.create("extended");

    public static final PropertyEnum<BlockPistonExtension.EnumPistonType> PISTON_TYPE = PropertyEnum.create("type", BlockPistonExtension.EnumPistonType.class);

    private final Minecraft minecraft = Minecraft.getMinecraft();

    private final List<Class<?>> cannoningSmartBreak = Arrays.asList(new Class[]{
            BlockRedstoneWire.class, BlockRedstoneDiode.class, BlockRedstoneTorch.class, BlockLever.class, BlockTrapDoor.class, BlockFenceGate.class, BlockDoor.class, BlockTripWire.class, BlockTripWireHook.class, BlockCactus.class,
            BlockReed.class});

    private final List<Class<? extends Block>> requiresSolidFloor = Arrays.asList((Class<? extends Block>[]) new Class[]{BlockRedstoneDiode.class, BlockRedstoneWire.class, BlockFenceGate.class, BlockPressurePlate.class, BlockPressurePlateWeighted.class, BlockDoor.class, BlockPumpkin.class, BlockFlower.class});

    private boolean isEnabled = true;

    private boolean isPrinting = false;

    private SchematicWorld schematic = null;

    private byte[][][] timeout = (byte[][][]) null;

    private byte[][][] attempts = (byte[][][]) null;

    private final HashMap<BlockPos, Integer> syncBlacklist = new HashMap<>();

    private final HashMap<BlockPos, Integer> breakList = new HashMap<>();

    private BridgeLocation activeBridge = null;

    private C03PacketPlayer.C05PacketPlayerLook packetPlayerLook = null;

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean togglePrinting() {
        this.isPrinting = (!this.isPrinting && this.schematic != null);
        this.activeBridge = null;
        this.syncBlacklist.clear();
        this.breakList.clear();
        return this.isPrinting;
    }

    public boolean isPrinting() {
        return this.isPrinting;
    }

    public void setPrinting(boolean isPrinting) {
        this.isPrinting = isPrinting;
        this.activeBridge = null;
        this.breakList.clear();
        this.syncBlacklist.clear();
    }

    public SchematicWorld getSchematic() {
        return this.schematic;
    }

    public void setSchematic(SchematicWorld schematic) {
        this.isPrinting = false;
        this.schematic = schematic;
        refresh();
    }

    public void refresh() {
        if (this.schematic != null) {
            this.timeout = new byte[this.schematic.getWidth()][this.schematic.getHeight()][this.schematic.getLength()];
            this.attempts = new byte[this.schematic.getWidth()][this.schematic.getHeight()][this.schematic.getLength()];
        } else {
            this.timeout = (byte[][][]) null;
            this.attempts = (byte[][][]) null;
        }
        this.activeBridge = null;
        this.syncBlacklist.clear();
        this.breakList.clear();
    }

    public boolean print(WorldClient world, EntityPlayerSP player) {
        double dX = ClientProxy.playerPosition.x - this.schematic.position.x;
        double dY = ClientProxy.playerPosition.y - this.schematic.position.y;
        double dZ = ClientProxy.playerPosition.z - this.schematic.position.z;
        int x = (int) Math.floor(dX);
        int y = (int) Math.floor(dY);
        int z = (int) Math.floor(dZ);
        int range = (Schematica.getInstance()).placementDistance;
        int minX = Math.max(0, x - range);
        int maxX = Math.min(this.schematic.getWidth() - 1, x + range);
        int minY = Math.max(0, y - range);
        int maxY = Math.min(this.schematic.getHeight() - 1, y + range);
        int minZ = Math.max(0, z - range);
        int maxZ = Math.min(this.schematic.getLength() - 1, z + range);
        if (minX > maxX || minY > maxY || minZ > maxZ)
            return false;
        int slot = player.inventory.currentItem;
        boolean isSneaking = player.isSneaking();
        boolean isRenderingLayer = this.schematic.isRenderingLayer;
        int renderingLayer = this.schematic.renderingLayer;
        if (isRenderingLayer) {
            if (renderingLayer > maxY || renderingLayer < minY)
                return false;
            minY = maxY = renderingLayer;
        }
        syncSneaking(player, true);
        double blockReachDistance = this.minecraft.playerController.getBlockReachDistance() - 0.1D;
        double blockReachDistanceSq = blockReachDistance * blockReachDistance;
        int reachDistSq = (int) blockReachDistance + 2;
        for (MBlockPos pos : BlockPosHelper.getAllInBoxXZYFromFacing(player.getHorizontalFacing().getOpposite(), -reachDistSq, -reachDistSq, -reachDistSq, reachDistSq, reachDistSq, reachDistSq)) {
            pos = pos.add(dX, dY, dZ);
            if (pos.x < minX || pos.x > maxX || pos.y < minY || pos.y > maxY || pos.z < minZ || pos.z > maxZ)
                continue;
            if (pos.distanceSqToCenter(dX, dY, dZ) > blockReachDistanceSq)
                continue;
            try {
                if (placeBlock(world, player, (BlockPos) pos))
                    return syncSlotAndSneaking(player, slot, isSneaking, true);
            } catch (Exception e) {
                Reference.LOGGER.error("Could not place block!", e);
                return syncSlotAndSneaking(player, slot, isSneaking, false);
            }
        }
        if ((Schematica.getInstance()).autoBridge)
            if (this.activeBridge != null && (!this.activeBridge.isComplete() || this.activeBridge.isLastAttempt())) {
                BlockPos pos = this.activeBridge.getRealPos();
                if (!this.activeBridge.isInRange(dX, dY, dZ, blockReachDistanceSq)) {
                    this.activeBridge = null;
                    return syncSlotAndSneaking(player, slot, isSneaking, true);
                }
                try {
                    if (placeBlock(world, player, this.activeBridge.getSchemPos(), pos, this.activeBridge.getState(), this.activeBridge.getStack(), true)) {
                        this.activeBridge.setPlaceAttempts(0);
                        this.activeBridge.incPositions();
                        this.breakList.put(pos, Integer.valueOf(2));
                        if (this.activeBridge.isLastAttempt())
                            this.activeBridge = null;
                        return syncSlotAndSneaking(player, slot, isSneaking, true);
                    }
                    if (this.activeBridge.incAttempts() > 5)
                        this.activeBridge = null;
                } catch (Exception ex) {
                    Reference.LOGGER.error("Could not place block!", ex);
                    return syncSlotAndSneaking(player, slot, isSneaking, false);
                }
            } else if (this.activeBridge != null) {
                this.activeBridge = null;
            }
        return syncSlotAndSneaking(player, slot, isSneaking, true);
    }

    private boolean syncSlotAndSneaking(EntityPlayerSP player, int slot, boolean isSneaking, boolean success) {
        player.inventory.currentItem = slot;
        syncSneaking(player, isSneaking);
        return success;
    }

    private boolean placeBlock(WorldClient world, EntityPlayerSP player, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if (this.timeout[x][y][z] > 0) {
            this.timeout[x][y][z] = (byte) (this.timeout[x][y][z] - 1);
            return false;
        }
        int wx = this.schematic.position.x + x;
        int wy = this.schematic.position.y + y;
        int wz = this.schematic.position.z + z;
        BlockPos realPos = new BlockPos(wx, wy, wz);
        IBlockState blockState = this.schematic.getBlockState(pos);
        IBlockState realBlockState = world.getBlockState(realPos);
        Block realBlock = realBlockState.getBlock();
        if (BlockStateHelper.areBlockStatesEqual(blockState, realBlockState)) {
            NBTSync handler = SyncRegistry.INSTANCE.getHandler(realBlock);
            if (handler != null) {
                this.timeout[x][y][z] = (byte) ConfigurationHandler.timeout;
                Integer tries = this.syncBlacklist.get(realPos);
                if (tries == null) {
                    tries = Integer.valueOf(0);
                } else if (tries.intValue() >= 10) {
                    return false;
                }
                Reference.LOGGER.trace("Trying to sync block at {} {}", new Object[]{realPos, tries});
                boolean success = handler.execute((EntityPlayer) player, (World) this.schematic, pos, (World) world, realPos);
                if (success)
                    this.syncBlacklist.put(realPos, Integer.valueOf(tries.intValue() + 1));
                return success;
            }
            return false;
        }
        if ((Schematica.getInstance()).autoTick &&

                arePropertiesEqual(FACING, blockState, realBlockState) && ((realBlock instanceof BlockRedstoneRepeater && blockState
                .getBlock() instanceof BlockRedstoneRepeater && arePropertiesNotEqual(BlockRedstoneRepeater.DELAY, blockState, realBlockState)) || (realBlock instanceof BlockRedstoneComparator && blockState
                .getBlock() instanceof BlockRedstoneComparator && arePropertiesNotEqual(BlockRedstoneComparator.MODE, blockState, realBlockState)) || (realBlock instanceof BlockTrapDoor && blockState
                .getBlock() instanceof BlockTrapDoor && arePropertiesNotEqual(BlockTrapDoor.OPEN, blockState, realBlockState)) || (realBlock instanceof BlockLever && blockState
                .getBlock() instanceof BlockLever && arePropertiesNotEqual(BlockLever.POWERED, blockState, realBlockState)))) {
            syncSneaking(player, false);
            boolean success = this.minecraft.playerController.onPlayerRightClick(player, world, player.getHeldItem(), realPos, EnumFacing.UP, new Vec3(0.0D, 0.0D, 0.0D));
            if (success) {
                this.timeout[x][y][z] = (byte) (Schematica.getInstance()).autoTickTimeout;
                return !ConfigurationHandler.placeInstantly;
            }
        }
        boolean inBreakList = this.breakList.containsKey(realPos);
        if (inBreakList || ((Schematica.getInstance()).autoBreak && !world.isAirBlock(realPos) && this.minecraft.playerController.isInCreativeMode())) {
            Block schBlock = blockState.getBlock();
            boolean doBreak = !this.schematic.isAirBlock(pos);
            if (inBreakList) {
                int timeout = ((Integer) this.breakList.get(realPos)).intValue();
                if (timeout > 0) {
                    doBreak = false;
                    this.breakList.put(realPos, Integer.valueOf(timeout - 1));
                } else {
                    doBreak = true;
                }
            } else {
                if (this.cannoningSmartBreak.contains(realBlock.getClass()))
                    doBreak = false;
                if (realBlock != schBlock)
                    doBreak = true;
                if (arePropertiesNotEqual(FACING, blockState, realBlockState)) {
                    doBreak = true;
                } else if (isPiston(schBlock) && isPiston(realBlock)) {
                    if (arePropertiesNotEqual((IProperty<BlockPistonExtension.EnumPistonType>) PISTON_TYPE, blockState, realBlockState))
                        doBreak = true;
                    if (schBlock instanceof BlockPistonExtension || (realBlock instanceof net.minecraft.block.BlockPistonBase && arePropertiesNotEqual(EXTENDED, blockState, realBlockState)))
                        doBreak = false;
                }
                if (schBlock instanceof BlockRedstoneDiode && realBlock instanceof BlockRedstoneDiode && (
                        (MixinBlockRedstoneDiode) realBlock).getIsRepeaterPowered() != ((MixinBlockRedstoneDiode) schBlock).getIsRepeaterPowered())
                    doBreak = false;
                if (realBlock instanceof BlockRedstoneComparator && arePropertiesNotEqual(BlockRedstoneComparator.POWERED, blockState, realBlockState))
                    doBreak = false;
                if (schBlock.getMaterial() == Material.air || realBlock.getMaterial() == Material.water || realBlock.getMaterial() == Material.lava)
                    doBreak = false;
            }
            if (doBreak) {
                if (this.minecraft.playerController.clickBlock(realPos, EnumFacing.DOWN) && inBreakList)
                    this.breakList.remove(realPos);
                this.timeout[x][y][z] = (byte) ConfigurationHandler.timeout;
                return !ConfigurationHandler.destroyInstantly;
            }
        }
        if (this.schematic.isAirBlock(pos))
            return false;
        if (!realBlock.isReplaceable((World) world, realPos))
            return false;
        ItemStack itemStack = BlockStateToItemStack.getItemStack(blockState, new MovingObjectPosition((Entity) player), this.schematic, pos);
        if (itemStack == null || itemStack.getItem() == null) {
            Reference.LOGGER.debug("{} is missing a mapping!", new Object[]{blockState});
            return false;
        }
        if (placeBlock(world, player, pos, realPos, blockState, itemStack, false)) {
            this.timeout[x][y][z] = (byte) ConfigurationHandler.timeout;
            return !ConfigurationHandler.placeInstantly;
        }
        return false;
    }

    private <T extends Comparable<T>> boolean doesPropertyExist(IProperty<T> property, IBlockState main, IBlockState state) {
        return (main.getProperties().containsKey(property) && state.getProperties().containsKey(property));
    }

    private <T extends Comparable<T>> boolean arePropertiesEqual(IProperty<T> property, IBlockState main, IBlockState state) {
        T mainValue = getValue(property, main), stateValue = getValue(property, state);
        return (mainValue != null && mainValue == stateValue);
    }

    private <T extends Comparable<T>> boolean arePropertiesNotEqual(IProperty<T> property, IBlockState main, IBlockState state) {
        T mainValue = getValue(property, main), stateValue = getValue(property, state);
        return (mainValue != null && mainValue != stateValue);
    }

    private <T extends Comparable<T>> T getValue(IProperty<T> property, IBlockState state) {
        try {
            return state.getProperties().containsKey(property) ? (T) state.getValue(property) : null;
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private boolean isPiston(Block block) {
        return (block instanceof net.minecraft.block.BlockPistonBase || block instanceof BlockPistonExtension || block instanceof net.minecraft.block.BlockPistonMoving);
    }

    private boolean isSolid(World world, BlockPos pos, EnumFacing side) {
        BlockPos offset = pos.offset(side);
        IBlockState blockState = world.getBlockState(offset);
        Block block = blockState.getBlock();
        if (block == null)
            return false;
        if (block.getMaterial() == Material.air)
            return false;
        if (block instanceof net.minecraft.block.BlockLiquid)
            return ((Schematica.getInstance()).placeOnLiquid && this.minecraft.playerController.isInCreativeMode());
        return !block.isReplaceable(world, offset);
    }

    private List<ScanResult> scanCardinalDirections(BlockPos initial, double blockReachDistance, boolean recursive, EnumFacing... exclude) {
        List<ScanResult> posList = new ArrayList<>();
        for (int i = 0; i <= blockReachDistance; i++) {
            for (EnumFacing facing : MixinEnumFacing.getValues()) {
                if (exclude.length <= 0 || !Arrays.<EnumFacing>stream(exclude).anyMatch(f -> (f == facing))) {
                    Vec3i multiplied = facing.getDirectionVec();
                    BlockPos vec = new BlockPos(multiplied.getX() * i, multiplied.getY() * i, multiplied.getZ() * i);
                    posList.add(new ScanResult(facing, initial.add((Vec3i) vec), recursive));
                }
            }
        }
        if (!recursive) {
            posList.addAll(scanCardinalDirections(initial.north(), blockReachDistance - 1.0D, true, new EnumFacing[]{EnumFacing.NORTH, EnumFacing.SOUTH}));
            posList.addAll(scanCardinalDirections(initial.south(), blockReachDistance - 1.0D, true, new EnumFacing[]{EnumFacing.NORTH, EnumFacing.SOUTH}));
            posList.addAll(scanCardinalDirections(initial.east(), blockReachDistance - 1.0D, true, new EnumFacing[]{EnumFacing.EAST, EnumFacing.WEST}));
            posList.addAll(scanCardinalDirections(initial.west(), blockReachDistance - 1.0D, true, new EnumFacing[]{EnumFacing.EAST, EnumFacing.WEST}));
            posList.addAll(scanCardinalDirections(initial.up(), blockReachDistance - 1.0D, true, new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN}));
            posList.addAll(scanCardinalDirections(initial.down(), blockReachDistance - 1.0D, true, new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN}));
        }
        return posList;
    }

    private BridgeLocation findNearestBlock(World world, BlockPos schPos) {
        double dX = ClientProxy.playerPosition.x - this.schematic.position.x;
        double dY = ClientProxy.playerPosition.y - this.schematic.position.y;
        double dZ = ClientProxy.playerPosition.z - this.schematic.position.z;
        double blockReachDistance = this.minecraft.playerController.getBlockReachDistance() - 0.1D;
        double blockReachDistanceSq = blockReachDistance * blockReachDistance;
        for (ScanResult scan : scanCardinalDirections(schPos, blockReachDistance, false, new EnumFacing[0])) {
            BlockPos other = scan.getPos();
            if (other.distanceSq(dX, dY, dZ) > blockReachDistanceSq)
                continue;
            int wx = this.schematic.position.x + other.getX();
            int wy = this.schematic.position.y + other.getY();
            int wz = this.schematic.position.z + other.getZ();
            BlockPos realPos = new BlockPos(wx, wy, wz);
            List<EnumFacing> solidSides = getSolidSides(world, realPos);
            if (solidSides.size() > 0)
                return new BridgeLocation(scan
                        .getFacing().getOpposite(), other, realPos, schPos

                        .add((Vec3i) this.schematic.position));
        }
        return null;
    }

    private List<EnumFacing> getSolidSides(World world, BlockPos pos) {
        List<EnumFacing> list = new ArrayList<>();
        for (EnumFacing side : MixinEnumFacing.getValues()) {
            if (isSolid(world, pos, side))
                list.add(side);
        }
        return list;
    }

    private boolean placeBlock(WorldClient world, EntityPlayerSP player, BlockPos schPos, BlockPos pos, IBlockState blockState, ItemStack itemStack, boolean isBridge) {
        EnumFacing direction;
        float offsetX, offsetY, offsetZ;
        int extraClicks;
        if (itemStack.getItem() instanceof net.minecraft.item.ItemBucket)
            return false;
        PlacementData data = PlacementRegistry.INSTANCE.getPlacementData(blockState, itemStack);
        if (data != null && !data.isValidPlayerFacing(blockState, (EntityPlayer) player, pos, (World) world))
            return false;
        List<EnumFacing> solidSides = getSolidSides((World) world, pos);
        if (solidSides.size() == 0) {
            int x = schPos.getX(), y = schPos.getY(), z = schPos.getZ();
            this.attempts[x][y][z] = (byte) (this.attempts[x][y][z] + 1);
            if ((Schematica.getInstance()).autoBridge && !isBridge && this.activeBridge == null && !this.requiresSolidFloor.contains(blockState.getBlock().getClass()) && this.attempts[x][y][z] > 2) {
                this.attempts[x][y][z] = 0;
                BridgeLocation nearest = findNearestBlock((World) world, schPos);
                if (nearest != null) {
                    this.activeBridge = nearest;
                    this.activeBridge.setState(blockState);
                    this.activeBridge.setStack(itemStack);
                }
            }
            return false;
        }
        if (world.getBlockState(pos.down()).getBlock().isReplaceable((World) world, pos.down()) && this.requiresSolidFloor.contains(blockState.getBlock().getClass()))
            return false;
        if (data != null) {
            List<EnumFacing> validDirections = data.getValidBlockFacings(solidSides, blockState);
            if (validDirections.size() == 0)
                return false;
            direction = validDirections.get(0);
            offsetX = data.getOffsetX(blockState);
            offsetY = data.getOffsetY(blockState);
            offsetZ = data.getOffsetZ(blockState);
            extraClicks = data.getExtraClicks(blockState);
        } else {
            direction = solidSides.get(0);
            offsetX = 0.5F;
            offsetY = 0.5F;
            offsetZ = 0.5F;
            extraClicks = 0;
        }
        return (swapToItem(player.inventory, itemStack) && placeBlock(world, player, pos, direction, offsetX, offsetY, offsetZ, extraClicks));
    }

    private boolean placeBlock(WorldClient world, EntityPlayerSP player, BlockPos pos, EnumFacing direction, float offsetX, float offsetY, float offsetZ, int extraClicks) {
        ItemStack itemStack = player.getCurrentEquippedItem();
        if (!this.minecraft.playerController.isInCreativeMode() && itemStack != null && itemStack.stackSize <= extraClicks)
            return false;
        BlockPos offset = pos.offset(direction);
        EnumFacing side = direction.getOpposite();
        Vec3 hitVec = new Vec3((offset.getX() + offsetX), (offset.getY() + offsetY), (offset.getZ() + offsetZ));
        boolean success = placeBlock(world, player, itemStack, offset, side, hitVec);
        for (int i = 0; success && i < extraClicks; i++)
            success = placeBlock(world, player, itemStack, offset, side, hitVec);
        if (itemStack != null && itemStack.stackSize == 0 && success)
            player.inventory.mainInventory[player.inventory.currentItem] = null;
        return success;
    }

    private boolean placeBlock(WorldClient world, EntityPlayerSP player, ItemStack itemStack, BlockPos pos, EnumFacing side, Vec3 hitVec) {
        boolean success = this.minecraft.playerController.onPlayerRightClick(player, world, itemStack, pos, side, hitVec);
        if (success) {
            if (this.packetPlayerLook != null) {
                player.sendQueue.addToSendQueue((Packet) this.packetPlayerLook);
                this.packetPlayerLook = null;
            }
            player.swingItem();
        }
        return success;
    }

    public void setRotation(double target) {
        EntityPlayerSP player = (Minecraft.getMinecraft()).thePlayer;
        double angle = MathHelper.wrapAngleTo180_double(player.rotationYaw);
        double clamped = target - angle;
        clamped += (clamped > 180.0D) ? -360.0D : ((clamped < -180.0D) ? 360.0D : 0.0D);
        player.rotationYaw = (float) (player.rotationYaw + clamped);
    }

    private void syncSneaking(EntityPlayerSP player, boolean isSneaking) {
        player.setSneaking(isSneaking);
        player.sendQueue.addToSendQueue((Packet) new C0BPacketEntityAction((Entity) player, isSneaking ? C0BPacketEntityAction.Action.START_SNEAKING : C0BPacketEntityAction.Action.STOP_SNEAKING));
    }

    private boolean swapToItem(InventoryPlayer inventory, ItemStack itemStack) {
        return swapToItem(inventory, itemStack, true);
    }

    private boolean swapToItem(InventoryPlayer inventory, ItemStack itemStack, boolean swapSlots) {
        int slot = getInventorySlotWithItem(inventory, itemStack);
        if (this.minecraft.playerController.isInCreativeMode() && (slot < 0 || slot >= 9) && ConfigurationHandler.swapSlotsQueue.size() > 0) {
            inventory.currentItem = getNextSlot();
            inventory.setInventorySlotContents(inventory.currentItem, itemStack.copy());
            this.minecraft.playerController.sendSlotPacket(inventory.getStackInSlot(inventory.currentItem), 36 + inventory.currentItem);
            return true;
        }
        if (slot >= 0 && slot < 9) {
            inventory.currentItem = slot;
            return true;
        }
        if (swapSlots && slot >= 9 && slot < 36 &&
                swapSlots(inventory, slot))
            return swapToItem(inventory, itemStack, false);
        return false;
    }

    private int getInventorySlotWithItem(InventoryPlayer inventory, ItemStack itemStack) {
        for (int i = 0; i < inventory.mainInventory.length; i++) {
            if (inventory.mainInventory[i] != null && inventory.mainInventory[i].isItemEqual(itemStack))
                return i;
        }
        return -1;
    }

    private boolean swapSlots(InventoryPlayer inventory, int from) {
        if (ConfigurationHandler.swapSlotsQueue.size() > 0) {
            int slot = getNextSlot();
            swapSlots(from, slot);
            return true;
        }
        return false;
    }

    private int getNextSlot() {
        int slot = ((Integer) ConfigurationHandler.swapSlotsQueue.poll()).intValue() % 9;
        ConfigurationHandler.swapSlotsQueue.offer(Integer.valueOf(slot));
        return slot;
    }

    private boolean swapSlots(int from, int to) {
        return (this.minecraft.playerController.windowClick(this.minecraft.thePlayer.inventoryContainer.windowId, from, to, 2, (EntityPlayer) this.minecraft.thePlayer) == null);
    }

    private static class ScanResult {
        private final EnumFacing facing;

        private final BlockPos pos;

        private final boolean isSurroundingBlock;

        public ScanResult(EnumFacing facing, BlockPos pos, boolean isSurroundingBlock) {
            this.facing = facing;
            this.pos = pos;
            this.isSurroundingBlock = isSurroundingBlock;
        }

        public boolean equals(Object o) {
            if (o == this)
                return true;
            if (!(o instanceof ScanResult))
                return false;
            ScanResult other = (ScanResult) o;
            if (!other.canEqual(this))
                return false;
            if (isSurroundingBlock() != other.isSurroundingBlock())
                return false;
            Object this$facing = getFacing(), other$facing = other.getFacing();
            if ((this$facing == null) ? (other$facing != null) : !this$facing.equals(other$facing))
                return false;
            Object this$pos = getPos(), other$pos = other.getPos();
            return !((this$pos == null) ? (other$pos != null) : !this$pos.equals(other$pos));
        }

        protected boolean canEqual(Object other) {
            return other instanceof ScanResult;
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            result = result * 59 + (isSurroundingBlock() ? 79 : 97);
            Object $facing = getFacing();
            result = result * 59 + (($facing == null) ? 43 : $facing.hashCode());
            Object $pos = getPos();
            return result * 59 + (($pos == null) ? 43 : $pos.hashCode());
        }

        public String toString() {
            return "SchematicPrinter.ScanResult(facing=" + getFacing() + ", pos=" + getPos() + ", isSurroundingBlock=" + isSurroundingBlock() + ")";
        }

        public EnumFacing getFacing() {
            return this.facing;
        }

        public BlockPos getPos() {
            return this.pos;
        }

        public boolean isSurroundingBlock() {
            return this.isSurroundingBlock;
        }
    }

    private static class BridgeLocation {
        private final EnumFacing facing;

        private BlockPos schemPos;

        private BlockPos realPos;

        private BlockPos targetPos;

        public void setSchemPos(BlockPos schemPos) {
            this.schemPos = schemPos;
        }

        public void setRealPos(BlockPos realPos) {
            this.realPos = realPos;
        }

        public void setTargetPos(BlockPos targetPos) {
            this.targetPos = targetPos;
        }

        public void setState(IBlockState state) {
            this.state = state;
        }

        public void setStack(ItemStack stack) {
            this.stack = stack;
        }

        public void setPlaceAttempts(int placeAttempts) {
            this.placeAttempts = placeAttempts;
        }

        public void setLastAttempt(boolean lastAttempt) {
            this.lastAttempt = lastAttempt;
        }

        public boolean equals(Object o) {
            if (o == this)
                return true;
            if (!(o instanceof BridgeLocation))
                return false;
            BridgeLocation other = (BridgeLocation) o;
            if (!other.canEqual(this))
                return false;
            if (getPlaceAttempts() != other.getPlaceAttempts())
                return false;
            if (isLastAttempt() != other.isLastAttempt())
                return false;
            Object this$facing = getFacing(), other$facing = other.getFacing();
            if ((this$facing == null) ? (other$facing != null) : !this$facing.equals(other$facing))
                return false;
            Object this$schemPos = getSchemPos(), other$schemPos = other.getSchemPos();
            if ((this$schemPos == null) ? (other$schemPos != null) : !this$schemPos.equals(other$schemPos))
                return false;
            Object this$realPos = getRealPos(), other$realPos = other.getRealPos();
            if ((this$realPos == null) ? (other$realPos != null) : !this$realPos.equals(other$realPos))
                return false;
            Object this$targetPos = getTargetPos(), other$targetPos = other.getTargetPos();
            if ((this$targetPos == null) ? (other$targetPos != null) : !this$targetPos.equals(other$targetPos))
                return false;
            Object this$state = getState(), other$state = other.getState();
            if ((this$state == null) ? (other$state != null) : !this$state.equals(other$state))
                return false;
            Object this$stack = getStack(), other$stack = other.getStack();
            return !((this$stack == null) ? (other$stack != null) : !this$stack.equals(other$stack));
        }

        protected boolean canEqual(Object other) {
            return other instanceof BridgeLocation;
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            result = result * 59 + getPlaceAttempts();
            result = result * 59 + (isLastAttempt() ? 79 : 97);
            Object $facing = getFacing();
            result = result * 59 + (($facing == null) ? 43 : $facing.hashCode());
            Object $schemPos = getSchemPos();
            result = result * 59 + (($schemPos == null) ? 43 : $schemPos.hashCode());
            Object $realPos = getRealPos();
            result = result * 59 + (($realPos == null) ? 43 : $realPos.hashCode());
            Object $targetPos = getTargetPos();
            result = result * 59 + (($targetPos == null) ? 43 : $targetPos.hashCode());
            Object $state = getState();
            result = result * 59 + (($state == null) ? 43 : $state.hashCode());
            Object $stack = getStack();
            return result * 59 + (($stack == null) ? 43 : $stack.hashCode());
        }

        public String toString() {
            return "SchematicPrinter.BridgeLocation(facing=" + getFacing() + ", schemPos=" + getSchemPos() + ", realPos=" + getRealPos() + ", targetPos=" + getTargetPos() + ", state=" + getState() + ", stack=" + getStack() + ", placeAttempts=" + getPlaceAttempts() + ", lastAttempt=" + isLastAttempt() + ")";
        }

        public EnumFacing getFacing() {
            return this.facing;
        }

        public BlockPos getSchemPos() {
            return this.schemPos;
        }

        public BlockPos getRealPos() {
            return this.realPos;
        }

        public BlockPos getTargetPos() {
            return this.targetPos;
        }

        private IBlockState state = null;

        public IBlockState getState() {
            return this.state;
        }

        private ItemStack stack = null;

        public ItemStack getStack() {
            return this.stack;
        }

        private int placeAttempts = 0;

        public int getPlaceAttempts() {
            return this.placeAttempts;
        }

        private boolean lastAttempt = false;

        public boolean isLastAttempt() {
            return this.lastAttempt;
        }

        public BridgeLocation(EnumFacing facing, BlockPos schemPos, BlockPos realPos, BlockPos targetPos) {
            this.facing = facing;
            this.schemPos = schemPos;
            this.realPos = realPos;
            this.targetPos = targetPos;
        }

        public boolean isComplete() {
            boolean res = Arrays.<EnumFacing>stream(MixinEnumFacing.getValues()).anyMatch(f -> this.targetPos.offset(f).equals(this.realPos));
            if (res)
                this.lastAttempt = true;
            return res;
        }

        public void incPositions() {
            this.schemPos = this.schemPos.add(this.facing.getDirectionVec());
            this.realPos = this.realPos.add(this.facing.getDirectionVec());
        }

        public int incAttempts() {
            return this.placeAttempts++;
        }

        public boolean isInRange(double dX, double dY, double dZ, double maxDistanceSq) {
            return (this.schemPos.distanceSqToCenter(dX, dY, dZ) < maxDistanceSq);
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\printer\SchematicPrinter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */