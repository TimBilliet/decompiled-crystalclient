package mapwriter.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class Render {
    public static double zDepth = 0.0D;

    public static final double circleSteps = 30.0D;

    public static void setColourWithAlphaPercent(int colour, int alphaPercent) {
        setColour((alphaPercent * 255 / 100 & 0xFF) << 24 | colour & 0xFFFFFF);
    }

    public static void setColour(int colour) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.color((colour >> 16 & 0xFF) / 255.0F, (colour >> 8 & 0xFF) / 255.0F, (colour & 0xFF) / 255.0F, (colour >> 24 & 0xFF) / 255.0F);
        GlStateManager.disableBlend();
    }

    public static void resetColour() {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static int multiplyColours(int c1, int c2) {
        float c1A = (c1 >> 24 & 0xFF);
        float c1R = (c1 >> 16 & 0xFF);
        float c1G = (c1 >> 8 & 0xFF);
        float c1B = (c1 & 0xFF);
        float c2A = (c2 >> 24 & 0xFF);
        float c2R = (c2 >> 16 & 0xFF);
        float c2G = (c2 >> 8 & 0xFF);
        float c2B = (c2 & 0xFF);
        int r = (int) (c1R * c2R / 255.0F) & 0xFF;
        int g = (int) (c1G * c2G / 255.0F) & 0xFF;
        int b = (int) (c1B * c2B / 255.0F) & 0xFF;
        int a = (int) (c1A * c2A / 255.0F) & 0xFF;
        return a << 24 | r << 16 | g << 8 | b;
    }

    public static int getAverageOfPixelQuad(int[] pixels, int offset, int scanSize) {
        int p00 = pixels[offset];
        int p01 = pixels[offset + 1];
        int p10 = pixels[offset + scanSize];
        int p11 = pixels[offset + scanSize + 1];
        int r = (p00 >> 16 & 0xFF) + (p01 >> 16 & 0xFF) + (p10 >> 16 & 0xFF) + (p11 >> 16 & 0xFF);
        r >>= 2;
        int g = (p00 >> 8 & 0xFF) + (p01 >> 8 & 0xFF) + (p10 >> 8 & 0xFF) + (p11 >> 8 & 0xFF);
        g >>= 2;
        int b = (p00 & 0xFF) + (p01 & 0xFF) + (p10 & 0xFF) + (p11 & 0xFF);
        b >>= 2;
        return 0xFF000000 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
    }

    public static int getAverageColourOfArray(int[] pixels) {
        int count = 0;
        double totalA = 0.0D;
        double totalR = 0.0D;
        double totalG = 0.0D;
        double totalB = 0.0D;
        for (int pixel : pixels) {
            double a = (pixel >> 24 & 0xFF);
            double r = (pixel >> 16 & 0xFF);
            double g = (pixel >> 8 & 0xFF);
            double b = (pixel & 0xFF);
            totalA += a;
            totalR += r * a / 255.0D;
            totalG += g * a / 255.0D;
            totalB += b * a / 255.0D;
            count++;
        }
        totalR = totalR * 255.0D / totalA;
        totalG = totalG * 255.0D / totalA;
        totalB = totalB * 255.0D / totalA;
        totalA /= count;
        return ((int) totalA & 0xFF) << 24 | ((int) totalR & 0xFF) << 16 | ((int) totalG & 0xFF) << 8 | (int) totalB & 0xFF;
    }

    public static int adjustPixelBrightness(int colour, int brightness) {
        int r = colour >> 16 & 0xFF;
        int g = colour >> 8 & 0xFF;
        int b = colour & 0xFF;
        r = Math.min(Math.max(0, r + brightness), 255);
        g = Math.min(Math.max(0, g + brightness), 255);
        b = Math.min(Math.max(0, b + brightness), 255);
        return colour & 0xFF000000 | r << 16 | g << 8 | b;
    }

    public static int getTextureWidth() {
        return GL11.glGetTexLevelParameteri(3553, 0, 4096);
    }

    public static int getTextureHeight() {
        return GL11.glGetTexLevelParameteri(3553, 0, 4097);
    }

    public static int getBoundTextureId() {
        return GL11.glGetInteger(32873);
    }

    public static void printBoundTextureInfo(int texture) {
        int w = getTextureWidth();
        int h = getTextureHeight();
        int depth = GL11.glGetTexLevelParameteri(3553, 0, 32881);
        int format = GL11.glGetTexLevelParameteri(3553, 0, 4099);
        Logging.log("texture %d parameters: width=%d, height=%d, depth=%d, format=%08x", texture, w, h, depth, format);
    }

    public static int getMaxTextureSize() {
        return GL11.glGetInteger(3379);
    }

    public static void drawTexturedRect(double x, double y, double w, double h) {
        drawTexturedRect(x, y, w, h, 0.0D, 0.0D, 1.0D, 1.0D);
    }

    public static void drawTexturedRect(double x, double y, double w, double h, double u1, double v1, double u2, double v2) {
        try {
            GlStateManager.enableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer renderer = tessellator.getWorldRenderer();
            renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
            renderer.pos(x + w, y, zDepth).tex(u2, v1).endVertex();
            renderer.pos(x, y, zDepth).tex(u1, v1).endVertex();
            renderer.pos(x, y + h, zDepth).tex(u1, v2).endVertex();
            renderer.pos(x + w, y + h, zDepth).tex(u2, v2).endVertex();
            tessellator.draw();
            GlStateManager.disableBlend();
        } catch (NullPointerException e) {
            Logging.log("MwRender.drawTexturedRect: null pointer exception");
        }
    }

    public static void drawArrow(double x, double y, double angle, double length) {
        double arrowBackAngle = 2.356194490192345D;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(770, 771);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer renderer = tessellator.getWorldRenderer();
        renderer.begin(6, DefaultVertexFormats.POSITION);
        renderer.pos(x + length * Math.cos(angle), y + length * Math.sin(angle), zDepth).endVertex();
        renderer.pos(x + length * 0.5D * Math.cos(angle - arrowBackAngle), y + length * 0.5D * Math.sin(angle - arrowBackAngle), zDepth).endVertex();
        renderer.pos(x + length * 0.3D * Math.cos(angle + Math.PI), y + length * 0.3D * Math.sin(angle + Math.PI), zDepth).endVertex();
        renderer.pos(x + length * 0.5D * Math.cos(angle + arrowBackAngle), y + length * 0.5D * Math.sin(angle + arrowBackAngle), zDepth).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawTriangle(double x1, double y1, double x2, double y2, double x3, double y3) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(770, 771);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer renderer = tessellator.getWorldRenderer();
        renderer.begin(4, DefaultVertexFormats.POSITION);
        renderer.pos(x1, y1, zDepth).endVertex();
        renderer.pos(x2, y2, zDepth).endVertex();
        renderer.pos(x3, y3, zDepth).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawRect(double x, double y, double w, double h) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(770, 771);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer renderer = tessellator.getWorldRenderer();
        renderer.begin(7, DefaultVertexFormats.POSITION);
        renderer.pos(x + w, y, zDepth).endVertex();
        renderer.pos(x, y, zDepth).endVertex();
        renderer.pos(x, y + h, zDepth).endVertex();
        renderer.pos(x + w, y + h, zDepth).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawCircle(double x, double y, double r) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(770, 771);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer renderer = tessellator.getWorldRenderer();
        renderer.begin(6, DefaultVertexFormats.POSITION);
        renderer.pos(x, y, zDepth).endVertex();
        double end = 6.283185307179586D;
        double incr = end / 30.0D;
        double theta;
        for (theta = -incr; theta < end; theta += incr)
            renderer.pos(x + r * Math.cos(-theta), y + r * Math.sin(-theta), zDepth).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawCircleBorder(double x, double y, double r, double width) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(770, 771);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer renderer = tessellator.getWorldRenderer();
        renderer.begin(5, DefaultVertexFormats.POSITION);
        double end = 6.283185307179586D;
        double incr = end / 30.0D;
        double r2 = r + width;
        double theta;
        for (theta = -incr; theta < end; theta += incr) {
            renderer.pos(x + r * Math.cos(-theta), y + r * Math.sin(-theta), zDepth).endVertex();
            renderer.pos(x + r2 * Math.cos(-theta), y + r2 * Math.sin(-theta), zDepth).endVertex();
        }
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawRectBorder(double x, double y, double w, double h, double bw) {
        drawRect(x - bw, y - bw, w + bw + bw, bw);
        drawRect(x - bw, y + h, w + bw + bw, bw);
        drawRect(x - bw, y, bw, h);
        drawRect(x + w, y, bw, h);
    }

    public static void drawString(int x, int y, int colour, String formatString, Object... args) {
        Minecraft mc = Minecraft.getMinecraft();
        //FontRenderer fr = mc.fontRenderer;
        FontRenderer fr = mc.fontRendererObj;
        String s = String.format(formatString, args);
        fr.drawStringWithShadow(s, x, y, colour);
    }

    public static void drawCentredString(int x, int y, int colour, String formatString, Object... args) {
        Minecraft mc = Minecraft.getMinecraft();
        //FontRenderer fr = mc.fontRenderer;
        FontRenderer fr = mc.fontRendererObj;
        String s = String.format(formatString, args);
        int w = fr.getStringWidth(s);
        fr.drawStringWithShadow(s, (x - w / 2.0F), y, colour);//float
    }

    public static void setCircularStencil(double x, double y, double r) {
        GlStateManager.enableDepth();
        GlStateManager.colorMask(false, false, false, false);
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(519);
        setColour(-1);
        double zDepth = Render.zDepth;
        Render.zDepth = 1000.0D;
        drawCircle(x, y, r);
        Render.zDepth = zDepth;
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(516);
    }

    public static void disableStencil() {
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
        GlStateManager.disableDepth();
        zDepth = 0.0D;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwrite\\util\Render.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */