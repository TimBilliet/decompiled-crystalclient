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