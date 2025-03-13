package com.github.timmekeclient.feature.impl.mechanic;

import com.github.timmekeclient.event.EventBus;
import com.github.timmekeclient.event.IRegistrable;
import com.github.timmekeclient.event.impl.network.PacketReceivedEvent;
import com.github.timmekeclient.event.impl.tick.ClientTickEvent;
import com.github.timmekeclient.feature.annotations.properties.DropdownMenu;
import com.github.timmekeclient.feature.annotations.properties.ModuleInfo;
import com.github.timmekeclient.feature.annotations.properties.Slider;
import com.github.timmekeclient.feature.base.Category;
import com.github.timmekeclient.feature.base.Dropdown;
import com.github.timmekeclient.feature.base.Module;

@ModuleInfo(name = "Time Changer", description = "Forces the global time to your selected time", category = Category.MECHANIC)
public class TimeChanger extends Module implements IRegistrable {
    @DropdownMenu(label = "State", values = {"Day", "Sunset", "Night", "Vanilla", "Custom"}, defaultValues = {"Day"})
    public Dropdown<String> state;

    @Slider(label = "Current Time", placeholder = "Tick {value}", minimum = 0.0D, maximum = 24000.0D, standard = 6000.0D, integers = true)
    public int timeOfDay = 6000;

    @Slider(label = "Multiplier", placeholder = "{value}x", minimum = 1.0D, maximum = 10.0D, standard = 1.0D)
    public double multiplier = 1.0D;

    public TimeChanger() {
        this.enabled = false;
    }

    public void configPostInit() {
        super.configPostInit();
        setOptionVisibility("Current Time", f -> ((String) this.state.getCurrentSelection()).equalsIgnoreCase("Custom"));
        setOptionVisibility("Multiplier", f -> ((String) this.state.getCurrentSelection()).equalsIgnoreCase("Vanilla"));
    }

    public void disable() {
        this.mc.theWorld.setWorldTime(0L);
        super.disable();
    }

    public void registerEvents() {
        EventBus.register(this, PacketReceivedEvent.Pre.class, ev -> {
            if (ev.packet instanceof net.minecraft.network.play.server.S03PacketTimeUpdate)
                ev.setCancelled(true);
        });
        EventBus.register(this, ClientTickEvent.Pre.class, ev -> {
            if (this.mc.theWorld != null) {
                switch (((String) this.state.getCurrentSelection()).toLowerCase()) {
                    case "day":
                        this.mc.theWorld.setWorldTime(-6000L);
                        return;
                    case "sunset":
                        this.mc.theWorld.setWorldTime(-22880L);
                        return;
                    case "night":
                        this.mc.theWorld.setWorldTime(-18000L);
                        return;
                    case "vanilla":
                        this.mc.theWorld.setWorldTime((long) ((System.currentTimeMillis() / 50L) * this.multiplier % 24000.0D));
                        return;
                }
                this.mc.theWorld.setWorldTime(this.timeOfDay);
            }
        });
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\mechanic\TimeChanger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */