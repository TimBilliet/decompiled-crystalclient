package co.crystaldev.client.util.javax;

import java.io.Serializable;

public class TexCoord3f extends Tuple3f implements Serializable {
  static final long serialVersionUID = -3517736544731446513L;
  
  public TexCoord3f(float x, float y, float z) {
    super(x, y, z);
  }
  
  public TexCoord3f(float[] v) {
    super(v);
  }
  
  public TexCoord3f(TexCoord3f v1) {
    super(v1);
  }
  
  public TexCoord3f(Tuple3f t1) {
    super(t1);
  }
  
  public TexCoord3f(Tuple3d t1) {
    super(t1);
  }
  
  public TexCoord3f() {}
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\javax\TexCoord3f.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */