package mchorse.mclib.math.functions.rounding;

import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.Function;

public class Round extends Function {
  public Round(IValue[] values, String name) throws Exception {
    super(values, name);
  }
  
  public int getRequiredArguments() {
    return 1;
  }
  
  public double get() {
    return Math.round(getArg(0));
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mclib\math\functions\rounding\Round.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */