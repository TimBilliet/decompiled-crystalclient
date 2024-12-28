package co.crystaldev.client.util.javax;

import java.io.Serializable;

public class Point4i extends Tuple4i implements Serializable {
  static final long serialVersionUID = 620124780244617983L;
  
  public Point4i(int x, int y, int z, int w) {
    super(x, y, z, w);
  }
  
  public Point4i(int[] t) {
    super(t);
  }
  
  public Point4i(Tuple4i t1) {
    super(t1);
  }
  
  public Point4i() {}
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\javax\Point4i.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */