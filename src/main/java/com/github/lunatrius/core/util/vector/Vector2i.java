package com.github.lunatrius.core.util.vector;

public class Vector2i {
    public int x;

    public int y;

    public Vector2i() {
        this(0, 0);
    }

    public Vector2i(Vector2i vec) {
        this(vec.x, vec.y);
    }

    public Vector2i(int num) {
        this(num, num);
    }

    public Vector2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public final int getX() {
        return this.x;
    }

    public final int getY() {
        return this.y;
    }

    public final void setX(int x) {
        this.x = x;
    }

    public final void setY(int y) {
        this.y = y;
    }

    public Vector2i set(Vector2i vec) {
        return set(vec.x, vec.y);
    }

    public Vector2i set(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public final double length() {
        return Math.sqrt(lengthSquared());
    }

    public int lengthSquared() {
        return this.x * this.x + this.y * this.y;
    }

    public final double lengthTo(Vector2i vec) {
        return Math.sqrt(lengthSquaredTo(vec));
    }

    public int lengthSquaredTo(Vector2i vec) {
        return pow2(this.x - vec.x) + pow2(this.y - vec.y);
    }

    protected final int pow2(int num) {
        return num * num;
    }

    public final Vector2i normalize() {
        double len = length();
        if (len != 0.0D)
            return scale(1.0D / len);
        return this;
    }

    public Vector2i negate() {
        this.x = -this.x;
        this.y = -this.y;
        return this;
    }

    public double dot(Vector2i vec) {
        return (this.x * vec.x + this.y * vec.y);
    }

    public Vector2i scale(double scale) {
        this.x = (int) (this.x * scale);
        this.y = (int) (this.y * scale);
        return this;
    }

    public Vector2i add(Vector2i vec) {
        this.x += vec.x;
        this.y += vec.y;
        return this;
    }

    public Vector2i add(int x, int y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2i sub(Vector2i vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        return this;
    }

    public Vector2i sub(int x, int y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public Vector2f toVector2f() {
        return new Vector2f(this.x, this.y);
    }

    public Vector2f toVector2f(Vector2f vec) {
        return vec.set(this.x, this.y);
    }

    public Vector2d toVector2d() {
        return new Vector2d(this.x, this.y);
    }

    public Vector2d toVector2d(Vector2d vec) {
        return vec.set(this.x, this.y);
    }

    public Vector2i clone() {
        return new Vector2i(this);
    }

    public boolean equals(Object obj) {
        return (obj instanceof Vector2i && equals((Vector2i) obj));
    }

    public boolean equals(Vector2i vec) {
        return (this.x == vec.x && this.y == vec.y);
    }

    public String toString() {
        return String.format("[%s, %s]", new Object[]{Integer.valueOf(this.x), Integer.valueOf(this.y)});
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\cor\\util\vector\Vector2i.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */