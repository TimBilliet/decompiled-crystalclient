package co.crystaldev.client.util.objects.crumbs;

import co.crystaldev.client.feature.impl.factions.Patchcrumbs;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class Patchcrumb {
    private final long initTime;

    private final BlockPos pos;

    private final AxisAlignedBB boundingBox;

    private final Source source;

    private final Direction direction;

    public long getInitTime() {
        return this.initTime;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }

    public Source getSource() {
        return this.source;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public Patchcrumb(BlockPos pos, AxisAlignedBB boundingBox, Direction direction, Source source) {
        this.pos = pos;
        this.boundingBox = boundingBox;
        this.initTime = System.currentTimeMillis();
        this.source = source;
        this.direction = direction;
    }

    public boolean expired() {
        return (this.initTime + (Patchcrumbs.getInstance()).timeout * 1000L < System.currentTimeMillis());
    }

    public boolean equals(Object obj) {
        if (obj instanceof Patchcrumb) {
            Patchcrumb c = (Patchcrumb) obj;
            return c.pos.equals(this.pos);
        }
        return false;
    }

    public enum Direction {
        NORTH_SOUTH("NORTH/SOUTH"),
        EAST_WEST("EAST/WEST"),
        BOTH("BOTH");

        private final String name;

        Direction(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public static Direction fromString(String in) {
            for (Direction direction : values()) {
                if (direction.name.equals(in))
                    return direction;
            }
            return null;
        }
    }

    public enum Source {
        ENTITY, EXPLOSION, GROUP;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\objects\crumbs\Patchcrumb.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */