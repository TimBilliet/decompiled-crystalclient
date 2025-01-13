package co.crystaldev.client.event.impl.world;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ExplosionEvent extends WorldEvent {
    public final Entity entity;

    public final double posX;

    public final double posY;

    public final double posZ;

    public final float size;

    public final boolean isFlaming;

    public final boolean isSmoking;

    public ExplosionEvent(World world, Entity entity, double posX, double posY, double posZ, float size, boolean isFlaming, boolean isSmoking) {
        super(world);
        this.entity = entity;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.size = size;
        this.isFlaming = isFlaming;
        this.isSmoking = isSmoking;
    }

    public String toString() {
        String entityName = (this.entity == null) ? "null" : this.entity.getClass().getSimpleName();
        return String.format("Entity: %s\nPos: x%s y%s z%s\nSize: %s\nFlaming: %s, Smoking: %s", entityName,

                this.posX, Double.valueOf(this.posY), Double.valueOf(this.posZ), this.size,
                Boolean.valueOf(this.isFlaming), Boolean.valueOf(this.isSmoking));
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\impl\world\ExplosionEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */