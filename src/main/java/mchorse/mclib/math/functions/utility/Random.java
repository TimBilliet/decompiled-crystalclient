package mchorse.mclib.math.functions.utility;

import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.Function;

public class Random extends Function {
  public java.util.Random random;
  
  public Random(IValue[] values, String name) throws Exception {
    super(values, name);
    this.random = new java.util.Random();
  }
  
  public double get() {
    double random = 0.0D;
    if (this.args.length >= 3) {
      this.random.setSeed((long)getArg(2));
      random = this.random.nextDouble();
    } else {
      random = Math.random();
    } 
    if (this.args.length >= 2) {
      double a = getArg(0);
      double b = getArg(1);
      double min = Math.min(a, b);
      double max = Math.max(a, b);
      random = random * (max - min) + min;
    } else if (this.args.length >= 1) {
      random *= getArg(0);
    } 
    return random;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mclib\math\function\\utility\Random.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */