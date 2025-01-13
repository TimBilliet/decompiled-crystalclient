package co.crystaldev.client.util.objects.wecui;

import net.minecraft.util.BlockPos;

import java.util.HashMap;

public class Selection {
    private final Type type;

    public Type getType() {
        return this.type;
    }

    private final HashMap<Integer, BlockPos> points = new HashMap<>();

    private int size;

    public HashMap<Integer, BlockPos> getPoints() {
        return this.points;
    }

    public int getSize() {
        return this.size;
    }

    public Selection(String type) {
        this.type = Type.valueOf(type.toUpperCase());
    }

    public void addPoint(int id, int x, int y, int z, int size) {
        this.points.put(id, new BlockPos(x, y, z));
        this.size = size;
    }

    public enum Type {
        CUBOID, ELLIPSOID, CYLINDER, POLYGON2D, POLYHEDRON;
    }

    public String toString() {
        return "Selection{type=" + this.type.toString() + ",points=" + this.points + "}";
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\objects\wecui\Selection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */