package mapwriter.region;

import co.crystaldev.client.feature.impl.hud.MapWriter;

public class ChunkRender {
    public static final byte FLAG_UNPROCESSED = 0;

    public static final byte FLAG_NON_OPAQUE = 1;

    public static final byte FLAG_OPAQUE = 2;

    public static final double brightenExponent = 0.35D;

    public static final double darkenExponent = 0.35D;

    public static final double brightenAmplitude = 0.7D;

    public static final double darkenAmplitude = 1.4D;

    public static double getHeightShading(int height, int heightW, int heightN) {
        int samples = 0;
        int heightDiff = 0;
        if (heightW > 0 && heightW < 255) {
            heightDiff += height - heightW;
            samples++;
        }
        if (heightN > 0 && heightN < 255) {
            heightDiff += height - heightN;
            samples++;
        }
        double heightDiffFactor = 0.0D;
        if (samples > 0)
            heightDiffFactor = (float) heightDiff / samples;
        if ((MapWriter.getInstance()).realisticMap)
            return Math.atan(heightDiffFactor) * 0.3D;
        return (heightDiffFactor >= 0.0D) ? (Math.pow(heightDiffFactor * 0.00392156862745098D, 0.35D) * 0.7D) : (-Math.pow(-(heightDiffFactor * 0.00392156862745098D), 0.35D) * 1.4D);
    }

    public static int getColumnColour(BlockColors bc, IChunk chunk, int x, int y, int z, int heightW, int heightN) {
        double a = 1.0D;
        double r = 0.0D;
        double g = 0.0D;
        double b = 0.0D;
        if (bc != null)
            for (; y > 0; y--) {
                int blockAndMeta = chunk.getBlockAndMetadata(x, y, z);
                int c1 = bc.getColour(blockAndMeta);
                int alpha = c1 >> 24 & 0xFF;
                if (c1 == -8650628)
                    alpha = 0;
                if (alpha > 0) {
                    int biome = chunk.getBiome(x, z);
                    int c2 = bc.getBiomeColour(blockAndMeta, biome);
                    double c1A = alpha / 255.0D;
                    double c1R = (c1 >> 16 & 0xFF) / 255.0D;
                    double c1G = (c1 >> 8 & 0xFF) / 255.0D;
                    double c1B = (c1 & 0xFF) / 255.0D;
                    double c2R = (c2 >> 16 & 0xFF) / 255.0D;
                    double c2G = (c2 >> 8 & 0xFF) / 255.0D;
                    double c2B = (c2 & 0xFF) / 255.0D;
                    r += a * c1A * c1R * c2R;
                    g += a * c1A * c1G * c2G;
                    b += a * c1A * c1B * c2B;
                    a *= 1.0D - c1A;
                }
                if (alpha == 255)
                    break;
            }
        double heightShading = getHeightShading(y, heightW, heightN);
        int lightValue = chunk.getLightValue(x, y + 1, z);
        double lightShading = lightValue / 15.0D;
        double shading = (heightShading + 1.0D) * lightShading;
        r = Math.min(Math.max(0.0D, r * shading), 1.0D);
        g = Math.min(Math.max(0.0D, g * shading), 1.0D);
        b = Math.min(Math.max(0.0D, b * shading), 1.0D);
        return (y & 0xFF) << 24 | ((int) (r * 255.0D) & 0xFF) << 16 | ((int) (g * 255.0D) & 0xFF) << 8 | (int) (b * 255.0D) & 0xFF;
    }

    static int getPixelHeightN(int[] pixels, int offset, int scanSize) {
        return (offset >= scanSize) ? (pixels[offset - scanSize] >> 24 & 0xFF) : -1;
    }

    static int getPixelHeightW(int[] pixels, int offset, int scanSize) {
        return ((offset & scanSize - 1) >= 1) ? (pixels[offset - 1] >> 24 & 0xFF) : -1;
    }

    public static void renderSurface(BlockColors bc, IChunk chunk, int[] pixels, int offset, int scanSize, boolean dimensionHasCeiling) {
        int chunkMaxY = chunk.getMaxY();
        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
                int y;
                if (dimensionHasCeiling && !(MapWriter.getInstance()).fixNetherCeiling) {
                    for (y = 127; y >= 0; y--) {
                        int blockAndMeta = chunk.getBlockAndMetadata(x, y, z);
                        int alpha = bc.getColour(blockAndMeta) >> 24 & 0xFF;
                        if (bc.getColour(blockAndMeta) == -8650628)
                            alpha = 0;
                        if (alpha != 255)
                            break;
                    }
                } else {
                    y = chunkMaxY - 1;
                }
                int pixelOffset = offset + z * scanSize + x;
                pixels[pixelOffset] = getColumnColour(bc, chunk, x, y, z, getPixelHeightW(pixels, pixelOffset, scanSize), getPixelHeightN(pixels, pixelOffset, scanSize));
            }
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwriter\region\ChunkRender.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */