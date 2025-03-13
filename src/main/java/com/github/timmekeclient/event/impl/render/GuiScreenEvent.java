package com.github.timmekeclient.event.impl.render;

import com.github.timmekeclient.event.Cancellable;
import com.github.timmekeclient.event.Event;
import net.minecraft.client.gui.GuiScreen;

public class GuiScreenEvent extends Event {
    public GuiScreen gui;

    private GuiScreenEvent(GuiScreen gui) {
        this.gui = gui;
    }

    @Cancellable
    public static class Pre extends GuiScreenEvent {
        public final GuiScreen oldScreen;

        public Pre(GuiScreen gui, GuiScreen oldScreen) {
            super(gui);
            this.oldScreen = oldScreen;
        }
    }

    public static class Post extends GuiScreenEvent {
        public Post(GuiScreen gui) {
            super(gui);
        }
    }
}
