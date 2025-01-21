package co.crystaldev.client.event.impl.render;

import co.crystaldev.client.event.Cancellable;
import co.crystaldev.client.event.Event;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

@Cancellable
public class RenderBlockHighlightEvent extends Event {
    private final RenderGlobal context;

    private final EntityPlayer player;

    private final MovingObjectPosition target;

    private final float partialTicks;

    public RenderBlockHighlightEvent(RenderGlobal context, EntityPlayer player, MovingObjectPosition target, float partialTicks) {
        this.context = context;
        this.player = player;
        this.target = target;
        this.partialTicks = partialTicks;
    }

    public RenderGlobal getContext() {
        return this.context;
    }

    public EntityPlayer getPlayer() {
        return this.player;
    }

    public MovingObjectPosition getTarget() {
        return this.target;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }
}
