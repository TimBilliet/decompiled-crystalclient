package com.github.lunatrius.core.util.vector;

public class Vector4f extends Vector3f {
    public float w;

    public Vector4f() {
        this(0.0F, 0.0F, 0.0F, 0.0F);
    }

    public Vector4f(Vector4f vec) {
        this(vec.x, vec.y, vec.z, vec.w);
    }

    public Vector4f(float num) {
        this(num, num, num, num);
    }

    public Vector4f(float x, float y, float z, float w) {
        super(x, y, z);
        this.w = w;
    }

    public final float getW() {
        return this.w;
    }

    public final void setW(float w) {
        this.w = w;
    }

    public Vector4f set(Vector4f vec) {
        return set(vec.x, vec.y, vec.z, vec.w);
    }

    public Vector4f set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    public float lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
    }

    public final double lengthTo(Vector4f vec) {
        return Math.sqrt(lengthSquaredTo(vec));
    }

    public float lengthSquaredTo(Vector4f vec) {
        return pow2(this.x - vec.x) + pow2(this.y - vec.y) + pow2(this.z - vec.z) + pow2(this.w - vec.w);
    }

    public Vector4f negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        this.w = -this.w;
        return this;
    }

    public float dot(Vector4f vec) {
        return this.x * vec.x + this.y * vec.y + this.z * vec.z + this.w * vec.w;
    }

    public Vector4f scale(double scale) {
        this.x = (float) (this.x * scale);
        this.y = (float) (this.y * scale);
        this.z = (float) (this.z * scale);
        this.w = (float) (this.w * scale);
        return this;
    }

    public Vector4f add(Vector4f vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
        this.w += vec.w;
        return this;
    }

    public Vector4f add(float x, float y, float z, float w) {
        this.x += x;
        this.y += y;
        this.z += z;
        this.w += w;
        return this;
    }

    public Vector4f sub(Vector4f vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        this.z -= vec.z;
        this.w -= vec.w;
        return this;
    }

    public Vector4f sub(float x, float y, float z, float w) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        this.w -= w;
        return this;
    }

    public Vector4i toVector4i() {
        return new Vector4i((int) Math.floor(this.x), (int) Math.floor(this.y), (int) Math.floor(this.z), (int) Math.floor(this.w));
    }

    public Vector4i toVector4i(Vector4i vec) {
        return vec.set((int) Math.floor(this.x), (int) Math.floor(this.y), (int) Math.floor(this.z), (int) Math.floor(this.w));
    }

    public Vector4d toVector4d() {
        return new Vector4d(this.x, this.y, this.z, this.w);
    }

    public Vector4d toVector4d(Vector4d vec) {
        return vec.set(this.x, this.y, this.z, this.w);
    }

    public Vector4f clone() {
        return new Vector4f(this);
    }

    public boolean equals(Object obj) {
        return (obj instanceof Vector4f && equals((Vector4f) obj));
    }

    public boolean equals(Vector4f vec) {
        return equals(vec, 1.0E-5F);
    }

    public boolean equals(Vector4f vec, float epsilon) {
        return (Math.abs(this.x - vec.x) < epsilon && Math.abs(this.y - vec.y) < epsilon && Math.abs(this.z - vec.z) < epsilon && Math.abs(this.w - vec.w) < epsilon);
    }

    public String toString() {
        return String.format("[%s, %s, %s, %s]", new Object[]{Float.valueOf(this.x), Float.valueOf(this.y), Float.valueOf(this.z), Float.valueOf(this.w)});
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\cor\\util\vector\Vector4f.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */