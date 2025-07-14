package com.github.timmekeclient.feature.impl.all;

import com.github.timmekeclient.Client;
import com.github.timmekeclient.event.EventBus;
import com.github.timmekeclient.event.IRegistrable;
import com.github.timmekeclient.event.impl.tick.ClientTickEvent;
import com.github.timmekeclient.feature.annotations.ReloadRenderers;
import com.github.timmekeclient.feature.annotations.properties.ModuleInfo;
import com.github.timmekeclient.feature.annotations.properties.Toggle;
import com.github.timmekeclient.feature.base.Category;
import com.github.timmekeclient.feature.base.Module;
import com.github.timmekeclient.handler.NotificationHandler;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@ModuleInfo(name = "Farming", description = "Helpful features for farming crops", category = Category.ALL)
public class Farming extends Module implements IRegistrable {
    @Toggle(label = "Avoid Breaking Bottom Cane")
    public boolean avoidBreakingBottomCane = true;

    @Toggle(label = "Cac Mod")
    public boolean cacMod = false;

    @ReloadRenderers
    @Toggle(label = "Cactus Mode")
    public boolean cactusMode = false;

    @ReloadRenderers
    @Toggle(label = "Disable Cactus Rendering")
    public boolean disableCactusRendering = false;

    @ReloadRenderers
    @Toggle(label = "Disable String Rendering")
    public boolean disableStringRendering = false;

    private static Farming INSTANCE;

    private int currentChunkX;

    private int currentChunkZ;

    private long lastCaneMessage;

    private long lastChunkMessage;

    public Farming() {
        INSTANCE = this;
    }

    public boolean onClickBlock(BlockPos loc) {
        boolean flag = false;
        if (this.enabled && this.avoidBreakingBottomCane) {
            Block block = this.mc.theWorld.getBlockState(loc).getBlock();
            if (block instanceof net.minecraft.block.BlockReed) {
                Block blockBelow = this.mc.theWorld.getBlockState(loc.down()).getBlock();
                if (!(blockBelow instanceof net.minecraft.block.BlockReed)) {
                    flag = true;
                    if (System.currentTimeMillis() - this.lastCaneMessage > 5000L) {
                        this.lastCaneMessage = System.currentTimeMillis();
                        NotificationHandler.addNotification("Farming Module", "The client has prevented you from breaking a bottom cane block");
                    }
                }
            }
        }
        return flag;
    }

    public boolean onPlayerRightClick(BlockPos loc, EnumFacing dir) {
        if (enabled && cacMod) {
            if (mc.thePlayer == null)
                return false;
            ItemStack stack = mc.thePlayer.getHeldItem();
            if (stack != null) {
                Item item = stack.getItem();
                if (item != null) {
                    Block block = this.mc.theWorld.getBlockState(loc).getBlock();
                    return (Item.getIdFromItem(item) == 12 && (block instanceof net.minecraft.block.BlockCactus || block instanceof net.minecraft.block.BlockSand || (block instanceof net.minecraft.block.BlockTripWire && !dir.equals(EnumFacing.UP))))
                            || (Item.getIdFromItem(item) == 287 && (block instanceof net.minecraft.block.BlockTripWire || block instanceof net.minecraft.block.BlockSand || (block instanceof net.minecraft.block.BlockCactus && dir.equals(EnumFacing.UP))))
                            || (Item.getIdFromItem(item) == 81 && (block instanceof net.minecraft.block.BlockCactus));

                }
            }

        }
        return false;
    }


    public void enable() {
        super.enable();
        if (Client.isCallingFromMainThread())
            this.mc.renderGlobal.loadRenderers();
    }

    public void disable() {
        super.disable();
        if (Client.isCallingFromMainThread())
            this.mc.renderGlobal.loadRenderers();
    }

    public static Farming getInstance() {
        return INSTANCE;
    }

    public void registerEvents() {
        EventBus.register(this, ClientTickEvent.Post.class, ev -> {
            if (this.cactusMode && this.mc.thePlayer != null && (this.mc.thePlayer.chunkCoordX != this.currentChunkX || this.mc.thePlayer.chunkCoordZ != this.currentChunkZ)) {
                this.currentChunkX = this.mc.thePlayer.chunkCoordX;
                this.currentChunkZ = this.mc.thePlayer.chunkCoordZ;
                this.mc.renderGlobal.loadRenderers();
                if (System.currentTimeMillis() - this.lastChunkMessage > 120000L) {
                    NotificationHandler.addNotification("Farming Module", "Chunks were reloaded due to you entering a new chunk and the toggle 'Cactus Mode' being enabled", 10000L);
                    this.lastChunkMessage = System.currentTimeMillis();
                }
            }
        });
    }
}
