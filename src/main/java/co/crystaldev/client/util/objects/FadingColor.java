package co.crystaldev.client.util.objects;

import co.crystaldev.client.gui.ease.Animation;
import co.crystaldev.client.gui.ease.Easing;
import co.crystaldev.client.gui.ease.IEasingFunction;
import co.crystaldev.client.util.ColorObject;

import java.awt.Color;

public class FadingColor implements Cloneable {
    private Color color1;

    private Color color2;

    private Color currentColor;

    private long fadeTimeMs;

    public Color getColor1() {
        return this.color1;
    }

    public void setColor1(Color color1) {
        this.color1 = color1;
    }

    public Color getColor2() {
        return this.color2;
    }

    public void setColor2(Color color2) {
        this.color2 = color2;
    }

    public Color getCurrentColor() {
        return this.currentColor;
    }

    public long getFadeTimeMs() {
        return this.fadeTimeMs;
    }

    public void setFadeTimeMs(long fadeTimeMs) {
        this.fadeTimeMs = fadeTimeMs;
    }

    private boolean forceUpdate = false;

    private IEasingFunction function;

    private Animation animationRed;

    private Animation animationGreen;

    private Animation animationBlue;

    private Animation animationAlpha;

    public boolean isForceUpdate() {
        return this.forceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public IEasingFunction getFunction() {
        return this.function;
    }

    public void setFunction(IEasingFunction function) {
        this.function = function;
    }

    private boolean wasFading = true;

    public FadingColor(Color color1, Color color2, long fadeTimeMs, IEasingFunction function) {
        this.color1 = color1;
        this.color2 = color2;
        this.fadeTimeMs = fadeTimeMs;
        this.function = function;
    }

    public FadingColor(Color color1, Color color2, IEasingFunction function) {
        this(color1, color2, 300L, function);
    }

    public FadingColor(Color color1, Color color2) {
        this(color1, color2, Easing.OUT_QUAD);
    }

    public FadingColor(Color color1, Color color2, long fadeTimeMs) {
        this(color1, color2, fadeTimeMs, Easing.OUT_QUAD);
    }

    public FadingColor(Color color, int minAlpha, int maxAlpha) {
        this(new Color(color
                .getRed(), color.getGreen(), color.getBlue(), Math.max(minAlpha, 0)), new Color(color
                .getRed(), color.getGreen(), color.getBlue(), Math.min(maxAlpha, 255)));
    }

    public FadingColor(Color color, int minAlpha, int maxAlpha, int fadeTimeMs) {
        this(color, minAlpha, maxAlpha);
        this.fadeTimeMs = fadeTimeMs;
    }

    public void setMinAlpha(int minAlpha) {
        this.color1 = new Color(this.color1.getRed(), this.color1.getGreen(), this.color1.getBlue(), Math.max(minAlpha, 0));
    }

    public void setMaxAlpha(int maxAlpha) {
        this.color2 = new Color(this.color2.getRed(), this.color2.getGreen(), this.color2.getBlue(), Math.min(maxAlpha, 255));
    }

    public void fade(boolean shouldFade) {
        Color sel = shouldFade ? this.color2 : this.color1;
        if (this.currentColor == null)
            this.currentColor = sel;
        if (shouldFade != this.wasFading || areAnimtionsNull()) {
            this.wasFading = shouldFade;
            this.animationRed = new Animation(this.fadeTimeMs, this.currentColor.getRed(), sel.getRed(), this.function);
            this.animationGreen = new Animation(this.fadeTimeMs, this.currentColor.getGreen(), sel.getGreen(), this.function);
            this.animationBlue = new Animation(this.fadeTimeMs, this.currentColor.getBlue(), sel.getBlue(), this.function);
            this.animationAlpha = new Animation(this.fadeTimeMs, this.currentColor.getAlpha(), sel.getAlpha(), this.function);
        }
        if (this.forceUpdate || !isComplete())
            this

                    .currentColor = new Color((int) Math.floor(this.animationRed.getValue()), (int) Math.floor(this.animationGreen.getValue()), (int) Math.floor(this.animationBlue.getValue()), (int) Math.floor(this.animationAlpha.getValue()));
    }

    public ColorObject getCurrentColorObject() {
        ColorObject color = ColorObject.fromColor(getCurrentColor());
        if (this.color1 instanceof ColorObject && this.color2 instanceof ColorObject) {
            ColorObject c1 = (ColorObject) this.color1;
            ColorObject c2 = (ColorObject) this.color2;
            color = new ColorObject(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha(), (c1.isChroma() || c2.isChroma()), (c1.isBold() || c2.isBold()), (c1.isUnderline() || c2.isUnderline()), (c1.isItalic() || c2.isItalic()));
        }
        return color;
    }

    public double getComplete() {
        return (this.animationRed.getValue() + this.animationGreen.getValue() + this.animationBlue.getValue() + this.animationAlpha.getValue()) / 4.0D;
    }

    private boolean areAnimtionsNull() {
        return (this.animationRed == null || this.animationGreen == null || this.animationBlue == null || this.animationAlpha == null);
    }

    private boolean isComplete() {
        return (this.animationRed.isComplete() && this.animationGreen.isComplete() && this.animationBlue.isComplete() && this.animationAlpha.isComplete());
    }

    public FadingColor clone() {
        try {
            return (FadingColor) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }
}
