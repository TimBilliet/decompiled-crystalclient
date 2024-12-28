package co.crystaldev.client.util.objects;

import com.google.gson.annotations.SerializedName;
import net.minecraft.util.EnumFacing;

public class Transformation {
  @SerializedName("type")
  private final Type type;
  
  @SerializedName("direction")
  private final EnumFacing direction;
  
  @SerializedName("x")
  private final int x;
  
  @SerializedName("y")
  private final int y;
  
  @SerializedName("z")
  private final int z;
  
  public String toString() {
    return "Transformation(type=" + getType() + ", direction=" + getDirection() + ", x=" + getX() + ", y=" + getY() + ", z=" + getZ() + ")";
  }
  
  public Type getType() {
    return this.type;
  }
  
  public EnumFacing getDirection() {
    return this.direction;
  }
  
  public int getX() {
    return this.x;
  }
  
  public int getY() {
    return this.y;
  }
  
  public int getZ() {
    return this.z;
  }
  
  public Transformation(Type type, EnumFacing direction, int x, int y, int z) {
    this.type = type;
    this.direction = direction;
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  public enum Type {
    ROTATION("ROTATION"),
    FLIP("FLIP");
    
    Type(String fmt) {
      this.fmt = fmt;
    }
    
    private final String fmt;
    
    public String toString() {
      return this.fmt;
    }
    
    public static Type fromFmt(String in) {
      for (Type value : values()) {
        if (value.toString().equalsIgnoreCase(in))
          return value; 
      } 
      return ROTATION;
    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\objects\Transformation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */