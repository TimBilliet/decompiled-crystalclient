package co.crystaldev.client.cache;

import it.unimi.dsi.fastutil.ints.Int2BooleanArrayMap;
import net.minecraft.block.BlockFalling;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;


public class SandCache {
    private static final Minecraft mc = Minecraft.getMinecraft();

    private final Int2BooleanArrayMap checkedBlocks = new Int2BooleanArrayMap();

    public boolean isBlockSand(BlockPos pos) {
        if (mc.theWorld == null) {
            if (this.checkedBlocks.size() > 0)
                this.checkedBlocks.clear();
            return false;
        }
        int hash = pos.hashCode();
        return this.checkedBlocks.computeIfAbsent(hash, integer -> mc.theWorld.getBlockState(pos).getBlock() instanceof BlockFalling);
    }

    public void clean() {
        this.checkedBlocks.clear();
    }
}