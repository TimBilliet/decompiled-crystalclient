package mchorse.mclib.math;

public class Ternary implements IValue {
    public IValue condition;

    public IValue ifTrue;

    public IValue ifFalse;

    public Ternary(IValue condition, IValue ifTrue, IValue ifFalse) {
        this.condition = condition;
        this.ifTrue = ifTrue;
        this.ifFalse = ifFalse;
    }

    public double get() {
        return (this.condition.get() != 0.0D) ? this.ifTrue.get() : this.ifFalse.get();
    }

    public String toString() {
        return this.condition.toString() + " ? " + this.ifTrue.toString() + " : " + this.ifFalse.toString();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mclib\math\Ternary.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */