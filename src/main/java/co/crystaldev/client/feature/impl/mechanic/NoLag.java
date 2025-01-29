package co.crystaldev.client.feature.impl.mechanic;


import co.crystaldev.client.Client;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.entity.EntitySpawnEvent;
import co.crystaldev.client.event.impl.tick.ClientTickEvent;
import co.crystaldev.client.feature.annotations.HoverOverlay;
import co.crystaldev.client.feature.annotations.ReloadRenderers;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.PageBreak;
import co.crystaldev.client.feature.annotations.properties.Slider;
import co.crystaldev.client.feature.annotations.properties.Toggle;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Module;
import co.crystaldev.client.handler.ModuleHandler;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHalfStoneSlab;
import net.minecraft.block.BlockHalfStoneSlabNew;
import net.minecraft.block.BlockHalfWoodSlab;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityTNTPrimed;

@ModuleInfo(name = "No Lag", nameAliases = {"FPS"}, description = "Improve game performance", category = Category.MECHANIC)
public class NoLag extends Module implements IRegistrable {
    @PageBreak(label = "Cannoning")
    @HoverOverlay({"Stops TNT from rendering."})
    @Toggle(label = "Hide TNT")
    public boolean hideTnt = false;

    @HoverOverlay({"Stops Falling Blocks from rendering."})
    @Toggle(label = "Hide Sand")
    public boolean hideSand = false;

    @HoverOverlay({"Remove the flashing effect from TNT while ignited."})
    @Toggle(label = "Disable TNT Flash")
    public boolean disableTNTFlash = false;

    @HoverOverlay({"Prevents TNT entities from expanding prior to detonation."})
    @Toggle(label = "Disable TNT Expansion")
    public boolean disableTNTExpansion = false;

    @HoverOverlay({"Only display a minimal amount of TNT/Sand."})
    @Toggle(label = "Minimal TNT/Sand")
    public boolean minimal = false;

    @PageBreak(label = "Base")
    @HoverOverlay({"Stops hostile mobs from rendering."})
    @Toggle(label = "Hide Mobs")
    public boolean hideMobs = false;

    @ReloadRenderers
    @Toggle(label = "Hide Chests")
    public boolean hideChests = false;

    @ReloadRenderers
    @Toggle(label = "Hide Hoppers")
    public boolean hideHoppers = false;

    @Toggle(label = "Disable Enchantment Table Books")
    @ReloadRenderers
    public boolean disableEnchantmentTableBooks = false;

    @HoverOverlay({"Disables rendering of the spinning mob from the inside of spawners."})
    @Toggle(label = "Disable Spawner Animation")
    public boolean disableSpawnerAnimation = false;

    @HoverOverlay({"Removes the transparency from mob spawners."})
    @Toggle(label = "Fast Spawner Render")
    @ReloadRenderers
    public boolean fasterSpawnerRendering = false;

    @HoverOverlay({"Makes chests full blocks with no transparency."})
    @Toggle(label = "Fast Chest Render")
    @ReloadRenderers
    public boolean fasterChestRendering = false;

    @PageBreak(label = "World")
    @Toggle(label = "Low Animation Tick")
    public boolean lowAnimationTick = true;

    @ReloadRenderers
    @Toggle(label = "Disable Liquid Rendering")
    public boolean disableLiquids = false;

    @Toggle(label = "Disable Attached Arrows")
    public boolean disableAttachedArrows = false;

    @Toggle(label = "Disable Ground Arrows")
    public boolean disableGroundArrows = false;

    @Toggle(label = "Disable Holograms Inside Blocks")
    public boolean disableHologramsInBlocks = false;

    @Slider(label = "Hologram Render Distance", placeholder = "{value} blocks", minimum = 0.0D, maximum = 64.0D, standard = 64.0D, integers = true)
    public int hologramRenderDistance = 64;

    @Slider(label = "Tile Entity Render Distance", placeholder = "{value} blocks", minimum = 0.0D, maximum = 64.0D, standard = 64.0D, integers = true)
    public int tileEntityRenderDistance = 64;

    @Slider(label = "Tile Entity Tick Rate", placeholder = "Every {value} Ticks", minimum = 1.0D, maximum = 250.0D, standard = 1.0D, integers = true)
    public int tileEntityTickRate = 1;

    @PageBreak(label = "Render")
    @Toggle(label = "Batch Model Rendering")
    public boolean batchModelRendering = true;

    @Toggle(label = "Disable Block Break Particles")
    public boolean disableBlockBreakParticles = false;

    @Toggle(label = "Hide Foliage")
    @ReloadRenderers
    public boolean hideFoliage = false;

    @Toggle(label = "Static Particle Color")
    public boolean staticParticleColor = true;

    @Toggle(label = "Animated End Portals")
    @ReloadRenderers
    public boolean animatedEndPortal = true;

    @HoverOverlay({"DO NOT ENABLE, MAY CAUSE STACKOVERFLOW"})
    @Toggle(label = "Disable Slab Rendering")
    @ReloadRenderers
    public boolean disableSlabRendering = false;

    @Toggle(label = "Disable End Portals")
    @ReloadRenderers
    public boolean disableEndPortals = false;

    @Toggle(label = "Disable Enchantment Glint")
    public boolean disableEnchantmentGlint = false;

    @Toggle(label = "Disable Skull Rendering")
    @ReloadRenderers
    public boolean disableSkulls = false;

    @Toggle(label = "Disable Stacked Items")
    @ReloadRenderers
    public boolean disableStackedItems = false;

    @Toggle(label = "Disable Nausea Effect")
    public boolean disableNausea = false;

    @Toggle(label = "Show own Potion Effect Particles")
    public boolean showOwnParticles = true;

    @Slider(label = "Max Displayed Particle Limit", minimum = 1.0D, maximum = 4000.0D, standard = 4000.0D, integers = true)
    public int maxDisplayedParticleLimit = 4000;

    private static NoLag INSTANCE;

    private static final long LONG_BITS = Double.doubleToLongBits(0.0D);

    private final AbstractDoubleList minimalTnt = new DoubleArrayList(2048);

    private final AbstractDoubleList minimalSand = new DoubleArrayList(2048);

    private long lastClearTime = System.currentTimeMillis();

    public NoLag() {
        this.enabled = true;
        INSTANCE = this;
    }

    public void enable() {
        super.enable();
        if (Client.isCallingFromMainThread())
            this.mc.renderGlobal.loadRenderers();
    }

    public void disable() {
        super.disable();
        this.minimalTnt.clear();
        if (Client.isCallingFromMainThread())
            this.mc.renderGlobal.loadRenderers();
    }

    public static boolean isEnabled(boolean toggle) {
        return (getInstance() != null && (getInstance()).enabled && toggle);
    }

    public static boolean isDisabled(boolean toggle) {
        return (getInstance() != null && (getInstance()).enabled && !toggle);
    }

    public static boolean isSlab(Block block) {
        return (block instanceof BlockHalfWoodSlab || block instanceof BlockHalfStoneSlabNew || block instanceof BlockHalfStoneSlab);
    }

    public static NoLag getInstance() {
        return INSTANCE;
    }

    public void registerEvents() {
        EventBus.register(this, ClientTickEvent.Post.class, ev -> {
            long currentMs = System.currentTimeMillis();
            if (currentMs - this.lastClearTime > 5000L) {
                this.minimalTnt.clear();
                this.minimalSand.clear();
                this.lastClearTime = currentMs;
            }
        });
        EventBus.register(this, EntitySpawnEvent.Pre.class, ev -> {
            if (this.minimal) {
                Entity entity = ev.getEntity();
                double hash = 0.0D;
                if (entity instanceof EntityTNTPrimed) {
                    hash = 17.0D;
                    hash = 31.0D * hash + ((int) entity.posX & 0xFFFFFFFE);
                    hash = 31.0D * hash + ((int) entity.posZ & 0xFFFFFFFE);
                    hash = 31.0D * hash + (int) entity.posY;
                    hash = 31.0D * hash + ModuleHandler.getTotalTicks();
                    hash = 31.0D * hash + ((EntityTNTPrimed) entity).fuse;
                } else if (entity instanceof EntityFallingBlock) {
                    hash = 23.0D;
                    hash = 47.0D * hash + ((int) entity.posX & 0xFFFFFFFE);
                    hash = 47.0D * hash + ((int) entity.posZ & 0xFFFFFFFE);
                    hash = 47.0D * hash + (int) entity.posY;
                    hash = 47.0D * hash + ModuleHandler.getTotalTicks();
                }
                if (Double.doubleToLongBits(hash) != LONG_BITS) {
                    AbstractDoubleList list = (entity instanceof EntityTNTPrimed) ? this.minimalTnt : this.minimalSand;
                    if (list.contains(hash)) {
                        ev.setCancelled(true);
                        return;
                    }
                    list.add(hash);
                }
            }
        });
    }
}