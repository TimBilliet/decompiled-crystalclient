package com.github.lunatrius.core.util.vector;

public class Vector2f {
    public static final float FLOAT_EPSILON = 1.0E-5F;

    public float x;

    public float y;

    public Vector2f() {
        this(0.0F, 0.0F);
    }

    public Vector2f(Vector2f vec) {
        this(vec.x, vec.y);
    }

    public Vector2f(float num) {
        this(num, num);
    }

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public final float getX() {
        return this.x;
    }

    public final float getY() {
        return this.y;
    }

    public final void setX(float x) {
        this.x = x;
    }

    public final void setY(float y) {
        this.y = y;
    }

    public Vector2f set(Vector2f vec) {
        return set(vec.x, vec.y);
    }

    public Vector2f set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public final double length() {
        return Math.sqrt(lengthSquared());
    }

    public float lengthSquared() {
        return this.x * this.x + this.y * this.y;
    }

    public final double lengthTo(Vector2f vec) {
        return Math.sqrt(lengthSquaredTo(vec));
    }

    public float lengthSquaredTo(Vector2f vec) {
        return pow2(this.x - vec.x) + pow2(this.y - vec.y);
    }

    protected final float pow2(float num) {
        return num * num;
    }

    public final Vector2f normalize() {
        double len = length();
        if (len != 0.0D)
            return scale(1.0D / len);
        return this;
    }

    public Vector2f negate() {
        this.x = -this.x;
        this.y = -this.y;
        return this;
    }

    public float dot(Vector2f vec) {
        return this.x * vec.x + this.y * vec.y;
    }

    public Vector2f scale(double scale) {
        this.x = (float) (this.x * scale);
        this.y = (float) (this.y * scale);
        return this;
    }

    public Vector2f add(Vector2f vec) {
        this.x += vec.x;
        this.y += vec.y;
        return this;
    }

    public Vector2f add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2f sub(Vector2f vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        return this;
    }

    public Vector2f sub(float x, float y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public Vector2i toVector2i() {
        return new Vector2i((int) Math.floor(this.x), (int) Math.floor(this.y));
    }

    public Vector2i toVector2i(Vector2i vec) {
        return vec.set((int) Math.floor(this.x), (int) Math.floor(this.y));
    }

    public Vector2d toVector2d() {
        return new Vector2d(this.x, this.y);
    }

    public Vector2d toVector2d(Vector2d vec) {
        return vec.set(this.x, this.y);
    }

    public Vector2f clone() {
        return new Vector2f(this);
    }

    public boolean equals(Object obj) {
        return (obj instanceof Vector2f && equals((Vector2f) obj));
    }

    public boolean equals(Vector2f vec) {
        return equals(vec, 1.0E-5F);
    }

    public boolean equals(Vector2f vec, float epsilon) {
        return (Math.abs(this.x - vec.x) < epsilon && Math.abs(this.y - vec.y) < epsilon);
    }

    public String toString() {
        return String.format("[%s, %s]", new Object[]{Float.valueOf(this.x), Float.valueOf(this.y)});
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\cor\\util\vector\Vector2f.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */