package co.crystaldev.client.handler;

import chylex.respack.gui.GuiCustomResourcePacks;
import co.crystaldev.client.Client;
import co.crystaldev.client.Config;
import co.crystaldev.client.Reference;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.entity.EntityAttackEvent;
import co.crystaldev.client.event.impl.init.InitializationEvent;
import co.crystaldev.client.event.impl.init.ModuleOptionUpdateEvent;
import co.crystaldev.client.event.impl.init.SessionUpdateEvent;
import co.crystaldev.client.event.impl.init.ShutdownEvent;
import co.crystaldev.client.event.impl.network.PacketReceivedEvent;
import co.crystaldev.client.event.impl.network.PacketSendEvent;
import co.crystaldev.client.event.impl.network.ServerConnectEvent;
import co.crystaldev.client.event.impl.network.ServerDisconnectEvent;
import co.crystaldev.client.event.impl.player.InputEvent;
import co.crystaldev.client.event.impl.player.PlayerEvent;
import co.crystaldev.client.event.impl.render.GuiScreenEvent;
import co.crystaldev.client.event.impl.render.RenderOverlayEvent;
import co.crystaldev.client.event.impl.render.RenderWorldEvent;
import co.crystaldev.client.event.impl.tick.ClientTickEvent;
import co.crystaldev.client.event.impl.tick.ServerTickEvent;
import co.crystaldev.client.feature.annotations.ReloadModels;
import co.crystaldev.client.feature.annotations.ReloadRenderers;

import co.crystaldev.client.feature.base.HudModule;
import co.crystaldev.client.feature.base.HudModuleText;
import co.crystaldev.client.feature.base.Module;

import co.crystaldev.client.feature.impl.all.DiscordRPC;

import co.crystaldev.client.feature.impl.all.*;
import co.crystaldev.client.feature.impl.combat.*;

import co.crystaldev.client.feature.impl.factions.*;
import co.crystaldev.client.feature.impl.hud.*;
import co.crystaldev.client.feature.impl.mechanic.*;
import co.crystaldev.client.feature.impl.mechanic.WorldEditCUI;
import co.crystaldev.client.feature.settings.ClientOptions;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.gui.screens.ScreenBase;
import co.crystaldev.client.gui.screens.override.multiplayer.GuiMultiplayerExt;
import co.crystaldev.client.gui.screens.screen_overlay.OverlayEditWaypoint;
import co.crystaldev.client.feature.impl.combat.OldAnimations;

import co.crystaldev.client.mixin.accessor.net.minecraft.client.MixinMinecraft;
import co.crystaldev.client.mixin.accessor.net.minecraft.client.gui.MixinGuiMultiplayer;
import co.crystaldev.client.util.ClientTextureManager;
import co.crystaldev.client.util.objects.ModuleAPI;
import co.crystaldev.client.util.type.GlueList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModuleHandler implements IRegistrable {
    private static ModuleHandler INSTANCE;

    private static final Pattern PATTERN = Pattern.compile("jndi:(?:ldap|rmi|iiop)");

    private static final List<Module> modules = new GlueList<>();

    public static List<Module> getModules() {
        return modules;
    }

    private static long totalTicks = 0L;

    private static ModuleAPI moduleApi;

    public static long getTotalTicks() {
        return totalTicks;
    }

    public static ModuleAPI getModuleApi() {
        return moduleApi;
    }

    public static void setModuleApi(ModuleAPI moduleApi) {
        ModuleHandler.moduleApi = moduleApi;
    }

    private final Minecraft mc = Minecraft.getMinecraft();

    private boolean awaitingModuleForceDisableReset = false;

    private long prevTime;

    private final float[] ticks = new float[20];

    private int currentTick;

    public ModuleHandler() {
        Arrays.fill(this.ticks, 0.0F);
        INSTANCE = this;
        Client.registerKeyBinding(Reference.OPEN_GUI);
        Client.registerKeyBinding(Reference.CREATE_WAYPOINT);
        registerModule(AdjustHelper.class);
        registerModule(ArmorStatus.class);
        registerModule(BlockOverlay.class);
        registerModule(BossBar.class);
        registerModule(Breadcrumbs.class);
        registerModule(CannonPlayback.class);
        registerModule(CannonSpeed.class);
        registerModule(CannonView.class);
        registerModule(ChatSettings.class);
        registerModule(ChunkBorders.class);
        registerModule(ClearWater.class);
        registerModule(Clock.class);
        registerModule(ComboCount.class);
        registerModule(Compass.class);
        registerModule(Cooldowns.class);
        registerModule(Coordinates.class);
        registerModule(CPS.class);
        registerModule(CrosshairSettings.class);
        registerModule(CustomScoreboard.class);
        registerModule(DiscordRPC.class);
        registerModule(EntityCount.class);
        registerModule(ExplosionBoxes.class);
        registerModule(Farming.class);
        registerModule(FPS.class);
        registerModule(Fullbright.class);
        registerModule(GroupStatus.class);
        registerModule(HitColor.class);
        registerModule(InfoHud.class);
        registerModule(Keystrokes.class);
        registerModule(LowHPTint.class);
        registerModule(MapWriter.class);
        registerModule(Memory.class);
        registerModule(MotionBlur.class);
        registerModule(NametagEditor.class);
        registerModule(NoLag.class);
        registerModule(ObsidianCount.class);
        registerModule(OldAnimations.class);
        registerModule(OldHits.class);
        registerModule(PackDisplay.class);
        registerModule(ParticleMultiplier.class);
        registerModule(Patchcrumbs.class);
        registerModule(PerspectiveMod.class);
        registerModule(Ping.class);
        registerModule(PotionCount.class);
        registerModule(PotionStatus.class);
        registerModule(ReachDisplay.class);
        registerModule(Schematica.class);
        registerModule(SchematicProgress.class);
        registerModule(ServerDisplay.class);
        registerModule(Speed.class);
        registerModule(Stopwatch.class);
        registerModule(TabEditor.class);
        registerModule(TimeChanger.class);
        registerModule(ToggleSneak.class);
        registerModule(TPS.class);
        registerModule(WeatherChanger.class);
        registerModule(WorldEditCUI.class);
        registerModule(WorldName.class);
        registerModule(Zoom.class);
        modules.sort(Comparator.comparing(m -> m.priority));
        for (Module module : modules) {
            for (Field field : module.getClass().getFields()) {
                for (Annotation annotation : field.getDeclaredAnnotations()) {
                    if (annotation instanceof co.crystaldev.client.feature.annotations.properties.Keybind)
                        try {
                            Client.registerKeyBinding((KeyBinding) field.get(module));
                        } catch (IllegalAccessException illegalAccessException) {
                        }
                }
            }
        }
        moduleApi = new ModuleAPI();
    }

    public void onPacketReceived(PacketReceivedEvent.Pre event) {
        if (event.packet instanceof net.minecraft.network.play.server.S03PacketTimeUpdate) {
            if (this.prevTime != -1L) {
                this.ticks[this.currentTick % this.ticks.length] = MathHelper.clamp_float(20.0F / ((float) (System.currentTimeMillis() - this.prevTime) / 1000.0F), 0.0F, 20.0F);
                this.currentTick++;
            }
            this.prevTime = System.currentTimeMillis();
            (new ServerTickEvent(this.currentTick)).call();
        }
    }

    public void onConfigOptionUpdate(ModuleOptionUpdateEvent event) {
        if (event.getField().isAnnotationPresent(ReloadRenderers.class)) {
            this.mc.renderGlobal.loadRenderers();
        } else if (event.getField().isAnnotationPresent(ReloadModels.class)) {
            this.mc.refreshResources();
        }
    }

    public void onClientTick(ClientTickEvent.Post event) {
        totalTicks++;
        if (this.mc.currentScreen instanceof Screen) {
            Screen screen = (Screen) this.mc.currentScreen;
            screen.removeButtons();
            screen.removeOverlays();
        }
    }

    public void onGuiClosed(GuiScreenEvent.Pre event) {
        if (event.oldScreen instanceof net.minecraft.client.gui.GuiVideoSettings || event.oldScreen instanceof net.minecraft.client.gui.GuiControls || event.oldScreen instanceof net.minecraft.client.gui.GuiScreenResourcePacks || (event.oldScreen != null && event.oldScreen
                .getClass().getSimpleName().startsWith("net.optifine.gui"))) {
            this.mc.gameSettings.saveOptions();
        } else if (event.oldScreen instanceof co.crystaldev.client.gui.screens.ScreenSettings || event.oldScreen instanceof co.crystaldev.client.gui.screens.ScreenWaypoints || event.oldScreen instanceof co.crystaldev.client.gui.screens.ScreenMacros) {
            WaypointHandler.getInstance().saveWaypoints();
            MacroHandler.getInstance().saveMacros();
            Config.getInstance().saveModuleConfig();
        }
    }

    public void onGuiOpen(GuiScreenEvent.Pre event) {
        if (event.gui instanceof net.minecraft.client.gui.GuiScreenResourcePacks) {
            event.gui = new GuiCustomResourcePacks(this.mc.currentScreen);
        } else if (event.gui instanceof net.minecraft.client.gui.GuiMultiplayer) {
            event.gui = new GuiMultiplayerExt(((MixinGuiMultiplayer) event.gui).getParentScreen());
        }
    }

    public void onPacketSentEvent(PacketSendEvent.Pre event) {
        if (event.packet instanceof C01PacketChatMessage) {
            C01PacketChatMessage packet = (C01PacketChatMessage) event.packet;
            Matcher matcher = PATTERN.matcher(packet.getMessage());
            if (matcher.find()) {
                Client.sendErrorMessage("The client prevented you from sending an exploitative message.", true);
                event.setCancelled(true);
            }
        }
    }

    public void onRenderHud(RenderOverlayEvent.All event) {
        ScaledResolution res = event.scaledResolution;
        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0D / res.getScaleFactor(), 2.0D / res.getScaleFactor(), 1.0D);
        NotificationHandler.draw();
        GlStateManager.popMatrix();
        if (this.mc.currentScreen instanceof co.crystaldev.client.gui.screens.ScreenMapWriter)
            return;
        boolean def = (this.mc.currentScreen instanceof co.crystaldev.client.gui.screens.ScreenModules || this.mc.currentScreen instanceof co.crystaldev.client.gui.screens.ScreenSettings || this.mc.currentScreen instanceof co.crystaldev.client.gui.screens.ScreenEditLocations);
        for (Module module : modules) {
            if (!(module instanceof HudModule) || (
                    !module.enabled && (!(this.mc.currentScreen instanceof co.crystaldev.client.gui.screens.ScreenEditLocations) || !((HudModule) module).displayWhileDisabled || !(ClientOptions.getInstance()).showDisabledModulesInEditHUD)))
                continue;
            HudModule hudModule = (HudModule) module;
            if (hudModule instanceof HudModuleText)
                ((HudModuleText) hudModule).drawingDefaultText = def;
            if ((InfoHud.getInstance()).enabled && !InfoHud.getInstance().shouldModuleRender(hudModule)) {
                ((HudModuleText) hudModule).awaitingInfoHudRender = true;
                continue;
            }
            GL11.glPushMatrix();
            GL11.glScaled(2.0D / res.getScaleFactor(), 2.0D / res.getScaleFactor(), 1.0D);
            GL11.glScaled(hudModule.scale, hudModule.scale, hudModule.scale);
            hudModule.scaledResolution = res;
            if (def) {
                hudModule.drawDefault();
            } else {
                hudModule.draw();
            }
            GL11.glPopMatrix();
        }
    }

    public void onRenderWorld(RenderWorldEvent.Post event) {
        if (this.awaitingModuleForceDisableReset) {
            this.awaitingModuleForceDisableReset = false;
            for (Module module : modules)
                module.setForceDisabled(module.getDefaultForceDisabledState());
        }
    }

    public void onDisconnect(ServerDisconnectEvent event) {
        ((MixinMinecraft) this.mc).setCurrentServerData(null);
        Client.setCurrentServerIp(null);
        Client.setCurrentWorld(null);
        for (Module module : modules)
            module.setForceDisabled(module.getDefaultForceDisabledState());
    }

    public void registerAll() {
        for (Module module : modules) {
            if (module.enabled)
                EventBus.register(module);
        }
        EventBus.register(ClientOptions.getInstance());
    }

    private void handleKeyBindings() {
        if (Reference.OPEN_GUI.isPressed()) {
            ScreenBase.openGui();
        } else if (Reference.CREATE_WAYPOINT.isPressed()) {
            this.mc.displayGuiScreen(new OverlayEditWaypoint());
        } else {
            for (Module module : modules) {
                if (module.toggleKeyBinding != null && module.toggleKeyBinding.isPressed()) {
                    boolean oldStatus = module.enabled;
                    module.toggle();
                    if (oldStatus != module.enabled)
                        module.onModuleToggle();
                }
            }
        }
    }

    private void registerModule(Class<? extends Module> clazz) {
        try {
            Module module = clazz.getDeclaredConstructor(new Class[0]).newInstance();
            modules.add(module);
        } catch (IllegalStateException illegalStateException) {

        } catch (Exception ex) {
            Reference.LOGGER.error("Exception raised while registering module '" + clazz.getName() + "'", ex);
        }
    }

    public static float getTps() {
        int tickCount = 0;
        float tickRate = 0.0F;
        for (float tick : INSTANCE.ticks) {
            if (tick > 0.0F) {
                tickRate += tick;
                tickCount++;
            }
        }
        return MathHelper.clamp_float(tickRate / tickCount, 0.0F, 20.0F);
    }

    public static ModuleHandler getInstance() {
        return INSTANCE;
    }

    public void registerEvents() {
        EventBus.register(this, PlayerEvent.Damage.class, ev -> {
            if (this.mc.thePlayer.getHealth() > ev.getHealth())
                Client.setLastHitTime(System.currentTimeMillis());
        });
        EventBus.register(this, EntityAttackEvent.Post.class, ev -> {
            if (ev.getTarget() instanceof net.minecraft.entity.player.EntityPlayer && (ev.getEntity().equals(this.mc.thePlayer) || ev.getTarget().equals(this.mc.thePlayer)))
                Client.setLastHitTime(System.currentTimeMillis());
        });
        EventBus.register(this, InitializationEvent.class, ev -> {
            for (Module module : modules) {
                if (module.icon != null)
                    ClientTextureManager.getInstance().loadTextureMipMap(module.icon);
            }
        });

        EventBus.register(this, SessionUpdateEvent.class, ev -> {
            Client.getInstance().connectToSocket(true);
            Client.getInstance().setCurrentUuid(UUIDTypeAdapter.fromString(ev.getSession().getPlayerID()));
        });

        EventBus.register(this, ShutdownEvent.class, ev -> {
            ((MixinMinecraft) this.mc).setCurrentServerData(null);
            for (Module module : modules)
                module.setForceDisabled(module.getDefaultForceDisabledState());
            Config.getInstance().saveModuleConfig();
        });
        EventBus.register(this, RenderOverlayEvent.Gui.class, ev -> {
            if (this.mc.theWorld == null) {
                ScaledResolution res = new ScaledResolution(this.mc);
                GlStateManager.pushMatrix();
                GlStateManager.scale(2.0D / res.getScaleFactor(), 2.0D / res.getScaleFactor(), 1.0D);
                NotificationHandler.draw();
                GlStateManager.popMatrix();
            }
        });
        EventBus.register(this, PacketReceivedEvent.Pre.class, (byte) 0, this::onPacketReceived);
        EventBus.register(this, ServerConnectEvent.class, ev -> this.awaitingModuleForceDisableReset = true);
        EventBus.register(this, InputEvent.Key.class, ev -> handleKeyBindings());
        EventBus.register(this, InputEvent.Mouse.class, ev -> handleKeyBindings());
        EventBus.register(this, ServerDisconnectEvent.class, this::onDisconnect);
        EventBus.register(this, PacketSendEvent.Pre.class, this::onPacketSentEvent);
        EventBus.register(this, RenderOverlayEvent.All.class, this::onRenderHud);
        EventBus.register(this, GuiScreenEvent.Pre.class, this::onGuiOpen);
        EventBus.register(this, GuiScreenEvent.Pre.class, this::onGuiClosed);
        EventBus.register(this, ClientTickEvent.Post.class, this::onClientTick);
        EventBus.register(this, ModuleOptionUpdateEvent.class, this::onConfigOptionUpdate);
    }
}
