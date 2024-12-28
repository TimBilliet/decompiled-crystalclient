package mchorse.mclib.utils.keyframes;

public enum KeyframeEasing {
  IN("in"),
  OUT("out"),
  INOUT("inout");
  
  public final String key;
  
  KeyframeEasing(String key) {
    this.key = key;
  }
  
  public String getKey() {
    return "mclib.easing." + this.key;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\keyframes\KeyframeEasing.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */