package co.crystaldev.client;

import co.crystaldev.client.command.*;
import co.crystaldev.client.cosmetic.CosmeticManager;
import co.crystaldev.client.cosmetic.type.cloak.LayerCloak;
import co.crystaldev.client.cosmetic.type.wings.LayerWings;
import co.crystaldev.client.duck.NetworkPlayerInfoExt;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.impl.init.InitializationEvent;
import co.crystaldev.client.handler.*;
import co.crystaldev.client.mixin.accessor.net.minecraft.client.entity.MixinAbstractClientPlayer;
import co.crystaldev.client.mixin.accessor.net.minecraft.client.renderer.entity.MixinRenderManager;
import co.crystaldev.client.mixin.accessor.net.minecraft.client.renderer.entity.MixinRendererLivingEntity;
import co.crystaldev.client.network.plugin.impl.ClientApiHandler;
import co.crystaldev.client.network.plugin.impl.HandshakeHandler;
import co.crystaldev.client.network.plugin.impl.ModuleApiHandler;
import co.crystaldev.client.network.plugin.impl.WorldEditCuiHandler;
import co.crystaldev.client.util.Log4jPatch;
import com.google.common.collect.ImmutableMap;
import mchorse.emoticons.Emoticons;
import net.minecraft.client.Minecraft;
import com.google.common.collect.ImmutableMap.Builder;
import java.io.File;
import java.net.URI;
import java.util.UUID;

import co.crystaldev.client.account.AltManager;
import co.crystaldev.client.command.CrashCommand;
import co.crystaldev.client.command.DupeCommand;
import co.crystaldev.client.command.FindSandCommand;
import co.crystaldev.client.command.FlyboostCommand;
import co.crystaldev.client.command.GroupCommand;
import co.crystaldev.client.command.ProfileCommand;
import co.crystaldev.client.command.SchemshareCommand;
import co.crystaldev.client.command.SudoCommand;
import co.crystaldev.client.command.ThumbnailCommand;
import co.crystaldev.client.command.WaypointsCommand;
import co.crystaldev.client.feature.settings.ClientOptions;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.handler.MacroHandler;
import co.crystaldev.client.mixin.accessor.net.minecraft.client.MixinMinecraft;
import co.crystaldev.client.network.Packet;
import co.crystaldev.client.network.WebClient;
import co.crystaldev.client.network.plugin.ChannelRegistry;
import co.crystaldev.client.network.socket.NetHandlerClient;
import co.crystaldev.client.util.ClientTextureManager;
import co.crystaldev.client.util.enums.ChatColor;
import co.crystaldev.client.util.enums.MinecraftVersion;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Timer;
import org.apache.commons.lang3.ArrayUtils;

public class Client {
    private static Client INSTANCE;

    private static File CLIENT_RUN_DIRECTORY;

    private static final boolean OPTIFINE_LOADED = isClassLoaded("optifine.OptiFineClassTransformer");

    private static final boolean OBFUSCATED = !isClassLoaded(String.format("%s.%s.%s.%s", "co", "crystaldev", "client", "Client"));

    private static String currentServerIp = null;

    private static String currentWorld = null;

    private static Timer timer;

    public static void setCurrentServerIp(String currentServerIp) {
        Client.currentServerIp = currentServerIp;
    }

    public static void setCurrentWorld(String currentWorld) {
        Client.currentWorld = currentWorld;
    }

    public static Timer getTimer() {
        return timer;
    }

    public static void setTimer(Timer timer) {
        Client.timer = timer;
    }

    private static float lastBrightnessX = 0.0F;

    private static float lastBrightnessY = 0.0F;

    public static float getLastBrightnessX() {
        return lastBrightnessX;
    }

    public static float getLastBrightnessY() {
        return lastBrightnessY;
    }

    public static void setLastBrightnessX(float lastBrightnessX) {
        Client.lastBrightnessX = lastBrightnessX;
    }

    public static void setLastBrightnessY(float lastBrightnessY) {
        Client.lastBrightnessY = lastBrightnessY;
    }

    private static long lastHitTime = 0L;

    private final Minecraft mc;

    private final ClientOptions options;

    private final ClientTextureManager textureManager;

    private final AltManager accountManager;

    private final Emoticons emoticons;

    private final Config config;

    private final ModuleHandler moduleHandler;

    private final PlayerHandler playerHandler;

    private final OverlayHandler overlayHandler;

    private final GroupHandler groupHandler;

    private final MacroHandler macroHandler;

    private final ProfileHandler profileHandler;

    private final WaypointHandler waypointHandler;

    private final ClientCommandHandler commandHandler;

    public static long getLastHitTime() {
        return lastHitTime;
    }

    public static void setLastHitTime(long lastHitTime) {
        Client.lastHitTime = lastHitTime;
    }

    public ClientOptions getOptions() {
        return this.options;
    }

    public ClientTextureManager getTextureManager() {
        return this.textureManager;
    }

    public AltManager getAccountManager() {
        return this.accountManager;
    }

    public Emoticons getEmoticons() {
        return this.emoticons;
    }

    public Config getConfig() {
        return this.config;
    }

    public ModuleHandler getModuleHandler() {
        return this.moduleHandler;
    }

    public PlayerHandler getPlayerHandler() {
        return this.playerHandler;
    }

    public OverlayHandler getOverlayHandler() {
        return this.overlayHandler;
    }

    public GroupHandler getGroupHandler() {
        return this.groupHandler;
    }

    public MacroHandler getMacroHandler() {
        return this.macroHandler;
    }

    public ProfileHandler getProfileHandler() {
        return this.profileHandler;
    }

    public WaypointHandler getWaypointHandler() {
        return this.waypointHandler;
    }

    public ClientCommandHandler getCommandHandler() {
        return this.commandHandler;
    }

    private final AtomicInteger entityCounter = new AtomicInteger();

    public AtomicInteger getEntityCounter() {
        return this.entityCounter;
    }

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private final ScheduledExecutorService cacheExecutor = Executors.newSingleThreadScheduledExecutor();

    private WebClient webClient;

    public ScheduledExecutorService getExecutor() {
        return this.executor;
    }

    public ScheduledExecutorService getCacheExecutor() {
        return this.cacheExecutor;
    }

    public WebClient getWebClient() {
        return this.webClient;
    }

    private UUID currentUuid = null;

    public void setCurrentUuid(UUID currentUuid) {
        this.currentUuid = currentUuid;
    }

    public Client() {
        INSTANCE = this;
        CLIENT_RUN_DIRECTORY = new File((this.mc = Minecraft.getMinecraft()).mcDataDir, "crystalclient");
        Reference.LOGGER.info("{} version {} is being initialized.", "Crystal Client", "1.1.16-projectassfucker");
        SplashScreen.setProgress(1, "Registering Modules...");
        EventBus.register(this.moduleHandler = new ModuleHandler());
        SplashScreen.setProgress(2, "Registering Handlers...");
        EventBus.register(this.playerHandler = new PlayerHandler());
        EventBus.register(this.overlayHandler = new OverlayHandler());
        EventBus.register(this.groupHandler = new GroupHandler());
        EventBus.register(this.macroHandler = new MacroHandler());
        EventBus.register(this.profileHandler = new ProfileHandler());
        EventBus.register(this.waypointHandler = new WaypointHandler());
        EventBus.register(this.commandHandler = ClientCommandHandler.getInstance());
        SplashScreen.setProgress(3, "Registering Emoticons...");
        this.emoticons = new Emoticons();
        SplashScreen.setProgress(4, "Registering Cosmetics...");
        EventBus.register(CosmeticManager.getInstance());
        SplashScreen.setProgress(5, "Loading Configurations...");
        this.config = new Config(new File(new File(CLIENT_RUN_DIRECTORY, "config"), "mods"));
        SplashScreen.setProgress(6, "Registering Loading...");
        Fonts.initialize();
        SplashScreen.setProgress(7, "Initializing Texture Manager...");
        this.textureManager = new ClientTextureManager(this.mc.getResourceManager());
        SplashScreen.setProgress(8, "Populating Account Manager...");
        this.accountManager = new AltManager();
        SplashScreen.setProgress(9, "Finishing...");
        this.options = ClientOptions.getInstance();
        this.moduleHandler.registerAll();
        this.mc.gameSettings.loadOptions();
        EventBus.register(new ChannelRegistry());
        ChannelRegistry.getInstance().newChannel("CC|Init", new HandshakeHandler());
        ChannelRegistry.getInstance().newChannel("CC|Module", new ModuleApiHandler());
        ChannelRegistry.getInstance().newChannel("CC|API", new ClientApiHandler());
        ChannelRegistry.getInstance().newChannel("WECUI", new WorldEditCuiHandler());
        this.commandHandler.registerCommand(new GroupCommand());
        this.commandHandler.registerCommand(new FlyboostCommand());
        this.commandHandler.registerCommand(new SudoCommand());
        this.commandHandler.registerCommand(new FindSandCommand());
        this.commandHandler.registerCommand(new SchemshareCommand());
        this.commandHandler.registerCommand(new AdjustHelperCommand());
        this.commandHandler.registerCommand(new ProfileCommand());
        this.commandHandler.registerCommand(new WaypointsCommand());
        this.commandHandler.registerCommand(new CrashCommand());
        this.commandHandler.registerCommand(new DupeCommand());
        this.commandHandler.registerCommand(new FloatFinderCommand());
        if (!OBFUSCATED) {
            this.commandHandler.registerCommand(new ThumbnailCommand());
            Log4jPatch.patchLogger();
        }
        for (RenderPlayer render : ((MixinRenderManager) Minecraft.getMinecraft().getRenderManager()).getSkinMap().values()) {
            ((MixinRendererLivingEntity) render).callAddLayer(new LayerCloak());
            ((MixinRendererLivingEntity) render).callAddLayer(new LayerWings());
        }
        Reference.LOGGER.info("{} version {} has been initialized.", "Crystal Client", "1.1.16-projectassfucker");
        (new InitializationEvent(this)).call();
        SplashScreen.markComplete();
        while (!SplashScreen.isComplete())
            SplashScreen.renderSplash(this.mc.getTextureManager());
    }

    public void connectToSocket(boolean blocking) {
        if (this.webClient != null && !this.webClient.isClosed())
            try {
                this.webClient.closeBlocking();
            } catch (InterruptedException ex) {
                Reference.LOGGER.error("Unable to initiate closing handshake", ex);
            }
        ImmutableMap immutableMap = (new Builder()).put("playerId", Minecraft.getMinecraft().getSession().getProfile().getId().toString()).put("username", Minecraft.getMinecraft().getSession().getUsername()).put("client", String.format("%s-v%s-%s/%s", "crystalclient", "1.1.12", "cbd77ac", "main")).put("clientVersion", "1.1.12").put("gitCommitId", "cbd77ac1ae06d9986b554a7a9709972587397126").put("gitCommitIdAbbr", "cbd77ac").put("gitBranch", "main").build();

        try {
            this.webClient = new WebClient(new URI("ws://websocket.crystalclient.net:25565"), immutableMap);
            if (blocking) {
                this.webClient.connectBlocking();
            } else {
                this.webClient.connect();
            }
        } catch (Throwable ex) {
            Reference.LOGGER.error("Unable to connect to WebSocket", ex);
        }
    }

    public NetHandlerClient getNetHandler() {
        if (getWebClient() == null || getWebClient().isClosed())
            connectToSocket(true);
        return getWebClient().getHandler();
    }

    public static void sendPacket(Packet packet) {
//        if(AltManager.isLoggedIn()) {
           getInstance().getNetHandler().sendPacket(packet);
//        }
    }

    public static void registerKeyBinding(KeyBinding key) {
        (Minecraft.getMinecraft()).gameSettings.keyBindings = (KeyBinding[]) ArrayUtils.add((Object[]) (Minecraft.getMinecraft()).gameSettings.keyBindings, key);
    }

    public static void sendErrorMessage(String errorMessage, boolean appendPrefix) {
        Client instance = getInstance();
        if (instance.mc.thePlayer != null) {
            errorMessage = ChatColor.translate((appendPrefix ? (getErrorPrefix() + " ") : "") + errorMessage);
            instance.mc.thePlayer.addChatMessage(new ChatComponentText(errorMessage));
        }
    }

    public static void sendErrorMessage(Exception ex, boolean appendPrefix) {
        sendErrorMessage(ex.getMessage(), appendPrefix);
    }

    public static void sendMessage(String message, boolean appendPrefix) {
        Client instance = getInstance();
        if (instance.mc.thePlayer != null) {
            message = ChatColor.translate((appendPrefix ? (getPrefix() + " ") : "") + message);
            instance.mc.thePlayer.addChatMessage(new ChatComponentText(message));
        }
    }

    public static String getPrefix() {
        return ChatColor.translate(String.format("&8<&b&l%s&8>&r", "Crystal Client"));
    }

    public static String getErrorPrefix() {
        return ChatColor.translate(String.format("&8<&c&l%s&8>&r", "Crystal Client"));
    }

    public static Thread getMainThread() {
        return ((MixinMinecraft) Minecraft.getMinecraft()).getMcThread();
    }

    public static boolean isCallingFromMainThread() {
        return Minecraft.getMinecraft().isCallingFromMinecraftThread();
    }

    public static boolean isOnHypixel() {
        String server = formatConnectedServerIp();
        return (server != null && server.toLowerCase().endsWith("hypixel.net"));
    }

    public static String formatConnectedServerIp() {
        return formatConnectedServerIp(true);
    }

    public static String formatConnectedServerIp(boolean useDns) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld != null && mc.isSingleplayer())
            return "singleplayer";
        if (useDns && currentServerIp != null)
            return currentServerIp;
        ServerData data = (mc.theWorld == null) ? null : Minecraft.getMinecraft().getCurrentServerData();
        return (data == null || data.serverIP == null) ? null : data.serverIP.toLowerCase();
    }

    public static String getCurrentWorldName() {
        if (currentWorld != null)
            return currentWorld;
        Minecraft mc = INSTANCE.mc;
        if (mc.theWorld == null)
            return "unknown_world";
        switch (mc.theWorld.provider.getDimensionId()) {
            case -1:
                return "unknown_world<nether>";
            case 1:
                return "unknown_world<end>";
        }
        return "unknown_world";
    }

    public static MinecraftVersion getMinecraftVersion() {
        return MinecraftVersion.V1_8_9;
    }

    public static File getClientRunDirectory() {
        if (!CLIENT_RUN_DIRECTORY.exists())
            CLIENT_RUN_DIRECTORY.mkdirs();
        return CLIENT_RUN_DIRECTORY;
    }

    public static boolean isOnCrystalClient(Entity entity) {
        NetworkPlayerInfo info = (entity instanceof net.minecraft.client.entity.AbstractClientPlayer) ? ((MixinAbstractClientPlayer) entity).invokeGetPlayerInfo() : null;
        return (info != null && ((NetworkPlayerInfoExt) info).isOnCrystalClient());
    }

    public static boolean isOnOrbitClient(Entity entity) {
        NetworkPlayerInfo info = (entity instanceof net.minecraft.client.entity.AbstractClientPlayer) ? ((MixinAbstractClientPlayer) entity).invokeGetPlayerInfo() : null;
        return (info != null && ((NetworkPlayerInfoExt) info).isOnOrbitClient());
    }

    public static UUID getUniqueID() {
        Client instance = getInstance();
        return (instance.currentUuid != null) ? instance.currentUuid : ((instance.mc.thePlayer != null) ? instance.mc.thePlayer.getUniqueID() : UUID.randomUUID());
    }

    private static boolean isClassLoaded(String classpath) {
        try {
            Class.forName(classpath);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isOptiFineLoaded() {
        return OPTIFINE_LOADED;
    }

    public static boolean isObfuscated() {
        return OBFUSCATED;
    }

    public static Client getInstance() {
        return INSTANCE;
    }
}

