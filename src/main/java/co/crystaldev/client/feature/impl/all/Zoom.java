package co.crystaldev.client.feature.impl.all;


import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.Toggle;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Module;
import co.crystaldev.client.gui.ease.Easing;
import co.crystaldev.client.util.Reflector;
import com.google.common.base.Preconditions;
import org.lwjgl.input.Mouse;

@ModuleInfo(name = "Zoom", description = "Settings to modify the behavior of OptiFine zoom", category = Category.ALL)
public class Zoom extends Module {
    @Toggle(label = "Scroll to Zoom")
    public boolean scrollToZoom = true;

    @Toggle(label = "Remove Cinematic Effect")
    public boolean removeCinematic = false;

    @Toggle(label = "Smooth Zoom")
    public boolean smoothZoom = true;

    private static Zoom INSTANCE;

    private final float defaultMultiplier = 4.0F;

    private float currentModifier;

    private boolean hasScrolled;

    private long lastMillis;

    private float desiredModifier;

    public boolean zoomed;

    public float progress;

    public Zoom() {
        getClass();
        this.currentModifier = 4.0F;
        this.hasScrolled = false;
        this.lastMillis = System.currentTimeMillis();
        this.desiredModifier = this.currentModifier;
        this.zoomed = false;
        this.progress = 0.0F;
        Preconditions.checkState(Reflector.isOptiFineLoaded(), "OptiFine must be loaded.");
        this.canBeDisabled = false;
        this.enabled = true;
        INSTANCE = this;
    }

    public float getScrollZoomModifier() {
        if (!this.scrollToZoom)
            return this.desiredModifier;
        long time = System.currentTimeMillis();
        long timeSinceLastChange = time - this.lastMillis;
        if (!this.zoomed)
            this.lastMillis = time;
        int dWheel = Mouse.getDWheel();
        if (dWheel > 0) {
            this.progress = 0.0F;
            this.hasScrolled = true;
            this.desiredModifier += 0.25F * this.desiredModifier;
        } else if (dWheel < 0) {
            this.progress = 0.0F;
            this.hasScrolled = true;
            this.desiredModifier -= 0.25F * this.desiredModifier;
        }
        if (this.desiredModifier < 1.0F)
            this.desiredModifier = 1.0F;
        if (this.desiredModifier > 600.0F)
            this.desiredModifier = 600.0F;
        if (this.hasScrolled && this.progress < 1.0F) {
            this.progress += 0.004F * (float) timeSinceLastChange;
            this.progress = (this.progress > 1.0F) ? 1.0F : this.progress;
            return this.currentModifier += (this.desiredModifier - this.currentModifier) * calculateZoomEasing(this.progress);
        }
        return this.desiredModifier;
    }

    public float getSmoothZoomModifier() {
        long time = System.currentTimeMillis();
        long timeSinceLastChange = time - this.lastMillis;
        this.lastMillis = time;
        if (this.zoomed) {
            if (this.hasScrolled)
                return 1.0F;
            if (this.progress < 1.0F) {
                this.progress += 0.005F * (float) timeSinceLastChange;
                this.progress = (this.progress > 1.0F) ? 1.0F : this.progress;
                return 4.0F - 3.0F * calculateZoomEasing(this.progress);
            }
        } else {
            if (this.hasScrolled) {
                this.hasScrolled = false;
                this.progress = 1.0F;
            }
            if (this.progress > 0.0F) {
                this.progress -= 0.005F * (float) timeSinceLastChange;
                this.progress = (this.progress < 0.0F) ? 0.0F : this.progress;
                float progress = 1.0F - this.progress;
                float diff = this.scrollToZoom ? (1.0F / this.currentModifier) : 0.25F;
                return diff + (1.0F - diff) * calculateZoomEasing(progress);
            }
        }
        return 1.0F;
    }

    private float calculateZoomEasing(float x) {
        return Easing.IN_OUT_CIRCULAR.getValue(x);
    }

    public void resetZoomState() {
        this.hasScrolled = false;
        getClass();
        this.currentModifier = this.desiredModifier = 4.0F;
        this.progress = 0.0F;
    }

    public void handleZoomStateChange(boolean newZoomed) {
        if (newZoomed && !this.zoomed)
            Mouse.getDWheel();
        this.zoomed = newZoomed;
    }

    public static Zoom getInstance() {
        return INSTANCE;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\all\Zoom.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */