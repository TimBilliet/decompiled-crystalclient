package mchorse.mclib.utils;

public enum Direction {
    TOP(0.5F, 0.0F),
    LEFT(0.0F, 0.5F),
    BOTTOM(0.5F, 1.0F),
    RIGHT(1.0F, 0.5F);

    public final int factorY;

    public final int factorX;

    public final float anchorY;

    public final float anchorX;

    Direction(float anchorX, float anchorY) {
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        this.factorX = (int) Interpolations.lerp(-1.0F, 1.0F, anchorX);
        this.factorY = (int) Interpolations.lerp(-1.0F, 1.0F, anchorY);
    }

    public boolean isHorizontal() {
        return (this == LEFT || this == RIGHT);
    }

    public boolean isVertical() {
        return (this == TOP || this == BOTTOM);
    }

    public Direction opposite() {
        if (this == TOP)
            return BOTTOM;
        if (this == BOTTOM)
            return TOP;
        if (this == LEFT)
            return RIGHT;
        return LEFT;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\Direction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */