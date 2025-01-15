package co.crystaldev.client.util.objects.cannonplayback;

import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.world.World;

public class PlaybackEntityTNTPrimed extends EntityTNTPrimed {
    public PlaybackEntityTNTPrimed(World world, double x, double y, double z) {
        super(world, x, y, z, null);
        this.fuse = 80;
    }

    public void moveEntity(double x, double y, double z) {
    }

    public void onUpdate() {
    }
}