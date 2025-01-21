package co.crystaldev.client.event.impl.render;

import co.crystaldev.client.event.Cancellable;
import co.crystaldev.client.event.impl.player.PlayerEvent;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;

public class RenderPlayerEvent extends PlayerEvent {
    public final RenderPlayer renderer;

    public final float partialTicks;

    public final double x;

    public final double y;

    public final double z;

    private RenderPlayerEvent(EntityPlayer player, RenderPlayer renderer, float partialTicks, double x, double y, double z) {
        super(player);
        this.renderer = renderer;
        this.partialTicks = partialTicks;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Cancellable
    public static class Pre extends RenderPlayerEvent {
        public Pre(EntityPlayer player, RenderPlayer renderer, float partialTicks, double x, double y, double z) {
            super(player, renderer, partialTicks, x, y, z);
        }
    }

    public static class Post extends RenderPlayerEvent {
        public Post(EntityPlayer player, RenderPlayer renderer, float partialTicks, double x, double y, double z) {
            super(player, renderer, partialTicks, x, y, z);
        }
    }
}
