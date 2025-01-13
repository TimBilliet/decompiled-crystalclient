package co.crystaldev.client.util.javax;

class VecMathUtil {
    static final long hashLongBits(long hash, long l) {
        hash *= 31L;
        return hash + l;
    }

    static final long hashFloatBits(long hash, float f) {
        hash *= 31L;
        if (f == 0.0F)
            return hash;
        return hash + Float.floatToIntBits(f);
    }

    static final long hashDoubleBits(long hash, double d) {
        hash *= 31L;
        if (d == 0.0D)
            return hash;
        return hash + Double.doubleToLongBits(d);
    }

    static final int hashFinish(long hash) {
        return (int) (hash ^ hash >> 32L);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\javax\VecMathUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */