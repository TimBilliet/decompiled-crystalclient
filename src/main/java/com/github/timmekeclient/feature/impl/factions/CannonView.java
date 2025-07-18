package com.github.timmekeclient.feature.impl.factions;

import com.github.timmekeclient.Client;
import com.github.timmekeclient.event.EventBus;
import com.github.timmekeclient.event.IRegistrable;
import com.github.timmekeclient.event.impl.init.InitializationEvent;
import com.github.timmekeclient.event.impl.network.ServerDisconnectEvent;
import com.github.timmekeclient.event.impl.render.RenderPlayerEvent;
import com.github.timmekeclient.event.impl.world.WorldEvent;
import com.github.timmekeclient.feature.annotations.HoverOverlay;
import com.github.timmekeclient.feature.annotations.properties.Keybind;
import com.github.timmekeclient.feature.annotations.properties.ModuleInfo;
import com.github.timmekeclient.feature.annotations.properties.Toggle;
import com.github.timmekeclient.feature.base.Category;
import com.github.timmekeclient.feature.base.Module;
import net.minecraft.block.*;
import net.minecraft.client.settings.KeyBinding;

import java.util.Arrays;
import java.util.List;

@ModuleInfo(name = "Cannon View", description = "Allows you to see through non-cannon blocks", category = Category.FACTIONS)
public class CannonView extends Module implements IRegistrable {
    @Keybind(label = "Toggle Keybinding")
    public KeyBinding keybind = new KeyBinding("timmekeclient.key.toggle_cannon_view", 0, "Timmeke_ Client");

    @HoverOverlay({"Still render players while having cannonview enabled"})
    @Toggle(label = "Render Players")
    public boolean renderPlayers = true;

    @HoverOverlay({"Show a message in chat when toggling the mod"})
    @Toggle(label = "Show Toggle Tessage")
    public boolean showInChat = false;

    private static CannonView INSTANCE;

    public static final List<Class<? extends Block>> BLOCKS = Arrays.asList(BlockButtonStone.class, BlockButtonWood.class, BlockCarpet.class, BlockLadder.class, BlockLever.class, BlockPistonBase.class, BlockPistonExtension.class, BlockPistonMoving.class, BlockRedstoneComparator.class, BlockRedstoneRepeater.class,
            BlockRedstoneTorch.class, BlockRedstoneWire.class, BlockSlime.class, BlockStairs.class, BlockTrapDoor.class, BlockMobSpawner.class, BlockChest.class, BlockEnderChest.class, BlockWorkbench.class, BlockBrewingStand.class);

    public CannonView() {
        this.enabled = false;
        INSTANCE = this;
        this.toggleKeyBinding = this.keybind;
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

    @Override
    public void onModuleToggle() {
        if(showInChat)
            Client.sendMessage(getToggleMessage(this.name, this.enabled), true);
    }

    public static CannonView getInstance() {
        return INSTANCE;
    }

    public void registerEvents() {
        EventBus.register(this, InitializationEvent.class, ev -> this.enabled = false);
        EventBus.register(this, RenderPlayerEvent.Pre.class, ev -> {
            if (!renderPlayers)
                ev.setCancelled(!ev.player.getUniqueID().equals(this.mc.thePlayer.getUniqueID()));
        });
        EventBus.register(this, WorldEvent.Load.class, ev -> disable());
        EventBus.register(this, ServerDisconnectEvent.class, ev -> disable());
    }
}
