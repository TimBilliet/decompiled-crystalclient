package com.github.lunatrius.schematica.handler.client;

import co.crystaldev.client.event.SubscribeEvent;
import co.crystaldev.client.event.impl.init.ShutdownEvent;
import co.crystaldev.client.event.impl.world.WorldEvent;
import com.github.lunatrius.schematica.Schematica;
import com.github.lunatrius.schematica.client.renderer.RenderSchematic;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import com.github.lunatrius.schematica.reference.Reference;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.World;

public class WorldHandler {
    public static WorldHandler INSTANCE = new WorldHandler();

    @SubscribeEvent
    public void onShutdown(ShutdownEvent event) {
        if (ClientProxy.currentSchematic.schematic != null)
            Schematica.proxy.unloadSchematic();
    }

    @SubscribeEvent
    public void onLoad(WorldEvent.Load event) {
        if (event.world.isRemote && !(event.world instanceof com.github.lunatrius.schematica.client.world.SchematicWorld)) {
            RenderSchematic.INSTANCE.setWorldAndLoadRenderers(ClientProxy.currentSchematic.schematic);
            addWorldAccess(event.world, (IWorldAccess) RenderSchematic.INSTANCE);
        }
    }

    @SubscribeEvent
    public void onUnload(WorldEvent.Unload event) {
        if (event.world.isRemote)
            removeWorldAccess(event.world, (IWorldAccess) RenderSchematic.INSTANCE);
    }

    public static void addWorldAccess(World world, IWorldAccess schematic) {
        if (world != null && schematic != null) {
            Reference.logger.debug("Adding world access to {}", new Object[]{world});
            world.addWorldAccess(schematic);
        }
    }

    public static void removeWorldAccess(World world, IWorldAccess schematic) {
        if (world != null && schematic != null) {
            Reference.logger.debug("Removing world access from {}", new Object[]{world});
            world.removeWorldAccess(schematic);
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\handler\client\WorldHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */