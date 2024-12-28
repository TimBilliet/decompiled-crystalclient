package co.crystaldev.client.util;

import co.crystaldev.client.Reference;
import co.crystaldev.client.duck.EntityExt;
import co.crystaldev.client.feature.settings.ClientOptions;
import co.crystaldev.client.shader.ShaderManager;
import co.crystaldev.client.shader.chroma.ChromaScreenShader;
import co.crystaldev.client.shader.chroma.ChromaScreenTexturedShader;
import co.crystaldev.client.util.enums.ChatColor;
import co.crystaldev.client.util.objects.Vec3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class RenderUtils {
  private static final Tessellator tessellator = Tessellator.getInstance();

  private static final WorldRenderer worldRenderer = tessellator.getWorldRenderer();

  public static void setRenderManager(RenderManager renderManager) {
    RenderUtils.renderManager = renderManager;
  }

  private static RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();

  public static void setFontRenderer(FontRenderer fontRenderer) {
    RenderUtils.fontRenderer = fontRenderer;
  }

  private static FontRenderer fontRenderer = (Minecraft.getMinecraft()).fontRendererObj;

  private static int scrollX;

  private static int scrollY;

  private static boolean allowScrolling;

  public static void drawLines(float[] points, float lineWidth, int color) {
    if (points.length < 2 || points.length % 2 != 0) {
      Reference.LOGGER.warn("Attempting to call drawLines with less than 2 points.");
      return;
    }
    setGlColor(color);
    GL11.glLineWidth(lineWidth);
    worldRenderer.begin(1, DefaultVertexFormats.POSITION);
    for (int i = 0; i < points.length; i += 2)
      worldRenderer.pos(points[i], points[i + 1], 0.0D).endVertex();
    tessellator.draw();
    GL11.glLineWidth(1.0F);
    resetColor();
  }

  public static void drawTorus(int x, int y, int innerRadius, int outerRadius, int color) {
    GL11.glPushMatrix();
    GL11.glEnable(2848);
    setGlColor(color);
    float ratio = 0.017453292F;
    worldRenderer.begin(1, DefaultVertexFormats.POSITION);
    for (int i = 0; i <= 360; i++) {
      float radians = (i - 90) * ratio;
      worldRenderer.pos((x + (float)Math.cos(radians) * innerRadius), (y + (float)Math.sin(radians) * innerRadius), 0.0D).endVertex();
      worldRenderer.pos((x + (float)Math.cos(radians) * outerRadius), (y + (float)Math.sin(radians) * outerRadius), 0.0D).endVertex();
    }
    tessellator.draw();
    GL11.glPopMatrix();
  }

  public static void drawSemiCircle(double x, double y, double radius, double percent, int color) {
    float alpha = (color >> 24 & 0xFF) / 255.0F;
    float red = (color >> 16 & 0xFF) / 255.0F;
    float green = (color >> 8 & 0xFF) / 255.0F;
    float blue = (color & 0xFF) / 255.0F;
    boolean wasBlend = GL11.glGetBoolean(3042);
    boolean wasTex2d = GL11.glGetBoolean(3553);
    GL11.glPushMatrix();
    GL11.glLineWidth(1.0F);
    GL11.glEnable(3042);
    GL11.glDisable(3553);
    GL11.glBlendFunc(770, 771);
    GL11.glShadeModel(7425);
    GL11.glEnable(2848);
    for (int i = 0; i < 360.0D * percent; i++) {
      worldRenderer.begin(1, DefaultVertexFormats.POSITION_COLOR);
      worldRenderer.pos(x, y, 0.0D).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(x +
          Math.sin(Math.toRadians(i)) * radius, y +
          Math.cos(Math.toRadians(i)) * radius, 0.0D)

        .color(red, green, blue, alpha).endVertex();
      tessellator.draw();
    }
    if (!wasBlend)
      GL11.glDisable(3042);
    if (wasTex2d)
      GL11.glEnable(3553);
    GL11.glShadeModel(7424);
    GL11.glDisable(2848);
    GL11.glPopMatrix();
  }

  public static void drawCircle(double x, double y, double radius, int color) {
    float alpha = (color >> 24 & 0xFF) / 255.0F;
    float red = (color >> 16 & 0xFF) / 255.0F;
    float green = (color >> 8 & 0xFF) / 255.0F;
    float blue = (color & 0xFF) / 255.0F;
    boolean wasBlend = GL11.glGetBoolean(3042);
    boolean wasTex2d = GL11.glGetBoolean(3553);
    GL11.glPushMatrix();
    GL11.glEnable(3042);
    GL11.glDisable(3553);
    GL11.glBlendFunc(770, 771);
    GL11.glShadeModel(7425);
    GL11.glEnable(2848);
    for (int i = 0; i < 360; i++) {
      worldRenderer.begin(1, DefaultVertexFormats.POSITION_COLOR);
      worldRenderer.pos(x, y, 0.0D).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(x +
          Math.sin(Math.toRadians(i)) * radius, y +
          Math.cos(Math.toRadians(i)) * radius, 0.0D)

        .color(red, green, blue, alpha).endVertex();
      tessellator.draw();
    }
    if (!wasBlend)
      GL11.glDisable(3042);
    if (wasTex2d)
      GL11.glEnable(3553);
    GL11.glShadeModel(7424);
    GL11.glDisable(2848);
    GL11.glPopMatrix();
  }

  public static void drawCircle(float x, float y, float radius, int color) {
    GL11.glPushMatrix();
    boolean wasBlend = GL11.glGetBoolean(3042);
    boolean wasTex2d = GL11.glGetBoolean(3553);
    GL11.glEnable(3042);
    GL11.glDisable(3553);
    GL11.glBlendFunc(770, 771);
    GL11.glShadeModel(7425);
    GL11.glEnable(2832);
    GL11.glPointSize(radius);
    glColor(color);
    GL11.glBegin(0);
    GL11.glVertex2f(x, y);
    GL11.glEnd();
    if (!wasBlend)
      GL11.glDisable(3042);
    if (wasTex2d)
      GL11.glEnable(3553);
    GL11.glShadeModel(7424);
    GL11.glDisable(2832);
    GL11.glPopMatrix();
  }

  public static void drawCircle(double x, double y, double radius, ColorObject color) {
    GL11.glPushMatrix();
    boolean wasBlend = GL11.glGetBoolean(3042);
    boolean wasLineSmooth = GL11.glGetBoolean(2848);
    boolean wasTex2d = GL11.glGetBoolean(3553);
    GL11.glEnable(3042);
    GL11.glDisable(3553);
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GL11.glShadeModel(7425);
    GL11.glEnable(2848);
    glColor(color);
    if (color.isChroma())
      ShaderManager.getInstance().enableShader(ChromaScreenShader.class);
    for (int i = 0; i < 360; i++) {
      GL11.glBegin(1);
      GL11.glVertex2d(x, y);
      GL11.glVertex2d(x +
          Math.sin(Math.toRadians(i)) * radius, y +
          Math.cos(Math.toRadians(i)) * radius);
      GL11.glEnd();
    }
    if (!wasBlend)
      GL11.glDisable(3042);
    if (!wasLineSmooth)
      GL11.glDisable(2848);
    if (wasTex2d)
      GL11.glEnable(3553);
    GL11.glShadeModel(7424);
    if (color.isChroma())
      ShaderManager.getInstance().disableShader();
    GL11.glPopMatrix();
  }

  public static void drawColoredCircle(double x, double y, double radius) {
    boolean wasBlend = GL11.glGetBoolean(3042);
    boolean wasTex2d = GL11.glGetBoolean(3553);
    GL11.glPushMatrix();
    GL11.glEnable(3042);
    GL11.glDisable(3553);
    GL11.glBlendFunc(770, 771);
    GL11.glShadeModel(7425);
    GL11.glEnable(2848);
    for (int i = 0; i < 360; i++) {
      worldRenderer.begin(1, DefaultVertexFormats.POSITION_COLOR);
      worldRenderer.pos(x, y, 0.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      int colour = Color.HSBtoRGB(i / 360.0F, 1.0F, 1.0F);
      worldRenderer.pos(x +
          Math.sin(Math.toRadians(i)) * radius, y +
          Math.cos(Math.toRadians(i)) * radius, 0.0D)

        .color((colour & 0xFF0000) >> 16, (colour & 0xFF00) >> 8, colour & 0xFF, 255).endVertex();
      tessellator.draw();
    }
    GL11.glDisable(2848);
    GL11.glShadeModel(7424);
    if (wasTex2d)
      GL11.glEnable(3553);
    if (!wasBlend)
      GL11.glDisable(3042);
    GL11.glPopMatrix();
  }

  public static void drawTracer(Vec3d location, ColorObject colour, EntityPlayerSP player, float partialTicks) {
    Vec3 eyePos = player.getPositionEyes(partialTicks);
    Vec3d playerLocation = new Vec3d(eyePos.xCoord, eyePos.yCoord, eyePos.zCoord);
    if (renderManager.options.thirdPersonView == 0) {
      double pitch = (player.rotationPitch + 90.0D) * Math.PI / 180.0D;
      double yaw = (player.rotationYaw + 90.0D) * Math.PI / 180.0D;
      playerLocation.add(Math.sin(pitch) * Math.cos(yaw), Math.cos(pitch), Math.sin(pitch) * Math.sin(yaw));
    }
    location = normalize(location);
    playerLocation = normalize(playerLocation);
    boolean oldViewBobbing = (Minecraft.getMinecraft()).gameSettings.viewBobbing;
    boolean wasBlend = GL11.glGetBoolean(3042);
    boolean wasTex2d = GL11.glGetBoolean(3553);
    boolean wasLineSmooth = GL11.glGetBoolean(2848);
    (Minecraft.getMinecraft()).gameSettings.viewBobbing = false;
    GL11.glPushMatrix();
    GL11.glEnable(3042);
    GL11.glBlendFunc(770, 771);
    GL11.glDisable(3553);
    GL11.glEnable(2848);
    GL11.glLineWidth(2.5F);
    if (colour.isChroma())
      ShaderManager.getInstance().enableShader(ChromaScreenShader.class);
    setGlColor(colour, colour.getAlpha());
    drawLine(location, playerLocation);
    ShaderManager.getInstance().disableShader();
    if (!wasBlend)
      GL11.glDisable(3042);
    if (!wasLineSmooth)
      GL11.glDisable(2848);
    if (wasTex2d)
      GL11.glEnable(3553);
    GL11.glColor4d(1.0D, 1.0D, 1.0D, 1.0D);
    GL11.glPopMatrix();
    (Minecraft.getMinecraft()).gameSettings.viewBobbing = oldViewBobbing;
  }

  public static Color getCurrentChromaColor() {
    double step = (ClientOptions.getInstance()).chromaSpeed / 20.0D * 2.0D;
    double time = System.currentTimeMillis() % 18000.0D / step / 18000.0D / step;
    return Color.getHSBColor((float)time, 1.0F, 1.0F);
  }

  public static void setChromaColor() {
    setGlColor(getCurrentChromaColor());
  }

  public static void setGlColor(int color) {
    float red = (color >> 16 & 0xFF) / 255.0F;
    float green = (color >> 8 & 0xFF) / 255.0F;
    float blue = (color & 0xFF) / 255.0F;
    float alpha = (color >> 24 & 0xFF) / 255.0F;
    GL11.glColor4f(red, green, blue, alpha);
  }

  public static void setGlColor(int colour, int alpha) {
    setGlColor(new Color(colour), alpha);
  }

  public static void setGlColor(Color colour, int alpha) {
    GL11.glColor4d(colour.getRed() / 255.0D, colour.getGreen() / 255.0D, colour.getBlue() / 255.0D, alpha / 255.0D);
  }

  public static void setGlColor(Color colour) {
    GL11.glColor4d(colour.getRed() / 255.0D, colour.getGreen() / 255.0D, colour.getBlue() / 255.0D, colour.getAlpha() / 255.0D);
  }

  public static void resetColor() {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
  }

  public static void drawPolygon(int x, int y, int sideAmount, int radius, int color) {
    float alpha = (color >> 24 & 0xFF) / 255.0F;
    float red = (color >> 16 & 0xFF) / 255.0F;
    float green = (color >> 8 & 0xFF) / 255.0F;
    float blue = (color & 0xFF) / 255.0F;
    boolean wasBlend = GL11.glGetBoolean(3042);
    boolean wasTex2d = GL11.glGetBoolean(3553);
    GL11.glPushMatrix();
    GL11.glEnable(3042);
    GL11.glDisable(3553);
    GL11.glBlendFunc(770, 771);
    GL11.glShadeModel(7425);
    GL11.glTranslated(x, y, 0.0D);
    GL11.glRotatef(180.0F / sideAmount, 0.0F, 0.0F, 1.0F);
    GL11.glScaled(radius, radius, 1.0D);
    worldRenderer.begin(9, DefaultVertexFormats.POSITION_COLOR);
    for (int i = 0; i < sideAmount; i++) {
      double angle = (float)i / sideAmount * 2.0D * Math.PI;
      worldRenderer.pos(Math.sin(angle), Math.cos(angle), 0.0D)
        .color(red, green, blue, alpha)
        .endVertex();
    }
    tessellator.draw();
    GL11.glShadeModel(7424);
    if (!wasBlend)
      GL11.glDisable(3042);
    if (wasTex2d)
      GL11.glEnable(3553);
    GL11.glPopMatrix();
  }

  public static void drawPolygonOutline(int x, int y, int sideAmount, int radius, float lineWidth, int color) {
    float alpha = (color >> 24 & 0xFF) / 255.0F;
    float red = (color >> 16 & 0xFF) / 255.0F;
    float green = (color >> 8 & 0xFF) / 255.0F;
    float blue = (color & 0xFF) / 255.0F;
    boolean wasBlend = GL11.glGetBoolean(3042);
    boolean wasTex2d = GL11.glGetBoolean(3553);
    boolean wasLineSmooth = GL11.glGetBoolean(2848);
    GL11.glPushMatrix();
    GL11.glEnable(3042);
    GL11.glEnable(2848);
    GL11.glDisable(3553);
    GL11.glBlendFunc(770, 771);
    GL11.glShadeModel(7425);
    GL11.glTranslated(x, y, 0.0D);
    GL11.glRotatef(180.0F / sideAmount, 0.0F, 0.0F, 1.0F);
    GL11.glScaled(radius, radius, 1.0D);
    GL11.glLineWidth(lineWidth);
    worldRenderer.begin(2, DefaultVertexFormats.POSITION_COLOR);
    for (int i = 0; i < sideAmount; i++) {
      double angle = (float)i / sideAmount * 2.0D * Math.PI;
      worldRenderer.pos(Math.sin(angle), Math.cos(angle), 0.0D)
        .color(red, green, blue, alpha)
        .endVertex();
    }
    tessellator.draw();
    GL11.glShadeModel(7424);
    if (!wasBlend)
      GL11.glDisable(3042);
    if (!wasLineSmooth)
      GL11.glDisable(2848);
    if (wasTex2d)
      GL11.glEnable(3553);
    GL11.glPopMatrix();
  }

  public static void drawHorizontalGradientRect(int left, int top, int right, int bottom, long startColor, long endColor) {
    float f = (float)(startColor >> 24L & 0xFFL) / 255.0F;
    float f1 = (float)(startColor >> 16L & 0xFFL) / 255.0F;
    float f2 = (float)(startColor >> 8L & 0xFFL) / 255.0F;
    float f3 = (float)(startColor & 0xFFL) / 255.0F;
    float f4 = (float)(endColor >> 24L & 0xFFL) / 255.0F;
    float f5 = (float)(endColor >> 16L & 0xFFL) / 255.0F;
    float f6 = (float)(endColor >> 8L & 0xFFL) / 255.0F;
    float f7 = (float)(endColor & 0xFFL) / 255.0F;
    boolean wasBlend = GL11.glGetBoolean(3042);
    boolean wasTex2d = GL11.glGetBoolean(3553);
    boolean wasAlpha = GL11.glGetBoolean(3008);
    GL11.glPushMatrix();
    GL11.glDisable(3553);
    GL11.glEnable(3042);
    GL11.glDisable(3008);
    GL11.glBlendFunc(770, 771);
    GL11.glShadeModel(7425);
    worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
    worldRenderer.pos(left, top, 0.0D).color(f1, f2, f3, f).endVertex();
    worldRenderer.pos(left, bottom, 0.0D).color(f1, f2, f3, f).endVertex();
    worldRenderer.pos(right, bottom, 0.0D).color(f5, f6, f7, f4).endVertex();
    worldRenderer.pos(right, top, 0.0D).color(f5, f6, f7, f4).endVertex();
    tessellator.draw();
    GL11.glShadeModel(7424);
    if (wasAlpha)
      GL11.glEnable(3008);
    if (!wasBlend)
      GL11.glDisable(3042);
    if (wasTex2d)
      GL11.glEnable(3553);
    GL11.glPopMatrix();
  }

  public static void drawGradientRect(int left, int top, int right, int bottom, long startColor, long endColor) {
    float f = (float)(startColor >> 24L & 0xFFL) / 255.0F;
    float f1 = (float)(startColor >> 16L & 0xFFL) / 255.0F;
    float f2 = (float)(startColor >> 8L & 0xFFL) / 255.0F;
    float f3 = (float)(startColor & 0xFFL) / 255.0F;
    float f4 = (float)(endColor >> 24L & 0xFFL) / 255.0F;
    float f5 = (float)(endColor >> 16L & 0xFFL) / 255.0F;
    float f6 = (float)(endColor >> 8L & 0xFFL) / 255.0F;
    float f7 = (float)(endColor & 0xFFL) / 255.0F;
    boolean wasBlend = GL11.glGetBoolean(3042);
    boolean wasTex2d = GL11.glGetBoolean(3553);
    boolean wasAlpha = GL11.glGetBoolean(3008);
    GL11.glPushMatrix();
    GL11.glDisable(3553);
    GL11.glEnable(3042);
    GL11.glDisable(3008);
    GL11.glBlendFunc(770, 771);
    GL11.glShadeModel(7425);
    worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
    worldRenderer.pos(right, top, 0.0D).color(f1, f2, f3, f).endVertex();
    worldRenderer.pos(left, top, 0.0D).color(f1, f2, f3, f).endVertex();
    worldRenderer.pos(left, bottom, 0.0D).color(f5, f6, f7, f4).endVertex();
    worldRenderer.pos(right, bottom, 0.0D).color(f5, f6, f7, f4).endVertex();
    tessellator.draw();
    GL11.glShadeModel(7424);
    if (wasAlpha)
      GL11.glEnable(3008);
    if (!wasBlend)
      GL11.glDisable(3042);
    if (wasTex2d)
      GL11.glEnable(3553);
    GL11.glPopMatrix();
  }

  public static void drawRoundedBorder(double x, double y, double x1, double y1, double radius, float borderSize, int color) {
    float r = (color >> 16 & 0xFF) / 255.0F;
    float g = (color >> 8 & 0xFF) / 255.0F;
    float b = (color & 0xFF) / 255.0F;
    float a = (color >> 24 & 0xFF) / 255.0F;
    GL11.glPushMatrix();
    GL11.glScaled(0.5D, 0.5D, 0.5D);
    x *= 2.0D;
    y *= 2.0D;
    x1 *= 2.0D;
    y1 *= 2.0D;
    boolean wasTex2d = GL11.glGetBoolean(3553);
    boolean wasBlend = GL11.glGetBoolean(3042);
    boolean wasLineSmooth = GL11.glGetBoolean(2848);
    GlStateManager.disableCull();
    GL11.glDisable(3553);
    GL11.glEnable(3042);
    GL11.glEnable(2848);
    GL11.glBlendFunc(770, 771);
    GL11.glShadeModel(7425);
    GL11.glLineWidth(borderSize);
    GL11.glColor4f(r, g, b, a);
    worldRenderer.begin(2, DefaultVertexFormats.POSITION);
    int i;
    for (i = 0; i <= 90; i += 2)
      worldRenderer.pos(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y + radius +
          Math.cos(i * Math.PI / 180.0D) * radius * -1.0D, 0.0D)

        .endVertex();
    for (i = 90; i <= 180; i += 2)
      worldRenderer.pos(x + radius +
          Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y1 - radius +
          Math.cos(i * Math.PI / 180.0D) * radius * -1.0D, 0.0D)

        .endVertex();
    for (i = 0; i <= 90; i += 2)
      worldRenderer.pos(x1 - radius +
          Math.sin(i * Math.PI / 180.0D) * radius, y1 - radius +
          Math.cos(i * Math.PI / 180.0D) * radius, 0.0D)

        .endVertex();
    for (i = 90; i <= 180; i += 2)
      worldRenderer.pos(x1 - radius +
          Math.sin(i * Math.PI / 180.0D) * radius, y + radius +
          Math.cos(i * Math.PI / 180.0D) * radius, 0.0D)

        .endVertex();
    tessellator.draw();
    resetColor();
    GlStateManager.enableCull();
    GL11.glShadeModel(7424);
    if (!wasLineSmooth)
      GL11.glDisable(2848);
    if (!wasBlend)
      GL11.glDisable(3042);
    if (wasTex2d)
      GL11.glEnable(3553);
    GL11.glScaled(2.0D, 2.0D, 2.0D);
    GL11.glPopMatrix();
  }

  public static void drawRoundedRectWithBorder(double x, double y, double x1, double y1, double radius, float borderSize, int borderColor, int color, boolean topLeft, boolean topRight, boolean bottomLeft, boolean bottomRight) {
    drawRoundedRect(x, y, x1, y1, radius - 1.0D, color, topLeft, bottomLeft, topRight, bottomRight);
    float r = (borderColor >> 16 & 0xFF) / 255.0F;
    float g = (borderColor >> 8 & 0xFF) / 255.0F;
    float b = (borderColor & 0xFF) / 255.0F;
    float a = (borderColor >> 24 & 0xFF) / 255.0F;
    GL11.glPushMatrix();
    GL11.glScaled(0.5D, 0.5D, 0.5D);
    x *= 2.0D;
    y *= 2.0D;
    x1 *= 2.0D;
    y1 *= 2.0D;
    boolean wasTex2d = GL11.glGetBoolean(3553);
    boolean wasBlend = GL11.glGetBoolean(3042);
    boolean wasLineSmooth = GL11.glGetBoolean(2848);
    GlStateManager.disableCull();
    GL11.glDisable(3553);
    GL11.glEnable(3042);
    GL11.glEnable(2848);
    GL11.glBlendFunc(770, 771);
    GL11.glShadeModel(7425);
    GL11.glLineWidth(borderSize);
    GL11.glColor4f(r, g, b, a);
    worldRenderer.begin(2, DefaultVertexFormats.POSITION);
    if (topLeft) {
      for (int i = 0; i <= 90; i += 2)
        worldRenderer.pos(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y + radius +
            Math.cos(i * Math.PI / 180.0D) * radius * -1.0D, 0.0D)

          .endVertex();
    } else {
      worldRenderer.pos(x, y, 0.0D).endVertex();
    }
    if (bottomLeft) {
      for (int i = 90; i <= 180; i += 2)
        worldRenderer.pos(x + radius +
            Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y1 - radius +
            Math.cos(i * Math.PI / 180.0D) * radius * -1.0D, 0.0D)

          .endVertex();
    } else {
      worldRenderer.pos(x, y1, 0.0D).endVertex();
    }
    if (bottomRight) {
      for (int i = 0; i <= 90; i += 2)
        worldRenderer.pos(x1 - radius +
            Math.sin(i * Math.PI / 180.0D) * radius, y1 - radius +
            Math.cos(i * Math.PI / 180.0D) * radius, 0.0D)

          .endVertex();
    } else {
      worldRenderer.pos(x1, y1, 0.0D).endVertex();
    }
    if (topRight) {
      for (int i = 90; i <= 180; i += 2)
        worldRenderer.pos(x1 - radius +
            Math.sin(i * Math.PI / 180.0D) * radius, y + radius +
            Math.cos(i * Math.PI / 180.0D) * radius, 0.0D)

          .endVertex();
    } else {
      worldRenderer.pos(x1, y, 0.0D).endVertex();
    }
    tessellator.draw();
    resetColor();
    GlStateManager.enableCull();
    GL11.glShadeModel(7424);
    if (!wasLineSmooth)
      GL11.glDisable(2848);
    if (!wasBlend)
      GL11.glDisable(3042);
    if (wasTex2d)
      GL11.glEnable(3553);
    GL11.glScaled(2.0D, 2.0D, 2.0D);
    GL11.glPopMatrix();
  }

  public static void drawRoundedRectWithGradientBorder(double x, double y, double x1, double y1, double radius, float borderSize, int borderColor, int borderColor1, int color, boolean topLeft, boolean topRight, boolean bottomLeft, boolean bottomRight) {
    drawRoundedRect(x, y, x1, y1, radius - 1.0D, color, topLeft, bottomLeft, topRight, bottomRight);
    float r = (borderColor >> 16 & 0xFF) / 255.0F;
    float g = (borderColor >> 8 & 0xFF) / 255.0F;
    float b = (borderColor & 0xFF) / 255.0F;
    float a = (borderColor >> 24 & 0xFF) / 255.0F;
    float r1 = (borderColor1 >> 16 & 0xFF) / 255.0F;
    float g1 = (borderColor1 >> 8 & 0xFF) / 255.0F;
    float b1 = (borderColor1 & 0xFF) / 255.0F;
    float a1 = (borderColor1 >> 24 & 0xFF) / 255.0F;
    GL11.glPushMatrix();
    GL11.glScaled(0.5D, 0.5D, 0.5D);
    x *= 2.0D;
    y *= 2.0D;
    x1 *= 2.0D;
    y1 *= 2.0D;
    boolean wasTex2d = GL11.glGetBoolean(3553);
    boolean wasBlend = GL11.glGetBoolean(3042);
    boolean wasLineSmooth = GL11.glGetBoolean(2848);
    GlStateManager.disableCull();
    GL11.glDisable(3553);
    GL11.glEnable(3042);
    GL11.glEnable(2848);
    GL11.glBlendFunc(770, 771);
    GL11.glShadeModel(7425);
    GL11.glLineWidth(borderSize);
    worldRenderer.begin(2, DefaultVertexFormats.POSITION_COLOR);
    if (topLeft) {
      for (int i = 0; i <= 90; i += 2)
        worldRenderer.pos(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y + radius +
            Math.cos(i * Math.PI / 180.0D) * radius * -1.0D, 0.0D)

          .color(r, g, b, a).endVertex();
    } else {
      worldRenderer.pos(x, y, 0.0D).color(r, g, b, a).endVertex();
    }
    if (bottomLeft) {
      for (int i = 90; i <= 180; i += 2)
        worldRenderer.pos(x + radius +
            Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y1 - radius +
            Math.cos(i * Math.PI / 180.0D) * radius * -1.0D, 0.0D)

          .color(r, g, b, a).endVertex();
    } else {
      worldRenderer.pos(x, y1, 0.0D).color(r, g, b, a).endVertex();
    }
    if (bottomRight) {
      for (int i = 0; i <= 90; i += 2)
        worldRenderer.pos(x1 - radius +
            Math.sin(i * Math.PI / 180.0D) * radius, y1 - radius +
            Math.cos(i * Math.PI / 180.0D) * radius, 0.0D)

          .color(r1, g1, b1, a1).endVertex();
    } else {
      worldRenderer.pos(x1, y1, 0.0D).color(r1, g1, b1, a1).endVertex();
    }
    if (topRight) {
      for (int i = 90; i <= 180; i += 2)
        worldRenderer.pos(x1 - radius +
            Math.sin(i * Math.PI / 180.0D) * radius, y + radius +
            Math.cos(i * Math.PI / 180.0D) * radius, 0.0D)

          .color(r1, g1, b1, a1).endVertex();
    } else {
      worldRenderer.pos(x1, y, 0.0D).color(r1, g1, b1, a1).endVertex();
    }
    tessellator.draw();
    resetColor();
    GlStateManager.enableCull();
    GL11.glShadeModel(7424);
    if (!wasLineSmooth)
      GL11.glDisable(2848);
    if (!wasBlend)
      GL11.glDisable(3042);
    if (wasTex2d)
      GL11.glEnable(3553);
    GL11.glScaled(2.0D, 2.0D, 2.0D);
    GL11.glPopMatrix();
  }

  public static void drawRoundedRectWithBorder(double x, double y, double x1, double y1, double radius, float borderSize, long borderColor, int color) {
    drawRoundedRect(x, y, x1, y1, radius - 1.0D, color);
    float r = (borderColor >> 16 & 0xFF) / 255.0F;
    float g = (borderColor >> 8 & 0xFF) / 255.0F;
    float b = (borderColor & 0xFF) / 255.0F;
    float a = (borderColor >> 24 & 0xFF) / 255.0F;
    GL11.glPushMatrix();
    GL11.glScaled(0.5D, 0.5D, 0.5D);
    x *= 2.0D;
    y *= 2.0D;
    x1 *= 2.0D;
    y1 *= 2.0D;
    boolean wasTex2d = GL11.glGetBoolean(3553);
    boolean wasBlend = GL11.glGetBoolean(3042);
    boolean wasLineSmooth = GL11.glGetBoolean(2848);
    GlStateManager.disableCull();
    GL11.glDisable(3553);
    GL11.glEnable(3042);
    GL11.glEnable(2848);
    GL11.glBlendFunc(770, 771);
    GL11.glShadeModel(7425);
    GL11.glLineWidth(borderSize);
    GL11.glColor4f(r, g, b, a);
    worldRenderer.begin(2, DefaultVertexFormats.POSITION);
    int i;
    for (i = 0; i <= 90; i += 2)
      worldRenderer.pos(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y + radius +
          Math.cos(i * Math.PI / 180.0D) * radius * -1.0D, 0.0D)

        .endVertex();
    for (i = 90; i <= 180; i += 2)
      worldRenderer.pos(x + radius +
          Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y1 - radius +
          Math.cos(i * Math.PI / 180.0D) * radius * -1.0D, 0.0D)

        .endVertex();
    for (i = 0; i <= 90; i += 2)
      worldRenderer.pos(x1 - radius +
          Math.sin(i * Math.PI / 180.0D) * radius, y1 - radius +
          Math.cos(i * Math.PI / 180.0D) * radius, 0.0D)

        .endVertex();
    for (i = 90; i <= 180; i += 2)
      worldRenderer.pos(x1 - radius +
          Math.sin(i * Math.PI / 180.0D) * radius, y + radius +
          Math.cos(i * Math.PI / 180.0D) * radius, 0.0D)

        .endVertex();
    tessellator.draw();
    resetColor();
    GlStateManager.enableCull();
    GL11.glShadeModel(7424);
    if (!wasLineSmooth)
      GL11.glDisable(2848);
    if (!wasBlend)
      GL11.glDisable(3042);
    if (wasTex2d)
      GL11.glEnable(3553);
    GL11.glScaled(2.0D, 2.0D, 2.0D);
    GL11.glPopMatrix();
  }

  public static void drawRoundedRectWithGradientBorder(double x, double y, double x1, double y1, double radius, float borderSize, int borderColor, int borderColor1, int color) {
    drawRoundedRect(x, y, x1, y1, radius - 1.0D, color);
    float a = (borderColor >> 24 & 0xFF) / 255.0F;
    float r = (borderColor >> 16 & 0xFF) / 255.0F;
    float g = (borderColor >> 8 & 0xFF) / 255.0F;
    float b = (borderColor & 0xFF) / 255.0F;
    float a1 = (borderColor1 >> 24 & 0xFF) / 255.0F;
    float r1 = (borderColor1 >> 16 & 0xFF) / 255.0F;
    float g1 = (borderColor1 >> 8 & 0xFF) / 255.0F;
    float b1 = (borderColor1 & 0xFF) / 255.0F;
    GL11.glPushMatrix();
    GL11.glScaled(0.5D, 0.5D, 0.5D);
    x *= 2.0D;
    y *= 2.0D;
    x1 *= 2.0D;
    y1 *= 2.0D;
    boolean wasTex2d = GL11.glGetBoolean(3553);
    boolean wasBlend = GL11.glGetBoolean(3042);
    boolean wasLineSmooth = GL11.glGetBoolean(2848);
    GlStateManager.disableCull();
    GL11.glDisable(3553);
    GL11.glEnable(3042);
    GL11.glEnable(2848);
    GL11.glBlendFunc(770, 771);
    GL11.glShadeModel(7425);
    GL11.glLineWidth(borderSize);
    worldRenderer.begin(2, DefaultVertexFormats.POSITION_COLOR);
    int i;
    for (i = 0; i <= 90; i += 2)
      worldRenderer.pos(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y + radius +
          Math.cos(i * Math.PI / 180.0D) * radius * -1.0D, 0.0D)

        .color(r, g, b, a).endVertex();
    for (i = 90; i <= 180; i += 2)
      worldRenderer.pos(x + radius +
          Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y1 - radius +
          Math.cos(i * Math.PI / 180.0D) * radius * -1.0D, 0.0D)

        .color(r, g, b, a).endVertex();
    for (i = 0; i <= 90; i += 2)
      worldRenderer.pos(x1 - radius +
          Math.sin(i * Math.PI / 180.0D) * radius, y1 - radius +
          Math.cos(i * Math.PI / 180.0D) * radius, 0.0D)

        .color(r1, g1, b1, a1).endVertex();
    for (i = 90; i <= 180; i += 2)
      worldRenderer.pos(x1 - radius +
          Math.sin(i * Math.PI / 180.0D) * radius, y + radius +
          Math.cos(i * Math.PI / 180.0D) * radius, 0.0D)

        .color(r1, g1, b1, a1).endVertex();
    tessellator.draw();
    GlStateManager.enableCull();
    GL11.glShadeModel(7424);
    if (!wasLineSmooth)
      GL11.glDisable(2848);
    if (!wasBlend)
      GL11.glDisable(3042);
    if (wasTex2d)
      GL11.glEnable(3553);
    GL11.glScaled(2.0D, 2.0D, 2.0D);
    GL11.glPopMatrix();
  }

  public static void drawRoundedRect(double x, double y, double x1, double y1, double radius, int color) {
    float f = (color >> 24 & 0xFF) / 255.0F;
    float f2 = (color >> 16 & 0xFF) / 255.0F;
    float f3 = (color >> 8 & 0xFF) / 255.0F;
    float f4 = (color & 0xFF) / 255.0F;
    GL11.glPushMatrix();
    boolean blend = GL11.glGetBoolean(3042);
    boolean lineSmooth = GL11.glGetBoolean(2848);
    boolean tex2d = GL11.glGetBoolean(3553);
    GL11.glScaled(0.5D, 0.5D, 0.5D);
    x *= 2.0D;
    y *= 2.0D;
    x1 *= 2.0D;
    y1 *= 2.0D;
    GL11.glEnable(3042);
    GL11.glEnable(2848);
    GL11.glDisable(3553);
    worldRenderer.begin(9, DefaultVertexFormats.POSITION_COLOR);
    int i;
    for (i = 0; i <= 90; i += 3)
      worldRenderer.pos(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y + radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D, 0.0D).color(f2, f3, f4, f).endVertex();
    for (i = 90; i <= 180; i += 3)
      worldRenderer.pos(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y1 - radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D, 0.0D).color(f2, f3, f4, f).endVertex();
    for (i = 0; i <= 90; i += 3)
      worldRenderer.pos(x1 - radius + Math.sin(i * Math.PI / 180.0D) * radius, y1 - radius + Math.cos(i * Math.PI / 180.0D) * radius, 0.0D).color(f2, f3, f4, f).endVertex();
    for (i = 90; i <= 180; i += 3)
      worldRenderer.pos(x1 - radius + Math.sin(i * Math.PI / 180.0D) * radius, y + radius + Math.cos(i * Math.PI / 180.0D) * radius, 0.0D).color(f2, f3, f4, f).endVertex();
    tessellator.draw();
    if (!blend)
      GL11.glDisable(3042);
    if (!lineSmooth)
      GL11.glDisable(2848);
    if (tex2d)
      GL11.glEnable(3553);
    GL11.glPopMatrix();
  }

  public static void drawRoundedHorizontalGradientRect(double x, double y, double x1, double y1, double radius, int color, int color1) {
    float a = (color >> 24 & 0xFF) / 255.0F;
    float r = (color >> 16 & 0xFF) / 255.0F;
    float g = (color >> 8 & 0xFF) / 255.0F;
    float b = (color & 0xFF) / 255.0F;
    float a1 = (color1 >> 24 & 0xFF) / 255.0F;
    float r1 = (color1 >> 16 & 0xFF) / 255.0F;
    float g1 = (color1 >> 8 & 0xFF) / 255.0F;
    float b1 = (color1 & 0xFF) / 255.0F;
    GL11.glPushMatrix();
    GL11.glScaled(0.5D, 0.5D, 0.5D);
    x *= 2.0D;
    y *= 2.0D;
    x1 *= 2.0D;
    y1 *= 2.0D;
    boolean wasTex2d = GL11.glGetBoolean(3553);
    boolean wasBlend = GL11.glGetBoolean(3042);
    boolean wasLineSmooth = GL11.glGetBoolean(2848);
    GL11.glEnable(3042);
    GL11.glEnable(2848);
    GL11.glDisable(3553);
    GL11.glShadeModel(7425);
    worldRenderer.begin(9, DefaultVertexFormats.POSITION_COLOR);
    int i;
    for (i = 0; i <= 90; i += 3)
      worldRenderer.pos(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y + radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D, 0.0D)
        .color(r, g, b, a).endVertex();
    for (i = 90; i <= 180; i += 3)
      worldRenderer.pos(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y1 - radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D, 0.0D)
        .color(r, g, b, a).endVertex();
    for (i = 0; i <= 90; i += 3)
      worldRenderer.pos(x1 - radius + Math.sin(i * Math.PI / 180.0D) * radius, y1 - radius + Math.cos(i * Math.PI / 180.0D) * radius, 0.0D)
        .color(r1, g1, b1, a1).endVertex();
    for (i = 90; i <= 180; i += 3)
      worldRenderer.pos(x1 - radius + Math.sin(i * Math.PI / 180.0D) * radius, y + radius + Math.cos(i * Math.PI / 180.0D) * radius, 0.0D)
        .color(r1, g1, b1, a1).endVertex();
    tessellator.draw();
    if (wasTex2d)
      GL11.glEnable(3553);
    if (!wasLineSmooth)
      GL11.glDisable(2848);
    if (!wasBlend)
      GL11.glDisable(3042);
    GL11.glShadeModel(7424);
    GL11.glPopMatrix();
  }

  public static void drawRoundedRect(double x, double y, double x1, double y1, double radius, int color, boolean topLeft, boolean bottomLeft, boolean topRight, boolean bottomRight) {
    float f = (color >> 24 & 0xFF) / 255.0F;
    float f2 = (color >> 16 & 0xFF) / 255.0F;
    float f3 = (color >> 8 & 0xFF) / 255.0F;
    float f4 = (color & 0xFF) / 255.0F;
    GL11.glPushMatrix();
    GL11.glScaled(0.5D, 0.5D, 0.5D);
    x *= 2.0D;
    y *= 2.0D;
    x1 *= 2.0D;
    y1 *= 2.0D;
    boolean wasTex2d = GL11.glGetBoolean(3553);
    boolean wasBlend = GL11.glGetBoolean(3042);
    boolean wasLineSmooth = GL11.glGetBoolean(2848);
    GL11.glEnable(3042);
    GL11.glEnable(2848);
    GL11.glDisable(3553);
    worldRenderer.begin(9, DefaultVertexFormats.POSITION_COLOR);
    if (topLeft) {
      for (int i = 0; i <= 90; i += 3)
        worldRenderer.pos(x + radius +
            Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y + radius +
            Math.cos(i * Math.PI / 180.0D) * radius * -1.0D, 0.0D)
          .color(f2, f3, f4, f).endVertex();
    } else {
      worldRenderer.pos(x, y, 0.0D).color(f2, f3, f4, f).endVertex();
    }
    if (bottomLeft) {
      for (int i = 90; i <= 180; i += 3)
        worldRenderer.pos(x + radius +
            Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y1 - radius +
            Math.cos(i * Math.PI / 180.0D) * radius * -1.0D, 0.0D)
          .color(f2, f3, f4, f).endVertex();
    } else {
      worldRenderer.pos(x, y1, 0.0D).color(f2, f3, f4, f).endVertex();
    }
    if (bottomRight) {
      for (int i = 0; i <= 90; i += 3)
        worldRenderer.pos(x1 - radius +
            Math.sin(i * Math.PI / 180.0D) * radius, y1 - radius +
            Math.cos(i * Math.PI / 180.0D) * radius, 0.0D)
          .color(f2, f3, f4, f).endVertex();
    } else {
      worldRenderer.pos(x1, y1, 0.0D).color(f2, f3, f4, f).endVertex();
    }
    if (topRight) {
      for (int i = 90; i <= 180; i += 3)
        worldRenderer.pos(x1 - radius +
            Math.sin(i * Math.PI / 180.0D) * radius, y + radius +
            Math.cos(i * Math.PI / 180.0D) * radius, 0.0D)
          .color(f2, f3, f4, f).endVertex();
    } else {
      worldRenderer.pos(x1, y, 0.0D).color(f2, f3, f4, f).endVertex();
    }
    tessellator.draw();
    if (wasTex2d)
      GL11.glEnable(3553);
    if (!wasLineSmooth)
      GL11.glDisable(2848);
    if (!wasBlend)
      GL11.glDisable(3042);
    GL11.glPopMatrix();
  }

  public static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height) {
    drawTexturedModalRect(x, y, u, v, width, height, 0);
  }

  public static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height, int z) {
    float f = 0.00390625F;
    float f1 = 0.00390625F;
    worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
    worldRenderer.pos(x, (y + height), z).tex((u * f), ((v + height) * f1)).endVertex();
    worldRenderer.pos((x + width), (y + height), z).tex(((u + width) * f), ((v + height) * f1)).endVertex();
    worldRenderer.pos((x + width), y, z).tex(((u + width) * f), (v * f1)).endVertex();
    worldRenderer.pos(x, y, z).tex((u * f), (v * f1)).endVertex();
    tessellator.draw();
  }

  public static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight) {
    float f = 1.0F / tileWidth;
    float f1 = 1.0F / tileHeight;
    worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
    worldRenderer.pos(x, (y + height), 0.0D).tex((u * f), ((v + vHeight) * f1)).endVertex();
    worldRenderer.pos((x + width), (y + height), 0.0D).tex(((u + uWidth) * f), ((v + vHeight) * f1)).endVertex();
    worldRenderer.pos((x + width), y, 0.0D).tex(((u + uWidth) * f), (v * f1)).endVertex();
    worldRenderer.pos(x, y, 0.0D).tex((u * f), (v * f1)).endVertex();
    tessellator.draw();
  }

  public static void drawScaledCustomSizeModalRect(double x, double y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight) {
    float f = 1.0F / tileWidth;
    float f1 = 1.0F / tileHeight;
    worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
    worldRenderer.pos(x, y + height, 0.0D).tex((u * f), ((v + vHeight) * f1)).endVertex();//tex = func_181673_a
    worldRenderer.pos(x + width, y + height, 0.0D).tex(((u + uWidth) * f), ((v + vHeight) * f1)).endVertex();
    worldRenderer.pos(x + width, y, 0.0D).tex(((u + uWidth) * f), (v * f1)).endVertex();
    worldRenderer.pos(x, y, 0.0D).tex((u * f), (v * f1)).endVertex();
    tessellator.draw();
  }

  public static void drawModalRectWithCustomSizedTexture(int x, int y, float u, float v, int width, int height, float textureWidth, float textureHeight) {
    float f = 1.0F / textureWidth;
    float f1 = 1.0F / textureHeight;
    worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
    worldRenderer.pos(x, (y + height), 0.0D).tex((u * f), ((v + height) * f1)).endVertex();
    worldRenderer.pos((x + width), (y + height), 0.0D).tex(((u + width) * f), ((v + height) * f1)).endVertex();
    worldRenderer.pos((x + width), y, 0.0D).tex(((u + width) * f), (v * f1)).endVertex();
    worldRenderer.pos(x, y, 0.0D).tex((u * f), (v * f1)).endVertex();
    tessellator.draw();
  }

  public static void drawCustomSizedResource(ResourceLocation resourceLocation, int xLoc, int yLoc, int width, int height) {
    boolean wasBlend = GL11.glGetBoolean(3042);
    GL11.glEnable(3042);
    ClientTextureManager.getInstance().bindTextureMipmapped(resourceLocation);
    drawScaledCustomSizeModalRect(xLoc, yLoc, 0.0F, 0.0F, width, height, width, height, width, height);
    if (!wasBlend)
      GL11.glDisable(3042);
  }

  public static void drawCustomSizedResource(ResourceLocation resourceLocation, double xLoc, double yLoc, int width, int height) {
    boolean wasBlend = GL11.glGetBoolean(3042);
    GL11.glEnable(3042);
    ClientTextureManager.getInstance().bindTextureMipmapped(resourceLocation);
    drawScaledCustomSizeModalRect(xLoc, yLoc, 0.0F, 0.0F, width, height, width, height, width, height);
    if (wasBlend)
      GL11.glDisable(3042);
  }

  public static void drawRect(float minX, float minY, float maxX, float maxY, ColorObject color) {
    GL11.glPushMatrix();
    if (minX < maxX) {
      float bounds = minX;
      minX = maxX;
      maxX = bounds;
    }
    if (minY < maxY) {
      float bounds = minY;
      minY = maxY;
      maxY = bounds;
    }
    float a = color.getAlpha() / 255.0F;
    float r = color.getRed() / 255.0F;
    float g = color.getGreen() / 255.0F;
    float b = color.getBlue() / 255.0F;
    boolean wasTex2d = GL11.glGetBoolean(3553);
    boolean wasBlend = GL11.glGetBoolean(3042);
    GL11.glEnable(3042);
    GL11.glDisable(3553);
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GL11.glColor4f(r, g, b, a);
    if (color.isChroma())
      ShaderManager.getInstance().enableShader(ChromaScreenShader.class);
    worldRenderer.begin(7, DefaultVertexFormats.POSITION);
    worldRenderer.pos(minX, maxY, 0.0D).endVertex();
    worldRenderer.pos(maxX, maxY, 0.0D).endVertex();
    worldRenderer.pos(maxX, minY, 0.0D).endVertex();
    worldRenderer.pos(minX, minY, 0.0D).endVertex();
    tessellator.draw();
    if (wasTex2d)
      GL11.glEnable(3553);
    if (!wasBlend)
      GL11.glDisable(3042);
    if (color.isChroma())
      ShaderManager.getInstance().disableShader();
    GL11.glPopMatrix();
  }

  public static void drawRect(float minX, float minY, float maxX, float maxY, int color) {
    GL11.glPushMatrix();
    if (minX < maxX) {
      float bounds = minX;
      minX = maxX;
      maxX = bounds;
    }
    if (minY < maxY) {
      float bounds = minY;
      minY = maxY;
      maxY = bounds;
    }
    float r = (color >> 24 & 0xFF) / 255.0F;
    float g = (color >> 16 & 0xFF) / 255.0F;
    float b = (color >> 8 & 0xFF) / 255.0F;
    float a = (color & 0xFF) / 255.0F;
    boolean wasTex2d = GL11.glGetBoolean(3553);
    boolean wasBlend = GL11.glGetBoolean(3042);
    GL11.glEnable(3042);
    GL11.glDisable(3553);
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GL11.glColor4f(g, b, a, r);
    worldRenderer.begin(7, DefaultVertexFormats.POSITION);
    worldRenderer.pos(minX, maxY, 0.0D).endVertex();
    worldRenderer.pos(maxX, maxY, 0.0D).endVertex();
    worldRenderer.pos(maxX, minY, 0.0D).endVertex();
    worldRenderer.pos(minX, minY, 0.0D).endVertex();
    tessellator.draw();
    if (wasTex2d)
      GL11.glEnable(3553);
    if (!wasBlend)
      GL11.glDisable(3042);
    GL11.glPopMatrix();
  }

  public static void drawBorderedRect(float x1, float y1, float x2, float y2, float border, ColorObject bColor, ColorObject color) {
    drawFastRect(x1, y1, x2, y2, color);
    if (bColor.isChroma())
      ShaderManager.getInstance().enableShader(ChromaScreenShader.class);
    GlStateManager.enableBlend();
    GlStateManager.disableTexture2D();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    glColor(bColor.getRGB());
    border = MathHelper.floor_float(border);
    x1 -= border;
    x2 += border;
    drawFastRect(x1, y1, x2, y1 + border);
    drawFastRect(x1, y2 - border, x2, y2);
    drawFastRect(x1, y1 + border, x1 + border, y2 - border);
    drawFastRect(x2 - border, y1 + border, x2, y2 - border);
    GlStateManager.resetColor();
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
    ShaderManager.getInstance().disableShader();
  }

  public static void drawBorderedRect(float x1, float y1, float x2, float y2, float border, Color bColor, Color color) {
    drawFastRect(x1, y1, x2, y2, color.getRGB());
    GlStateManager.enableBlend();
    GlStateManager.disableTexture2D();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    glColor(bColor.getRGB());
    border = MathHelper.floor_float(border);
    x1 -= border;
    x2 += border;
    drawFastRect(x1, y1, x2, y1 + border);
    drawFastRect(x1, y2 - border, x2, y2);
    drawFastRect(x1, y1 + border, x1 + border, y2 - border);
    drawFastRect(x2 - border, y1 + border, x2, y2 - border);
    GlStateManager.resetColor();
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
  }

  public static void drawBorderedRect(float x1, float y1, float x2, float y2, float border, int bColor, int color) {
    drawFastRect(x1, y1, x2, y2, color);
    GlStateManager.enableBlend();
    GlStateManager.disableTexture2D();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    glColor(bColor);
    border = MathHelper.floor_float(border);
    x1 -= border;
    x2 += border;
    drawFastRect(x1, y1, x2, y1 + border);
    drawFastRect(x1, y2 - border, x2, y2);
    drawFastRect(x1, y1 + border, x1 + border, y2 - border);
    drawFastRect(x2 - border, y1 + border, x2, y2 - border);
    GlStateManager.resetColor();
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
  }

  public static void drawFastRect(float x, float y, float x1, float y1, int color) {
    boolean wasTex2d = GL11.glGetBoolean(3553);
    boolean wasBlend = GL11.glGetBoolean(3042);
    GL11.glEnable(3042);
    GL11.glDisable(3553);
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    glColor(color);
    drawFastRect(x, y, x1, y1);
    if (wasTex2d)
      GL11.glEnable(3553);
    if (!wasBlend)
      GL11.glDisable(3042);
  }

  public static void drawFastRect(float x, float y, float x1, float y1, Color color) {
    boolean wasTex2d = GL11.glGetBoolean(3553);
    boolean wasBlend = GL11.glGetBoolean(3042);
    GL11.glEnable(3042);
    GL11.glDisable(3553);
    GL11.glBlendFunc(770, 771);
    glColor(color);
    drawFastRect(x, y, x1, y1);
    if (wasTex2d)
      GL11.glEnable(3553);
    if (!wasBlend)
      GL11.glDisable(3042);
  }

  public static void drawFastRect(float x, float y, float x1, float y1) {
    if (x < x1) {
      float i = x;
      x = x1;
      x1 = i;
    }
    if (y < y1) {
      float j = y;
      y = y1;
      y1 = j;
    }
    GL11.glBegin(7);
    GL11.glVertex2f(x, y1);
    GL11.glVertex2f(x1, y1);
    GL11.glVertex2f(x1, y);
    GL11.glVertex2f(x, y);
    GL11.glEnd();
  }

  public static void drawFastRect(double x, double y, double x1, double y1) {
    if (x < x1) {
      double i = x;
      x = x1;
      x1 = i;
    }
    if (y < y1) {
      double j = y;
      y = y1;
      y1 = j;
    }
    GL11.glBegin(7);
    GL11.glVertex2d(x, y1);
    GL11.glVertex2d(x1, y1);
    GL11.glVertex2d(x1, y);
    GL11.glVertex2d(x, y);
    GL11.glEnd();
  }

  public static void glColor(Color color) {
    GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
  }

  public static void glColor(int hex) {
    float alpha = (hex >> 24 & 0xFF) / 255.0F;
    float red = (hex >> 16 & 0xFF) / 255.0F;
    float green = (hex >> 8 & 0xFF) / 255.0F;
    float blue = (hex & 0xFF) / 255.0F;
    GL11.glColor4f(red, green, blue, alpha);
  }

  public static void drawCenteredString(String text, int x, int y, int color) {
    fontRenderer.drawStringWithShadow(text, (x - fontRenderer.getStringWidth(text) / 2.0F), y - fontRenderer.FONT_HEIGHT / 2.0F, color);
  }

  public static void drawCenteredString(String text, float x, float y, int color) {
    fontRenderer.drawStringWithShadow(text, x - fontRenderer.getStringWidth(text) / 2.0F, y, color);
  }

  public static int drawString(String text, int x, int y, int color) {
    return fontRenderer.drawStringWithShadow(text, x, y, color);
  }

  public static int drawString(String text, float x, float y, int color) {
    return fontRenderer.drawStringWithShadow(text, x, y, color);
  }

  public static int drawCenteredString(String text, int x, int y, ColorObject color) {
    if (color.isChroma())
      ShaderManager.getInstance().enableShader(ChromaScreenTexturedShader.class);
    text = format(text, color);
    int w = fontRenderer.drawStringWithShadow(text, (x - fontRenderer.getStringWidth(text) / 2.0F), y - fontRenderer.FONT_HEIGHT / 2.0F, color.getRGB());
    if (color.isChroma())
      ShaderManager.getInstance().disableShader();
    return w;
  }

  public static int drawCenteredString(String text, float x, float y, ColorObject color) {
    if (color.isChroma())
      ShaderManager.getInstance().enableShader(ChromaScreenTexturedShader.class);
    text = format(text, color);
    int r = fontRenderer.drawStringWithShadow(text, x - fontRenderer.getStringWidth(text) / 2.0F, y, color.getRGB());
    if (color.isChroma())
      ShaderManager.getInstance().disableShader();
    return r;
  }

  public static int drawString(String text, int x, int y, ColorObject color) {
    if (color.isChroma())
      ShaderManager.getInstance().enableShader(ChromaScreenTexturedShader.class);
    text = format(text, color);
    int r = fontRenderer.drawStringWithShadow(text, x, y, color.getRGB());
    if (color.isChroma())
      ShaderManager.getInstance().disableShader();
    return r;
  }

  public static int drawString(String text, int x, int y, ColorObject color, boolean dropShadow) {
    if (color.isChroma())
      ShaderManager.getInstance().enableShader(ChromaScreenTexturedShader.class);
    text = format(text, color);
    int r = fontRenderer.drawString(text, x, y, color.getRGB(), dropShadow);
    if (color.isChroma())
      ShaderManager.getInstance().disableShader();
    return r;
  }

  public static int drawRightAlignedString(String text, int x, int y, ColorObject color) {
    if (color.isChroma())
      ShaderManager.getInstance().enableShader(ChromaScreenTexturedShader.class);
    text = format(text, color);
    int r = fontRenderer.drawStringWithShadow(text, (x - fontRenderer.getStringWidth(text)), y, color.getRGB());
    if (color.isChroma())
      ShaderManager.getInstance().disableShader();
    return r;
  }

  public static int drawRightAlignedString(String text, int x, int y, ColorObject color, boolean dropShadow) {
    if (color.isChroma())
      ShaderManager.getInstance().enableShader(ChromaScreenTexturedShader.class);
    text = format(text, color);
    int r = fontRenderer.drawString(text, (x - fontRenderer.getStringWidth(text)), y, color.getRGB(), dropShadow);
    if (color.isChroma())
      ShaderManager.getInstance().disableShader();
    return r;
  }

  public static String format(String text, ColorObject color) {
    String b = color.isBold() ? "&l" : "";
    String i = color.isItalic() ? "&o" : "";
    String u = color.isUnderline() ? "&n" : "";
    return ChatColor.translate(b + i + u + text);
  }

  public static AxisAlignedBB normalize(AxisAlignedBB boundingBoxIn) {
    return new AxisAlignedBB(boundingBoxIn.minX - renderManager.viewerPosX, boundingBoxIn.minY - renderManager.viewerPosY, boundingBoxIn.minZ - renderManager.viewerPosZ, boundingBoxIn.maxX - renderManager.viewerPosX, boundingBoxIn.maxY - renderManager.viewerPosY, boundingBoxIn.maxZ - renderManager.viewerPosZ);
  }

  public static Vec3d normalize(Vec3d vecIn) {
    return new Vec3d(vecIn.x - renderManager.viewerPosX, vecIn.y - renderManager.viewerPosY, vecIn.z - renderManager.viewerPosZ);
  }

  public static Vec3d normalize(BlockPos posIn) {
    return new Vec3d(posIn
        .getX() + 0.5D - renderManager.viewerPosX, posIn
        .getY() + 0.5D - renderManager.viewerPosY, posIn
        .getZ() + 0.5D - renderManager.viewerPosZ);
  }

  public static AxisAlignedBB posToAABB(BlockPos pos) {
    return new AxisAlignedBB(pos
        .getX(), pos
        .getY(), pos
        .getZ(), (pos
        .getX() + 1), (pos
        .getY() + 1), (pos
        .getZ() + 1));
  }

  public static void glScissor(int x, int y, int width, int height, int scale) {
    int x1 = x + width;
    int y1 = y + height;
    GL11.glScissor(x * scale,

        (Minecraft.getMinecraft()).displayHeight - y1 * scale, (x1 - x) * scale, (y1 - y) * scale);
    GL11.glEnable(3089);
  }

  public static void glScissor(int x, int y, int width, int height) {
    glScissor(x, y, width, height, 1);
  }

  public static void drawTextureRect(float x, float y, float width, float height, float u, float v, float t, float s) {
    worldRenderer.begin(4, DefaultVertexFormats.POSITION_TEX);
    worldRenderer.pos((x + width), y, 0.0D).tex(t, v).endVertex();
    worldRenderer.pos(x, y, 0.0D).tex(u, v).endVertex();
    worldRenderer.pos(x, (y + height), 0.0D).tex(u, s).endVertex();
    worldRenderer.pos(x, (y + height), 0.0D).tex(u, s).endVertex();
    worldRenderer.pos((x + width), (y + height), 0.0D).tex(t, s).endVertex();
    worldRenderer.pos((x + width), y, 0.0D).tex(t, v).endVertex();
    tessellator.draw();
  }

  public static void drawLine(Vec3d start, Vec3d end) {
    worldRenderer.begin(1, DefaultVertexFormats.POSITION);
    worldRenderer.pos(start.x, start.y, start.z).endVertex();
    worldRenderer.pos(end.x, end.y, end.z).endVertex();
    tessellator.draw();
  }

  public static void drawLine(float size, float x, float y, float x1, float y1) {
    GlStateManager.pushMatrix();
    GlStateManager.disableTexture2D();
    GlStateManager.enableBlend();
    GL11.glEnable(2848);
    GL11.glLineWidth(size);
    worldRenderer.begin(1, DefaultVertexFormats.POSITION);
    worldRenderer.pos(x, y, 0.0D).endVertex();
    worldRenderer.pos(x1, y1, 0.0D).endVertex();
    tessellator.draw();
    GL11.glDisable(2848);
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
    GlStateManager.popMatrix();
  }

  public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, int yaw, EntityLivingBase ent) {
    if (ent == null)
      return;
    GlStateManager.enableColorMaterial();
    GlStateManager.pushMatrix();
    GL11.glTranslatef(posX, posY, 50.0F);
    GL11.glScalef(-scale, scale, scale);
    GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
    InventoryPlayer inv = ((EntityPlayer)ent).inventory;
    boolean b0 = ((EntityExt)ent).isShouldRenderNametag();
    int i = ent.getArrowCountInEntity();
    float f = ent.renderYawOffset;
    float f1 = ent.rotationYaw;
    float f2 = ent.rotationPitch;
    float f3 = ent.prevRotationYawHead;
    float f4 = ent.rotationYawHead;
    float f5 = ent.limbSwing;
    float f6 = ent.limbSwingAmount;
    float f7 = ent.prevLimbSwingAmount;
    ((EntityPlayer)ent).inventory = new InventoryPlayer((EntityPlayer)ent);
    ent.setArrowCountInEntity(0);
    ent.renderYawOffset = mouseX;
    ent.rotationYaw = mouseX + yaw;
    ent.rotationPitch = 0.0F;
    ent.rotationYawHead = ent.rotationYaw;
    ent.prevRotationYawHead = ent.rotationYaw;
    ent.limbSwing = 0.0F;
    ent.limbSwingAmount = 0.0F;
    ent.prevLimbSwingAmount = 0.0F;
    ((EntityExt)ent).setShouldRenderNametag(false);
    GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
    GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
    GL11.glRotatef(-((float)Math.atan((mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
    ent.renderYawOffset = (float)Math.atan((mouseX / 40.0F)) * 20.0F + yaw;
    ent.rotationYaw = (float)Math.atan((mouseX / 40.0F)) * 40.0F;
    ent.rotationPitch = -((float)Math.atan((mouseY / 40.0F))) * 20.0F;
    ent.rotationYawHead = ent.rotationYaw + yaw;
    ent.prevRotationYawHead = ent.rotationYaw + yaw;
    GL11.glTranslatef(0.0F, 0.0F, 1.0F);
    GlStateManager.enableDepth();
    RenderHelper.enableStandardItemLighting();
    RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
    rendermanager.setPlayerViewY(180.0F);
    rendermanager.setRenderShadow(false);
    rendermanager.renderEntityWithPosYaw((Entity)ent, 0.0D, 0.0D, 1.0D, 0.0F, 1.0F);
    rendermanager.setRenderShadow(true);
    GlStateManager.popMatrix();
    RenderHelper.disableStandardItemLighting();
    GlStateManager.disableRescaleNormal();
    GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
    GlStateManager.disableTexture2D();
    GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    ent.setArrowCountInEntity(i);
    ((EntityExt)ent).setShouldRenderNametag(b0);
    ent.renderYawOffset = f;
    ent.rotationYaw = f1;
    ent.rotationPitch = f2;
    ent.prevRotationYawHead = f3;
    ent.rotationYawHead = f4;
    ent.limbSwing = f5;
    ent.limbSwingAmount = f6;
    ent.prevLimbSwingAmount = f7;
    ((EntityPlayer)ent).inventory = inv;
  }

  public static void doScrollableTooltipTransform(int screenHeight, int tooltipY, int tooltipHeight) {
    if (!allowScrolling) {
      scrollX = 0;
      scrollY = 0;
    }
    allowScrolling = (tooltipY < 0);
    if (allowScrolling) {
      int eventDWheel = Mouse.getDWheel();
      if (Keyboard.isKeyDown(42)) {
        if (eventDWheel < 0) {
          scrollX += 10;
        } else if (eventDWheel > 0) {
          scrollX -= 10;
        }
      } else if (eventDWheel < 0) {
        scrollY -= 10;
      } else if (eventDWheel > 0) {
        scrollY += 10;
      }
      if (scrollY + tooltipY > 6) {
        scrollY = -tooltipY + 6;
      } else if (scrollY + tooltipY + tooltipHeight + 6 < screenHeight) {
        scrollY = screenHeight - 6 - tooltipY - tooltipHeight;
      }
    }
    GlStateManager.translate(scrollX, scrollY, 0.0F);
  }

  public static void drawFilledBoundingBox(AxisAlignedBB box) {
    drawFilledTopFace(box);
    drawFilledBottomFace(box);
    drawFilledNorthFace(box);
    drawFilledSouthFace(box);
    drawFilledEastFace(box);
    drawFilledWestFace(box);
  }

  public static void drawFilledTopFace(AxisAlignedBB box) {
    worldRenderer.begin(7, DefaultVertexFormats.POSITION);
    worldRenderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
    worldRenderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
    worldRenderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
    worldRenderer.pos(box.minX, box.maxY, box.minZ).endVertex();
    tessellator.draw();
  }

  public static void drawFilledBottomFace(AxisAlignedBB box) {
    worldRenderer.begin(7, DefaultVertexFormats.POSITION);
    worldRenderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
    worldRenderer.pos(box.minX, box.minY, box.maxZ).endVertex();
    worldRenderer.pos(box.minX, box.minY, box.minZ).endVertex();
    worldRenderer.pos(box.maxX, box.minY, box.minZ).endVertex();
    tessellator.draw();
  }

  public static void drawFilledNorthFace(AxisAlignedBB box) {
    worldRenderer.begin(7, DefaultVertexFormats.POSITION);
    worldRenderer.pos(box.minX, box.maxY, box.minZ).endVertex();
    worldRenderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
    worldRenderer.pos(box.maxX, box.minY, box.minZ).endVertex();
    worldRenderer.pos(box.minX, box.minY, box.minZ).endVertex();
    tessellator.draw();
  }

  public static void drawFilledSouthFace(AxisAlignedBB box) {
    worldRenderer.begin(7, DefaultVertexFormats.POSITION);
    worldRenderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
    worldRenderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
    worldRenderer.pos(box.minX, box.minY, box.maxZ).endVertex();
    worldRenderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
    tessellator.draw();
  }

  public static void drawFilledEastFace(AxisAlignedBB box) {
    worldRenderer.begin(7, DefaultVertexFormats.POSITION);
    worldRenderer.pos(box.maxX, box.maxY, box.minZ).endVertex();
    worldRenderer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
    worldRenderer.pos(box.maxX, box.minY, box.maxZ).endVertex();
    worldRenderer.pos(box.maxX, box.minY, box.minZ).endVertex();
    tessellator.draw();
  }

  public static void drawFilledWestFace(AxisAlignedBB box) {
    worldRenderer.begin(7, DefaultVertexFormats.POSITION);
    worldRenderer.pos(box.minX, box.maxY, box.maxZ).endVertex();
    worldRenderer.pos(box.minX, box.maxY, box.minZ).endVertex();
    worldRenderer.pos(box.minX, box.minY, box.minZ).endVertex();
    worldRenderer.pos(box.minX, box.minY, box.maxZ).endVertex();
    tessellator.draw();
  }
}