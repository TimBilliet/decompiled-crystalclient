package co.crystaldev.client.feature.base;

import co.crystaldev.client.feature.annotations.ConfigurableSize;
import co.crystaldev.client.feature.annotations.Hidden;
import co.crystaldev.client.feature.annotations.properties.Position;
import co.crystaldev.client.feature.annotations.properties.Slider;
import co.crystaldev.client.util.enums.AnchorRegion;
import co.crystaldev.client.util.objects.ModulePosition;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

public abstract class HudModule extends Module {
    @Slider(label = "Scale", minimum = 0.5D, maximum = 1.5D, standard = 1.0D)
    public double scale = 1.0D;

    @Slider(label = "Width", minimum = 60.0D, maximum = 300.0D, standard = 60.0D, integers = true, requires = {ConfigurableSize.class})
    public int width = 60;

    @Slider(label = "Height", minimum = 18.0D, maximum = 300.0D, standard = 18.0D, integers = true, requires = {ConfigurableSize.class})
    public int height = 18;

    @Position
    @Hidden
    public ModulePosition position = new ModulePosition(AnchorRegion.TOP_LEFT, 0.0F, 0.0F);

    public ScaledResolution scaledResolution;

    public boolean canScale = true;

    public boolean displayWhileDisabled = true;

    public HudModule() {
        this.enabled = false;
    }

    public void drawDefault() {
        draw();
    }

    public void configPostInit() {
        super.configPostInit();
        setOptionVisibility("Scale", f -> this.canScale);
        if (!this.canScale)
            this.scale = 1.0D;
    }

    public boolean isHovered(int mouseX, int mouseY) {
        return (mouseX > getX() && mouseX <
                getX() + this.width * this.scale && mouseY >
                getY() && mouseY <
                getY() + this.height * this.scale);
    }

    public int getX() {
        AnchorRegion ar = this.position.getAnchorRegion();
        int width = (int) (this.width * this.scale);
        int x = ar.isRightSided() ? (int) (ar.getRelativeX() - this.position.getX() - width) : (int) (ar.getRelativeX() + this.position.getX() - (ar.isCenteredHorizontally() ? (width / 2) : 0));
        return Math.max(ar.clampX(x + width) - width, 0);
    }

    public int getY() {
        AnchorRegion ar = this.position.getAnchorRegion();
        int height = (int) (this.height * this.scale);
        int y = ar.isBottomSided() ? (int) (ar.getRelativeY() - this.position.getY() - height) : (int) (ar.getRelativeY() + this.position.getY() - (ar.isCenteredVertically() ? (height / 2) : 0));
        return Math.max(ar.clampY(y + height) - height, 0);
    }

    public int getUntranslatedX() {
        AnchorRegion ar = this.position.getAnchorRegion();
        int x = ar.isRightSided() ? (int) (ar.getRelativeX() - this.position.getX()) : (int) (ar.getRelativeX() + this.position.getX());
        return Math.max(ar.clampX(x), 0);
    }

    public int getUntranslatedY() {
        AnchorRegion ar = this.position.getAnchorRegion();
        int y = ar.isBottomSided() ? (int) (ar.getRelativeY() - this.position.getY()) : (int) (ar.getRelativeY() + this.position.getY());
        return Math.max(ar.clampY(y), 0);
    }

    public int getRenderUntranslatedX() {
        return (int) (getUntranslatedX() / this.scale);
    }

    public int getRenderUntranslatedY() {
        return (int) (getUntranslatedY() / this.scale);
    }

    public int getRenderX() {
        return (int) (getX() / this.scale);
    }

    public int getRenderY() {
        return (int) (getY() / this.scale);
    }

    public void setupAnchorRegion() {
        for (AnchorRegion ar : AnchorRegion.values()) {
            if (ar.isInBounds(getX(), getY())) {
                double width = this.width * this.scale;
                double height = this.height * this.scale;
                int offsetX = ar.isCenteredHorizontally() ? (int) (width / 2.0D) : 0;
                int offsetY = ar.isCenteredVertically() ? (int) (height / 2.0D) : 0;
                float newX = ar.isRightSided() ? (ar.getRelativeX() - (int) (getX() + width)) : ((getX() + offsetX) - ar.getRelativeX());
                float newY = ar.isBottomSided() ? (ar.getRelativeY() - (int) (getY() + height)) : ((getY() + offsetY) - ar.getRelativeY());
                this.position = new ModulePosition(ar, newX, newY);
                break;
            }
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), getX() + (int) (this.width * this.scale), getY() + (int) (this.height * this.scale));
    }

    public abstract void draw();
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\base\HudModule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */