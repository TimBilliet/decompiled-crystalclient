package co.crystaldev.client.mixin.net.minecraft.util;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin({BlockPos.class})
public abstract class MixinBlockPos extends Vec3i {
    public MixinBlockPos(int xIn, int yIn, int zIn) {
        super(xIn, yIn, zIn);
    }

    @Overwrite
    public BlockPos up() {
        return new BlockPos(getX(), getY() + 1, getZ());
    }

    @Overwrite
    public BlockPos up(int offset) {
        return (offset == 0) ? (BlockPos) (Object) this : new BlockPos(getX(), getY() + offset, getZ());
    }

    @Overwrite
    public BlockPos down() {
        return new BlockPos(getX(), getY() - 1, getZ());
    }

    @Overwrite
    public BlockPos down(int offset) {
        return (offset == 0) ? (BlockPos) (Object) this : new BlockPos(getX(), getY() - offset, getZ());
    }

    @Overwrite
    public BlockPos north() {
        return new BlockPos(getX(), getY(), getZ() - 1);
    }

    @Overwrite
    public BlockPos north(int offset) {
        return (offset == 0) ? (BlockPos) (Object) this : new BlockPos(getX(), getY(), getZ() - offset);
    }

    @Overwrite
    public BlockPos south() {
        return new BlockPos(getX(), getY(), getZ() + 1);
    }

    @Overwrite
    public BlockPos south(int offset) {
        return (offset == 0) ? (BlockPos) (Object) this : new BlockPos(getX(), getY(), getZ() + offset);
    }

    @Overwrite
    public BlockPos west() {
        return new BlockPos(getX() - 1, getY(), getZ());
    }

    @Overwrite
    public BlockPos west(int offset) {
        return (offset == 0) ? (BlockPos) (Object) this : new BlockPos(getX() - offset, getY(), getZ());
    }

    @Overwrite
    public BlockPos east() {
        return new BlockPos(getX() + 1, getY(), getZ());
    }

    @Overwrite
    public BlockPos east(int offset) {
        return (offset == 0) ? (BlockPos) (Object) this : new BlockPos(getX() + offset, getY(), getZ());
    }

    @Overwrite
    public BlockPos offset(EnumFacing direction) {
        return new BlockPos(getX() + direction.getFrontOffsetX(), getY() + direction.getFrontOffsetY(), getZ() + direction.getFrontOffsetZ());
    }
}

