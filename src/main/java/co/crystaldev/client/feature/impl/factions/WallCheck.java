package co.crystaldev.client.feature.impl.factions;


import co.crystaldev.client.Client;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.network.ChatReceivedEvent;
import co.crystaldev.client.event.impl.tick.ClientTickEvent;
import co.crystaldev.client.feature.annotations.HoverOverlay;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.Slider;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Module;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Random;

@ModuleInfo(name = "Wall Check", description = "Auto wallcheck for SaicoPvP using water gen bucket", category = Category.FACTIONS)
public class WallCheck extends Module implements IRegistrable {

    @Slider(label = "Checking Interval", placeholder = "{value}s", minimum = 10, maximum = 500, standard = 120, integers = true)
    public int checkingInterval = 120;

    @HoverOverlay({"The maximum amount of random delay deviation from the original checking delay. Used to make it less obvious"})
    @Slider(label = "Random delay range", placeholder = "{value}s", minimum = 0, maximum = 300, standard = 60, integers = true)
    public int randomTime = 60;

    @HoverOverlay({"The amount of times an anvil sound is placed when the mod detects you're getting raided"})
    @Slider(label = "Amount of sounds played ", minimum = 1, maximum = 150, standard = 20, integers = true)
    public int maxSoundAmount = 20;

    private long lastCheckTime = 0;
    private boolean gettingRaided = false;
    private long lastSoundPlayedTime = 0;
    private int soundPlayedAmount = 0;
    private int randomdelay = randomTime;

    public void enable() {
        super.enable();
        gettingRaided = false;
    }

    public void onClientTick(ClientTickEvent.Post ev) {
        if (mc.thePlayer == null)
            return;
        if (System.currentTimeMillis() - lastCheckTime > ((checkingInterval - randomTime / 2.0) + randomdelay) * 1000) {
            ItemStack stack = mc.thePlayer.getHeldItem();
            if (stack != null) {
                Item item = stack.getItem();
                if (item != null && Item.getIdFromItem(item) == 326 && stack.getDisplayName().contains("Infinity Bucket") && stack.getDisplayName().contains("Water")) {
                    mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, stack);
                }
            }
            this.lastCheckTime = System.currentTimeMillis();
            Random random = new Random();
            if(randomTime > 0)
                randomdelay = random.nextInt(randomTime + 1);
            else
                randomdelay = 0;
        }
        playGettingRaidedSounds();
    }


    private void playGettingRaidedSounds() {
        long current = System.currentTimeMillis();
        if (current - lastSoundPlayedTime > 1500 && soundPlayedAmount < maxSoundAmount && gettingRaided) {
            lastSoundPlayedTime = current;
            soundPlayedAmount++;
            mc.thePlayer.playSound("random.anvil_land", 1.0f, 1);
        }
        if (soundPlayedAmount >= maxSoundAmount) {
            gettingRaided = false;
            soundPlayedAmount = 0;
        }
    }

    @Override
    public void registerEvents() {
        EventBus.register(this, ClientTickEvent.Post.class, this::onClientTick);
        EventBus.register(this, ChatReceivedEvent.class, ev -> {
            String message = ev.message.getUnformattedText();
            if (message.contains("place gen buckets while you are being Raided")) {
                gettingRaided = true;
            }
        });
    }
}
