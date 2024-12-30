package co.crystaldev.client.cache;

import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class SandCache {
    private static final Minecraft mc = Minecraft.getMinecraft();

    private final Map<Integer, Boolean> checkedBlocks = new HashMap<>();

    public boolean isBlockSand(BlockPos pos) {
        if (mc.theWorld == null) {
            if (this.checkedBlocks.size() > 0)
                this.checkedBlocks.clear();
            return false;
        }
        int hash = pos.hashCode();
        return this.checkedBlocks.computeIfAbsent(hash, integer -> mc.theWorld.getBlockState(pos).getBlock() instanceof net.minecraft.block.BlockFalling);
    }

    public void clean() {
        this.checkedBlocks.clear();
    }
}