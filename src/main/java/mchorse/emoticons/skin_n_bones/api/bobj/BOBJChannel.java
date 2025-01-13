package mchorse.emoticons.skin_n_bones.api.bobj;

import java.util.ArrayList;
import java.util.List;

public class BOBJChannel {
    public String path;

    public int index;

    public List<BOBJKeyframe> keyframes = new ArrayList<>();

    public BOBJChannel(String path, int index) {
        this.path = path;
        this.index = index;
    }

    public float calculate(float frame) {
        int c = this.keyframes.size();
        if (c <= 0)
            return 0.0F;
        if (c == 1)
            return ((BOBJKeyframe) this.keyframes.get(0)).value;
        BOBJKeyframe keyframe = this.keyframes.get(0);
        if (keyframe.frame > frame)
            return keyframe.value;
        for (int i = 0; i < c; i++) {
            keyframe = this.keyframes.get(i);
            if (keyframe.frame > frame && i != 0) {
                BOBJKeyframe prev = this.keyframes.get(i - 1);
                float x = (frame - prev.frame) / (keyframe.frame - prev.frame);
                return prev.interpolate(x, keyframe);
            }
        }
        return keyframe.value;
    }

    public BOBJKeyframe get(float frame, boolean next) {
        int c = this.keyframes.size();
        if (c == 0)
            return null;
        if (c == 1)
            return this.keyframes.get(0);
        BOBJKeyframe keyframe = null;
        for (int i = 0; i < c; i++) {
            keyframe = this.keyframes.get(i);
            if (keyframe.frame > frame && i != 0)
                return next ? keyframe : this.keyframes.get(i - 1);
        }
        return keyframe;
    }

    public void apply(BOBJBone bone, float frame) {
        if (this.path.equals("location")) {
            if (this.index == 0) {
                bone.x = calculate(frame);
            } else if (this.index == 1) {
                bone.y = calculate(frame);
            } else if (this.index == 2) {
                bone.z = calculate(frame);
            }
        } else if (this.path.equals("rotation")) {
            if (this.index == 0) {
                bone.rotateX = calculate(frame);
            } else if (this.index == 1) {
                bone.rotateY = calculate(frame);
            } else if (this.index == 2) {
                bone.rotateZ = calculate(frame);
            }
        } else if (this.path.equals("scale")) {
            if (this.index == 0) {
                bone.scaleX = calculate(frame);
            } else if (this.index == 1) {
                bone.scaleY = calculate(frame);
            } else if (this.index == 2) {
                bone.scaleZ = calculate(frame);
            }
        }
    }

    public void applyInterpolate(BOBJBone bone, float frame, float x) {
        float value = calculate(frame);
        if (this.path.equals("location")) {
            if (this.index == 0) {
                bone.x = value + (bone.x - value) * x;
            } else if (this.index == 1) {
                bone.y = value + (bone.y - value) * x;
            } else if (this.index == 2) {
                bone.z = value + (bone.z - value) * x;
            }
        } else if (this.path.equals("rotation")) {
            if (this.index == 0) {
                bone.rotateX = value + (bone.rotateX - value) * x;
            } else if (this.index == 1) {
                bone.rotateY = value + (bone.rotateY - value) * x;
            } else if (this.index == 2) {
                bone.rotateZ = value + (bone.rotateZ - value) * x;
            }
        } else if (this.path.equals("scale")) {
            if (this.index == 0) {
                bone.scaleX = value + (bone.scaleX - value) * x;
            } else if (this.index == 1) {
                bone.scaleY = value + (bone.scaleY - value) * x;
            } else if (this.index == 2) {
                bone.scaleZ = value + (bone.scaleZ - value) * x;
            }
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\skin_n_bones\api\bobj\BOBJChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */