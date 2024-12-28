package co.crystaldev.client.util.objects;

import co.crystaldev.client.util.enums.AnchorRegion;
import com.google.gson.annotations.SerializedName;

public class ModulePosition {
    @SerializedName("anchorRegion")
    private final AnchorRegion anchorRegion;

    @SerializedName("x")
    private float x;

    @SerializedName("y")
    private float y;

    public ModulePosition(AnchorRegion anchorRegion, float x, float y) {
        this.anchorRegion = anchorRegion;
        this.x = x;
        this.y = y;
    }

    public AnchorRegion getAnchorRegion() {
        return this.anchorRegion;
    }

    public float getX() {
        return this.x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float y) {
        this.y = y;
    }
}