package co.crystaldev.client.feature.impl.all;

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
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;

@ModuleInfo(name = "Auto Fish", description = "Auto fish for the enchanted lake on SaicoPvP", category = Category.ALL)

public class AutoFish extends Module implements IRegistrable {

    @Slider(label = "Re-cast delay", placeholder = "{value}ms", minimum = 50.0D, maximum = 800.0D, standard = 100.0D, integers = true)
    public int reCastDelay = 100;

    @HoverOverlay({"Sound mode requires sound to be on, text mode doesn't"})
    @DropdownMenu(label = "Detection mode", values = {"Sound", "Text"}, defaultValues = {"Sound"})
    public Dropdown<String> detectionMode;

    @Toggle(label = "Disable fish sounds")
    public boolean disableSound = false;

    private static final String SOUND_NAME = "random.splash";
    private long castScheduledAt = 0L;
    private static final int TICKS_PER_SECOND = 20;
    private String previousTitle = "Watch";

    public AutoFish() {
        this.enabled = false;
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

    @Override
    public void registerEvents() {
        EventBus.register(this, PlaySoundEvent.class, ev -> {
            if (disableSound && mc.thePlayer != null && isPlayerHoldingRod()
                    && (ev.name.equals(SOUND_NAME) || ev.name.equals("random.bow") || ev.name.contains("game.neutral.swim") || ev.name.equals("random.orb"))) {
                ev.setCancelled(true);
            }
            if (mc.thePlayer != null && detectionMode.isSelected("Sound") && isPlayerHoldingRod() && ev.name.equals(SOUND_NAME)) {
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
