package com.github.lunatrius.core.client.renderer;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class GeometryTessellator extends Tessellator {
  private static GeometryTessellator instance = null;
  
  private static double deltaS = 0.0D;
  
  private double delta = 0.0D;
  
  public GeometryTessellator() {
    this(2097152);
  }
  
  public GeometryTessellator(int size) {
    super(size);
  }
  
  public static GeometryTessellator getInstance() {
    if (instance == null)
      instance = new GeometryTessellator(); 
    return instance;
  }
  
  public void setTranslation(double x, double y, double z) {
    getWorldRenderer().setTranslation(x, y, z);
  }
  
  public void beginQuads() {
    begin(7);
  }
  
  public void beginLines() {
    begin(1);
  }
  
  public void begin(int mode) {
    getWorldRenderer().begin(mode, DefaultVertexFormats.POSITION_COLOR);
  }
  
  public void draw() {
    super.draw();
  }
  
  public void setDelta(double delta) {
    this.delta = delta;
  }
  
  public static void setStaticDelta(double delta) {
    deltaS = delta;
  }
  
  public void drawCuboid(BlockPos pos, int sides, int argb) {
    drawCuboid(pos, pos, sides, argb);
  }
  
  public void drawCuboid(BlockPos begin, BlockPos end, int sides, int argb) {
    drawCuboid(getWorldRenderer(), begin, end, sides, argb, this.delta);
  }
  
  public static void drawCuboid(WorldRenderer worldRenderer, BlockPos pos, int sides, int argb) {
    drawCuboid(worldRenderer, pos, pos, sides, argb);
  }
  
  public static void drawCuboid(WorldRenderer worldRenderer, BlockPos begin, BlockPos end, int sides, int argb) {
    drawCuboid(worldRenderer, begin, end, sides, argb, deltaS);
  }
  
  private static void drawCuboid(WorldRenderer worldRenderer, BlockPos begin, BlockPos end, int sides, int argb, double delta) {
    if (worldRenderer.getDrawMode() == -1 || sides == 0 || worldRenderer.getVertexFormat() == null)
      return; 
    double x0 = begin.getX() - delta;
    double y0 = begin.getY() - delta;
    double z0 = begin.getZ() - delta;
    double x1 = (end.getX() + 1) + delta;
    double y1 = (end.getY() + 1) + delta;
    double z1 = (end.getZ() + 1) + delta;
    switch (worldRenderer.getDrawMode()) {
      case 7:
        drawQuads(worldRenderer, x0, y0, z0, x1, y1, z1, sides, argb);
        return;
      case 1:
        drawLines(worldRenderer, x0, y0, z0, x1, y1, z1, sides, argb);
        return;
    } 
    throw new IllegalStateException("Unsupported mode!");
  }
  
  public static void drawCuboid(WorldRenderer worldRenderer, AxisAlignedBB aabb, int sides, int argb) {
    drawCuboid(worldRenderer, aabb, sides, argb, deltaS);
  }
  
  public static void drawCuboid(WorldRenderer worldRenderer, AxisAlignedBB aabb, int sides, int argb, double delta) {
    if (worldRenderer.getDrawMode() == -1 || sides == 0 || worldRenderer.getVertexFormat() == null)
      return; 
    double x0 = aabb.minX - delta;
    double y0 = aabb.minY - delta;
    double z0 = aabb.minZ - delta;
    double x1 = aabb.maxX + delta;
    double y1 = aabb.maxY + delta;
    double z1 = aabb.maxZ + delta;
    switch (worldRenderer.getDrawMode()) {
      case 7:
        drawQuads(worldRenderer, x0, y0, z0, x1, y1, z1, sides, argb);
        return;
      case 1:
        drawLines(worldRenderer, x0, y0, z0, x1, y1, z1, sides, argb);
        return;
    } 
    throw new IllegalStateException("Unsupported mode!");
  }
  
  public static void drawQuads(WorldRenderer worldRenderer, double x0, double y0, double z0, double x1, double y1, double z1, int sides, int argb) {
    int a = argb >>> 24 & 0xFF;
    int r = argb >>> 16 & 0xFF;
    int g = argb >>> 8 & 0xFF;
    int b = argb & 0xFF;
    drawQuads(worldRenderer, x0, y0, z0, x1, y1, z1, sides, a, r, g, b);
  }
  
  public static void drawQuads(WorldRenderer worldRenderer, double x0, double y0, double z0, double x1, double y1, double z1, int sides, int a, int r, int g, int b) {
    if ((sides & 0x1) != 0) {
      worldRenderer.pos(x1, y0, z0).color(r, g, b, a).endVertex();
      worldRenderer.pos(x1, y0, z1).color(r, g, b, a).endVertex();
      worldRenderer.pos(x0, y0, z1).color(r, g, b, a).endVertex();
      worldRenderer.pos(x0, y0, z0).color(r, g, b, a).endVertex();
    } 
    if ((sides & 0x2) != 0) {
      worldRenderer.pos(x1, y1, z0).color(r, g, b, a).endVertex();
      worldRenderer.pos(x0, y1, z0).color(r, g, b, a).endVertex();
      worldRenderer.pos(x0, y1, z1).color(r, g, b, a).endVertex();
      worldRenderer.pos(x1, y1, z1).color(r, g, b, a).endVertex();
    } 
    if ((sides & 0x4) != 0) {
      worldRenderer.pos(x1, y0, z0).color(r, g, b, a).endVertex();
      worldRenderer.pos(x0, y0, z0).color(r, g, b, a).endVertex();
      worldRenderer.pos(x0, y1, z0).color(r, g, b, a).endVertex();
      worldRenderer.pos(x1, y1, z0).color(r, g, b, a).endVertex();
    } 
    if ((sides & 0x8) != 0) {
      worldRenderer.pos(x0, y0, z1).color(r, g, b, a).endVertex();
      worldRenderer.pos(x1, y0, z1).color(r, g, b, a).endVertex();
      worldRenderer.pos(x1, y1, z1).color(r, g, b, a).endVertex();
      worldRenderer.pos(x0, y1, z1).color(r, g, b, a).endVertex();
    } 
    if ((sides & 0x10) != 0) {
      worldRenderer.pos(x0, y0, z0).color(r, g, b, a).endVertex();
      worldRenderer.pos(x0, y0, z1).color(r, g, b, a).endVertex();
      worldRenderer.pos(x0, y1, z1).color(r, g, b, a).endVertex();
      worldRenderer.pos(x0, y1, z0).color(r, g, b, a).endVertex();
    } 
    if ((sides & 0x20) != 0) {
      worldRenderer.pos(x1, y0, z1).color(r, g, b, a).endVertex();
      worldRenderer.pos(x1, y0, z0).color(r, g, b, a).endVertex();
      worldRenderer.pos(x1, y1, z0).color(r, g, b, a).endVertex();
      worldRenderer.pos(x1, y1, z1).color(r, g, b, a).endVertex();
    } 
  }
  
  public static void drawLines(WorldRenderer worldRenderer, double x0, double y0, double z0, double x1, double y1, double z1, int sides, int argb) {
    int a = argb >>> 24 & 0xFF;
    int r = argb >>> 16 & 0xFF;
    int g = argb >>> 8 & 0xFF;
    int b = argb & 0xFF;
    drawLines(worldRenderer, x0, y0, z0, x1, y1, z1, sides, a, r, g, b);
  }
  
  public static void drawLines(WorldRenderer worldRenderer, double x0, double y0, double z0, double x1, double y1, double z1, int sides, int a, int r, int g, int b) {
    if ((sides & 0x11) != 0) {
      worldRenderer.pos(x0, y0, z0).color(r, g, b, a).endVertex();
      worldRenderer.pos(x0, y0, z1).color(r, g, b, a).endVertex();
    } 
    if ((sides & 0x12) != 0) {
      worldRenderer.pos(x0, y1, z0).color(r, g, b, a).endVertex();
      worldRenderer.pos(x0, y1, z1).color(r, g, b, a).endVertex();
    } 
    if ((sides & 0x21) != 0) {
      worldRenderer.pos(x1, y0, z0).color(r, g, b, a).endVertex();
      worldRenderer.pos(x1, y0, z1).color(r, g, b, a).endVertex();
    } 
    if ((sides & 0x22) != 0) {
      worldRenderer.pos(x1, y1, z0).color(r, g, b, a).endVertex();
      worldRenderer.pos(x1, y1, z1).color(r, g, b, a).endVertex();
    } 
    if ((sides & 0x5) != 0) {
      worldRenderer.pos(x0, y0, z0).color(r, g, b, a).endVertex();
      worldRenderer.pos(x1, y0, z0).color(r, g, b, a).endVertex();
    } 
    if ((sides & 0x6) != 0) {
      worldRenderer.pos(x0, y1, z0).color(r, g, b, a).endVertex();
      worldRenderer.pos(x1, y1, z0).color(r, g, b, a).endVertex();
    } 
    if ((sides & 0x9) != 0) {
      worldRenderer.pos(x0, y0, z1).color(r, g, b, a).endVertex();
      worldRenderer.pos(x1, y0, z1).color(r, g, b, a).endVertex();
    } 
    if ((sides & 0xA) != 0) {
      worldRenderer.pos(x0, y1, z1).color(r, g, b, a).endVertex();
      worldRenderer.pos(x1, y1, z1).color(r, g, b, a).endVertex();
    } 
    if ((sides & 0x14) != 0) {
      worldRenderer.pos(x0, y0, z0).color(r, g, b, a).endVertex();
      worldRenderer.pos(x0, y1, z0).color(r, g, b, a).endVertex();
    } 
    if ((sides & 0x24) != 0) {
      worldRenderer.pos(x1, y0, z0).color(r, g, b, a).endVertex();
      worldRenderer.pos(x1, y1, z0).color(r, g, b, a).endVertex();
    } 
    if ((sides & 0x18) != 0) {
      worldRenderer.pos(x0, y0, z1).color(r, g, b, a).endVertex();
      worldRenderer.pos(x0, y1, z1).color(r, g, b, a).endVertex();
    } 
    if ((sides & 0x28) != 0) {
      worldRenderer.pos(x1, y0, z1).color(r, g, b, a).endVertex();
      worldRenderer.pos(x1, y1, z1).color(r, g, b, a).endVertex();
    } 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\core\client\renderer\GeometryTessellator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */