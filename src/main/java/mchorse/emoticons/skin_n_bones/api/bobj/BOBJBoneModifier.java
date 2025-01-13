package mchorse.emoticons.skin_n_bones.api.bobj;

import co.crystaldev.client.util.javax.Matrix4f;
import co.crystaldev.client.util.javax.Tuple4f;
import co.crystaldev.client.util.javax.Vector3f;
import co.crystaldev.client.util.javax.Vector4f;

public class BOBJBoneModifier {
    public BOBJBone target;

    public int chain = 0;

    public boolean stick;

    private final Vector4f global = new Vector4f();

    private final Vector4f local = new Vector4f();

    private final Matrix4f inverse = new Matrix4f();

    public BOBJBoneModifier(BOBJBone target, int chain, boolean stick) {
        this.target = target;
        this.chain = chain;
        this.stick = stick;
    }

    public void apply(BOBJBone bone) {
        if (this.chain == 0 || this.target == null)
            return;
        this.global.set(0.0F, 0.0F, 0.0F, 1.0F);
        this.target.mat.transform((Tuple4f) this.global);
        this.local.set(0.0F, 0.0F, 0.0F, 1.0F);
        bone.mat.transform((Tuple4f) this.local);
        this.local.sub((Tuple4f) this.global);
        float distance = this.local.length();
        this.inverse.set(bone.mat);
        this.inverse.invert();
        this.local.set((Tuple4f) this.global);
        this.inverse.transform((Tuple4f) this.local);
        Vector3f forward = new Vector3f(this.local.x, this.local.y, this.local.z);
        forward.normalize();
        this.local.set(0.0F, 0.0F, 1.0F, 1.0F);
        this.target.mat.transform((Tuple4f) this.local);
        Vector3f right = new Vector3f(0.0F, 1.0F, 0.0F);
        right.normalize();
        right.cross(forward, right);
        right.normalize();
        Vector3f up = new Vector3f(0.0F, 0.0F, 0.0F);
        up.cross(right, forward);
        up.normalize();
        this.inverse.setIdentity();
        this.inverse.m00 = right.x;
        this.inverse.m10 = right.y;
        this.inverse.m20 = right.z;
        this.inverse.m01 = forward.x;
        this.inverse.m11 = forward.y;
        this.inverse.m21 = forward.z;
        this.inverse.m02 = up.x;
        this.inverse.m12 = up.y;
        this.inverse.m22 = up.z;
        if (this.stick) {
            this.local.set(0.0F, distance - bone.length, 0.0F, 1.0F);
            this.inverse.transform((Tuple4f) this.local);
            this.inverse.m03 = this.local.x;
            this.inverse.m13 = this.local.y;
            this.inverse.m23 = this.local.z;
        }
        Matrix4f m = new Matrix4f();
        bone.mat.set(bone.relBoneMat);
        bone.applyTransformations();
        bone.mat.mul(this.inverse);
        if (bone.parentBone != null)
            m = new Matrix4f(bone.parentBone.mat);
        m.mul(bone.mat);
        bone.mat.set(m);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\skin_n_bones\api\bobj\BOBJBoneModifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */