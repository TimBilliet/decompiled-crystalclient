package com.github.timmekeclient.event.impl.render;

import com.github.timmekeclient.event.Event;

public class RenderTickEvent extends Event {
    public final float partialTicks;

    private RenderTickEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public static class Pre extends RenderTickEvent {
        public Pre(float partialTicks) {
            super(partialTicks);
        }
    }

    public static class Post extends RenderTickEvent {
        public Post(float partialTicks) {
            super(partialTicks);
        }
    }
}
