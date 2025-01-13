package co.crystaldev.client.util.objects;

import co.crystaldev.client.gui.ease.Animation;
import co.crystaldev.client.gui.ease.Easing;
import co.crystaldev.client.gui.ease.IEasingFunction;

import java.awt.*;

public class Notification {
    public static final IEasingFunction EASING_FUNCTION = Easing.OUT_CUBIC;

    private final String title;

    private final String text;

    private final long delay;

    public String getTitle() {
        return this.title;
    }

    public String getText() {
        return this.text;
    }

    public long getDelay() {
        return this.delay;
    }

    private final FadingColor fadingColor = new FadingColor(new Color(0, 0, 0, 0), new Color(0, 0, 0, 255), 200L);

    private Animation animationX;

    private Animation animationY;

    private int width;

    public FadingColor getFadingColor() {
        return this.fadingColor;
    }

    public Animation getAnimationX() {
        return this.animationX;
    }

    public Animation getAnimationY() {
        return this.animationY;
    }

    public void setAnimationX(Animation animationX) {
        this.animationX = animationX;
    }

    public void setAnimationY(Animation animationY) {
        this.animationY = animationY;
    }

    private int y = -1;

    private long expireMs;

    private long createMs;

    public int getWidth() {
        return this.width;
    }

    public int getY() {
        return this.y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setY(int y) {
        this.y = y;
    }

    private boolean setAnimation = true;

    private boolean moved = false;

    private long expiredMs = 0L;

    public Notification(String title, String text, long delay, int width) {
        this.title = title;
        this.text = text;
        this.delay = delay;
        this.width = width;
        this.fadingColor.fade(true);
    }

    public Notification(String text, long delay, int width) {
        this(null, text, delay, width);
    }

    public boolean move() {
        if (!this.moved) {
            this.expireMs = this.delay + System.currentTimeMillis();
            this.createMs = System.currentTimeMillis();
            this.moved = true;
        }
        if (isExpired()) {
            if (this.expiredMs == 0L)
                this.expiredMs = System.currentTimeMillis();
            this.fadingColor.fade(false);
            if (!this.setAnimation) {
                this.animationX = new Animation(300L, 0.0F, (this.width + 10), EASING_FUNCTION);
                this.setAnimation = true;
            }
            return this.animationX.isComplete();
        }
        if (this.setAnimation) {
            this.animationX = new Animation(300L, (this.width + 10), 0.0F, EASING_FUNCTION);
            this.setAnimation = false;
        }
        this.fadingColor.fade(true);
        return false;
    }

    public double getProgress() {
        return (System.currentTimeMillis() - this.createMs) / (float)this.delay;
    }

    public boolean isExpired() {
        return (System.currentTimeMillis() >= this.expireMs);
    }
}