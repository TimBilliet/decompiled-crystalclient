package co.crystaldev.client.util;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3i;

public class BoundingBox extends AxisAlignedBB {
  public static final BoundingBox INFINITE_EXTENT_AABB = new BoundingBox(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
  
  public BoundingBox(double x1, double y1, double z1, double x2, double y2, double z2) {
    super(x1, y1, z1, x2, y2, z2);
  }
  
  public BoundingBox(BlockPos pos1, BlockPos pos2) {
    super(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ());
  }
  
  public BoundingBox(Vec3i pos1, Vec3i pos2) {
    this(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ());
  }
  
  public BoundingBox addMax(double x, double y, double z) {
    double d0 = this.maxX + x;
    double d1 = this.maxY + y;
    double d2 = this.maxZ + z;
    return new BoundingBox(this.minX, this.minY, this.minZ, d0, d1, d2);
  }
  
  public BoundingBox addMin(double x, double y, double z) {
    double d0 = this.minX + x;
    double d1 = this.minY + y;
    double d2 = this.minZ + z;
    return new BoundingBox(d0, d1, d2, this.maxX, this.maxY, this.maxZ);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\BoundingBox.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */