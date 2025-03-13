package com.github.timmekeclient.event.impl.render;

import com.github.timmekeclient.event.Event;
import net.minecraft.client.gui.ScaledResolution;

public class WindowResizeEvent extends Event {
    public final int width;

    public final int height;

    public final ScaledResolution sr;

    public WindowResizeEvent(int width, int height, ScaledResolution sr) {
        this.width = width;
        this.height = height;
        this.sr = sr;
    }
}
