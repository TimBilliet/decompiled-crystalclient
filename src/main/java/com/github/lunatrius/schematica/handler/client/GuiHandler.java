package com.github.lunatrius.schematica.handler.client;

import co.crystaldev.client.event.SubscribeEvent;
import co.crystaldev.client.event.impl.render.GuiScreenEvent;
import com.github.lunatrius.schematica.client.printer.SchematicPrinter;

public class GuiHandler {
    public static final GuiHandler INSTANCE = new GuiHandler();

    @SubscribeEvent
    public void onGuiOpen(GuiScreenEvent.Pre event) {
        if (SchematicPrinter.INSTANCE.isPrinting() &&
                event.gui instanceof net.minecraft.client.gui.inventory.GuiEditSign)
            event.gui = null;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\handler\client\GuiHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */