package mchorse.mclib.client;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class Draw {
    public static void axis(float length) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer buffer = tessellator.getWorldRenderer();
        GL11.glLineWidth(5.0F);
        buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(0.0D, 0.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(length, 0.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(0.0D, 0.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(0.0D, length, 0.0D).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(0.0D, 0.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(0.0D, 0.0D, length).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
        tessellator.draw();
        GL11.glLineWidth(3.0F);
        buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(0.0D, 0.0D, 0.0D).color(1.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(length, 0.0D, 0.0D).color(1.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(0.0D, 0.0D, 0.0D).color(0.0F, 1.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(0.0D, length, 0.0D).color(0.0F, 1.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(0.0D, 0.0D, 0.0D).color(0.0F, 0.0F, 1.0F, 1.0F).endVertex();
        buffer.pos(0.0D, 0.0D, length).color(0.0F, 0.0F, 1.0F, 1.0F).endVertex();
        tessellator.draw();
        GL11.glLineWidth(1.0F);
        point(0.0D, 0.0D, 0.0D);
    }

    public static void point(double x, double y, double z) {
        GL11.glPointSize(12.0F);
        GL11.glBegin(0);
        GL11.glColor3d(0.0D, 0.0D, 0.0D);
        GL11.glVertex3d(0.0D, 0.0D, 0.0D);
        GL11.glEnd();
        GL11.glPointSize(10.0F);
        GL11.glBegin(0);
        GL11.glColor3d(1.0D, 1.0D, 1.0D);
        GL11.glVertex3d(0.0D, 0.0D, 0.0D);
        GL11.glEnd();
        GL11.glPointSize(1.0F);
    }

    public static void cube(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer buffer = tessellator.getWorldRenderer();
        buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        cube(buffer, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
        tessellator.draw();
    }

    public static void cube(WorldRenderer buffer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha) {
        buffer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mclib\client\Draw.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */