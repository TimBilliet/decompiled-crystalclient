package mchorse.mclib.client.gui.utils.keys;

public class CompoundKey implements IKey {
  public static long lastTime;
  
  public IKey[] keys;
  
  public String string;
  
  public long time = -1L;
  
  public CompoundKey(IKey... keys) {
    this.keys = keys;
  }
  
  public String get() {
    if (lastTime > this.time) {
      this.time = lastTime;
      construct();
    } 
    return this.string;
  }
  
  private void construct() {
    StringBuilder builder = new StringBuilder();
    for (IKey key : this.keys)
      builder.append(key.get()); 
    this.string = builder.toString();
  }
  
  public void set(String string) {
    throw new IllegalStateException("Not implemented!");
  }
  
  public void set(IKey... keys) {
    this.keys = keys;
    construct();
  }
  
  public String toString() {
    return get();
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mclib\client\gu\\utils\keys\CompoundKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */