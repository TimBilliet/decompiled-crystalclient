package com.github.lunatrius.core.util.vector;

public class Vector2d {
    public static final double DOUBLE_EPSILON = 9.999999747378752E-6D;

    public double x;

    public double y;

    public Vector2d() {
        this(0.0D, 0.0D);
    }

    public Vector2d(Vector2d vec) {
        this(vec.x, vec.y);
    }

    public Vector2d(double num) {
        this(num, num);
    }

    public Vector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public final double getX() {
        return this.x;
    }

    public final double getY() {
        return this.y;
    }

    public final void setX(double x) {
        this.x = x;
    }

    public final void setY(double y) {
        this.y = y;
    }

    public Vector2d set(Vector2d vec) {
        return set(vec.x, vec.y);
    }

    public Vector2d set(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public final double length() {
        return Math.sqrt(lengthSquared());
    }

    public double lengthSquared() {
        return this.x * this.x + this.y * this.y;
    }

    public final double lengthTo(Vector2d vec) {
        return Math.sqrt(lengthSquaredTo(vec));
    }

    public double lengthSquaredTo(Vector2d vec) {
        return pow2(this.x - vec.x) + pow2(this.y - vec.y);
    }

    protected final double pow2(double num) {
        return num * num;
    }

    public final Vector2d normalize() {
        double len = length();
        if (len != 0.0D)
            return scale(1.0D / len);
        return this;
    }

    public Vector2d negate() {
        this.x = -this.x;
        this.y = -this.y;
        return this;
    }

    public double dot(Vector2d vec) {
        return this.x * vec.x + this.y * vec.y;
    }

    public Vector2d scale(double scale) {
        this.x *= scale;
        this.y *= scale;
        return this;
    }

    public Vector2d add(Vector2d vec) {
        this.x += vec.x;
        this.y += vec.y;
        return this;
    }

    public Vector2d add(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2d sub(Vector2d vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        return this;
    }

    public Vector2d sub(double x, double y) {
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

    public Vector2f toVector2f() {
        return new Vector2f((float) Math.floor(this.x), (float) Math.floor(this.y));
    }

    public Vector2f toVector2f(Vector2f vec) {
        return vec.set((float) Math.floor(this.x), (float) Math.floor(this.y));
    }

    public Vector2d clone() {
        return new Vector2d(this);
    }

    public boolean equals(Object obj) {
        return (obj instanceof Vector2d && equals((Vector2d) obj));
    }

    public boolean equals(Vector2d vec) {
        return equals(vec, 9.999999747378752E-6D);
    }

    public boolean equals(Vector2d vec, double epsilon) {
        return (Math.abs(this.x - vec.x) < epsilon && Math.abs(this.y - vec.y) < epsilon);
    }

    public String toString() {
        return String.format("[%s, %s]", new Object[]{Double.valueOf(this.x), Double.valueOf(this.y)});
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\cor\\util\vector\Vector2d.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */