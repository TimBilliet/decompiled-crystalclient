package co.crystaldev.client.feature.impl.hud;

import co.crystaldev.client.Client;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.Slider;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.HudModuleBackground;
import co.crystaldev.client.font.FontRenderer;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.shader.ShaderManager;
import co.crystaldev.client.shader.chroma.ChromaScreenTexturedShader;
import co.crystaldev.client.util.ColorObject;
import co.crystaldev.client.util.MathUtils;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.enums.AnchorRegion;
import co.crystaldev.client.util.objects.ModulePosition;
import co.crystaldev.client.util.type.Tuple;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;

@ModuleInfo(name = "Compass", description = "Displays a compass onscreen", category = Category.HUD)
public class Compass extends HudModuleBackground {
  @Slider(label = "Width", minimum = 100.0D, maximum = 400.0D, standard = 250.0D, integers = true)
  public int adjustableWidth = 250;
  
  private static final String[] directions = new String[] { "N", "NE", "E", "SE", "S", "SW", "W", "NW" };
  
  private static final String[] directionX = new String[] { " ", "+", "+", "+", " ", "-", "-", "-" };
  
  private static final String[] directionZ = new String[] { "-", "-", " ", "+", "+", "+", " ", "-" };
  
  private final FontRenderer primaryFr;
  
  private final FontRenderer secondaryFr;
  
  private final FontRenderer tertiaryFr;
  
  public Compass() {
    this.enabled = true;
    this.hasInfoHud = true;
    this.infoHudEnabled = false;
    this.width = this.adjustableWidth;
    this.height = 30;
    this.position = new ModulePosition(AnchorRegion.TOP_CENTER, 0.0F, 5.0F);
    this.primaryFr = Fonts.PT_SANS_BOLD_16;
    this.secondaryFr = Fonts.PT_SANS_BOLD_16;
    this.tertiaryFr = Fonts.PT_SANS_BOLD_12;
  }
  
  public Tuple<String, String> getInfoHud() {
    int direction = getDirection(this.mc.thePlayer.rotationYaw);
    return new Tuple("Compass", directions[direction] + " (" + directionX[direction] + directionZ[direction] + ")");
  }
  
  public String getDisplayText() {
    return null;
  }
  
  public void draw() {
    float yaw = MathUtils.lerp((Client.getTimer()).renderPartialTicks, this.mc.thePlayer.prevRotationYawHead, this.mc.thePlayer.rotationYawHead) % 360.0F;
    if (yaw < 0.0F)
      yaw += 360.0F; 
    this.width = this.adjustableWidth;
    int x = getRenderX(), y = getRenderY();
    if (this.textColor.isChroma())
      ShaderManager.getInstance().enableShader(ChromaScreenTexturedShader.class);
    this.secondaryFr.drawCenteredString((int)yaw + "", x + this.width / 2, y + this.height / 4, this.textColor.getRGB());
    ShaderManager.getInstance().disableShader();
    if (this.drawBackground)
      drawGradientBackground(x, y); 
    drawCardinalDirection("S", x, y, yaw, 0.0F);
    drawCardinalDirection("W", x, y, yaw, 90.0F);
    drawCardinalDirection("N", x, y, yaw, 180.0F);
    drawCardinalDirection("E", x, y, yaw, 270.0F);
    drawOrdinalDirection("SW", x, y, yaw, 45.0F);
    drawOrdinalDirection("NW", x, y, yaw, 135.0F);
    drawOrdinalDirection("NE", x, y, yaw, 225.0F);
    drawOrdinalDirection("SE", x, y, yaw, 315.0F);
    RenderUtils.drawRect(x + this.width / 2.0F - 0.5F, y + this.height / 2.0F, x + this.width / 2.0F + 0.5F, (y + this.height), -1);
  }
  
  private void drawCardinalDirection(String text, int renderX, int renderY, float yaw, float angle) {
    float f = distanceAngle(yaw, angle);
    if (Math.abs(f) <= this.width / 2.0F) {
      float textX = renderX + this.width / 2.0F + f;
      float textY = (renderY + this.height) - this.primaryFr.getStringHeight(text) / 2.0F;
      int color = calculateAlpha(renderX, textX, this.textColor.getRGB());
      if (color != -1) {
        if (this.textColor.isChroma())
          ShaderManager.getInstance().enableShader(ChromaScreenTexturedShader.class);
        RenderUtils.setGlColor(color);
        RenderUtils.drawLine(1.5F, textX, renderY + this.height / 2.0F, textX, textY - 4.0F);
        this.primaryFr.drawCenteredString(text, textX, textY, color);
        ShaderManager.getInstance().disableShader();
      } 
    } 
    drawAngle(renderX, renderY, yaw, angle + 15.0F);
    drawAngle(renderX, renderY, yaw, angle + 30.0F);
    drawAngle(renderX, renderY, yaw, ((angle == 0.0F) ? 360.0F : angle) - 15.0F);
    drawAngle(renderX, renderY, yaw, ((angle == 0.0F) ? 360.0F : angle) - 30.0F);
  }
  
  private void drawOrdinalDirection(String text, int renderX, int renderY, float yaw, float angle) {
    float f = distanceAngle(yaw, angle);
    if (Math.abs(f) <= this.width / 2.0F) {
      float textX = renderX + this.width / 2.0F + f;
      float textY = renderY + this.height / 2.0F + this.height / 4.0F - 2.0F;
      int color = calculateAlpha(renderX, textX, this.textColor.getRGB());
      if (color != -1) {
        if (this.textColor.isChroma())
          ShaderManager.getInstance().enableShader(ChromaScreenTexturedShader.class);
        this.secondaryFr.drawCenteredString(text, textX, textY, color);
        ShaderManager.getInstance().disableShader();
      } 
    } 
  }
  
  private void drawAngle(int renderX, int renderY, float yaw, float angle) {
    float f = distanceAngle(yaw, angle);
    if (Math.abs(f) <= this.width / 2.0F) {
      float textX = renderX + this.width / 2.0F + f;
      float textY = renderY + this.height / 2.0F + this.height / 4.0F;
      int color = calculateAlpha(renderX, textX, this.textColor.getRGB());
      if (color != -1) {
        if (this.textColor.isChroma())
          ShaderManager.getInstance().enableShader(ChromaScreenTexturedShader.class);
        RenderUtils.setGlColor(color);
        RenderUtils.drawLine(0.8F, textX, renderY + this.height / 2.0F, textX, textY - 3.0F);
        this.tertiaryFr.drawCenteredString(Integer.toString((int)angle), textX, textY, color);
        ShaderManager.getInstance().disableShader();
      } 
    } 
  }
  
  private void drawGradientBackground(int x, int y) {
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldRenderer = tessellator.getWorldRenderer();
    float a1 = 0.0F, r1 = 0.0F, g1 = 0.0F, b1 = 0.0F;
    float a2 = (this.backgroundColor.getRGB() >> 24 & 0xFF) / 255.0F;
    float r2 = (this.backgroundColor.getRGB() >> 16 & 0xFF) / 255.0F;
    float g2 = (this.backgroundColor.getRGB() >> 8 & 0xFF) / 255.0F;
    float b2 = (this.backgroundColor.getRGB() & 0xFF) / 255.0F;
    GlStateManager.disableTexture2D();
    GlStateManager.enableBlend();
    GlStateManager.disableAlpha();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GlStateManager.shadeModel(7425);
    int right = x + this.width, center = x + this.width / 2;
    int top = y + this.height / 2, bottom = y + this.height;
    worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
    worldRenderer.pos(x, top, 0.0D).color(r1, g1, b1, a1).endVertex();
    worldRenderer.pos(x, bottom, 0.0D).color(r1, g1, b1, a1).endVertex();
    worldRenderer.pos(center, bottom, 0.0D).color(r2, g2, b2, a2).endVertex();
    worldRenderer.pos(center, top, 0.0D).color(r2, g2, b2, a2).endVertex();
    tessellator.draw();
    worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
    worldRenderer.pos(center, top, 0.0D).color(r2, g2, b2, a2).endVertex();
    worldRenderer.pos(center, bottom, 0.0D).color(r2, g2, b2, a2).endVertex();
    worldRenderer.pos(right, bottom, 0.0D).color(r1, g1, b1, a1).endVertex();
    worldRenderer.pos(right, top, 0.0D).color(r1, g1, b1, a1).endVertex();
    tessellator.draw();
    GlStateManager.shadeModel(7424);
    GlStateManager.disableBlend();
    GlStateManager.enableAlpha();
    GlStateManager.enableTexture2D();
    if (this.drawBorder)
      RenderUtils.drawBorderedRect(x, y, right, bottom, 0.5F, this.borderColor, ColorObject.TRANSPARENT); 
  }
  
  private int calculateAlpha(int renderX, float renderPosX, int color) {
    int center = renderX + this.width / 2;
    float distance = MathHelper.clamp_float(Math.abs(renderPosX - center), 1.0F, this.width / 2.0F);
    int r = color >> 16 & 0xFF;
    int g = color >> 8 & 0xFF;
    int b = color & 0xFF;
    int a = 255 - MathHelper.clamp_int((int)(distance / this.width / 2.0F * 255.0F), 0, 255);
    return (a < 35) ? -1 : ((a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b);
  }
  
  private float distanceAngle(float yaw, float other) {
    float dist = other - yaw;
    if (dist > 0.0F)
      return (dist > 180.0F) ? (dist - 360.0F) : dist; 
    return (dist < -180.0F) ? (dist + 360.0F) : dist;
  }
  
  private int getDirection(float yaw) {
    double point = MathHelper.wrapAngleTo180_float(yaw) + 180.0D;
    point += 22.5D;
    point %= 360.0D;
    point /= 45.0D;
    return MathHelper.floor_double(point);
  }
}
