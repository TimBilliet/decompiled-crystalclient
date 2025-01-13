package mchorse.mclib.utils.resources;

import mchorse.mclib.utils.Color;

public enum PixelAccessor {
    BYTE {
        public void get(Pixels pixels, int index, Color color) {
            index *= pixels.pixelLength;
            int offset = 0;
            if (pixels.hasAlpha()) {
                color.a = (pixels.pixelBytes[index + offset++] & 0xFF) / 255.0F;
            } else {
                color.a = 1.0F;
            }
            color.b = (pixels.pixelBytes[index + offset++] & 0xFF) / 255.0F;
            color.g = (pixels.pixelBytes[index + offset++] & 0xFF) / 255.0F;
            color.r = (pixels.pixelBytes[index + offset] & 0xFF) / 255.0F;
        }

        public void set(Pixels pixels, int index, Color color) {
            index *= pixels.pixelLength;
            int offset = 0;
            if (pixels.hasAlpha())
                pixels.pixelBytes[index + offset++] = (byte) (int) (color.a * 255.0F);
            pixels.pixelBytes[index + offset++] = (byte) (int) (color.b * 255.0F);
            pixels.pixelBytes[index + offset++] = (byte) (int) (color.g * 255.0F);
            pixels.pixelBytes[index + offset] = (byte) (int) (color.r * 255.0F);
        }
    },
    INT {
        public void get(Pixels pixels, int index, Color color) {
            int c = pixels.pixelInts[index];
            int a = c >> 24 & 0xFF;
            int b = c >> 16 & 0xFF;
            int g = c >> 8 & 0xFF;
            int r = c & 0xFF;
            color.r = r / 255.0F;
            color.g = g / 255.0F;
            color.b = b / 255.0F;
            color.a = a / 255.0F;
        }

        public void set(Pixels pixels, int index, Color color) {
            pixels.pixelInts[index] = ((int) (color.a * 255.0F) << 24) + ((int) (color.b * 255.0F) << 16) + ((int) (color.g * 255.0F) << 8) + (int) (color.r * 255.0F);
        }
    };

    public abstract void get(Pixels paramPixels, int paramInt, Color paramColor);

    public abstract void set(Pixels paramPixels, int paramInt, Color paramColor);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\resources\PixelAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */