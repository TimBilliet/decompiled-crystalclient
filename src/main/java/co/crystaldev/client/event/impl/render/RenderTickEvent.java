package co.crystaldev.client.event.impl.render;

import co.crystaldev.client.event.Event;

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


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\impl\render\RenderTickEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */