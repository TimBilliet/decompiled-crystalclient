package com.github.timmekeclient.feature.impl.all;

import com.github.timmekeclient.Reference;
import com.github.timmekeclient.event.EventBus;
import com.github.timmekeclient.event.IRegistrable;
import com.github.timmekeclient.event.impl.network.ChatReceivedEvent;
import com.github.timmekeclient.event.impl.render.RenderOverlayEvent;
import com.github.timmekeclient.event.impl.tick.ClientTickEvent;
import com.github.timmekeclient.event.impl.world.PlaySoundEvent;
import com.github.timmekeclient.feature.annotations.HoverOverlay;
import com.github.timmekeclient.feature.annotations.properties.DropdownMenu;
import com.github.timmekeclient.feature.annotations.properties.ModuleInfo;
import com.github.timmekeclient.feature.annotations.properties.Slider;
import com.github.timmekeclient.feature.annotations.properties.Toggle;
import com.github.timmekeclient.feature.base.Category;
import com.github.timmekeclient.feature.base.Dropdown;
import com.github.timmekeclient.feature.base.Module;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@ModuleInfo(name = "Auto Fish", description = "Auto fish for the enchanted lake on SaicoPvP", category = Category.ALL)

public class AutoFish extends Module implements IRegistrable {

    @Slider(label = "Re-cast delay", placeholder = "{value}ms", minimum = 50.0D, maximum = 800.0D, standard = 120.0D, integers = true)
    public int reCastDelay = 120;

    @HoverOverlay({"Sound mode requires sound to be on, text mode doesn't"})
    @DropdownMenu(label = "Detection mode", values = {"Sound", "Text"}, defaultValues = {"Sound"})
    public Dropdown<String> detectionMode;

    @Toggle(label = "Disable fish sounds")
    public boolean disableSound = false;

    @HoverOverlay({"Automatically store caught fish into open vault"})
    @Toggle(label = "Auto store")
    public boolean autoStore = false;

    @HoverOverlay({"Automatically store fish from this hotbar slot"})
    @Slider(label = "Hotbar slot", minimum = 1.0D, maximum = 9.0D, standard = 3.0D, integers = true)
    public int hotbarSlot = 3;

    @HoverOverlay({"Automatically sell your inventory when a certain amount of slots are filled in your inventory"})
    @Toggle(label = "Auto sell")
    public boolean autoSell = false;

    @HoverOverlay({"Amount of slots before selling"})
    @Slider(label = "Filled Slot Amount", minimum = 1.0D, maximum = 36.0D, standard = 9.0D, integers = true)
    public int slotAmount = 9;

    private static final String SOUND_NAME = "random.splash";
    private long castScheduledAt = 0L;
    private static final int TICKS_PER_SECOND = 20;
    private String previousTitle = "Watch";
    private final Set<String> disabledSounds = new HashSet<>(Arrays.asList(SOUND_NAME, "random.bow", "game.neutral.swim", "game.neutral.swim.splash", "random.orb"));
    private int previousAmount = -1;

    public AutoFish() {
        this.enabled = false;
    }

    public void configPostInit() {
        super.configPostInit();
        setOptionVisibility("Hotbar slot", f -> autoStore);
        setOptionVisibility("Filled Slot Amount", f -> autoSell);
    }

    private boolean isPlayerHoldingRod() {
        ItemStack heldItem = mc.thePlayer.getHeldItem();
        return heldItem != null && heldItem.getItem() instanceof ItemFishingRod;
    }

    private void onPlayerUseItem() {
        if (isPlayerHoldingRod()) {
            if (!isRodCast()) {
                castScheduledAt = 0L;
            }
        }
    }

    private boolean isTimeToCast() {
        return (castScheduledAt > 0 && mc.theWorld.getTotalWorldTime() > castScheduledAt + (reCastDelay / 1000f * TICKS_PER_SECOND));
    }

    private boolean isRodCast() {
        if (!isPlayerHoldingRod()) {
            return false;
        }
        return mc.thePlayer.fishEntity != null;
    }

    private boolean waitingToRecast() {
        return (this.castScheduledAt > 0);
    }

    private void scheduleNextCast() {
        this.castScheduledAt = mc.theWorld.getTotalWorldTime();
    }

    private void playerUseRod() {
        mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
        onPlayerUseItem();
    }

    private String getInventoryName(GuiContainer guiContainer) {
        try {
            if (guiContainer instanceof GuiChest) {
                GuiChest guiChest = (GuiChest) guiContainer;
                IInventory inventory = (guiChest.inventorySlots.getSlot(0)).inventory;
                if (inventory != null) {
                    String inventoryName = inventory.getDisplayName().getUnformattedText();

                    inventoryName = inventoryName.replaceAll("§[0-9a-fA-Fk-or]", "");
                    return inventoryName;
                }
            }
        } catch (Exception e) {
            Reference.LOGGER.error("Error while getting GUI name: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void registerEvents() {
        EventBus.register(this, PlaySoundEvent.class, ev -> {
            if (mc.thePlayer == null)
                return;
            if (disableSound && isPlayerHoldingRod() && (disabledSounds.contains(ev.name))) {
                ev.setCancelled(true);
            }
            if (detectionMode.isSelected("Sound") && isPlayerHoldingRod() && ev.name.equals(SOUND_NAME)) {
                playerUseRod();
                scheduleNextCast();
            }
        });
        EventBus.register(this, ClientTickEvent.Post.class, ev -> {
            if (!mc.isGamePaused() && mc.thePlayer != null) {
                if (isPlayerHoldingRod() || waitingToRecast()) {
                    if (isTimeToCast()) {
                        if (isPlayerHoldingRod()) {
                            playerUseRod();
                        }
                        castScheduledAt = 0L;
                    }
                }
            }
            if (autoSell && mc.thePlayer != null && isPlayerHoldingRod()) {
                InventoryPlayer inv = mc.thePlayer.inventory;
                int amount = 0;
                for (int i = 0; i < inv.getSizeInventory(); i++) {
                    ItemStack stack = inv.getStackInSlot(i);
                    if (stack != null && stack.stackSize > 0) {
                        amount++;
                    }
                }
                if(previousAmount != amount) {
                    if (amount >= slotAmount) {
                        mc.thePlayer.sendChatMessage("/sell inv");
                    }
                    previousAmount = amount;
                }
            }
            if (autoStore && mc.thePlayer != null && isPlayerHoldingRod() && mc.currentScreen instanceof GuiContainer) {
                GuiContainer guiContainer = (GuiContainer) mc.currentScreen;
                String inventoryName = getInventoryName(guiContainer);
                if (inventoryName != null && (inventoryName.contains("Vault #") || inventoryName.contains("Ender Chest"))) {
                    Container container = mc.thePlayer.openContainer;
                    if (container != null) {
                        int hotbarSlotIndex = container.inventorySlots.size() - 10 + hotbarSlot;
                        ItemStack stack = mc.thePlayer.inventory.getStackInSlot(hotbarSlot - 1);
                        if (stack != null && stack.getItem() != null && stack.stackSize > 0) {
                            mc.playerController.windowClick(guiContainer.inventorySlots.windowId, hotbarSlotIndex, 0, 1, this.mc.thePlayer);
                        }
                    }
                }
            }
        });
        EventBus.register(this, RenderOverlayEvent.Title.class, ev -> {
            if (mc.thePlayer != null && detectionMode.isSelected("Text")) {
                if (previousTitle.contains("Watch") && ev.getSubTitle().contains("Biting") && isPlayerHoldingRod()) {
                    playerUseRod();
                    scheduleNextCast();
                }
                previousTitle = ev.getSubTitle();
            }
        });
        EventBus.register(this, ChatReceivedEvent.class, ev -> {
            String message = ev.message.getUnformattedText();
            if (!mc.isGamePaused() && mc.thePlayer != null && isRodCast() && message.contains("(!) Removed ")) {
                playerUseRod();
            }
        });
    }
}
