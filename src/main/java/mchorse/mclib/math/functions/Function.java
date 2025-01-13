package mchorse.mclib.math.functions;

import mchorse.mclib.math.IValue;

public abstract class Function implements IValue {
    protected IValue[] args;

    protected String name;

    public Function(IValue[] values, String name) throws Exception {
        if (values.length < getRequiredArguments()) {
            String message = String.format("Function '%s' requires at least %s arguments. %s are given!", getName(), getRequiredArguments(), values.length);
            throw new Exception(message);
        }
        this.args = values;
        this.name = name;
    }

    public double getArg(int index) {
        if (index < 0 || index >= this.args.length)
            return 0.0D;
        return this.args[index].get();
    }

    public String toString() {
        String args = "";
        for (int i = 0; i < this.args.length; i++) {
            args = args + this.args[i].toString();
            if (i < this.args.length - 1)
                args = args + ", ";
        }
        return getName() + "(" + args + ")";
    }

    public String getName() {
        return this.name;
    }

    public int getRequiredArguments() {
        return 0;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mclib\math\functions\Function.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */