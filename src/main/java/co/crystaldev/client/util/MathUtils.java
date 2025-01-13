package co.crystaldev.client.util;

public class MathUtils {
    public static final float PI = roundToFloat(Math.PI);

    public static float roundToFloat(double d) {
        return (float) (Math.round(d * 1.0E8D) / 1.0E8D);
    }

    public static float toRadians(float deg) {
        return deg / 180.0F * PI;
    }

    public static float lerp(float pct, float start, float end) {
        return start + pct * (end - start);
    }

    public static double lerp(double pct, double start, double end) {
        return start + pct * (end - start);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\MathUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */