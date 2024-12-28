package mchorse.mclib.math;

public class Constant implements IValue {
  private double value;
  
  public Constant(double value) {
    this.value = value;
  }
  
  public double get() {
    return this.value;
  }
  
  public void set(double value) {
    this.value = value;
  }
  
  public String toString() {
    return String.valueOf(this.value);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mclib\math\Constant.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */