package co.crystaldev.client.util.objects;

import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class Vec3d {
  public double x;
  
  public double y;
  
  public double z;
  
  public Vec3d(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  public Vec3d(BlockPos pos) {
    this.x = pos.getX();
    this.y = pos.getY();
    this.z = pos.getZ();
  }
  
  public static Vec3d getNormalizedFromBlockPos(BlockPos pos) {
    return new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
  }
  
  public static Vec3d getFromBlockPos(BlockPos pos) {
    return new Vec3d(MathHelper.floor_double(pos.getX()), MathHelper.floor_double(pos.getY()), MathHelper.floor_double(pos.getZ()));
  }
  
  public void add(double x, double y, double z) {
    this.x += x;
    this.y += y;
    this.z += z;
  }
  
  public int hashCode() {
    int result = 1;
    long temp = Double.doubleToLongBits(this.x);
    result = 31 * result + (int)(temp ^ temp >>> 32L);
    temp = Double.doubleToLongBits(this.y);
    result = 31 * result + (int)(temp ^ temp >>> 32L);
    temp = Double.doubleToLongBits(this.z);
    result = 31 * result + (int)(temp ^ temp >>> 32L);
    return result;
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (obj == null)
      return false; 
    if (getClass() != obj.getClass())
      return false; 
    Vec3d other = (Vec3d)obj;
    return (Double.doubleToLongBits(this.x) == Double.doubleToLongBits(other.x) && 
      Double.doubleToLongBits(this.y) == Double.doubleToLongBits(other.y) && 
      Double.doubleToLongBits(this.z) == Double.doubleToLongBits(other.z));
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\objects\Vec3d.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */