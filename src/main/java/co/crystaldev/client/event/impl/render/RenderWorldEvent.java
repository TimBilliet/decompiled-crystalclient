package co.crystaldev.client.event.impl.render;

import co.crystaldev.client.event.Event;
import net.minecraft.client.renderer.RenderGlobal;

public class RenderWorldEvent extends Event {
    public final RenderGlobal context;

    public final float partialTicks;

    private RenderWorldEvent(RenderGlobal context, float partialTicks) {
        this.context = context;
        this.partialTicks = partialTicks;
    }

    public static class Pre extends RenderWorldEvent {
        public Pre(RenderGlobal context, float partialTicks) {
            super(context, partialTicks);
        }
    }

    public static class Post extends RenderWorldEvent {
        public Post(RenderGlobal context, float partialTicks) {
            super(context, partialTicks);
        }
    }
}
