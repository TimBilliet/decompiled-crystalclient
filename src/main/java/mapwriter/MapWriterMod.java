package mapwriter;

import co.crystaldev.client.Client;
import co.crystaldev.client.feature.impl.hud.MapWriter;
import mapwriter.config.Config;
import mapwriter.config.ConfigurationHandler;
import mapwriter.config.WorldConfig;
import mapwriter.map.MapTexture;
import mapwriter.map.MiniMap;
import mapwriter.region.BlockColors;
import mapwriter.region.RegionManager;
import mapwriter.tasks.CloseRegionManagerTask;
import mapwriter.tasks.Task;
import mapwriter.util.Logging;
import mapwriter.util.Render;
import mapwriter.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.chunk.Chunk;

import java.io.File;

public class MapWriterMod {
    private static MapWriterMod INSTANCE;

    public Minecraft mc;

    private final File saveDir;

    public File worldDir = null;

    public File imageDir = null;

    public boolean ready;

    public int tickCounter = 0;

    public int textureSize = 2048;

    public double playerX = 0.0D;

    public double playerZ = 0.0D;

    public double playerY = 0.0D;

    public int playerXInt = 0;

    public int playerYInt = 0;

    public int playerZInt = 0;

    public double playerHeading = 0.0D;

    public int playerDimension = 0;

    public float mapRotationDegrees = 0.0F;

    public MapTexture mapTexture = null;

    public BackgroundExecutor executor = null;

    public MiniMap miniMap = null;

    public BlockColors blockColours = null;

    public RegionManager regionManager = null;

    public ChunkManager chunkManager = null;

    public MapWriterMod() {
        INSTANCE = this;
        this.mc = Minecraft.getMinecraft();
        this.saveDir = new File(Client.getClientRunDirectory(), "mapwriter-worlds");
        this.ready = false;
        MwKeyHandler.registerKeyBindings();
        ConfigurationHandler.loadConfig();
    }

    public void setTextureSize() {
        if (Config.configTextureSize != this.textureSize) {
            int maxTextureSize = Render.getMaxTextureSize();
            int textureSize = 1024;
            while (textureSize <= maxTextureSize && textureSize <= Config.configTextureSize)
                textureSize *= 2;
            textureSize /= 2;
            Logging.log("GL reported max texture size = %d", maxTextureSize);
            Logging.log("texture size from config = %d", Config.configTextureSize);
            Logging.log("setting map texture size to = %d", textureSize);
            this.textureSize = textureSize;
            if (this.ready)
                reloadMapTexture();
        }
    }

    public void updatePlayer() {
        this.playerX = this.mc.thePlayer.posX;
        this.playerY = this.mc.thePlayer.posY;
        this.playerZ = this.mc.thePlayer.posZ;
        this.playerXInt = (int) Math.floor(this.playerX);
        this.playerYInt = (int) Math.floor(this.playerY);
        this.playerZInt = (int) Math.floor(this.playerZ);
        this.playerHeading = Math.toRadians(this.mc.thePlayer.rotationYaw) + 1.5707963267948966D;
        this.mapRotationDegrees = -this.mc.thePlayer.rotationYaw + 180.0F;
        if (this.mc.theWorld != null && this.miniMap != null) {
            this.playerDimension = this.mc.theWorld.provider.getDimensionId();
            if (this.miniMap.view.getDimension() != this.playerDimension) {
                WorldConfig.getInstance().addDimension(this.playerDimension);
                this.miniMap.view.setDimension(this.playerDimension);
            }
        }
    }

    public void reloadBlockColours() {
        this.blockColours = new BlockColors();
        this.blockColours.loadFromFile();
    }

    public void reloadMapTexture() {
        this.executor.addTask((Task) new CloseRegionManagerTask(this.regionManager));
        this.executor.close();
        MapTexture oldMapTexture = this.mapTexture;
        this.mapTexture = new MapTexture(this.textureSize, Config.linearTextureScaling);
        if (oldMapTexture != null)
            oldMapTexture.close();
        this.executor = new BackgroundExecutor();
        this.regionManager = new RegionManager(this.worldDir, this.imageDir, this.blockColours, (MapWriter.getInstance()).zoomInLevels, (MapWriter.getInstance()).zoomOutLevels);
    }

    public void load() {
        if (this.ready || this.mc.theWorld == null || this.mc.thePlayer == null)
            return;
        Logging.log("Mw.load: loading...");
        if (!this.mc.isSingleplayer()) {
            this.worldDir = new File(new File(this.saveDir, "mapwriter_mp_worlds"), Utils.getWorldName());
        } else {
            this.worldDir = new File(new File(this.saveDir, "mapwriter_sp_worlds"), Utils.getWorldName());
        }
        this.imageDir = new File(this.worldDir, "images");
        if (!this.imageDir.exists())
            this.imageDir.mkdirs();
        if (!this.imageDir.isDirectory())
            Logging.log("Mapwriter: ERROR: could not create images directory '%s'", this.imageDir.getPath());
        this.tickCounter = 0;
        this.executor = new BackgroundExecutor();
        this.mapTexture = new MapTexture(this.textureSize, Config.linearTextureScaling);
        if (this.blockColours == null)
            reloadBlockColours();
        this.regionManager = new RegionManager(this.worldDir, this.imageDir, this.blockColours, (MapWriter.getInstance()).zoomInLevels, (MapWriter.getInstance()).zoomOutLevels);
        this.miniMap = new MiniMap(this);
        this.miniMap.view.setDimension(this.mc.thePlayer.dimension);
        this.chunkManager = new ChunkManager(this);
        this.ready = true;
    }

    public void close() {
        Logging.log("Mw.close: closing...");
        if (this.ready) {
            this.ready = false;
            this.chunkManager.close();
            this.chunkManager = null;
            this.executor.addTask((Task) new CloseRegionManagerTask(this.regionManager));
            this.regionManager = null;
            this.miniMap.close();
            this.miniMap = null;
            this.mapTexture.close();
            this.tickCounter = 0;
        }
    }

    public void draw() {
        if (isReady() && !(this.mc.currentScreen instanceof co.crystaldev.client.gui.screens.ScreenMapWriter)) {
            this.miniMap.view.setViewCentreScaled(this.playerX, this.playerZ, this.playerDimension);
            this.miniMap.drawCurrentMap();
        }
    }

    public void onTick() {
        load();
        if (isReady()) {
            setTextureSize();
            int maxTasks = 50;
            for (; !this.executor.processTaskQueue() && maxTasks > 0; maxTasks--) ;
            this.chunkManager.onTick();
            this.mapTexture.processTextureUpdates();
            this.tickCounter++;
        }
    }

    public void onChunkLoad(Chunk chunk) {
        MapWriter.getInstance().setAttemptLoad(true);
        if (chunk != null && chunk.getWorld() instanceof net.minecraft.client.multiplayer.WorldClient &&
                this.ready)
            this.chunkManager.addChunk(chunk);
    }

    public void onChunkUnload(Chunk chunk) {
        if (this.ready && chunk != null && chunk.getWorld() instanceof net.minecraft.client.multiplayer.WorldClient)
            this.chunkManager.removeChunk(chunk);
    }

    public boolean isReady() {
        return (this.ready && this.mc.thePlayer != null && this.mc.theWorld != null);
    }

    public static MapWriterMod getInstance() {
        return INSTANCE;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwriter\MapWriterMod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */