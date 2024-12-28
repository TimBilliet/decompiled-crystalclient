package mchorse.mclib.math;

public class Variable implements IValue {
  private final String name;
  
  private double value;
  
  public Variable(String name, double value) {
    this.name = name;
    this.value = value;
  }
  
  public void set(double value) {
    this.value = value;
  }
  
  public double get() {
    return this.value;
  }
  
  public String getName() {
    return this.name;
  }
  
  public String toString() {
    return this.name;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mclib\math\Variable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */