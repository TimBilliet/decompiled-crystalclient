package co.crystaldev.client.patcher.enhancement.text;

public final class CachedString {
    private final String text;

    private final int listId;

    private final float height;

    private float width;

    private float lastRed;

    private float lastBlue;

    private float lastGreen;

    private float lastAlpha;

    public CachedString(String text, int listId, float width, float height) {
        this.text = text;
        this.listId = listId;
        this.width = width;
        this.height = height;
    }

    public String getText() {
        return this.text;
    }

    public int getListId() {
        return this.listId;
    }

    public float getLastAlpha() {
        return this.lastAlpha;
    }

    public void setLastAlpha(float lastAlpha) {
        this.lastAlpha = lastAlpha;
    }

    public float getLastRed() {
        return this.lastRed;
    }

    public void setLastRed(float lastRed) {
        this.lastRed = lastRed;
    }

    public float getLastBlue() {
        return this.lastBlue;
    }

    public void setLastBlue(float lastBlue) {
        this.lastBlue = lastBlue;
    }

    public float getLastGreen() {
        return this.lastGreen;
    }

    public void setLastGreen(float lastGreen) {
        this.lastGreen = lastGreen;
    }

    public float getWidth() {
        return this.width;
    }

    public void setWidth(float v) {
        this.width = v;
    }

    public float getHeight() {
        return this.height;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\patcher\enhancement\text\CachedString.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */