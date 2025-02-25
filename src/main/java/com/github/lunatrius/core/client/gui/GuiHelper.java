package com.github.lunatrius.core.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;

public class GuiHelper {
    private static final RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

    public static void drawItemStackWithSlot(TextureManager textureManager, ItemStack itemStack, int x, int y) {
        drawItemStackSlot(textureManager, x, y);
        if (itemStack != null && itemStack.getItem() != null)
            drawItemStack(itemStack, x + 2, y + 2);
    }

    public static void drawItemStackSlot(TextureManager textureManager, int x, int y) {
        textureManager.bindTexture(Gui.statIcons);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        double uScale = 0.0078125D;
        double vScale = 0.0078125D;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        drawTexturedRectangle(worldRenderer, (x + 1), (y + 1), (x + 1 + 18), (y + 1 + 18), 0.0D, 0.0D, 0.0D, 0.140625D, 0.140625D);
        tessellator.draw();
    }

    public static void drawItemStack(ItemStack itemStack, int x, int y) {
        GlStateManager.enableRescaleNormal();
        RenderHelper.enableGUIStandardItemLighting();
        renderItem.renderItemIntoGUI(itemStack, x, y);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
    }

    public static void drawTexturedRectangle(WorldRenderer worldRenderer, double x0, double y0, double x1, double y1, double z, double u0, double v0, double u1, double v1) {
        worldRenderer.pos(x0, y0, z).tex(u0, v0).endVertex();
        worldRenderer.pos(x0, y1, z).tex(u0, v1).endVertex();
        worldRenderer.pos(x1, y1, z).tex(u1, v1).endVertex();
        worldRenderer.pos(x1, y0, z).tex(u1, v0).endVertex();
    }

    public static void drawTexturedRectangle(WorldRenderer worldRenderer, double x0, double y0, double x1, double y1, double z, double textureWidth, double textureHeight, int argb) {
        double u0 = x0 / textureWidth;
        double v0 = y0 / textureHeight;
        double u1 = x1 / textureWidth;
        double v1 = y1 / textureHeight;
        drawTexturedRectangle(worldRenderer, x0, y0, x1, y1, z, u0, v0, u1, v1, argb);
    }

    public static void drawTexturedRectangle(WorldRenderer worldRenderer, double x0, double y0, double x1, double y1, double z, double u0, double v0, double u1, double v1, int argb) {
        int a = argb >> 24 & 0xFF;
        int r = argb >> 16 & 0xFF;
        int g = argb >> 8 & 0xFF;
        int b = argb & 0xFF;
        drawTexturedRectangle(worldRenderer, x0, y0, x1, y1, z, u0, v0, u1, v1, r, g, b, a);
    }

    public static void drawTexturedRectangle(WorldRenderer worldRenderer, double x0, double y0, double x1, double y1, double z, double u0, double v0, double u1, double v1, int r, int g, int b, int a) {
        worldRenderer.pos(x0, y0, z).tex(u0, v0).color(r, g, b, a).endVertex();
        worldRenderer.pos(x0, y1, z).tex(u0, v1).color(r, g, b, a).endVertex();
        worldRenderer.pos(x1, y1, z).tex(u1, v1).color(r, g, b, a).endVertex();
        worldRenderer.pos(x1, y0, z).tex(u1, v0).color(r, g, b, a).endVertex();
    }

    public static void drawColoredRectangle(WorldRenderer worldRenderer, double x0, double y0, double x1, double y1, double z, int argb) {
        int a = argb >> 24 & 0xFF;
        int r = argb >> 16 & 0xFF;
        int g = argb >> 8 & 0xFF;
        int b = argb & 0xFF;
        drawColoredRectangle(worldRenderer, x0, y0, x1, y1, z, r, g, b, a);
    }

    public static void drawColoredRectangle(WorldRenderer worldRenderer, double x0, double y0, double x1, double y1, double z, int r, int g, int b, int a) {
        worldRenderer.pos(x0, y0, z).color(r, g, b, a).endVertex();
        worldRenderer.pos(x0, y1, z).color(r, g, b, a).endVertex();
        worldRenderer.pos(x1, y1, z).color(r, g, b, a).endVertex();
        worldRenderer.pos(x1, y0, z).color(r, g, b, a).endVertex();
    }

    public static void drawVerticalGradientRectangle(WorldRenderer worldRenderer, double x0, double y0, double x1, double y1, double z, int startColor, int endColor) {
        int sa = startColor >> 24 & 0xFF;
        int sr = startColor >> 16 & 0xFF;
        int sg = startColor >> 8 & 0xFF;
        int sb = startColor & 0xFF;
        int ea = endColor >> 24 & 0xFF;
        int er = endColor >> 16 & 0xFF;
        int eg = endColor >> 8 & 0xFF;
        int eb = endColor & 0xFF;
        drawVerticalGradientRectangle(worldRenderer, x0, y0, x1, y1, z, sr, sg, sb, sa, er, eg, eb, ea);
    }

    public static void drawVerticalGradientRectangle(WorldRenderer worldRenderer, double x0, double y0, double x1, double y1, double z, int sr, int sg, int sb, int sa, int er, int eg, int eb, int ea) {
        worldRenderer.pos(x0, y0, z).color(sr, sg, sb, sa).endVertex();
        worldRenderer.pos(x0, y1, z).color(er, eg, eb, ea).endVertex();
        worldRenderer.pos(x1, y1, z).color(er, eg, eb, ea).endVertex();
        worldRenderer.pos(x1, y0, z).color(sr, sg, sb, sa).endVertex();
    }

    public static void drawHorizontalGradientRectangle(WorldRenderer worldRenderer, double x0, double y0, double x1, double y1, double z, int startColor, int endColor) {
        int sa = startColor >> 24 & 0xFF;
        int sr = startColor >> 16 & 0xFF;
        int sg = startColor >> 8 & 0xFF;
        int sb = startColor & 0xFF;
        int ea = endColor >> 24 & 0xFF;
        int er = endColor >> 16 & 0xFF;
        int eg = endColor >> 8 & 0xFF;
        int eb = endColor & 0xFF;
        drawHorizontalGradientRectangle(worldRenderer, x0, y0, x1, y1, z, sr, sg, sb, sa, er, eg, eb, ea);
    }

    public static void drawHorizontalGradientRectangle(WorldRenderer worldRenderer, double x0, double y0, double x1, double y1, double z, int sr, int sg, int sb, int sa, int er, int eg, int eb, int ea) {
        worldRenderer.pos(x0, y0, z).color(sr, sg, sb, sa).endVertex();
        worldRenderer.pos(x0, y1, z).color(sr, sg, sb, sa).endVertex();
        worldRenderer.pos(x1, y1, z).color(er, eg, eb, ea).endVertex();
        worldRenderer.pos(x1, y0, z).color(er, eg, eb, ea).endVertex();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\core\client\gui\GuiHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */