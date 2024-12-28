package mchorse.mclib.client.gui.utils.keys;

public class StringKey implements IKey {
  public String string;
  
  public StringKey(String string) {
    this.string = string;
  }
  
  public String get() {
    return this.string;
  }
  
  public void set(String string) {
    this.string = string;
  }
  
  public String toString() {
    return this.string;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mclib\client\gu\\utils\keys\StringKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */