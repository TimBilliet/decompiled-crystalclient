package co.crystaldev.client.feature.impl.all;

import co.crystaldev.client.Reference;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.render.RenderOverlayEvent;
import co.crystaldev.client.event.impl.tick.ClientTickEvent;
import co.crystaldev.client.event.impl.world.PlaySoundEvent;
import co.crystaldev.client.feature.annotations.HoverOverlay;
import co.crystaldev.client.feature.annotations.properties.DropdownMenu;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.Slider;
import co.crystaldev.client.feature.annotations.properties.Toggle;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Dropdown;
import co.crystaldev.client.feature.base.Module;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
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

    private static final String SOUND_NAME = "random.splash";
    private long castScheduledAt = 0L;
    private static final int TICKS_PER_SECOND = 20;
    private String previousTitle = "Watch";
    private final Set<String> disabledSounds = new HashSet<>(Arrays.asList(SOUND_NAME, "random.bow", "game.neutral.swim", "game.neutral.swim.splash", "random.orb"));
    private boolean isGuiOpened = false;

    public AutoFish() {
        this.enabled = false;
    }

    public void configPostInit() {
        super.configPostInit();
        setOptionVisibility("Hotbar slot", f -> this.autoStore);
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
            if (!autoStore || mc.theWorld == null)
                return;
            if (mc.currentScreen instanceof GuiContainer) {
                GuiContainer guiContainer = (GuiContainer) mc.currentScreen;
                String inventoryName = getInventoryName(guiContainer);
                if (inventoryName != null && (inventoryName.contains("Vault #") || inventoryName.contains("Ender Chest"))) {
                    int hotbarSlotIndex = (inventoryName.contains("Vault #") ? 80 : 53) + hotbarSlot;
                    ItemStack stack = mc.thePlayer.inventory.getStackInSlot(hotbarSlot - 1);
                    if (stack != null && stack.getItem() != null && stack.stackSize > 0) {
                        mc.playerController.windowClick(guiContainer.inventorySlots.windowId, hotbarSlotIndex, 0, 1, this.mc.thePlayer);
                    }
                }
            } else if (this.isGuiOpened) {
                this.isGuiOpened = false;
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
    }
}
