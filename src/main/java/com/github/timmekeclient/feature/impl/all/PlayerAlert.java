package com.github.timmekeclient.feature.impl.all;

import com.github.timmekeclient.event.EventBus;
import com.github.timmekeclient.event.IRegistrable;
import com.github.timmekeclient.event.impl.tick.ClientTickEvent;
import com.github.timmekeclient.feature.annotations.HoverOverlay;
import com.github.timmekeclient.feature.annotations.properties.ModuleInfo;
import com.github.timmekeclient.feature.annotations.properties.Slider;
import com.github.timmekeclient.feature.annotations.properties.Toggle;
import com.github.timmekeclient.feature.base.Category;
import com.github.timmekeclient.feature.base.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;

@ModuleInfo(name = "Player Alert", description = "Play an anvil sound when an enemy player is nearby", category = Category.ALL)

public class PlayerAlert extends Module implements IRegistrable {

    @Slider(label = "Checking interval", placeholder = "{value}ms", minimum = 50.0D, maximum = 4000.0D, standard = 1000.0D, integers = true)
    public int interval = 1000;

    @HoverOverlay({"The amount of times an anvil sound is played when the mod detects you're getting raided"})
    @Slider(label = "Amount of sounds played ", minimum = 1, maximum = 100, standard = 20, integers = true)
    public int maxSoundAmount = 20;

    @HoverOverlay({"Only trigger the sound sequence once"})
    @Toggle(label = "Trigger Once")
    public boolean disableOnTrigger = false;

    private int ticksSinceLastCheck = 0;
    private boolean triggered = false;
    private long lastSoundPlayedTime = 0;
    private int soundPlayedAmount = 0;

    public void enable() {
        super.enable();
        triggered = false;
    }

    private void playSounds() {
        long current = System.currentTimeMillis();
        if (current - lastSoundPlayedTime > 500 && soundPlayedAmount < maxSoundAmount && !triggered) {
            lastSoundPlayedTime = current;
            soundPlayedAmount++;
            mc.thePlayer.playSound("random.anvil_land", 1.0f, 1);
        }
        if (soundPlayedAmount >= maxSoundAmount) {
            if (disableOnTrigger)
                triggered = true;
            soundPlayedAmount = 0;
        }
    }

    @Override
    public void registerEvents() {
        EventBus.register(this, ClientTickEvent.Post.class, ev -> {
            if (!triggered) {
                ticksSinceLastCheck++;
                if (ticksSinceLastCheck >= interval / 20) {
                    ticksSinceLastCheck = 0;
                    if (mc.theWorld != null) {
                        for (Entity entity : mc.theWorld.loadedEntityList) {
                            if (!(entity instanceof EntityPlayer) || entity.getName().equals(mc.thePlayer.getName()) || entity.getDistance(mc.thePlayer.getPosition().getX(), mc.thePlayer.getPosition().getY(), mc.thePlayer.getPosition().getZ()) > 40)
                                continue;
                            ScorePlayerTeam scorePlayerTeam = (ScorePlayerTeam) ((EntityPlayer) entity).getTeam();
                            if (scorePlayerTeam != null) {
                                if (!(scorePlayerTeam.getColorPrefix().endsWith("c ") || scorePlayerTeam.getColorPrefix().endsWith("b ")))
                                    continue;
                                playSounds();
                            }
                        }
                    }
                }
            }
        });
    }
}
