package co.crystaldev.client.mixin.net.minecraft.client.particle;

import co.crystaldev.client.feature.settings.ClientOptions;
import co.crystaldev.client.shader.ShaderManager;
import co.crystaldev.client.shader.chroma.ChromaScreenTexturedShader;
import co.crystaldev.client.util.ColorObject;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityLargeExplodeFX;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({EntityLargeExplodeFX.class})
public abstract class MixinEntityLargeExplodeFX extends EntityFX {
  @Shadow
  private int field_70581_a;
  
  @Shadow
  private int field_70584_aq;
  
  @Shadow
  private TextureManager theRenderEngine;
  
  @Shadow
  @Final
  private static ResourceLocation EXPLOSION_TEXTURE;
  
  @Shadow
  private float field_70582_as;
  
  @Shadow
  @Final
  private static VertexFormat field_181549_az;
  
  protected MixinEntityLargeExplodeFX(World worldIn, double posXIn, double posYIn, double posZIn) {
    super(worldIn, posXIn, posYIn, posZIn);
  }
  
  public void renderParticle(WorldRenderer worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
    int i = (int)((this.field_70581_a + partialTicks) * 15.0F / this.field_70584_aq);
    if (i <= 15) {
      this.theRenderEngine.bindTexture(EXPLOSION_TEXTURE);
      float f = (i % 4) / 4.0F;
      float f1 = f + 0.24975F;
      float f2 = (i / 4.0F) / 4.0F;//float
      float f3 = f2 + 0.24975F;
      float f4 = 2.0F * this.field_70582_as;
      float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
      float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
      float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);
      GlStateManager.disableLighting();
      RenderHelper.disableStandardItemLighting();
      float x = f5 - rotationX * f4 - rotationXY * f4;
      float x1 = f5 - rotationX * f4 + rotationXY * f4;
      float x2 = f5 + rotationX * f4 + rotationXY * f4;
      float x3 = f5 + rotationX * f4 - rotationXY * f4;
      float z = f7 - rotationYZ * f4 - rotationXZ * f4;
      float z1 = f7 - rotationYZ * f4 + rotationXZ * f4;
      float z2 = f7 + rotationYZ * f4 + rotationXZ * f4;
      float z3 = f7 + rotationYZ * f4 - rotationXZ * f4;
      if ((ClientOptions.getInstance()).useExplosionColor) {
        ColorObject color = (ClientOptions.getInstance()).explosionColor;
        float r = color.getRed() / 255.0F;
        float g = color.getGreen() / 255.0F;
        float b = color.getBlue() / 255.0F;
        float a = color.getAlpha() / 255.0F;
        GlStateManager.enableBlend();
        if (color.isChroma())
          ShaderManager.getInstance().enableShader(ChromaScreenTexturedShader.class);
        worldRendererIn.begin(7, field_181549_az);

                //func_181671_a
        worldRendererIn.pos(x, (f6 - rotationZ * f4), z).tex(f1, f3).color(r, g, b, a).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
        worldRendererIn.pos(x1, (f6 + rotationZ * f4), z1).tex(f1, f2).color(r, g, b, a).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
        worldRendererIn.pos(x2, (f6 + rotationZ * f4), z2).tex(f, f2).color(r, g, b, a).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
        worldRendererIn.pos(x3, (f6 - rotationZ * f4), z3).tex(f, f3).color(r, g, b, a).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
      } else {
        worldRendererIn.begin(7, field_181549_az);
        worldRendererIn.pos(x, (f6 - rotationZ * f4), z).tex(f1, f3).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
        worldRendererIn.pos(x1, (f6 + rotationZ * f4), z1).tex(f1, f2).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
        worldRendererIn.pos(x2, (f6 + rotationZ * f4), z2).tex(f, f2).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
        worldRendererIn.pos(x3, (f6 - rotationZ * f4), z3).tex(f, f3).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
      }
      Tessellator.getInstance().draw();
      RenderHelper.enableStandardItemLighting();
      GlStateManager.enableLighting();
      if ((ClientOptions.getInstance()).useExplosionColor) {
        GlStateManager.disableBlend();
        if ((ClientOptions.getInstance()).explosionColor.isChroma())
          ShaderManager.getInstance().disableShader();
      }
    } 
  }
}
