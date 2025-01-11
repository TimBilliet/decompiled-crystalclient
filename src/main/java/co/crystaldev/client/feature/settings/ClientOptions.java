package co.crystaldev.client.feature.settings;

import co.crystaldev.client.Client;
import co.crystaldev.client.Config;
import co.crystaldev.client.Reference;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.init.ModuleOptionUpdateEvent;
import co.crystaldev.client.event.impl.init.ShutdownEvent;
import co.crystaldev.client.event.impl.render.GuiScreenEvent;
import co.crystaldev.client.event.impl.render.RenderTickEvent;
import co.crystaldev.client.feature.annotations.Hidden;
import co.crystaldev.client.feature.annotations.HoverOverlay;
import co.crystaldev.client.feature.annotations.ReloadRenderers;
import co.crystaldev.client.feature.annotations.properties.*;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Dropdown;
import co.crystaldev.client.feature.base.Module;
import co.crystaldev.client.gui.GuiOptions;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.gui.screens.ScreenSettings;
import co.crystaldev.client.handler.OverlayHandler;
import co.crystaldev.client.mixin.accessor.net.minecraft.client.renderer.MixinEntityRenderer;
import co.crystaldev.client.mixin.accessor.net.minecraft.client.shader.MixinShaderGroup;
import co.crystaldev.client.util.ColorObject;
import co.crystaldev.client.util.Reflector;
import co.crystaldev.client.util.objects.EmoteWheel;

import co.crystaldev.client.util.objects.Schematic;
import co.crystaldev.client.util.objects.Transformation;
import com.github.lunatrius.core.util.MBlockPos;
import com.github.lunatrius.schematica.Schematica;
import com.github.lunatrius.schematica.client.printer.SchematicPrinter;
import com.github.lunatrius.schematica.client.renderer.RenderSchematic;
import com.github.lunatrius.schematica.client.util.FlipHelper;
import com.github.lunatrius.schematica.client.util.RotationHelper;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import co.crystaldev.client.util.objects.EmoteWheel;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderUniform;
import org.lwjgl.opengl.Display;

import java.awt.*;
import java.io.File;
import java.util.LinkedHashSet;
import java.util.function.Function;

@ModuleInfo(name = "Settings", description = "Modify aspects of the game and client.", category = Category.ALL)
public class ClientOptions extends Module implements IRegistrable {
  @DropdownMenu(label = "GUI Theme", values = {"Default", "Dark Red", "Red", "Orange", "Green", "Pacific Blue", "Midnight Blue", "Purple", "Pink"}, defaultValues = {"Default"})
  public Dropdown<String> theme;
  
  @PageBreak(label = "Menu Blur")
  @Toggle(label = "Menu Blur")
  public boolean blurEnabled = true;
  
  @Toggle(label = "Inventory Blur")
  public boolean inventoryBlur = true;
  
  @Slider(label = "Fade Time", placeholder = "{value}ms", minimum = 0.0D, maximum = 500.0D, standard = 300.0D, integers = true)
  public int fadeTime = 200;
  
  @Slider(label = "Blur Radius", minimum = 1.0D, maximum = 15.0D, standard = 8.0D, integers = true)
  public int radius = 8;
  
  @PageBreak(label = "Menu Scroll")
  @Toggle(label = "Invert Scroll Direction")
  public boolean invertScrollDirection = false;
  
  @Slider(label = "Scroll Duration", minimum = 0.0D, maximum = 5000.0D, standard = 600.0D, integers = true)
  public int scrollDuration = 600;
  
  @Slider(label = "Scroll Step", minimum = 0.0D, maximum = 100.0D, standard = 19.0D)
  public double scrollStep = 19.0D;
  
  @PageBreak(label = "Chroma")
  @Slider(label = "Chroma Brightness", minimum = 0.0D, maximum = 1.0D, standard = 1.0D)
  public double chromaBrightness = 1.0D;
  
  @Slider(label = "Chroma Saturation", minimum = 0.0D, maximum = 1.0D, standard = 1.0D)
  public double chromaSaturation = 1.0D;
  
  @Slider(label = "Chroma Size", minimum = 0.0D, maximum = 100.0D, standard = 50.0D, integers = true)
  public int chromaSize = 50;
  
  @Slider(label = "Chroma Speed", minimum = 0.0D, maximum = 20.0D, standard = 5.0D, integers = true)
  public int chromaSpeed = 5;
  
  @PageBreak(label = "Game Colors")
  @Colour(label = "Explosion Particle Color")
  public ColorObject explosionColor = new ColorObject(255, 255, 255, 255);
  
  @Colour(label = "Redstone Wire Color")
  @ReloadRenderers
  public ColorObject redstoneColor = new ColorObject(255, 0, 0, 255);
  
  @Toggle(label = "Use Custom Explosion Color")
  public boolean useExplosionColor = false;
  
  @Toggle(label = "Use Custom Redstone Color")
  @ReloadRenderers
  public boolean useRedstoneColor = false;
  
  @Toggle(label = "Red String")
  public boolean redString = false;
  
  public boolean optimizedFontRenderer = true;
  
  @PageBreak(label = "Cosmetics")
  @Toggle(label = "Use New Wing Animation")
  public boolean newWingAnimation = false;
  
  @PageBreak(label = "Performance")
  @HoverOverlay({"Limits FPS while the game window isn't active."})
  @Toggle(label = "Unfocused FPS")
  public boolean unfocusedFps = true;
  
  @HoverOverlay({"Limited FPS amount while game window is not active."})
  @Slider(label = "Unfocused FPS Amount", minimum = 1.0D, maximum = 240.0D, standard = 30.0D, integers = true)
  public int unfocusedFpsAmount = 30;
  
  @HoverOverlay({"Limits how fast large chunks can load. This setting can drastically", "increase game performance.", " ", "Higher values will result in chunks loading over a longer period", "of time.", " ", "&lStatus:&r 1 = &cDisabled&r, 2-45 = &aEnabled&r."})
  @Slider(label = "Chunk Loading (Lazy)", minimum = 1.0D, maximum = 45.0D, standard = 5.0D, integers = true)
  public int lazyChunkLoading = 5;
  
  @PageBreak(label = "Improvements")
  @HoverOverlay({"Runs the game inside of a borderless window while in fullscreen."})
  @Toggle(label = "Borderless Fullscreen")
  public boolean borderlessFullscreen = true;
  
  @Toggle(label = "Disable Achievements")
  public boolean disableAchievements = false;
  
  @Toggle(label = "Show Barriers")
  public boolean showBarriers = false;
  
  @Toggle(label = "Show own Nametag")
  public boolean f5Nametags = true;
  
  @Toggle(label = "Instant Fullscreen")
  public boolean instantFullscreen = false;
  
  @HoverOverlay({"Disables the shifting of your inventory while potion effects are present."})
  @Toggle(label = "Inventory Shift Fix")
  public boolean inventoryShiftFix = true;
  
  @Toggle(label = "Inventory Search Bar")
  public boolean inventorySearchBar = true;
  
  @Toggle(label = "Keep Shaders on Perspective Change")
  public boolean keepShadersOnPerspectiveChange = true;
  
  @HoverOverlay({"Re-registers keybindings when a GUI is closed."})
  @Toggle(label = "Modern Keybinding Handling")
  public boolean modernKeybindHandling = true;
  
  @Toggle(label = "Translate Roman Numerals")
  public boolean translateRomanNumerals = false;
  
  @Toggle(label = "Show Disabled Modules in Editor")
  public boolean showDisabledModulesInEditHUD = true;
  
  @Slider(label = "Fire Overlay Height", minimum = -0.5D, maximum = 1.5D, standard = 0.0D)
  public double fireOverlayHeight = 0.0D;
  
  @Hidden
  @Toggle(label = "Show Unowned Cosmetics")
  public boolean showUnownedCosmetics = false;
  
  @Hidden
  @Toggle(label = "Use Vanilla Display Title")
  public boolean dontOverrideDisplayTitle = false;
  
  @Hidden
  @Property(label = "Schematic History")
  public String schematicHistoryCache = "[]";
  
  @Hidden
  @Property(label = "Selected Emotes")
  public String emoteCache = "null";
  
  private static ClientOptions INSTANCE;
  
  public LinkedHashSet<Schematic> schematicHistory;
  
  public EmoteWheel emoteWheel;
  
  public Function<Double, Double> easingMethod;
  
  public ClientOptions() {
    this.easingMethod = (v -> v);
    this.isBlurred = false;
    this.guiOpen = false;
    this.canBeDisabled = false;
  }
  
  private static final Color BACKGROUND_COLOR = new Color(16, 16, 16, 60);
  
  private long start;
  
  public boolean isBlurred;
  
  public boolean guiOpen;
  
  public void configPostInit() {
    super.configPostInit();
    updateTheme();
    loadSchematicHistory();
    this.emoteWheel = (this.emoteCache == null || this.emoteCache.equals("null")) ? new EmoteWheel() : (EmoteWheel)Reference.GSON.fromJson(this.emoteCache, EmoteWheel.class);
    if (this.dontOverrideDisplayTitle) {
      Display.setTitle("Minecraft " + Client.getMinecraftVersion().getVersionString());
    } else {
      Display.setTitle(String.format("%s (v%s-%s/%s)", "Crystal Client", Client.getMinecraftVersion().getVersionString(), "37aa61d", "offline"));
    } 
  }
  
  public void loadSchematicHistory() {
    System.out.println("loadschemhistory");
    this.schematicHistory = new LinkedHashSet<>();
    try {
      for (JsonElement element : Reference.GSON.fromJson(this.schematicHistoryCache, JsonArray.class)) {
        if (element.isJsonObject()) {
          JsonObject obj = element.getAsJsonObject();
          if (obj.has("file") && obj.has("id")) {
            File file = (File)Reference.GSON.fromJson(obj.get("file").getAsString(), File.class);
            if (file.exists())
              this.schematicHistory.add(new Schematic(file, null, obj.get("id").getAsString(), obj));
          }
        }
      }
    } catch (RuntimeException ex) {
      this.schematicHistoryCache = "[]";
      this.schematicHistory = new LinkedHashSet<>();
    }
  }
  
  public void updateTheme() {
    for (GuiOptions.Theme theme : GuiOptions.Theme.values()) {
      if (theme.name.equalsIgnoreCase((String)this.theme.getCurrentSelection())) {
        theme.setTheme();
        if (this.mc.currentScreen instanceof ScreenSettings) {
          ScreenSettings settings = (ScreenSettings)this.mc.currentScreen;
          settings.init();
          if (settings.parent instanceof Screen)
            ((Screen)settings.parent).init();
        }
        break;
      } 
    } 
  }
  
  private void onRenderTick(RenderTickEvent.Post event) {
    if (this.guiOpen && this.mc.currentScreen == null && !OverlayHandler.getInstance().hasOverlay()) {
      this.guiOpen = false;
      if (this.isBlurred) {
        this.isBlurred = false;
        this.mc.entityRenderer.stopUseShader();
      } 
    } 
    if (this.guiOpen && canBlur()) {
      if (!this.isBlurred)
        initBlur(); 
      if (this.mc.entityRenderer != null && this.mc.entityRenderer.isShaderActive()) {
        MixinShaderGroup sg = (MixinShaderGroup)((MixinEntityRenderer)this.mc.entityRenderer).getShaderGroup();
        try {
          for (Shader s : sg.getListShaders()) {
            ShaderUniform su = s.getShaderManager().getShaderUniform("Progress");
            ShaderUniform su1 = s.getShaderManager().getShaderUniform("Radius");
            if (su != null)
              su.set(getProgress()); 
            if (su1 != null)
              su1.set(this.radius); 
          } 
        } catch (IllegalArgumentException ex) {
          Reference.LOGGER.error("Error rendering menu blur", ex);
        } 
      } 
    } 
    if (!canBlur() && this.isBlurred) {
      this.isBlurred = false;
      this.mc.entityRenderer.stopUseShader();
    } 
  }
  
  public void blurScreen() {
    if (!this.guiOpen)
      initBlur(); 
  }
  
  public boolean canBlur() {
    if (this.mc.theWorld == null)
      return false; 
    if (!OverlayHandler.getInstance().hasOverlay() && 
      !(this.mc.currentScreen instanceof Screen) && !this.inventoryBlur)
      return false; 
    return (!Reflector.GameSettings$ofFastRender(this.mc.gameSettings) && this.blurEnabled);
  }
  
  public int getBackgroundColor() {
    float progress = getProgress();
    if (progress == 1.0F)
      return BACKGROUND_COLOR.getRGB(); 
    return (new ColorObject(BACKGROUND_COLOR.getRed(), BACKGROUND_COLOR.getGreen(), BACKGROUND_COLOR.getBlue(), 
        (int)(BACKGROUND_COLOR.getAlpha() * progress))).getRGB();
  }
  
  private float getProgress() {
    if (this.fadeTime <= 0)
      return 1.0F; 
    return Math.min((float)(System.currentTimeMillis() - this.start) / this.fadeTime, 1.0F);
  }
  
  private void initBlur() {
    if (canBlur()) {
      MixinEntityRenderer accessor = (MixinEntityRenderer)this.mc.entityRenderer;
      accessor.callLoadShader(Reference.BLUR_SHADER);
      this.isBlurred = true;
      this.start = System.currentTimeMillis();
    } else {
      this.isBlurred = false;
    } 
    this.guiOpen = true;
  }
  
  public void addSchematicToHistory(Schematic schematic) {
    this.schematicHistory.removeIf(schem -> schem.getId().equals(schematic.getId()));
    this.schematicHistory.add(schematic);
    while (this.schematicHistory.size() > 5)
      this.schematicHistory.remove(this.schematicHistory.stream().findFirst().get());
    JsonArray arr = new JsonArray();
    for (Schematic schem : this.schematicHistory) {
      JsonArray transformations = new JsonArray();
      for (Transformation t : schem.getTransformations()) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", t.getType().toString());
        jsonObject.addProperty("direction", t.getDirection().toString());
        jsonObject.addProperty("x", t.getX());
        jsonObject.addProperty("y", t.getY());
        jsonObject.addProperty("z", t.getZ());
        transformations.add((JsonElement)jsonObject);
      }
      JsonObject obj = new JsonObject();
      obj.addProperty("name", schem.getName());
      obj.addProperty("id", schem.getId());
      obj.addProperty("file", Reference.GSON.toJson(schem.getFile(), File.class));
      obj.addProperty("x", schem.getX());
      obj.addProperty("y", schem.getY());
      obj.addProperty("z", schem.getZ());
      obj.add("transformations", (JsonElement)transformations);
      arr.add((JsonElement)obj);
    }
    this.schematicHistoryCache = Reference.GSON.toJson(arr, JsonArray.class);
  }
  
  public void loadSchematicFromHistory(Schematic schematic) {
    File schem = schematic.getFile();
    if (schem.exists()) {
      ClientProxy proxy = Schematica.proxy;
      proxy.unloadSchematic();
      if (proxy.loadSchematic(null, schem.getParentFile(), schem.getName())) {
        SchematicWorld world = ClientProxy.currentSchematic.schematic;
        MBlockPos pos = world.position;
        for (Transformation t : schematic.getTransformations()) {
          ClientProxy.currentSchematic.transformations.add(t);
          pos.x = t.getX();
          pos.y = t.getY();
          pos.z = t.getZ();
          if (t.getType() == Transformation.Type.FLIP) {
            FlipHelper.INSTANCE.flip(world, t.getDirection(), false);
            continue;
          }
          RotationHelper.INSTANCE.rotate(world, t.getDirection(), false);
        }
        pos.x = schematic.getX();
        pos.y = schematic.getY();
        pos.z = schematic.getZ();
        ClientProxy.moveToPlayer = false;
        RenderSchematic.INSTANCE.refresh();
        SchematicPrinter.INSTANCE.refresh();
      }
    }
  }
  
  public LinkedHashSet<Schematic> getSchematicHistory() {
    return (this.schematicHistory == null) ? (this.schematicHistory = new LinkedHashSet<>()) : this.schematicHistory;
  }
  
  public static ClientOptions getInstance() {
    return (INSTANCE == null) ? (INSTANCE = new ClientOptions()) : INSTANCE;
  }
  
  public void registerEvents() {
    EventBus.register(this, GuiScreenEvent.Pre.class, ev -> {
          if ((ev.gui == null || ev.gui instanceof net.minecraft.client.gui.GuiChat) && this.guiOpen) {
            this.mc.entityRenderer.stopUseShader();
            this.guiOpen = false;
          } 
        });
    EventBus.register(this, ModuleOptionUpdateEvent.class, ev -> {
          if (ev.getModule() == this && "GUI Theme".equals(ev.getOptionName()))
            updateTheme(); 
        });
    EventBus.register(this, ShutdownEvent.class, ev -> Config.getInstance().saveModuleConfig(this));
    EventBus.register(this, RenderTickEvent.Post.class, this::onRenderTick);
  }
}
