package com.github.lunatrius.schematica.handler.client;

import co.crystaldev.client.event.SubscribeEvent;
import co.crystaldev.client.event.impl.network.ServerDisconnectEvent;
import co.crystaldev.client.event.impl.tick.ClientTickEvent;
import com.github.lunatrius.schematica.Schematica;
import com.github.lunatrius.schematica.client.printer.SchematicPrinter;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.handler.ConfigurationHandler;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import com.github.lunatrius.schematica.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;

public class TickHandler {
    public static final TickHandler INSTANCE = new TickHandler();

    private final Minecraft minecraft = Minecraft.getMinecraft();

    private int ticks = -1;

    @SubscribeEvent
    public void onClientDisconnect(ServerDisconnectEvent event) {
        Reference.logger.info("Scheduling client settings reset.");
        if (!(co.crystaldev.client.feature.impl.factions.Schematica.getInstance()).persist)
            ClientProxy.isPendingReset = true;
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        if (this.minecraft.isGamePaused())
            return;
        this.minecraft.mcProfiler.startSection("schematica");
        WorldClient world = this.minecraft.theWorld;
        EntityPlayerSP player = this.minecraft.thePlayer;
        SchematicWorld schematic = ClientProxy.currentSchematic.schematic;
        if (world != null && player != null && schematic != null && schematic.isRendering) {
            this.minecraft.mcProfiler.startSection("printer");
            SchematicPrinter printer = SchematicPrinter.INSTANCE;
            if (printer.isEnabled() && printer.isPrinting() && this.ticks-- < 0) {
                this.ticks = ConfigurationHandler.placeDelay;
                printer.print(world, player);
            }
            this.minecraft.mcProfiler.endSection();
        }
        if (ClientProxy.isPendingReset) {
            Schematica.proxy.resetSettings();
            ClientProxy.isPendingReset = false;
            Reference.logger.info("Client settings have been reset.");
        }
        this.minecraft.mcProfiler.endSection();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\handler\client\TickHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */