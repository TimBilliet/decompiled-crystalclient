package mchorse.emoticons.client.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class PopcornParticle extends EntityFX {
  public static ModelRenderer kernel1;
  
  public static ModelRenderer kernel2;
  
  public static ModelRenderer kernel3;
  
  protected int color;
  
  public PopcornParticle(World worldIn, double posXIn, double posYIn, double posZIn, double mY) {
    super(worldIn, posXIn, posYIn, posZIn);
    this.particleGravity = 0.5F;
    this.particleMaxAge = 20 + this.rand.nextInt(10);
    this.motionX = (this.rand.nextFloat() * 0.05F);
    this.motionZ = (this.rand.nextFloat() * 0.05F);
    this.motionY = (mY == 0.0D) ? mY : (this.rand.nextDouble() * 0.10000000149011612D + mY);
    if (kernel1 == null) {
      ModelBase model = new ModelBase() {
        
        };
      model.textureWidth = 64;
      model.textureHeight = 64;
      kernel1 = new ModelRenderer(model, 0, 2);
      kernel1.addBox(-0.5F, -0.5F, 0.5F, 1, 1, 1);
      kernel2 = new ModelRenderer(model, 0, 4);
      kernel2.addBox(-0.5F, -0.5F, 0.5F, 1, 1, 1);
      kernel3 = new ModelRenderer(model, 0, 6);
      kernel3.addBox(-0.5F, -0.5F, 0.5F, 1, 1, 1);
    } 
    this.color = this.rand.nextInt(2);
  }
  
  public void renderParticle(WorldRenderer buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
    float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
    float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
    float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);
    int age = this.particleMaxAge - this.particleAge;
    float scale = 0.75F * ((age < 5) ? (age / 5.0F) : 1.0F);
    ModelRenderer model = kernel1;
    if (this.color == 1) {
      model = kernel2;
    } else if (this.color == 2) {
      model = kernel3;
    } 
    Minecraft.getMinecraft().getTextureManager().bindTexture(SaltParticle.PARTICLES);
    int i = entityIn.getBrightnessForRender(0.0F);
    if (entityIn.isBurning())
      i = 15728880; 
    int j = i % 65536;
    int k = i / 65536;
    GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j, k);
    GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.pushMatrix();
    GlStateManager.translate(f5, f6, f7);
    GlStateManager.scale(scale, scale, scale);
    RenderHelper.enableStandardItemLighting();
    model.render(0.0625F);
    RenderHelper.disableStandardItemLighting();
    GlStateManager.popMatrix();
  }
  
  public int getFXLayer() {
    return 3;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\client\particles\PopcornParticle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */