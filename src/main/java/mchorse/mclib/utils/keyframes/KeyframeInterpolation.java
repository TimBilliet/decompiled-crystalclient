package mchorse.mclib.utils.keyframes;

import mchorse.mclib.utils.Interpolation;
import mchorse.mclib.utils.Interpolations;
import mchorse.mclib.utils.MathUtils;

public enum KeyframeInterpolation {
    CONST("const") {
        public double interpolate(Keyframe a, Keyframe b, float x) {
            return a.value;
        }
    },
    LINEAR("linear") {
        public double interpolate(Keyframe a, Keyframe b, float x) {
            return Interpolations.lerp(a.value, b.value, x);
        }
    },
    QUAD("quad") {
        public double interpolate(Keyframe a, Keyframe b, float x) {
            if (a.easing == KeyframeEasing.IN)
                return Interpolation.QUAD_IN.interpolate(a.value, b.value, x);
            if (a.easing == KeyframeEasing.OUT)
                return Interpolation.QUAD_OUT.interpolate(a.value, b.value, x);
            return Interpolation.QUAD_INOUT.interpolate(a.value, b.value, x);
        }
    },
    CUBIC("cubic") {
        public double interpolate(Keyframe a, Keyframe b, float x) {
            if (a.easing == KeyframeEasing.IN)
                return Interpolation.CUBIC_IN.interpolate(a.value, b.value, x);
            if (a.easing == KeyframeEasing.OUT)
                return Interpolation.CUBIC_OUT.interpolate(a.value, b.value, x);
            return Interpolation.CUBIC_INOUT.interpolate(a.value, b.value, x);
        }
    },
    HERMITE("hermite") {
        public double interpolate(Keyframe a, Keyframe b, float x) {
            double v0 = a.prev.value;
            double v1 = a.value;
            double v2 = b.value;
            double v3 = b.next.value;
            return Interpolations.cubicHermite(v0, v1, v2, v3, x);
        }
    },
    EXP("exp") {
        public double interpolate(Keyframe a, Keyframe b, float x) {
            if (a.easing == KeyframeEasing.IN)
                return Interpolation.EXP_IN.interpolate(a.value, b.value, x);
            if (a.easing == KeyframeEasing.OUT)
                return Interpolation.EXP_OUT.interpolate(a.value, b.value, x);
            return Interpolation.EXP_INOUT.interpolate(a.value, b.value, x);
        }
    },
    BEZIER("bezier") {
        public double interpolate(Keyframe a, Keyframe b, float x) {
            if (x <= 0.0F)
                return a.value;
            if (x >= 1.0F)
                return b.value;
            double w = (b.tick - a.tick);
            double h = b.value - a.value;
            if (h == 0.0D)
                h = 1.0E-5D;
            double x1 = a.rx / w;
            double y1 = a.ry / h;
            double x2 = (w - b.lx) / w;
            double y2 = (h + b.ly) / h;
            double e = 5.0E-4D;
            e = (h == 0.0D) ? e : Math.max(Math.min(e, 1.0D / h * e), 1.0E-5D);
            x1 = MathUtils.clamp(x1, 0.0D, 1.0D);
            x2 = MathUtils.clamp(x2, 0.0D, 1.0D);
            return Interpolations.bezier(0.0D, y1, y2, 1.0D, Interpolations.bezierX(x1, x2, x, e)) * h + a.value;
        }
    },
    BACK("back") {
        public double interpolate(Keyframe a, Keyframe b, float x) {
            if (a.easing == KeyframeEasing.IN)
                return Interpolation.BACK_IN.interpolate(a.value, b.value, x);
            if (a.easing == KeyframeEasing.OUT)
                return Interpolation.BACK_OUT.interpolate(a.value, b.value, x);
            return Interpolation.BACK_INOUT.interpolate(a.value, b.value, x);
        }
    },
    ELASTIC("elastic") {
        public double interpolate(Keyframe a, Keyframe b, float x) {
            if (a.easing == KeyframeEasing.IN)
                return Interpolation.ELASTIC_IN.interpolate(a.value, b.value, x);
            if (a.easing == KeyframeEasing.OUT)
                return Interpolation.ELASTIC_OUT.interpolate(a.value, b.value, x);
            return Interpolation.ELASTIC_INOUT.interpolate(a.value, b.value, x);
        }
    },
    BOUNCE("bounce") {
        public double interpolate(Keyframe a, Keyframe b, float x) {
            if (a.easing == KeyframeEasing.IN)
                return Interpolation.BOUNCE_IN.interpolate(a.value, b.value, x);
            if (a.easing == KeyframeEasing.OUT)
                return Interpolation.BOUNCE_OUT.interpolate(a.value, b.value, x);
            return Interpolation.BOUNCE_INOUT.interpolate(a.value, b.value, x);
        }
    };

    public final String key;

    KeyframeInterpolation(String key) {
        this.key = key;
    }

    public String getKey() {
        return "mclib.interpolations." + this.key;
    }

    public abstract double interpolate(Keyframe paramKeyframe1, Keyframe paramKeyframe2, float paramFloat);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\keyframes\KeyframeInterpolation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */