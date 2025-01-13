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


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\objects\cannonplayback\PlaybackEntityTNTPrimed.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */