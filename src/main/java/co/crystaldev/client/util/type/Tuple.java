package co.crystaldev.client.util.type;

import com.google.gson.annotations.SerializedName;

public class Tuple<T1, T2> {
    @SerializedName("item1")
    private T1 item1;

    @SerializedName("item2")
    private T2 item2;

    public void setItem1(T1 item1) {
        this.item1 = item1;
    }

    public void setItem2(T2 item2) {
        this.item2 = item2;
    }

    public T1 getItem1() {
        return this.item1;
    }

    public T2 getItem2() {
        return this.item2;
    }

    public Tuple(T1 item1, T2 item2) {
        this.item1 = item1;
        this.item2 = item2;
    }

    public int hashCode() {
        int h1 = this.item1.hashCode();
        int h2 = this.item2.hashCode();
        return (h1 << 5) + h1 ^ h2;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Tuple<T1, T2> other = (Tuple<T1, T2>)obj;
        return (hashCode() == other.hashCode());
    }

    public String toString() {
        return String.format("Tuple{item1=%s (%s), item2=%s (%s)}", new Object[] { this.item1, this.item1.getClass().getSimpleName(), this.item2, this.item2.getClass().getSimpleName() });
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\type\Tuple.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */