package mchorse.mclib.math.functions.rounding;

import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.Function;

public class Trunc extends Function {
    public Trunc(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    public int getRequiredArguments() {
        return 1;
    }

    public double get() {
        double value = getArg(0);
        return (value < 0.0D) ? Math.ceil(value) : Math.floor(value);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mclib\math\functions\rounding\Trunc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */