package com.github.timmekeclient.feature.impl.hud;

import com.github.timmekeclient.Client;
import com.github.timmekeclient.Reference;
import com.github.timmekeclient.event.EventBus;
import com.github.timmekeclient.event.IRegistrable;
import com.github.timmekeclient.event.impl.init.InitializationEvent;
import com.github.timmekeclient.event.impl.init.ShutdownEvent;
import com.github.timmekeclient.event.impl.player.InputEvent;
import com.github.timmekeclient.event.impl.render.RenderWorldEvent;
import com.github.timmekeclient.event.impl.render.WindowResizeEvent;
import com.github.timmekeclient.event.impl.tick.ClientTickEvent;
import com.github.timmekeclient.event.impl.world.ChunkEvent;
import com.github.timmekeclient.feature.annotations.Hidden;
import com.github.timmekeclient.feature.annotations.properties.*;
import com.github.timmekeclient.feature.base.Category;
import com.github.timmekeclient.feature.base.HudModule;
import com.github.timmekeclient.group.provider.GroupChunkProvider;
import com.github.timmekeclient.group.provider.GroupMapProvider;
import com.github.timmekeclient.gui.screens.ScreenMapWriter;
import com.github.timmekeclient.util.enums.AnchorRegion;
import com.github.timmekeclient.util.objects.ModulePosition;
import com.google.common.reflect.TypeToken;
import mapwriter.MapWriterMod;
import mapwriter.MwKeyHandler;
import mapwriter.api.IMwDataProvider;
import mapwriter.api.MwAPI;
import mapwriter.config.ConfigurationHandler;
import net.minecraft.client.gui.GuiScreen;

import java.util.HashSet;
import java.util.Set;

@ModuleInfo(name = "MapWriter", description = "A simple and highly configurable in-game minimap", category = Category.HUD)
public class MapWriter extends HudModule implements IRegistrable {
    private static MapWriter INSTANCE;

    @Slider(label = "Map Size", placeholder = "{value}px", minimum = 100.0D, maximum = 200.0D, standard = 120.0D, integers = true)
    public int size = 120;

    @Slider(label = "Arrow Size", minimum = 1.0D, maximum = 10.0D, standard = 4.0D, integers = true)
    public int playerArrowSize = 4;

    @Slider(label = "Chunks Updates", minimum = 10.0D, maximum = 100.0D, standard = 50.0D, integers = true)
    public int chunksPerTick = 50;

    @Selector(label = "Coordinates Mode", values = {"Disabled", "Small", "Large"})
    public String coordsMode = "Large";

    @Toggle(label = "Circular Map")
    public boolean circular = true;

    @Toggle(label = "Rotate Map")
    public boolean rotate = true;

    @Toggle(label = "Realistic Map")
    public boolean realisticMap = false;

    @Toggle(label = "Fix Nether Ceiling")
    public boolean fixNetherCeiling = false;

    @Toggle(label = "Draw Map Border")
    public boolean mapBorder = true;

    @PageBreak(label = "Background")
    @Selector(label = "Background Mode", values = {"None", "Static", "Planning"})
    public String backgroundMode = "None";

    @Slider(label = "Background Alpha", minimum = 0.0D, maximum = 100.0D, standard = 25.0D, integers = true)
    public int backgroundAlpha = 25;

    @PageBreak(label = "Map Zoom Levels")
    @Slider(label = "Zoom Out Levels", minimum = 1.0D, maximum = 10.0D, standard = 5.0D, integers = true)
    public int zoomOutLevels = 5;

    @Slider(label = "Zoom In Levels", minimum = -10.0D, maximum = -1.0D, standard = -5.0D, integers = true)
    public int zoomInLevels = -5;

    @Property(label = "Enabled Data Providers")
    @Hidden
    public String enabledMapwriterProviders = "[]";

    private MapWriterMod mapwriter = null;

    private boolean attemptLoad = false;

    public void setAttemptLoad(boolean attemptLoad) {
        this.attemptLoad = attemptLoad;
    }

    private final Set<String> enabledDataProviders = new HashSet<>();

    public Set<String> getEnabledDataProviders() {
        return this.enabledDataProviders;
    }

    public MapWriter() {
        this.enabled = true;
        this.position = new ModulePosition(AnchorRegion.TOP_RIGHT, 10.0F, 10.0F);
        this.width = 120;
        this.height = 120;
        this.canScale = false;
        INSTANCE = this;
    }

    public void configPostInit() {
        super.configPostInit();
        if (this.mapwriter == null)
            this.mapwriter = new MapWriterMod();
    }

    public void draw() {
        int fh = this.mc.fontRendererObj.FONT_HEIGHT;
        this.width = this.height = this.size;
        this.height += this.coordsMode.equalsIgnoreCase("disabled") ? 0 : (this.coordsMode.equalsIgnoreCase("large") ? (fh + 6) : (fh + 2));
        if (this.mapwriter != null)
            this.mapwriter.draw();
    }

    public void onUpdate() {
        ConfigurationHandler.loadConfig();
    }

    public boolean getDefaultForceDisabledState() {
        return Client.isOnHypixel();
    }

    private void onClientInit(InitializationEvent event) {
        MwAPI.registerDataProvider("Chunk Highlighting", new GroupChunkProvider(), true);
        MwAPI.registerDataProvider("Group Members", new GroupMapProvider(), true);
        this.enabledDataProviders.clear();
        this.enabledDataProviders.addAll(Reference.GSON.fromJson(this.enabledMapwriterProviders, (new TypeToken<HashSet<String>>() {

        }).getType()));
        for (String str : this.enabledDataProviders) {
            IMwDataProvider provider = MwAPI.getDataProvider(str);
            if (provider != null)
                MwAPI.setEnabled(provider, true);
        }
    }

    public void registerEvents() {
        EventBus.register(this, InitializationEvent.class, this::onClientInit);
        EventBus.register(this, WindowResizeEvent.class, ev -> ConfigurationHandler.loadConfig());
        EventBus.register(this, ShutdownEvent.class, (byte) 0, ev -> this.enabledMapwriterProviders = Reference.GSON.toJson(new HashSet<>(MwAPI.getEnabledProviderNames())));
        EventBus.register(this, RenderWorldEvent.Pre.class, ev -> {
            if (this.mapwriter != null)
                this.mapwriter.updatePlayer();
        });
        EventBus.register(this, ChunkEvent.Load.class, ev -> {
            if (this.mapwriter != null)
                this.mapwriter.onChunkLoad(ev.getChunk());
        });
        EventBus.register(this, ChunkEvent.Unload.class, ev -> {
            if (this.mapwriter != null)
                this.mapwriter.onChunkUnload(ev.getChunk());
        });
        EventBus.register(this, ClientTickEvent.Pre.class, ev -> {
            if (this.attemptLoad) {
                this.attemptLoad = false;
                this.mapwriter.load();
            }
            if (this.mapwriter != null && this.mapwriter.ready && this.mc.thePlayer == null)
                this.mapwriter.close();
            if (this.mapwriter != null)
                this.mapwriter.onTick();
        });
        EventBus.register(this, InputEvent.Key.class, ev -> {
            if (ev.isKeyDown() && this.mapwriter.ready)
                if (MwKeyHandler.keyMapGui.isPressed()) {
                    this.mc.displayGuiScreen((GuiScreen) new ScreenMapWriter(this.mapwriter));
                } else if (MwKeyHandler.keyZoomIn.isPressed()) {
                    this.mapwriter.miniMap.view.adjustZoomLevel(-1);
                } else if (MwKeyHandler.keyZoomOut.isPressed()) {
                    this.mapwriter.miniMap.view.adjustZoomLevel(1);
                } else if (MwKeyHandler.keyMapMode.isPressed()) {
                    this.mapwriter.miniMap.nextOverlayMode(1);
                }
        });
    }

    public static MapWriter getInstance() {
        return INSTANCE;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\hud\MapWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */