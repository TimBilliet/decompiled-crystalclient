package mchorse.mclib.math.functions.utility;

import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.Function;
import mchorse.mclib.utils.Interpolations;

public class LerpRotate extends Function {
    public LerpRotate(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    public int getRequiredArguments() {
        return 3;
    }

    public double get() {
        return Interpolations.lerpYaw(getArg(0), getArg(1), getArg(2));
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mclib\math\function\\utility\LerpRotate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */