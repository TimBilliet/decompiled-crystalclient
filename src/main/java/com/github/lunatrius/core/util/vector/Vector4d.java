package com.github.lunatrius.core.util.vector;

public class Vector4d extends Vector3d {
    public double w;

    public Vector4d() {
        this(0.0D, 0.0D, 0.0D, 0.0D);
    }

    public Vector4d(Vector4d vec) {
        this(vec.x, vec.y, vec.z, vec.w);
    }

    public Vector4d(double num) {
        this(num, num, num, num);
    }

    public Vector4d(double x, double y, double z, double w) {
        super(x, y, z);
        this.w = w;
    }

    public final double getW() {
        return this.w;
    }

    public final void setW(double w) {
        this.w = w;
    }

    public Vector4d set(Vector4d vec) {
        return set(vec.x, vec.y, vec.z, vec.w);
    }

    public Vector4d set(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    public double lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
    }

    public final double lengthTo(Vector4d vec) {
        return Math.sqrt(lengthSquaredTo(vec));
    }

    public double lengthSquaredTo(Vector4d vec) {
        return pow2(this.x - vec.x) + pow2(this.y - vec.y) + pow2(this.z - vec.z) + pow2(this.w - vec.w);
    }

    public Vector4d negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        this.w = -this.w;
        return this;
    }

    public double dot(Vector4d vec) {
        return this.x * vec.x + this.y * vec.y + this.z * vec.z + this.w * vec.w;
    }

    public Vector4d scale(double scale) {
        this.x *= scale;
        this.y *= scale;
        this.z *= scale;
        this.w *= scale;
        return this;
    }

    public Vector4d add(Vector4d vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
        this.w += vec.w;
        return this;
    }

    public Vector4d add(double x, double y, double z, double w) {
        this.x += x;
        this.y += y;
        this.z += z;
        this.w += w;
        return this;
    }

    public Vector4d sub(Vector4d vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        this.z -= vec.z;
        this.w -= vec.w;
        return this;
    }

    public Vector4d sub(double x, double y, double z, double w) {
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

    public Vector4f toVector4f() {
        return new Vector4f((float) Math.floor(this.x), (float) Math.floor(this.y), (float) Math.floor(this.z), (float) Math.floor(this.w));
    }

    public Vector4f toVector4f(Vector4f vec) {
        return vec.set((float) Math.floor(this.x), (float) Math.floor(this.y), (float) Math.floor(this.z), (float) Math.floor(this.w));
    }

    public Vector4d clone() {
        return new Vector4d(this);
    }

    public boolean equals(Object obj) {
        return (obj instanceof Vector4d && equals((Vector4d) obj));
    }

    public boolean equals(Vector4d vec) {
        return equals(vec, 9.999999747378752E-6D);
    }

    public boolean equals(Vector4d vec, double epsilon) {
        return (Math.abs(this.x - vec.x) < epsilon && Math.abs(this.y - vec.y) < epsilon && Math.abs(this.z - vec.z) < epsilon && Math.abs(this.w - vec.w) < epsilon);
    }

    public String toString() {
        return String.format("[%s, %s, %s, %s]", new Object[]{Double.valueOf(this.x), Double.valueOf(this.y), Double.valueOf(this.z), Double.valueOf(this.w)});
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\cor\\util\vector\Vector4d.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */