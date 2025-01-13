package mchorse.emoticons.skin_n_bones.api.bobj;

import mchorse.mclib.utils.Interpolations;
import mchorse.mclib.utils.MathUtils;

public class BOBJKeyframe {
    public int frame;

    public float value;

    public Interpolation interpolation = Interpolation.LINEAR;

    public float leftX;

    public float leftY;

    public float rightX;

    public float rightY;

    public static BOBJKeyframe parse(String[] tokens) {
        if (tokens.length == 8) {
            float leftX = Float.parseFloat(tokens[4]);
            float leftY = Float.parseFloat(tokens[5]);
            float rightX = Float.parseFloat(tokens[6]);
            float rightY = Float.parseFloat(tokens[7]);
            return new BOBJKeyframe(Integer.parseInt(tokens[1]), Float.parseFloat(tokens[2]), tokens[3], leftX, leftY, rightX, rightY);
        }
        if (tokens.length == 4)
            return new BOBJKeyframe(Integer.parseInt(tokens[1]), Float.parseFloat(tokens[2]), tokens[3]);
        if (tokens.length == 3)
            return new BOBJKeyframe(Integer.parseInt(tokens[1]), Float.parseFloat(tokens[2]));
        return null;
    }

    public static Interpolation interpolationFromString(String interp) {
        if (interp.equals("CONSTANT"))
            return Interpolation.CONSTANT;
        if (interp.equals("BEZIER"))
            return Interpolation.BEZIER;
        return Interpolation.LINEAR;
    }

    public BOBJKeyframe(int frame, float value) {
        this.frame = frame;
        this.value = value;
    }

    public BOBJKeyframe(int frame, float value, String interp) {
        this(frame, value);
        this.interpolation = interpolationFromString(interp);
    }

    public BOBJKeyframe(int frame, float value, String interp, float leftX, float leftY, float rightX, float rightY) {
        this(frame, value, interp);
        this.leftX = leftX;
        this.leftY = leftY;
        this.rightX = rightX;
        this.rightY = rightY;
    }

    public float interpolate(float x, BOBJKeyframe next) {
        return this.interpolation.interpolate(this, x, next);
    }

    public enum Interpolation {
        CONSTANT {
            public float interpolate(BOBJKeyframe keyframe, float x, BOBJKeyframe next) {
                return keyframe.value;
            }
        },
        LINEAR {
            public float interpolate(BOBJKeyframe keyframe, float x, BOBJKeyframe next) {
                return Interpolations.lerp(keyframe.value, next.value, x);
            }
        },
        BEZIER {
            public float interpolate(BOBJKeyframe keyframe, float x, BOBJKeyframe next) {
                if (x <= 0.0F)
                    return keyframe.value;
                if (x >= 1.0F)
                    return next.value;
                float w = (next.frame - keyframe.frame);
                float h = next.value - keyframe.value;
                if (h == 0.0F)
                    h = 1.0E-5F;
                float x1 = (keyframe.rightX - keyframe.frame) / w;
                float y1 = (keyframe.rightY - keyframe.value) / h;
                float x2 = (next.leftX - keyframe.frame) / w;
                float y2 = (next.leftY - keyframe.value) / h;
                float e = 5.0E-4F;
                e = (h == 0.0F) ? e : Math.max(Math.min(e, 1.0F / h * e), 1.0E-5F);
                x1 = MathUtils.clamp(x1, 0.0F, 1.0F);
                x2 = MathUtils.clamp(x2, 0.0F, 1.0F);
                return Interpolations.bezier(0.0F, y1, y2, 1.0F, Interpolations.bezierX(x1, x2, x, e)) * h + keyframe.value;
            }
        };

        public abstract float interpolate(BOBJKeyframe param1BOBJKeyframe1, float param1Float, BOBJKeyframe param1BOBJKeyframe2);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\skin_n_bones\api\bobj\BOBJKeyframe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */