package co.crystaldev.client.util;

public class ColorUtils {
    public static int hsbToRgb(int hue, int saturation, int brightness) {
        float r, g, b, m;
        hue %= 360;
        float s = saturation / 100.0F;
        float br = brightness / 100.0F;
        float c = br * s;
        float h = hue / 60.0F;
        float x = c * (1.0F - Math.abs(h % 2.0F - 1.0F));
        switch (hue / 60) {
            case 0:
                r = c;
                g = x;
                b = 0.0F;
                m = br - c;
                return (int) ((r + m) * 255.0F) << 16 | (int) ((g + m) * 255.0F) << 8 | (int) ((b + m) * 255.0F);
            case 1:
                r = x;
                g = c;
                b = 0.0F;
                m = br - c;
                return (int) ((r + m) * 255.0F) << 16 | (int) ((g + m) * 255.0F) << 8 | (int) ((b + m) * 255.0F);
            case 2:
                r = 0.0F;
                g = c;
                b = x;
                m = br - c;
                return (int) ((r + m) * 255.0F) << 16 | (int) ((g + m) * 255.0F) << 8 | (int) ((b + m) * 255.0F);
            case 3:
                r = 0.0F;
                g = x;
                b = c;
                m = br - c;
                return (int) ((r + m) * 255.0F) << 16 | (int) ((g + m) * 255.0F) << 8 | (int) ((b + m) * 255.0F);
            case 4:
                r = x;
                g = 0.0F;
                b = c;
                m = br - c;
                return (int) ((r + m) * 255.0F) << 16 | (int) ((g + m) * 255.0F) << 8 | (int) ((b + m) * 255.0F);
            case 5:
                r = c;
                g = 0.0F;
                b = x;
                m = br - c;
                return (int) ((r + m) * 255.0F) << 16 | (int) ((g + m) * 255.0F) << 8 | (int) ((b + m) * 255.0F);
        }
        return 0;
    }

    public static int[] rgbToHsb(int rgb) {
        float h, r = ((rgb & 0xFF0000) >> 16) / 255.0F;
        float g = ((rgb & 0xFF00) >> 8) / 255.0F;
        float b = (rgb & 0xFF) / 255.0F;
        float M = (r > g) ? Math.max(r, b) : Math.max(g, b);
        float m = (r < g) ? Math.min(r, b) : Math.min(g, b);
        float c = M - m;
        if (M == r) {
            for (h = (g - b) / c; h < 0.0F; h += 6.0F) ;
            h %= 6.0F;
        } else if (M == g) {
            h = (b - r) / c + 2.0F;
        } else {
            h = (r - g) / c + 4.0F;
        }
        h *= 60.0F;
        float s = c / M;
        return new int[]{(c == 0.0F) ? -1 : (int) h, (int) (s * 100.0F), (int) (M * 100.0F)};
    }

    public static int convertPercentToValue(float percent) {
        return (int) (percent * 255.0F);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\ColorUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */