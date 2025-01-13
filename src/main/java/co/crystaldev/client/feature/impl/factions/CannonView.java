package co.crystaldev.client.feature.impl.factions;

import co.crystaldev.client.Client;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.init.InitializationEvent;
import co.crystaldev.client.event.impl.network.ServerDisconnectEvent;
import co.crystaldev.client.event.impl.render.RenderPlayerEvent;
import co.crystaldev.client.event.impl.world.WorldEvent;
import co.crystaldev.client.feature.annotations.properties.Keybind;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Module;
import net.minecraft.block.*;
import net.minecraft.client.settings.KeyBinding;

import java.util.Arrays;
import java.util.List;

@ModuleInfo(name = "Cannon View", description = "Allows you to see through non-cannon blocks", category = Category.FACTIONS)
public class CannonView extends Module implements IRegistrable {
    @Keybind(label = "Toggle Keybinding")
    public KeyBinding keybind = new KeyBinding("crystalclient.key.toggle_cannon_view", 0, "Crystal Client");

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

    public static CannonView getInstance() {
        return INSTANCE;
    }

    public void registerEvents() {
        EventBus.register(this, InitializationEvent.class, ev -> this.enabled = false);
        EventBus.register(this, RenderPlayerEvent.Pre.class, ev -> ev.setCancelled(!ev.player.getUniqueID().equals(this.mc.thePlayer.getUniqueID())));
        EventBus.register(this, WorldEvent.Load.class, ev -> disable());
        EventBus.register(this, ServerDisconnectEvent.class, ev -> disable());
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\factions\CannonView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */