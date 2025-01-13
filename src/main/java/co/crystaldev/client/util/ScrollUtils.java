package co.crystaldev.client.util;

import co.crystaldev.client.feature.settings.ClientOptions;
import net.minecraft.util.MathHelper;

public class ScrollUtils {
    public static float handleScrollingPosition(float[] target, float scroll, float maxScroll, float delta, double start, double duration) {
        target[0] = clamp(target[0], maxScroll, 0.0F);
        if (!Precision.almostEquals(scroll, target[0], 0.001F))
            return expoEase(scroll, target[0], Math.min((System.currentTimeMillis() - start) / duration * delta * 3.0D, 1.0D));
        return target[0];
    }

    public static float expoEase(float start, float end, double amount) {
        return start + (end - start) * ((Double) (ClientOptions.getInstance()).easingMethod.apply(amount)).floatValue();
    }

    public static double clamp(double v, double maxScroll) {
        return clamp(v, maxScroll, 300.0D);
    }

    public static double clamp(double v, double maxScroll, double clampExtension) {
        return MathHelper.clamp_double(v, -clampExtension, maxScroll + clampExtension);
    }

    public static float clamp(float v, float maxScroll) {
        return clamp(v, maxScroll, 300.0F);
    }

    public static float clamp(float v, float maxScroll, float clampExtension) {
        return MathHelper.clamp_float(v, -clampExtension, maxScroll + clampExtension);
    }

    public static class Precision {
        public static final float FLOAT_EPSILON = 0.001F;

        public static final double DOUBLE_EPSILON = 1.0E-7D;

        public static boolean almostEquals(float value1, float value2, float acceptableDifference) {
            return (Math.abs(value1 - value2) <= acceptableDifference);
        }

        public static boolean almostEquals(double value1, double value2, double acceptableDifference) {
            return (Math.abs(value1 - value2) <= acceptableDifference);
        }
    }
}
