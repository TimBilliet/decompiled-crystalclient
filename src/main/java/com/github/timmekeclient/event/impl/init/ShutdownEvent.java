package com.github.timmekeclient.event.impl.init;

import com.github.timmekeclient.event.Event;
import net.minecraft.client.Minecraft;

public class ShutdownEvent extends Event {
    private final Minecraft mc = Minecraft.getMinecraft();

    public Minecraft getMc() {
        return this.mc;
    }
}
