package com.github.lunatrius.schematica.client.printer.nbtsync;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import java.util.HashMap;

public class SyncRegistry {
    public static final SyncRegistry INSTANCE = new SyncRegistry();

    private final HashMap<Block, NBTSync> map = new HashMap<>();

    public void register(Block block, NBTSync handler) {
        if (block == null || handler == null)
            return;
        this.map.put(block, handler);
    }

    public NBTSync getHandler(Block block) {
        return this.map.get(block);
    }

    static {
        INSTANCE.register(Blocks.command_block, new NBTSyncCommandBlock());
        INSTANCE.register(Blocks.standing_sign, new NBTSyncSign());
        INSTANCE.register(Blocks.wall_sign, new NBTSyncSign());
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\printer\nbtsync\SyncRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */