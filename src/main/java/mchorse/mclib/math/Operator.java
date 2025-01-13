package mchorse.mclib.math;

public class Operator implements IValue {
    public Operation operation;

    public IValue a;

    public IValue b;

    public Operator(Operation op, IValue a, IValue b) {
        this.operation = op;
        this.a = a;
        this.b = b;
    }

    public double get() {
        return this.operation.calculate(this.a.get(), this.b.get());
    }

    public String toString() {
        return this.a.toString() + " " + this.operation.sign + " " + this.b.toString();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mclib\math\Operator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */