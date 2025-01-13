package co.crystaldev.client.cosmetic;

import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CosmeticCache {
    private static final CosmeticCache INSTANCE = new CosmeticCache();

    private final Map<UUID, CosmeticPlayer> players = new HashMap<>();

    public CosmeticPlayer fromPlayer(EntityPlayer player) {
        return fromId(player.getUniqueID());
    }

    public CosmeticPlayer fromId(UUID uuid) {
        return this.players.computeIfAbsent(uuid, CosmeticPlayer::new);
    }

    public void clear() {
        this.players.clear();
    }

    public static CosmeticCache getInstance() {
        return INSTANCE;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\cosmetic\CosmeticCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */