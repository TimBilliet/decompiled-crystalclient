package co.crystaldev.client.util.javax;

import java.io.Serializable;

public class Point3i extends Tuple3i implements Serializable {
  static final long serialVersionUID = 6149289077348153921L;
  
  public Point3i(int x, int y, int z) {
    super(x, y, z);
  }
  
  public Point3i(int[] t) {
    super(t);
  }
  
  public Point3i(Tuple3i t1) {
    super(t1);
  }
  
  public Point3i() {}
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\javax\Point3i.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */