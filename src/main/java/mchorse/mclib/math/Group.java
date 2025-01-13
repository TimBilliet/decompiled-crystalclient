package mchorse.mclib.math;

public class Group implements IValue {
    private final IValue value;

    public Group(IValue value) {
        this.value = value;
    }

    public double get() {
        return this.value.get();
    }

    public String toString() {
        return "(" + this.value.toString() + ")";
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mclib\math\Group.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */