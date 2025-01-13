package com.github.lunatrius.schematica.handler;

import co.crystaldev.client.event.SubscribeEvent;
import co.crystaldev.client.event.impl.init.ConfigEvent;
import com.github.lunatrius.schematica.Schematica;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

public class ConfigurationHandler {
    public static final ConfigurationHandler INSTANCE = new ConfigurationHandler();

    public static final boolean ENABLE_ALPHA_DEFAULT = false;

    public static final double ALPHA_DEFAULT = 1.0D;

    public static final boolean HIGHLIGHT_DEFAULT = true;

    public static final boolean HIGHLIGHT_AIR_DEFAULT = true;

    public static final double BLOCK_DELTA_DEFAULT = 0.005D;

    public static final int RENDER_DISTANCE_DEFAULT = 8;

    public static final int PLACE_DELAY_DEFAULT = 1;

    public static final int TIMEOUT_DEFAULT = 10;

    public static final int PLACE_DISTANCE_DEFAULT = 5;

    public static final boolean PLACE_INSTANTLY_DEFAULT = false;

    public static final boolean DESTROY_BLOCKS_DEFAULT = false;

    public static final boolean DESTROY_INSTANTLY_DEFAULT = false;

    public static final boolean[] SWAP_SLOTS_DEFAULT = new boolean[]{false, false, false, false, true, true, true, true, true};

    public static final String SCHEMATIC_DIRECTORY_STR = "schematics";

    public static final File SCHEMATIC_DIRECTORY_DEFAULT = new File(Schematica.proxy.getDataDirectory(), "schematics");

    public static final String SORT_TYPE_DEFAULT = "";

    public static boolean enableAlpha = false;

    public static float alpha = 1.0F;

    public static boolean highlight = true;

    public static boolean highlightAir = true;

    public static double blockDelta = 0.005D;

    public static int renderDistance = 8;

    public static int placeDelay = 1;

    public static int timeout = 10;

    public static int placeDistance = 5;

    public static boolean placeInstantly = false;

    public static boolean destroyInstantly = false;

    public static boolean[] swapSlots = Arrays.copyOf(SWAP_SLOTS_DEFAULT, SWAP_SLOTS_DEFAULT.length);

    public static final Queue<Integer> swapSlotsQueue = new ArrayDeque<>();

    public static File schematicDirectory = SCHEMATIC_DIRECTORY_DEFAULT;

    public static String sortType = "";

    public static void loadConfiguration() {
        co.crystaldev.client.feature.impl.factions.Schematica instance = co.crystaldev.client.feature.impl.factions.Schematica.getInstance();
        placeDelay = instance.placementDelay;
        timeout = instance.timeout;
        placeDistance = instance.placementDistance;
        placeInstantly = instance.placeInstantly;
        destroyInstantly = instance.destroyInstantly;
        swapSlotsQueue.clear();
        setSlot(0, instance.hotbarSlot1);
        setSlot(1, instance.hotbarSlot2);
        setSlot(2, instance.hotbarSlot3);
        setSlot(3, instance.hotbarSlot4);
        setSlot(4, instance.hotbarSlot5);
        setSlot(5, instance.hotbarSlot6);
        setSlot(6, instance.hotbarSlot7);
        setSlot(7, instance.hotbarSlot8);
        setSlot(8, instance.hotbarSlot9);
        Schematica.proxy.createFolders();
    }

    private static void setSlot(int slot, boolean state) {
        swapSlots[slot] = state;
        if (swapSlots[slot])
            swapSlotsQueue.offer(slot);
    }

    @SubscribeEvent
    public void onConfigurationChangedEvent(ConfigEvent.ModuleSave.Post event) {
        if (event.getModule() instanceof co.crystaldev.client.feature.impl.factions.Schematica)
            loadConfiguration();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\handler\ConfigurationHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */