package com.github.lunatrius.core.util.vector;

public class Vector3d extends Vector2d {
    public double z;

    public Vector3d() {
        this(0.0D, 0.0D, 0.0D);
    }

    public Vector3d(Vector3d vec) {
        this(vec.x, vec.y, vec.z);
    }

    public Vector3d(double num) {
        this(num, num, num);
    }

    public Vector3d(double x, double y, double z) {
        super(x, y);
        this.z = z;
    }

    public final double getZ() {
        return this.z;
    }

    public final void setZ(double z) {
        this.z = z;
    }

    public Vector3d set(Vector3d vec) {
        return set(vec.x, vec.y, vec.z);
    }

    public Vector3d set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public double lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public final double lengthTo(Vector3d vec) {
        return Math.sqrt(lengthSquaredTo(vec));
    }

    public double lengthSquaredTo(Vector3d vec) {
        return pow2(this.x - vec.x) + pow2(this.y - vec.y) + pow2(this.z - vec.z);
    }

    public Vector3d negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        return this;
    }

    public double dot(Vector3d vec) {
        return this.x * vec.x + this.y * vec.y + this.z * vec.z;
    }

    public Vector3d scale(double scale) {
        this.x *= scale;
        this.y *= scale;
        this.z *= scale;
        return this;
    }

    public Vector3d add(Vector3d vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
        return this;
    }

    public Vector3d add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Vector3d sub(Vector3d vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        this.z -= vec.z;
        return this;
    }

    public Vector3d sub(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public Vector3i toVector3i() {
        return new Vector3i((int) Math.floor(this.x), (int) Math.floor(this.y), (int) Math.floor(this.z));
    }

    public Vector3i toVector3i(Vector3i vec) {
        return vec.set((int) Math.floor(this.x), (int) Math.floor(this.y), (int) Math.floor(this.z));
    }

    public Vector3f toVector3f() {
        return new Vector3f((float) Math.floor(this.x), (float) Math.floor(this.y), (float) Math.floor(this.z));
    }

    public Vector3f toVector3f(Vector3f vec) {
        return vec.set((float) Math.floor(this.x), (float) Math.floor(this.y), (float) Math.floor(this.z));
    }

    public Vector3d clone() {
        return new Vector3d(this);
    }

    public boolean equals(Object obj) {
        return (obj instanceof Vector3d && equals((Vector3d) obj));
    }

    public boolean equals(Vector3d vec) {
        return equals(vec, 9.999999747378752E-6D);
    }

    public boolean equals(Vector3d vec, double epsilon) {
        return (Math.abs(this.x - vec.x) < epsilon && Math.abs(this.y - vec.y) < epsilon && Math.abs(this.z - vec.z) < epsilon);
    }

    public String toString() {
        return String.format("[%s, %s, %s]", new Object[]{Double.valueOf(this.x), Double.valueOf(this.y), Double.valueOf(this.z)});
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\cor\\util\vector\Vector3d.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */