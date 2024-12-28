package mchorse.mclib.math.functions.limit;

import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.Function;

public class Max extends Function {
  public Max(IValue[] values, String name) throws Exception {
    super(values, name);
  }
  
  public int getRequiredArguments() {
    return 2;
  }
  
  public double get() {
    return Math.max(getArg(0), getArg(1));
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mclib\math\functions\limit\Max.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */