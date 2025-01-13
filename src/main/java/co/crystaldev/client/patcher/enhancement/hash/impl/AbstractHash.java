package co.crystaldev.client.patcher.enhancement.hash.impl;

import java.util.Objects;

public abstract class AbstractHash {
    private final int hash;

    private final Object[] objects;

    public AbstractHash(Object... items) {
        this.hash = Objects.hash(items);
        this.objects = items;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractHash))
            return false;
        Object[] a = this.objects;
        Object[] a2 = ((AbstractHash) obj).objects;
        if (a == a2)
            return true;
        if (a == null || a2 == null)
            return false;
        int length = a.length;
        if (a2.length != length)
            return false;
        for (int i = 0; i < length; i++) {
            if (a[i] != a2[i] &&
                    !a[i].equals(a2[i]))
                return false;
        }
        return true;
    }

    public int hashCode() {
        return this.hash;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\patcher\enhancement\hash\impl\AbstractHash.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */