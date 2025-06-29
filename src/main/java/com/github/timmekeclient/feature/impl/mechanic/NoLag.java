package com.github.timmekeclient.feature.impl.mechanic;


import com.github.timmekeclient.Client;
import com.github.timmekeclient.event.EventBus;
import com.github.timmekeclient.event.IRegistrable;
import com.github.timmekeclient.event.impl.entity.EntitySpawnEvent;
import com.github.timmekeclient.event.impl.network.PacketReceivedEvent;
import com.github.timmekeclient.event.impl.tick.ClientTickEvent;
import com.github.timmekeclient.feature.annotations.HoverOverlay;
import com.github.timmekeclient.feature.annotations.ReloadRenderers;
import com.github.timmekeclient.feature.annotations.properties.ModuleInfo;
import com.github.timmekeclient.feature.annotations.properties.PageBreak;
import com.github.timmekeclient.feature.annotations.properties.Slider;
import com.github.timmekeclient.feature.annotations.properties.Toggle;
import com.github.timmekeclient.feature.base.Category;
import com.github.timmekeclient.feature.base.Module;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHalfStoneSlab;
import net.minecraft.block.BlockHalfStoneSlabNew;
import net.minecraft.block.BlockHalfWoodSlab;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.network.play.server.S0EPacketSpawnObject;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(name = "No Lag", nameAliases = {"FPS"}, description = "Improve game performance", category = Category.MECHANIC)
public class NoLag extends Module implements IRegistrable {
    @PageBreak(label = "Cannoning")
    @HoverOverlay({"Stops TNT from rendering."})
    @Toggle(label = "Hide TNT")
    public boolean hideTnt = false;

    @HoverOverlay({"Stops Falling Blocks from rendering."})
    @Toggle(label = "Hide Sand")
    public boolean hideSand = false;

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

    private final List<Spedtity> spawnedEntities;
    private final List<Duplicates> duplicates;

    public NoLag() {
        this.enabled = true;
        spawnedEntities = new ArrayList<>();
        duplicates = new ArrayList<>();
        INSTANCE = this;
    }

    public void enable() {
        super.enable();
        if (Client.isCallingFromMainThread())
            this.mc.renderGlobal.loadRenderers();
    }

    public void disable() {
        super.disable();
        spawnedEntities.clear();
        duplicates.clear();
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
            spawnedEntities.clear();
            duplicates.clear();
        });
        EventBus.register(this, ClientTickEvent.Pre.class, ev -> {
            if (minimal && mc.thePlayer != null && mc.theWorld != null) {
                List<EntityTNTPrimed> tnts = mc.theWorld.getEntities(EntityTNTPrimed.class, tnt -> true);
                for(EntityTNTPrimed tnt : tnts){
                    int ticks = minimal ? 79 : tnt.ticksExisted;
                    Duplicates duplicate = new Duplicates(50,ticks,tnt.posX, tnt.posY, tnt.posZ);
                    if(duplicates.contains(duplicate)){
                        tnt.setDead();
                        continue;
                    }
                    duplicates.add(duplicate);
                }
                List<EntityFallingBlock> sands = mc.theWorld.getEntities(EntityFallingBlock.class, sand -> true);
                for(EntityFallingBlock sand : sands){
                    int ticks = minimal ? 79 : sand.ticksExisted;
                    Duplicates duplicate = new Duplicates(70,ticks,sand.posX, sand.posY, sand.posZ);
                    if(duplicates.contains(duplicate)){
                        sand.setDead();
                        continue;
                    }
                    duplicates.add(duplicate);
                }
            }
        });
        EventBus.register(this, PacketReceivedEvent.Pre.class, ev -> {
            if (ev.packet instanceof S0EPacketSpawnObject) {
                S0EPacketSpawnObject p = (S0EPacketSpawnObject) ev.packet;
                Spedtity spedtity = new Spedtity(p.getX(), p.getY(), p.getZ(), p.getType());
                if (spawnedEntities.contains(spedtity)) {
                    ev.setCancelled(true);
                }
                spawnedEntities.add(spedtity);
            }
        });
        EventBus.register(this, EntitySpawnEvent.Pre.class, ev -> {
            if (this.minimal) {
                Entity entity = ev.getEntity();
                if (entity instanceof EntityTNTPrimed || entity instanceof EntityFallingBlock) {
                    int type = entity instanceof EntityTNTPrimed ? 50 : 70;
                    Spedtity spedtity = new Spedtity(entity.posX, entity.posY, entity.posZ, type);
                    if (spawnedEntities.contains(spedtity)) {
                        entity.setDead();
                        ev.setCancelled(true);
                    }
                    spawnedEntities.add(spedtity);
                }
            }
        });
    }
}