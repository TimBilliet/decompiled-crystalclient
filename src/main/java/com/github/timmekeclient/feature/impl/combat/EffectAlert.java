package com.github.timmekeclient.feature.impl.combat;

import com.github.timmekeclient.event.EventBus;
import com.github.timmekeclient.event.IRegistrable;
import com.github.timmekeclient.event.impl.tick.PlayerTickEvent;
import com.github.timmekeclient.feature.annotations.properties.DropdownMenu;
import com.github.timmekeclient.feature.annotations.properties.ModuleInfo;
import com.github.timmekeclient.feature.base.Category;
import com.github.timmekeclient.feature.base.Dropdown;
import com.github.timmekeclient.feature.base.Module;
import net.minecraft.potion.PotionEffect;

import java.util.HashSet;
import java.util.Set;

@ModuleInfo(name = "Effect Alert", description = "Display a text on screen when a certain effect runs out", category = Category.COMBAT)
public class EffectAlert extends Module implements IRegistrable {

    public EffectAlert() {
        enabled = false;
    }

    @DropdownMenu(label = "Effects", values = {"Regeneration 5", "Strength 2", "Speed", "Fire resistance", "Invisibility"}, defaultValues = {"Regeneration 5"}, limitlessSelections = true)
    public Dropdown<String> effects;

    private Set<PotionEffect> previousEffects = new HashSet<>();

    @Override
    public void registerEvents() {
        EventBus.register(this, PlayerTickEvent.Post.class, ev -> {
            if (mc.thePlayer != null) {
                Set<PotionEffect> currentEffects = new HashSet<>(mc.thePlayer.getActivePotionEffects());
                for (PotionEffect effect : previousEffects) {
                    if (!currentEffects.contains(effect)) {
                        if (effects.isSelected("Speed") && effect.getEffectName().equals("potion.moveSpeed")) {
                            mc.ingameGUI.displayTitle("Speed ran out!", "", 0, 0, 0);
                        } else if (effects.isSelected("Invisibility") && effect.getEffectName().equals("potion.invisibility")) {
                            mc.ingameGUI.displayTitle("Invis ran out!", "", 0, 0, 0);
                        } else if (effects.isSelected("Fire resistance") && effect.getEffectName().equals("potion.fireResistance")) {
                            mc.ingameGUI.displayTitle("Fire res ran out!", "", 0, 0, 0);
                        } else if (effects.isSelected("Strength 2") && effect.getEffectName().equals("potion.damageBoost") && effect.getAmplifier() == 1) {
                            mc.ingameGUI.displayTitle("Strength 2 ran out!", "", 0, 0, 0);
                        } else if (effects.isSelected("Regeneration 5") && effect.getEffectName().equals("potion.regeneration") && effect.getAmplifier() == 4) {
                            mc.ingameGUI.displayTitle("Gap removed!", "", 0, 0, 0);
                        }
                    }
                }
                previousEffects = currentEffects;
            }
        });
    }
}
