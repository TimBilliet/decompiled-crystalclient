package co.crystaldev.client.cosmetic;

import co.crystaldev.client.Client;
import co.crystaldev.client.Reference;
import co.crystaldev.client.SplashScreen;
import co.crystaldev.client.cosmetic.base.Cosmetic;
import co.crystaldev.client.cosmetic.type.Color;
import co.crystaldev.client.cosmetic.type.cloak.Cloak;
import co.crystaldev.client.cosmetic.type.wings.Wings;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.init.SessionUpdateEvent;
import co.crystaldev.client.event.impl.network.ServerConnectEvent;
import co.crystaldev.client.event.impl.tick.ClientTickEvent;
import co.crystaldev.client.util.enums.IconColor;
import co.crystaldev.client.util.type.GlueList;
import mchorse.emoticons.common.emotes.Emotes;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class CosmeticManager implements IRegistrable {
    private static CosmeticManager INSTANCE;

    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public static ScheduledExecutorService getExecutor() {
        return executor;
    }

    public static File getCosmeticsDirectory() {
        return cosmeticsDirectory;
    }

    private static final File cosmeticsDirectory = new File(new File(new File(new File((Minecraft.getMinecraft()).mcDataDir, "crystalclient"), "cosmetics"), "assets"), "crystalclient");

    public static CosmeticResourcePack getResourcePack() {
        return resourcePack;
    }

    private static final CosmeticResourcePack resourcePack = new CosmeticResourcePack(new File(new File((Minecraft.getMinecraft()).mcDataDir, "crystalclient"), "cosmetics"));

    public static boolean wildcardCloaks = false;

    public static boolean wildcardWings = false;

    public static boolean wildcardColors = false;

    public static boolean wildcardEmotes = false;

    public static final Cloak EMPTY_CLOAK = (Cloak) (new Cloak(null)).withType(CosmeticType.CLOAK);

    public static final Wings EMPTY_WINGS = (Wings) (new Wings(null)).withType(CosmeticType.WINGS);

    public static final Color COLOR_WHITE = new Color((new CosmeticEntry()).withName("white").withType(CosmeticType.COLOR));

    private final Map<String, Cosmetic> cosmetics = new ConcurrentHashMap<>();

    private final Map<UUID, ArrayList<Cosmetic>> loadQueue = new ConcurrentHashMap<>();

    private final List<Cosmetic> ownedCosmetics = new GlueList<>();

    private ScheduledFuture<?> statusUpdateFuture;

    public List<Cosmetic> getOwnedCosmetics() {
        return this.ownedCosmetics;
    }

    public CosmeticManager() {
        registerCosmetics();
    }

    private void registerCosmetics() {
        Reference.LOGGER.info("Registering cosmetics...");
        for (IconColor color : IconColor.values())
            addCosmetic((new CosmeticEntry()).withName(color.getFormattedName().toLowerCase()).withType(CosmeticType.COLOR).setHiddenIfUnowned((color == IconColor.CHROMA)));
        for (String emote : Emotes.EMOTES.keySet())
            addCosmetic((new CosmeticEntry()).withName(emote).withType(CosmeticType.EMOTE));
        while (CosmeticDownloader.getInstance().hasNext()) {
            CosmeticDownloader.getInstance().downloadNext();
        }
        SplashScreen.setProgress(SplashScreen.getProgress(), "Registering Cosmetics");
        CosmeticDownloader.getInstance().getDownloaded().stream().filter(c -> !c.getPath().endsWith(".mcmeta")).forEach(this::addCosmetic);
        Reference.LOGGER.info("Cosmetic registration completed!");
    }

    private void addCosmetic(@NotNull CosmeticEntry entry) {
        Cosmetic cosmetic = entry.build();
        if (cosmetic != null)
            this.cosmetics.put(entry.getType().name().toLowerCase() + "_" + entry.getName(), cosmetic);
    }

    public boolean isWildcard(CosmeticType cosmeticType) {
        switch (cosmeticType) {
            case CLOAK:
                if (wildcardCloaks)
                    return true;
            case WINGS:
                if (wildcardWings)
                    return true;
            case COLOR:
                if (wildcardColors)
                    return true;
            case EMOTE:
                if (wildcardEmotes)
                    return true;
                break;
        }
        return false;
    }

    public Cosmetic getCosmetic(String name, CosmeticType type) {
        return this.cosmetics.get(type.getType().toLowerCase() + "_" + name);
    }

    public boolean isOwned(String name, CosmeticType type) {
        return true;
    }

    public ArrayList<Cosmetic> getAllCosmetics() {
        return new ArrayList<>(this.cosmetics.values());
    }

    public void addLoad(UUID id, ArrayList<Cosmetic> cosmetics) {
        this.loadQueue.put(id, cosmetics);
    }

    public void populateOwned(ArrayList<String> names) {
        this.ownedCosmetics.clear();
        for (Cosmetic cosmetic : getInstance().getAllCosmetics()) {
            if (getInstance().isWildcard(cosmetic.getType()) &&
                    !this.ownedCosmetics.contains(cosmetic))
                this.ownedCosmetics.add(cosmetic);
        }
        for (String name : names) {
            Cosmetic cosmetic = this.cosmetics.get(name);
            if (cosmetic != null && !this.ownedCosmetics.contains(cosmetic))
                this.ownedCosmetics.add(cosmetic);
        }
    }

    private void onServerConnect(ServerConnectEvent event) {
        if (this.statusUpdateFuture != null)
            this.statusUpdateFuture.cancel(false);
        this.statusUpdateFuture = Client.getInstance().getExecutor().scheduleAtFixedRate(new CosmeticRefreshTask(), 0L, 30000L, TimeUnit.MILLISECONDS);
    }

    private void onClientTick(ClientTickEvent.Pre event) {
        if (!this.loadQueue.isEmpty()) {
            for (Map.Entry<UUID, ArrayList<Cosmetic>> entry : this.loadQueue.entrySet()) {
                UUID id = entry.getKey();
                ArrayList<Cosmetic> cosmetics = entry.getValue();
                CosmeticPlayer cp = CosmeticCache.getInstance().fromId(id);
                if (!cp.shouldUpdateCosmetic())
                    continue;
                for (Cosmetic cosmetic : cosmetics) {
                    if (cosmetic != null) {
                        if (cosmetic.isUnknown()) {
                            switch (cosmetic.getType()) {
                                case CLOAK:
                                    cp.setCloak(null);
                                    continue;
                                case WINGS:
                                    cp.setWings(null);
                                    continue;
                                case COLOR:
                                    cp.setColor(COLOR_WHITE);
                                    continue;
                            }
                            continue;
                        }
                        switch (cosmetic.getType()) {
                            case CLOAK:
                                cp.setCloak(cosmetic);
                            case WINGS:
                                cp.setWings(cosmetic);
                            case COLOR:
                                cp.setColor(cosmetic);
                        }
                    }
                }
            }
            this.loadQueue.clear();
        }
    }

    public static CosmeticManager getInstance() {
        return (INSTANCE == null) ? (INSTANCE = new CosmeticManager()) : INSTANCE;
    }

    public void registerEvents() {
        EventBus.register(this, ClientTickEvent.Pre.class, this::onClientTick);
        EventBus.register(this, ServerConnectEvent.class, this::onServerConnect);
        EventBus.register(this, SessionUpdateEvent.class, ev -> this.ownedCosmetics.clear());
    }
}