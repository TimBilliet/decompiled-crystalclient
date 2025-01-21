package co.crystaldev.client.gui;

import co.crystaldev.client.font.FontRenderer;
import co.crystaldev.client.util.ArrayUtils;
import co.crystaldev.client.util.enums.ChatColor;

public class ButtonHoverOverlay {
    private final String[] lines;

    private FontRenderer fontRenderer;

    private int width;

    private int height;

    public String[] getLines() {
        return this.lines;
    }

    public FontRenderer getFontRenderer() {
        return this.fontRenderer;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public ButtonHoverOverlay(String[] lines, FontRenderer fontRenderer) {
        String[] arr = new String[0];
        for (String line : lines) {
            for (String split : line.split("\n"))
                arr = (String[]) ArrayUtils.add((Object[]) arr, ChatColor.translate(split));
        }
        this.lines = arr;
        this.fontRenderer = fontRenderer;
        setSize();
    }

    public ButtonHoverOverlay(String content, FontRenderer fontRenderer) {
        this(new String[]{content}, fontRenderer);
    }

    public void setFontRenderer(FontRenderer fontRenderer) {
        this.fontRenderer = fontRenderer;
        setSize();
    }

    private void setSize() {
        this.width = 6 + this.fontRenderer.getMaxWidth(this.lines);
        this.height = 6 + this.fontRenderer.getStringHeight(this.lines);
    }
}
