package mchorse.mclib.math.functions.classic;

import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.Function;

public class Sqrt extends Function {
  public Sqrt(IValue[] values, String name) throws Exception {
    super(values, name);
  }
  
  public int getRequiredArguments() {
    return 1;
  }
  
  public double get() {
    return Math.sqrt(getArg(0));
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mclib\math\functions\classic\Sqrt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */