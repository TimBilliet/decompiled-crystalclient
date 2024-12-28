package mchorse.mclib.math;

public class Negate implements IValue {
  public IValue value;
  
  public Negate(IValue value) {
    this.value = value;
  }
  
  public double get() {
    return (this.value.get() == 0.0D) ? 1.0D : 0.0D;
  }
  
  public String toString() {
    return "!" + this.value.toString();
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mclib\math\Negate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */