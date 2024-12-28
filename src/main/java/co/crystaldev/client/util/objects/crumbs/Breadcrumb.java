package co.crystaldev.client.util.objects.crumbs;

import co.crystaldev.client.feature.impl.factions.Breadcrumbs;
import co.crystaldev.client.util.objects.Vec3d;

import java.util.ArrayList;

public class Breadcrumb {
  public final int id;
  
  public final long initTime;
  
  public final Type type;
  
  public final ArrayList<Vec3d> locations = new ArrayList<>();
  
  public Breadcrumb(int id, double x, double y, double z, Type type) {
    this.id = id;
    this.locations.add(new Vec3d(x, y, z));
    this.type = type;
    this.initTime = System.currentTimeMillis();
  }
  
  public boolean expired() {
    return (this.initTime + (Breadcrumbs.getInstance()).timeout * 1000L < System.currentTimeMillis());
  }
  
  public void addLocation(double x, double y, double z) {
    this.locations.add(new Vec3d(x, y, z));
  }
  
  public int hashCode() {
    int result = 1;
    long temp = Integer.toUnsignedLong(this.id);
    result = 31 * result + (int)(temp ^ temp >>> 32L);
    temp = this.initTime;
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
    Breadcrumb other = (Breadcrumb)obj;
    return (Integer.toUnsignedLong(this.id) == Integer.toUnsignedLong(other.id) && this.initTime == other.initTime);
  }
  
  public enum Type {
    TNT, SAND, ENDER_PEARL;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\objects\crumbs\Breadcrumb.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */