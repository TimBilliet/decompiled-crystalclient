package wdl;

import com.google.common.collect.HashMultimap;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.network.NetworkManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.ThreadedFileIOBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wdl.api.IPlayerInfoEditor;
import wdl.api.ISaveListener;
import wdl.api.IWorldInfoEditor;
import wdl.api.WDLApi;
import wdl.gui.GuiWDLMultiworld;
import wdl.gui.GuiWDLMultiworldSelect;
import wdl.gui.GuiWDLOverwriteChanges;
import wdl.gui.GuiWDLSaveProgress;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class WDL {
    public static final String VERSION = "1.8.9a-beta2";

    public static final String EXPECTED_MINECRAFT_VERSION = "1.8.9";

    public static Minecraft minecraft;

    public static WorldClient worldClient;

    public static NetworkManager networkManager = null;

    public static EntityPlayerSP thePlayer;

    public static Container windowContainer;

    public static BlockPos lastClickedBlock;

    public static Entity lastEntity;

    public static SaveHandler saveHandler;

    public static IChunkLoader chunkLoader;

    public static HashMap<ChunkCoordIntPair, Map<BlockPos, TileEntity>> newTileEntities = new HashMap<>();

    public static HashMultimap<ChunkCoordIntPair, Entity> newEntities = HashMultimap.create();

    public static HashMap<Integer, MapData> newMapDatas = new HashMap<>();

    public static boolean downloading = false;

    public static boolean isMultiworld = false;

    public static boolean propsFound = false;

    public static boolean startOnChange = false;

    public static boolean overrideLastModifiedCheck = false;

    public static boolean saving = false;

    public static boolean worldLoadingDeferred = false;

    public static String worldName = "WorldDownloaderERROR";

    public static String baseFolderName = "WorldDownloaderERROR";

    public static Properties baseProps;

    public static Properties worldProps;

    public static final Properties globalProps;

    public static final Properties defaultProps;

    private static final Logger logger = LogManager.getLogger();

    private static final int ANVIL_SAVE_VERSION = 19133;

    static {
        minecraft = Minecraft.getMinecraft();
        defaultProps = new Properties();
        defaultProps.setProperty("ServerName", "");
        defaultProps.setProperty("WorldName", "");
        defaultProps.setProperty("LinkedWorlds", "");
        defaultProps.setProperty("Backup", "ZIP");
        defaultProps.setProperty("AllowCheats", "true");
        defaultProps.setProperty("GameType", "keep");
        defaultProps.setProperty("Time", "keep");
        defaultProps.setProperty("Weather", "keep");
        defaultProps.setProperty("MapFeatures", "false");
        defaultProps.setProperty("RandomSeed", "");
        defaultProps.setProperty("MapGenerator", "void");
        defaultProps.setProperty("GeneratorName", "flat");
        defaultProps.setProperty("GeneratorVersion", "0");
        defaultProps.setProperty("GeneratorOptions", ";0");
        defaultProps.setProperty("Spawn", "player");
        defaultProps.setProperty("SpawnX", "8");
        defaultProps.setProperty("SpawnY", "127");
        defaultProps.setProperty("SpawnZ", "8");
        defaultProps.setProperty("PlayerPos", "keep");
        defaultProps.setProperty("PlayerX", "8");
        defaultProps.setProperty("PlayerY", "127");
        defaultProps.setProperty("PlayerZ", "8");
        defaultProps.setProperty("PlayerHealth", "20");
        defaultProps.setProperty("PlayerFood", "20");
        defaultProps.setProperty("Messages.enableAll", "true");
        defaultProps.setProperty("Entity.TrackDistanceMode", "server");
        defaultProps.setProperty("Entity.FireworksRocketEntity.Enabled", "false");
        defaultProps.setProperty("Entity.EnderDragon.Enabled", "false");
        defaultProps.setProperty("Entity.WitherBoss.Enabled", "false");
        defaultProps.setProperty("Entity.PrimedTnt.Enabled", "false");
        defaultProps.setProperty("Entity.null.Enabled", "false");
        defaultProps.setProperty("EntityGroup.Other.Enabled", "true");
        defaultProps.setProperty("EntityGroup.Hostile.Enabled", "true");
        defaultProps.setProperty("EntityGroup.Passive.Enabled", "true");
        defaultProps.setProperty("LastSaved", "-1");
        defaultProps.setProperty("TutorialShown", "false");
        defaultProps.setProperty("UpdateMinecraftVersion", "client");
        defaultProps.setProperty("UpdateAllowBetas", "true");
        globalProps = new Properties(defaultProps);
        FileReader reader = null;
        try {
            reader = new FileReader(new File(minecraft.mcDataDir, "WorldDownloader.txt"));
            globalProps.load(reader);
        } catch (Exception e) {
            logger.debug("Failed to load global properties", e);
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (Exception e) {
                    logger.warn("Failed to close global properties reader", e);
                }
        }
        baseProps = new Properties(globalProps);
        worldProps = new Properties(baseProps);
    }

    public static void startDownload() {
        worldClient = minecraft.theWorld;
        if (isMultiworld && worldName.isEmpty()) {
            minecraft.displayGuiScreen((GuiScreen) new GuiWDLMultiworldSelect(
                    I18n.format("wdl.gui.multiworldSelect.title.startDownload", new Object[0]), new GuiWDLMultiworldSelect.WorldSelectionCallback() {
                public void onWorldSelected(String selectedWorld) {
                    WDL.worldName = selectedWorld;
                    WDL.isMultiworld = true;
                    WDL.propsFound = true;
                    WDL.minecraft.displayGuiScreen(null);
                    WDL.startDownload();
                }

                public void onCancel() {
                    WDL.minecraft.displayGuiScreen(null);
                    WDL.cancelDownload();
                }
            }));
            return;
        }
        if (!propsFound) {
            minecraft.displayGuiScreen((GuiScreen) new GuiWDLMultiworld(new GuiWDLMultiworld.MultiworldCallback() {
                public void onSelect(boolean enableMutliworld) {
                    WDL.isMultiworld = enableMutliworld;
                    if (WDL.isMultiworld) {
                        WDL.minecraft.displayGuiScreen((GuiScreen) new GuiWDLMultiworldSelect(
                                I18n.format("wdl.gui.multiworldSelect.title.startDownload", new Object[0]), new GuiWDLMultiworldSelect.WorldSelectionCallback() {
                            public void onWorldSelected(String selectedWorld) {
                                WDL.worldName = selectedWorld;
                                WDL.isMultiworld = true;
                                WDL.propsFound = true;
                                WDL.minecraft.displayGuiScreen(null);
                                WDL.startDownload();
                            }

                            public void onCancel() {
                                WDL.minecraft.displayGuiScreen(null);
                                WDL.cancelDownload();
                            }
                        }));
                    } else {
                        WDL.baseProps.setProperty("LinkedWorlds", "");
                        WDL.saveProps();
                        WDL.propsFound = true;
                        WDL.minecraft.displayGuiScreen(null);
                        WDL.startDownload();
                    }
                }

                public void onCancel() {
                    WDL.minecraft.displayGuiScreen(null);
                    WDL.cancelDownload();
                }
            }));
            return;
        }
        worldProps = loadWorldProps(worldName);
        saveHandler = (SaveHandler) minecraft.getSaveLoader().getSaveLoader(
                getWorldFolderName(worldName), true);
        FileInputStream worldDat = null;
        try {
            long lastSaved = Long.parseLong(worldProps.getProperty("LastSaved", "-1"));
            worldDat = new FileInputStream(new File(saveHandler.getWorldDirectory(), "level.dat"));
            long lastPlayed = CompressedStreamTools.readCompressed(worldDat).getCompoundTag("Data").getLong("LastPlayed");
            if (!overrideLastModifiedCheck && lastPlayed > lastSaved) {
                minecraft.displayGuiScreen((GuiScreen) new GuiWDLOverwriteChanges(lastSaved, lastPlayed));
                return;
            }
        } catch (Exception e) {
            logger.warn("Error while checking if the map has been played andneeds to be backed up (this is normal if this world has not been saved before): ", e);
        } finally {
            if (worldDat != null)
                try {
                    worldDat.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        minecraft.displayGuiScreen(null);
        minecraft.setIngameFocus();
        chunkLoader = (IChunkLoader) WDLChunkLoader.create(saveHandler, worldClient.provider);
        newTileEntities = new HashMap<>();
        newEntities = HashMultimap.create();
        newMapDatas = new HashMap<>();
        if (baseProps.getProperty("ServerName").isEmpty())
            baseProps.setProperty("ServerName", getServerName());
        startOnChange = true;
        downloading = true;
        WDLMessages.chatMessageTranslated(WDLMessageTypes.INFO, "wdl.messages.generalInfo.downloadStarted", new Object[0]);
    }

    public static void stopDownload() {
        if (downloading) {
            downloading = false;
            startOnChange = false;
            WDLMessages.chatMessageTranslated(WDLMessageTypes.INFO, "wdl.messages.generalInfo.downloadStopped", new Object[0]);
            startSaveThread();
        }
    }

    public static void cancelDownload() {
        boolean wasDownloading = downloading;
        if (wasDownloading) {
            minecraft.getSaveLoader().flushCache();
            saveHandler.flush();
            startOnChange = false;
            saving = false;
            downloading = false;
            worldLoadingDeferred = false;
            WDLMessages.chatMessageTranslated(WDLMessageTypes.INFO, "wdl.messages.generalInfo.downloadCanceled", new Object[0]);
        }
    }

    static void startSaveThread() {
        WDLMessages.chatMessageTranslated(WDLMessageTypes.INFO, "wdl.messages.generalInfo.saveStarted", new Object[0]);
        saving = true;
        Thread thread = new Thread("WDL Save Thread") {
            public void run() {
                try {
                    WDL.saveEverything();
                    WDL.saving = false;
                    WDL.onSaveComplete();
                } catch (Throwable e) {
                    WDL.crashed(e, "World Downloader Mod: Saving world");
                }
            }
        };
        thread.start();
    }

    public static boolean loadWorld() {
        worldName = "";
        worldClient = minecraft.theWorld;
        thePlayer = minecraft.thePlayer;
        windowContainer = thePlayer.openContainer;
        overrideLastModifiedCheck = false;
        NetworkManager newNM = thePlayer.sendQueue.getNetworkManager();
        if (networkManager != newNM) {
            loadBaseProps();
            WDLMessages.onNewServer();
        }
        if (networkManager != newNM) {
            WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_WORLD_LOAD, "wdl.messages.onWorldLoad.differentServer", new Object[0]);
            networkManager = newNM;
            if (isSpigot()) {
                WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_WORLD_LOAD, "wdl.messages.onWorldLoad.spigot", new Object[]{thePlayer
                        .getClientBrand()});
//              .getServerBrand() });
            } else {
                WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_WORLD_LOAD, "wdl.messages.onWorldLoad.vanilla", new Object[]{thePlayer

                        .getClientBrand()});
            }
            startOnChange = false;
            return true;
        }
        WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_WORLD_LOAD, "wdl.messages.onWorldLoad.sameServer", new Object[0]);
        if (isSpigot()) {
            WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_WORLD_LOAD, "wdl.messages.onWorldLoad.spigot", new Object[]{thePlayer

                    .getClientBrand()});
        } else {
            WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_WORLD_LOAD, "wdl.messages.onWorldLoad.vanilla", new Object[]{thePlayer

                    .getClientBrand()});
        }
        if (startOnChange)
            startDownload();
        return false;
    }

    public static void onSaveComplete() {
        minecraft.getSaveLoader().flushCache();
        saveHandler.flush();
        worldClient = null;
        worldLoadingDeferred = false;
        if (downloading) {
            WDLMessages.chatMessageTranslated(WDLMessageTypes.INFO, "wdl.messages.generalInfo.saveComplete.startingAgain", new Object[0]);
            loadWorld();
            return;
        }
        WDLMessages.chatMessageTranslated(WDLMessageTypes.INFO, "wdl.messages.generalInfo.saveComplete.done", new Object[0]);
    }

    public static void saveEverything() throws Exception {
        WorldBackup.WorldBackupType backupType = WorldBackup.WorldBackupType.match(baseProps.getProperty("Backup", "ZIP"));
        final GuiWDLSaveProgress progressScreen = new GuiWDLSaveProgress(I18n.format("wdl.saveProgress.title", new Object[0]), ((backupType != WorldBackup.WorldBackupType.NONE) ? 6 : 5) + WDLApi.getImplementingExtensions(ISaveListener.class).size());
        minecraft.addScheduledTask(new Runnable() {
            public void run() {
                WDL.minecraft.displayGuiScreen((GuiScreen) progressScreen);
            }
        });
        saveProps();
        try {
            saveHandler.checkSessionLock();
        } catch (MinecraftException e) {
            throw new RuntimeException("WorldDownloader: Couldn't get session lock for saving the world!", e);
        }
        NBTTagCompound playerNBT = savePlayer(progressScreen);
        saveWorldInfo(progressScreen, playerNBT);
        saveMapData(progressScreen);
        saveChunks(progressScreen);
        saveProps();
        for (WDLApi.ModInfo<ISaveListener> info : (Iterable<WDLApi.ModInfo<ISaveListener>>) WDLApi.getImplementingExtensions(ISaveListener.class)) {
            progressScreen.startMajorTask(
                    I18n.format("wdl.saveProgress.extension.title", new Object[]{info.getDisplayName()}), 1);
            ((ISaveListener) info.mod).afterChunksSaved(saveHandler.getWorldDirectory());
        }
        try {
            WDLMessages.chatMessageTranslated(WDLMessageTypes.SAVING, "wdl.messages.saving.flushingIO", new Object[0]);
            progressScreen.startMajorTask(
                    I18n.format("wdl.saveProgress.flushingIO.title", new Object[0]), 1);
            progressScreen.setMinorTaskProgress(
                    I18n.format("wdl.saveProgress.flushingIO.subtitle", new Object[0]), 1);
            ThreadedFileIOBase.getThreadedIOInstance().waitForFinish();
        } catch (Exception e) {
            throw new RuntimeException("Threw exception waiting for asynchronous IO to finish. Hmmm.", e);
        }
        if (backupType != WorldBackup.WorldBackupType.NONE) {
            WDLMessages.chatMessageTranslated(WDLMessageTypes.SAVING, "wdl.messages.saving.backingUp", new Object[0]);
            progressScreen.startMajorTask(backupType
                    .getTitle(), 1);
            progressScreen.setMinorTaskProgress(
                    I18n.format("wdl.saveProgress.backingUp.preparing", new Object[0]), 1);
            try {
                WorldBackup.backupWorld(saveHandler.getWorldDirectory(),
                        getWorldFolderName(worldName), backupType, (WorldBackup.IBackupProgressMonitor) progressScreen);
            } catch (IOException e) {
                WDLMessages.chatMessageTranslated(WDLMessageTypes.ERROR, "wdl.messages.generalError.failedToBackUp", new Object[0]);
            }
        }
        progressScreen.setDoneWorking();
    }

    public static NBTTagCompound savePlayer(GuiWDLSaveProgress progressScreen) {
        progressScreen.startMajorTask(
                I18n.format("wdl.saveProgress.playerData.title", new Object[0]), 3 +
                        WDLApi.getImplementingExtensions(IPlayerInfoEditor.class).size());
        WDLMessages.chatMessageTranslated(WDLMessageTypes.SAVING, "wdl.messages.saving.savingPlayer", new Object[0]);
        progressScreen.setMinorTaskProgress(
                I18n.format("wdl.saveProgress.playerData.creatingNBT", new Object[0]), 1);
        NBTTagCompound playerNBT = new NBTTagCompound();
        thePlayer.writeToNBT(playerNBT);
        progressScreen.setMinorTaskProgress(
                I18n.format("wdl.saveProgress.playerData.editingNBT", new Object[0]), 2);
        applyOverridesToPlayer(playerNBT);
        int taskNum = 3;
        for (WDLApi.ModInfo<IPlayerInfoEditor> info : (Iterable<WDLApi.ModInfo<IPlayerInfoEditor>>) WDLApi.getImplementingExtensions(IPlayerInfoEditor.class)) {
            progressScreen.setMinorTaskProgress(
                    I18n.format("wdl.saveProgress.playerData.extension", new Object[]{info.getDisplayName()}), taskNum);
            ((IPlayerInfoEditor) info.mod).editPlayerInfo(thePlayer, saveHandler, playerNBT);
            taskNum++;
        }
        progressScreen.setMinorTaskProgress(
                I18n.format("wdl.saveProgress.playerData.writingNBT", new Object[0]), taskNum);
        FileOutputStream stream = null;
        try {
            File playersDirectory = new File(saveHandler.getWorldDirectory(), "playerdata");
            File playerFileTmp = new File(playersDirectory, thePlayer.getUniqueID().toString() + ".dat.tmp");
            File playerFile = new File(playersDirectory, thePlayer.getUniqueID().toString() + ".dat");
            stream = new FileOutputStream(playerFileTmp);
            CompressedStreamTools.writeCompressed(playerNBT, stream);
            if (playerFile.exists())
                playerFile.delete();
            playerFileTmp.renameTo(playerFile);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't save the player!", e);
        } finally {
            if (stream != null)
                try {
                    stream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }
        WDLMessages.chatMessageTranslated(WDLMessageTypes.SAVING, "wdl.messages.saving.playerSaved", new Object[0]);
        return playerNBT;
    }

    public static void saveWorldInfo(GuiWDLSaveProgress progressScreen, NBTTagCompound playerInfoNBT) {
        progressScreen.startMajorTask(
                I18n.format("wdl.saveProgress.worldMetadata.title", new Object[0]), 3 +
                        WDLApi.getImplementingExtensions(IWorldInfoEditor.class).size());
        WDLMessages.chatMessageTranslated(WDLMessageTypes.SAVING, "wdl.messages.saving.savingWorld", new Object[0]);
        progressScreen.setMinorTaskProgress(
                I18n.format("wdl.saveProgress.worldMetadata.creatingNBT", new Object[0]), 1);
        worldClient.getWorldInfo().setSaveVersion(19133);
        NBTTagCompound worldInfoNBT = worldClient.getWorldInfo().cloneNBTCompound(playerInfoNBT);
        progressScreen.setMinorTaskProgress(
                I18n.format("wdl.saveProgress.worldMetadata.editingNBT", new Object[0]), 2);
        applyOverridesToWorldInfo(worldInfoNBT);
        int taskNum = 3;
        for (WDLApi.ModInfo<IWorldInfoEditor> info : (Iterable<WDLApi.ModInfo<IWorldInfoEditor>>) WDLApi.getImplementingExtensions(IWorldInfoEditor.class)) {
            progressScreen.setMinorTaskProgress(
                    I18n.format("wdl.saveProgress.worldMetadata.extension", new Object[]{info.getDisplayName()}), taskNum);
            ((IWorldInfoEditor) info.mod).editWorldInfo(worldClient, worldClient.getWorldInfo(), saveHandler, worldInfoNBT);
            taskNum++;
        }
        progressScreen.setMinorTaskProgress(
                I18n.format("wdl.saveProgress.worldMetadata.writingNBT", new Object[0]), taskNum);
        File saveDirectory = saveHandler.getWorldDirectory();
        NBTTagCompound dataNBT = new NBTTagCompound();
        dataNBT.setTag("Data", (NBTBase) worldInfoNBT);
        worldProps.setProperty("LastSaved",
                Long.toString(worldInfoNBT.getLong("LastPlayed")));
        FileOutputStream stream = null;
        try {
            File dataFile = new File(saveDirectory, "level.dat_new");
            File dataFileBackup = new File(saveDirectory, "level.dat_old");
            File dataFileOld = new File(saveDirectory, "level.dat");
            stream = new FileOutputStream(dataFile);
            CompressedStreamTools.writeCompressed(dataNBT, stream);
            if (dataFileBackup.exists())
                dataFileBackup.delete();
            dataFileOld.renameTo(dataFileBackup);
            if (dataFileOld.exists())
                dataFileOld.delete();
            dataFile.renameTo(dataFileOld);
            if (dataFile.exists())
                dataFile.delete();
        } catch (Exception e) {
            throw new RuntimeException("Couldn't save the world metadata!", e);
        } finally {
            if (stream != null)
                try {
                    stream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }
        WDLMessages.chatMessageTranslated(WDLMessageTypes.SAVING, "wdl.messages.saving.worldSaved", new Object[0]);
    }

    public static void saveChunks(GuiWDLSaveProgress progressScreen) throws IllegalArgumentException, IllegalAccessException {
        WDLMessages.chatMessageTranslated(WDLMessageTypes.SAVING, "wdl.messages.saving.savingChunks", new Object[0]);
        ChunkProviderClient chunkProvider = (ChunkProviderClient) worldClient.getChunkProvider();
        List<?> chunks = ReflectionUtils.<List>stealAndGetField(chunkProvider, List.class);
        progressScreen.startMajorTask(I18n.format("wdl.saveProgress.chunk.title", new Object[0]), chunks
                .size());
        for (int currentChunk = 0; currentChunk < chunks.size(); currentChunk++) {
            Chunk c = (Chunk) chunks.get(currentChunk);
            if (c != null) {
                progressScreen.setMinorTaskProgress(I18n.format("wdl.saveProgress.chunk.saving", new Object[]{Integer.valueOf(c.xPosition),
                        Integer.valueOf(c.zPosition)}), currentChunk);
                saveChunk(c);
            }
        }
        WDLMessages.chatMessageTranslated(WDLMessageTypes.SAVING, "wdl.messages.saving.chunksSaved", new Object[0]);
    }

    public static void saveChunk(Chunk c) {
        c.setTerrainPopulated(true);
        try {
            chunkLoader.saveChunk((World) worldClient, c);
        } catch (Exception e) {
            WDLMessages.chatMessageTranslated(WDLMessageTypes.ERROR, "wdl.messages.generalError.failedToSaveChunk", new Object[]{Integer.valueOf(c.xPosition), Integer.valueOf(c.zPosition), e});
        }
    }

    public static void loadBaseProps() {
        baseFolderName = getBaseFolderName();
        baseProps = new Properties(globalProps);
        FileReader reader = null;
        try {
            File savesFolder = new File(minecraft.mcDataDir, "saves");
            File baseFolder = new File(savesFolder, baseFolderName);
            reader = new FileReader(new File(baseFolder, "WorldDownloader.txt"));
            baseProps.load(reader);
            propsFound = true;
        } catch (Exception e) {
            propsFound = false;
            logger.debug("Failed to load base properties", e);
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (Exception e) {
                    logger.warn("Failed to close base properties reader", e);
                }
        }
        if (baseProps.getProperty("LinkedWorlds").isEmpty()) {
            isMultiworld = false;
            worldProps = new Properties(baseProps);
        } else {
            isMultiworld = true;
        }
    }

    public static Properties loadWorldProps(String theWorldName) {
        Properties ret = new Properties(baseProps);
        if (theWorldName.isEmpty())
            return ret;
        File savesDir = new File(minecraft.mcDataDir, "saves");
        String folder = getWorldFolderName(theWorldName);
        File worldFolder = new File(savesDir, folder);
        FileReader reader = null;
        try {
            ret.load(new FileReader(new File(worldFolder, "WorldDownloader.txt")));
            return ret;
        } catch (Exception e) {
            logger.debug("Failed to load world props for " + worldName, e);
            return ret;
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (Exception e) {
                    logger.warn("Failed to close world props reader for " + worldName, e);
                }
        }
    }

    public static void saveProps() {
        saveProps(worldName, worldProps);
    }

    public static void saveProps(String theWorldName, Properties theWorldProps) {
        File savesDir = new File(minecraft.mcDataDir, "saves");
        if (theWorldName.length() > 0) {
            String folder = getWorldFolderName(theWorldName);
            File worldFolder = new File(savesDir, folder);
            worldFolder.mkdirs();
            try {
                theWorldProps.store(new FileWriter(new File(worldFolder, "WorldDownloader.txt")),
                        I18n.format("wdl.props.world.title", new Object[0]));
            } catch (Exception exception) {
            }
        } else if (!isMultiworld) {
            baseProps.putAll(theWorldProps);
        }
        File baseFolder = new File(savesDir, baseFolderName);
        baseFolder.mkdirs();
        try {
            baseProps.store(new FileWriter(new File(baseFolder, "WorldDownloader.txt")),
                    I18n.format("wdl.props.base.title", new Object[0]));
        } catch (Exception exception) {
        }
        saveGlobalProps();
    }

    public static void saveGlobalProps() {
        try {
            globalProps.store(new FileWriter(new File(minecraft.mcDataDir, "WorldDownloader.txt")),
                    I18n.format("wdl.props.global.title", new Object[0]));
        } catch (Exception exception) {
        }
    }

    public static void applyOverridesToPlayer(NBTTagCompound playerNBT) {
        String health = worldProps.getProperty("PlayerHealth");
        if (!health.equals("keep")) {
            short h = Short.parseShort(health);
            playerNBT.setShort("Health", h);
        }
        String food = worldProps.getProperty("PlayerFood");
        if (!food.equals("keep")) {
            int f = Integer.parseInt(food);
            playerNBT.setInteger("foodLevel", f);
            playerNBT.setInteger("foodTickTimer", 0);
            if (f == 20) {
                playerNBT.setFloat("foodSaturationLevel", 5.0F);
            } else {
                playerNBT.setFloat("foodSaturationLevel", 0.0F);
            }
            playerNBT.setFloat("foodExhaustionLevel", 0.0F);
        }
        String playerPos = worldProps.getProperty("PlayerPos");
        if (playerPos.equals("xyz")) {
            int x = Integer.parseInt(worldProps.getProperty("PlayerX"));
            int y = Integer.parseInt(worldProps.getProperty("PlayerY"));
            int z = Integer.parseInt(worldProps.getProperty("PlayerZ"));
            NBTTagList pos = new NBTTagList();
            pos.appendTag((NBTBase) new NBTTagDouble(x + 0.5D));
            pos.appendTag((NBTBase) new NBTTagDouble(y + 0.621D));
            pos.appendTag((NBTBase) new NBTTagDouble(z + 0.5D));
            playerNBT.setTag("Pos", (NBTBase) pos);
            NBTTagList motion = new NBTTagList();
            motion.appendTag((NBTBase) new NBTTagDouble(0.0D));
            motion.appendTag((NBTBase) new NBTTagDouble(-1.0E-4D));
            motion.appendTag((NBTBase) new NBTTagDouble(0.0D));
            playerNBT.setTag("Motion", (NBTBase) motion);
            NBTTagList rotation = new NBTTagList();
            rotation.appendTag((NBTBase) new NBTTagFloat(0.0F));
            rotation.appendTag((NBTBase) new NBTTagFloat(0.0F));
            playerNBT.setTag("Rotation", (NBTBase) rotation);
        }
        if (thePlayer.capabilities.allowFlying)
            playerNBT.getCompoundTag("abilities").setBoolean("flying", true);
    }

    public static void applyOverridesToWorldInfo(NBTTagCompound worldInfoNBT) {
        String baseName = baseProps.getProperty("ServerName");
        String worldName = worldProps.getProperty("WorldName");
        if (worldName.isEmpty()) {
            worldInfoNBT.setString("LevelName", baseName);
        } else {
            worldInfoNBT.setString("LevelName", baseName + " - " + worldName);
        }
        worldInfoNBT.setBoolean("allowCommands", worldProps.getProperty("AllowCheats").equals("true"));
        String gametypeOption = worldProps.getProperty("GameType");
        if (gametypeOption.equals("keep")) {
            if (thePlayer.capabilities.isCreativeMode) {
                worldInfoNBT.setInteger("GameType", 1);
            } else {
                worldInfoNBT.setInteger("GameType", 0);
            }
        } else if (gametypeOption.equals("survival")) {
            worldInfoNBT.setInteger("GameType", 0);
        } else if (gametypeOption.equals("creative")) {
            worldInfoNBT.setInteger("GameType", 1);
        } else if (gametypeOption.equals("hardcore")) {
            worldInfoNBT.setInteger("GameType", 0);
            worldInfoNBT.setBoolean("hardcore", true);
        }
        String timeOption = worldProps.getProperty("Time");
        if (!timeOption.equals("keep")) {
            long t = Integer.parseInt(timeOption);
            worldInfoNBT.setLong("Time", t);
        }
        String randomSeed = worldProps.getProperty("RandomSeed");
        long seed = 0L;
        if (!randomSeed.isEmpty())
            try {
                seed = Long.parseLong(randomSeed);
            } catch (NumberFormatException numberformatexception) {
                seed = randomSeed.hashCode();
            }
        worldInfoNBT.setLong("RandomSeed", seed);
        boolean mapFeatures = Boolean.parseBoolean(worldProps
                .getProperty("MapFeatures"));
        worldInfoNBT.setBoolean("MapFeatures", mapFeatures);
        String generatorName = worldProps.getProperty("GeneratorName");
        worldInfoNBT.setString("generatorName", generatorName);
        String generatorOptions = worldProps.getProperty("GeneratorOptions");
        worldInfoNBT.setString("generatorOptions", generatorOptions);
        int generatorVersion = Integer.parseInt(worldProps
                .getProperty("GeneratorVersion"));
        worldInfoNBT.setInteger("generatorVersion", generatorVersion);
        String weather = worldProps.getProperty("Weather");
        if (weather.equals("sunny")) {
            worldInfoNBT.setBoolean("raining", false);
            worldInfoNBT.setInteger("rainTime", 0);
            worldInfoNBT.setBoolean("thundering", false);
            worldInfoNBT.setInteger("thunderTime", 0);
        } else if (weather.equals("rain")) {
            worldInfoNBT.setBoolean("raining", true);
            worldInfoNBT.setInteger("rainTime", 24000);
            worldInfoNBT.setBoolean("thundering", false);
            worldInfoNBT.setInteger("thunderTime", 0);
        } else if (weather.equals("thunderstorm")) {
            worldInfoNBT.setBoolean("raining", true);
            worldInfoNBT.setInteger("rainTime", 24000);
            worldInfoNBT.setBoolean("thundering", true);
            worldInfoNBT.setInteger("thunderTime", 24000);
        }
        String spawn = worldProps.getProperty("Spawn");
        if (spawn.equals("player")) {
            int x = MathHelper.floor_double(thePlayer.posX);
            int y = MathHelper.floor_double(thePlayer.posY);
            int z = MathHelper.floor_double(thePlayer.posZ);
            worldInfoNBT.setInteger("SpawnX", x);
            worldInfoNBT.setInteger("SpawnY", y);
            worldInfoNBT.setInteger("SpawnZ", z);
            worldInfoNBT.setBoolean("initialized", true);
        } else if (spawn.equals("xyz")) {
            int x = Integer.parseInt(worldProps.getProperty("SpawnX"));
            int y = Integer.parseInt(worldProps.getProperty("SpawnY"));
            int z = Integer.parseInt(worldProps.getProperty("SpawnZ"));
            worldInfoNBT.setInteger("SpawnX", x);
            worldInfoNBT.setInteger("SpawnY", y);
            worldInfoNBT.setInteger("SpawnZ", z);
            worldInfoNBT.setBoolean("initialized", true);
        }
    }

    public static void saveMapData(GuiWDLSaveProgress progressScreen) {
        File dataDirectory = new File(saveHandler.getWorldDirectory(), "data");
        dataDirectory.mkdirs();
        progressScreen.startMajorTask(
                I18n.format("wdl.saveProgress.map.title", new Object[0]), newMapDatas.size());
        WDLMessages.chatMessageTranslated(WDLMessageTypes.SAVING, "wdl.messages.saving.savingMapItemData", new Object[0]);
        int count = 0;
        for (Map.Entry<Integer, MapData> e : newMapDatas.entrySet()) {
            count++;
            progressScreen.setMinorTaskProgress(
                    I18n.format("wdl.saveProgress.map.saving", new Object[]{e.getKey()}), count);
            File mapFile = new File(dataDirectory, "map_" + e.getKey() + ".dat");
            NBTTagCompound mapNBT = new NBTTagCompound();
            NBTTagCompound data = new NBTTagCompound();
            ((MapData) e.getValue()).writeToNBT(data);
            mapNBT.setTag("data", (NBTBase) data);
            try {
                CompressedStreamTools.writeCompressed(mapNBT, new FileOutputStream(mapFile));
            } catch (IOException ex) {
                throw new RuntimeException("WDL: Exception while writing map data for map " + e
                        .getKey() + "!", ex);
            }
        }
        WDLMessages.chatMessageTranslated(WDLMessageTypes.SAVING, "wdl.messages.saving.mapItemDataSaved", new Object[0]);
    }

    public static String getServerName() {
        try {
            if (minecraft.getCurrentServerData() != null) {
                String name = (minecraft.getCurrentServerData()).serverName;
                if (name.equals(I18n.format("selectServer.defaultName", new Object[0])))
                    name = (minecraft.getCurrentServerData()).serverIP;
                return name;
            }
        } catch (Exception e) {
            logger.warn("Exception while getting server name: ", e);
        }
        return "Unidentified Server";
    }

    public static String getBaseFolderName() {
        return getServerName().replaceAll("\\W+", "_");
    }

    public static String getWorldFolderName(String theWorldName) {
        if (theWorldName.isEmpty())
            return baseFolderName;
        return baseFolderName + " - " + theWorldName;
    }

    public static void saveContainerItems(Container container, IInventory tileEntity, int containerStartIndex) {
        int containerSize = container.inventorySlots.size();
        int inventorySize = tileEntity.getSizeInventory();
        int containerIndex = containerStartIndex;
        int inventoryIndex = 0;
        while (containerIndex < containerSize && inventoryIndex < inventorySize) {
            ItemStack item = container.getSlot(containerIndex).getStack();
            tileEntity.setInventorySlotContents(inventoryIndex, item);
            inventoryIndex++;
            containerIndex++;
        }
    }

    public static void saveInventoryFields(IInventory inventory, IInventory tileEntity) {
        for (int i = 0; i < inventory.getFieldCount(); i++)
            tileEntity.setField(i, inventory.getField(i));
    }

    public static void saveTileEntity(BlockPos pos, TileEntity te) {
        int chunkX = pos.getX() / 16;
        int chunkZ = pos.getZ() / 16;
        ChunkCoordIntPair chunkPos = new ChunkCoordIntPair(chunkX, chunkZ);
        if (!newTileEntities.containsKey(chunkPos))
            newTileEntities.put(chunkPos, new HashMap<>());
        ((Map<BlockPos, TileEntity>) newTileEntities.get(chunkPos)).put(pos, te);
    }

    public static boolean isSpigot() {
        if (thePlayer != null && thePlayer.getClientBrand() != null)
            return thePlayer.getClientBrand().toLowerCase().contains("spigot");
        return false;
    }

    public static String getDebugInfo() {
        StringBuilder info = new StringBuilder();
        info.append("### CORE INFO\n\n");
        info.append("WDL version: ").append("1.8.9a-beta2").append('\n');
        info.append("Launched version: ")
                .append(Minecraft.getMinecraft().getVersion()).append('\n');
        info.append("Client brand: ")
                .append(ClientBrandRetriever.getClientModName()).append('\n');
        info.append("File location: ");
        try {
            String path = (new File(WDL.class.getProtectionDomain().getCodeSource().getLocation().toURI())).getPath();
            String username = System.getProperty("user.name");
            path = path.replace(username, "<USERNAME>");
            info.append(path);
        } catch (Exception e) {
            info.append("Unknown (").append(e).append(')');
        }
        info.append("\n\n### EXTENSIONS\n\n");
        Map<String, WDLApi.ModInfo<?>> extensions = WDLApi.getWDLMods();
        info.append(extensions.size()).append(" loaded\n");
        for (Map.Entry<String, WDLApi.ModInfo<?>> e : extensions.entrySet()) {
            info.append("\n#### ").append(e.getKey()).append("\n\n");
            try {
                info.append(((WDLApi.ModInfo) e.getValue()).getInfo());
            } catch (Exception ex) {
                info.append("ERROR: ").append(ex).append('\n');
                for (StackTraceElement elm : ex.getStackTrace())
                    info.append(elm).append('\n');
            }
        }
        info.append("\n### STATE\n\n");
        info.append("minecraft: ").append(minecraft).append('\n');
        info.append("worldClient: ").append(worldClient).append('\n');
        info.append("networkManager: ").append(networkManager).append('\n');
        info.append("thePlayer: ").append(thePlayer).append('\n');
        info.append("windowContainer: ").append(windowContainer).append('\n');
        info.append("lastClickedBlock: ").append(lastClickedBlock).append('\n');
        info.append("lastEntity: ").append(lastEntity).append('\n');
        info.append("saveHandler: ").append(saveHandler).append('\n');
        info.append("chunkLoader: ").append(chunkLoader).append('\n');
        info.append("newTileEntities: ").append(newTileEntities).append('\n');
        info.append("newEntities: ").append(newEntities).append('\n');
        info.append("newMapDatas: ").append(newMapDatas).append('\n');
        info.append("downloading: ").append(downloading).append('\n');
        info.append("isMultiworld: ").append(isMultiworld).append('\n');
        info.append("propsFound: ").append(propsFound).append('\n');
        info.append("startOnChange: ").append(startOnChange).append('\n');
        info.append("overrideLastModifiedCheck: ")
                .append(overrideLastModifiedCheck).append('\n');
        info.append("saving: ").append(saving).append('\n');
        info.append("worldLoadingDeferred: ").append(worldLoadingDeferred)
                .append('\n');
        info.append("worldName: ").append(worldName).append('\n');
        info.append("baseFolderName: ").append(baseFolderName).append('\n');
        info.append("### CONNECTED SERVER\n\n");
        ServerData data = Minecraft.getMinecraft().getCurrentServerData();
        if (data == null) {
            info.append("No data\n");
        } else {
            info.append("Name: ").append(data.serverName).append('\n');
            info.append("IP: ").append(data.serverIP).append('\n');
        }
        info.append("\n### PROPERTIES\n\n");
        info.append("\n#### BASE\n\n");
        if (baseProps != null) {
            if (!baseProps.isEmpty()) {
                for (Map.Entry<Object, Object> e : baseProps.entrySet()) {
                    info.append(e.getKey()).append(": ").append(e.getValue());
                    info.append('\n');
                }
            } else {
                info.append("empty\n");
            }
        } else {
            info.append("null\n");
        }
        info.append("\n#### WORLD\n\n");
        if (worldProps != null) {
            if (!worldProps.isEmpty()) {
                for (Map.Entry<Object, Object> e : worldProps.entrySet()) {
                    info.append(e.getKey()).append(": ").append(e.getValue());
                    info.append('\n');
                }
            } else {
                info.append("empty\n");
            }
        } else {
            info.append("null\n");
        }
        info.append("\n#### DEFAULT\n\n");
        if (globalProps != null) {
            if (!globalProps.isEmpty()) {
                for (Map.Entry<Object, Object> e : globalProps.entrySet()) {
                    info.append(e.getKey()).append(": ").append(e.getValue());
                    info.append('\n');
                }
            } else {
                info.append("empty\n");
            }
        } else {
            info.append("null\n");
        }
        return info.toString();
    }

    public static void crashed(Throwable t, String category) {
        CrashReport report;
        if (t instanceof ReportedException) {
            CrashReport oldReport = ((ReportedException) t).getCrashReport();
            report = CrashReport.makeCrashReport(oldReport.getCrashCause(), category + " (" + oldReport
                    .getCauseStackTraceOrString() + ")");
            try {
                List<CrashReportCategory> crashReportSectionsOld = ReflectionUtils.<List<CrashReportCategory>>stealAndGetField(oldReport, (Class) List.class);
                List<CrashReportCategory> crashReportSectionsNew = ReflectionUtils.<List<CrashReportCategory>>stealAndGetField(report, (Class) List.class);
                crashReportSectionsNew.addAll(crashReportSectionsOld);
            } catch (Exception e) {
                report.makeCategory("An exception occured while trying to copy the origional categories.")

                        .addCrashSectionThrowable(":(", e);
            }
        } else {
            report = CrashReport.makeCrashReport(t, category);
        }
        minecraft.crashed(report);
    }

    public static String getMinecraftVersion() {
        Map<?, ?> map = Minecraft.getSessionInfo();
        if (map.containsKey("X-Minecraft-Version"))
            return (String) map.get("X-Minecraft-Version");
        return "1.8.9";
    }

    public static String getMinecraftVersionInfo() {
        String version = getMinecraftVersion();
        String launchedVersion = Minecraft.getMinecraft().getVersion();
        String brand = ClientBrandRetriever.getClientModName();
        return String.format("Minecraft %s (%s/%s)", new Object[]{version, launchedVersion, brand});
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\WDL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */