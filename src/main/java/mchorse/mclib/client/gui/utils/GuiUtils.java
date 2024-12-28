package mchorse.mclib.client.gui.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import java.net.URI;

public class GuiUtils {
  public static void drawModel(ModelBase model, EntityPlayer player, int x, int y, float scale) {
    drawModel(model, player, x, y, scale, 1.0F);
  }
  
  public static void drawModel(ModelBase model, EntityPlayer player, int x, int y, float scale, float alpha) {
    float factor = 0.0625F;
    GlStateManager.enableColorMaterial();
    GlStateManager.pushMatrix();
    GlStateManager.translate(x, y, 50.0F);
    GlStateManager.scale(-scale, scale, scale);
    GlStateManager.rotate(45.0F, -1.0F, 0.0F, 0.0F);
    GlStateManager.rotate(45.0F, 0.0F, -1.0F, 0.0F);
    GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
    RenderHelper.enableStandardItemLighting();
    GlStateManager.pushMatrix();
    GlStateManager.disableCull();
    GlStateManager.enableRescaleNormal();
    GlStateManager.scale(-1.0F, -1.0F, 1.0F);
    GlStateManager.translate(0.0F, -1.501F, 0.0F);
    GlStateManager.enableAlpha();
    model.setLivingAnimations((EntityLivingBase)player, 0.0F, 0.0F, 0.0F);
    model.setRotationAngles(0.0F, 0.0F, player.ticksExisted, 0.0F, 0.0F, factor, (Entity)player);
    GlStateManager.enableDepth();
    GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
    model.render((Entity)player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, factor);
    GlStateManager.disableDepth();
    GlStateManager.disableRescaleNormal();
    GlStateManager.disableAlpha();
    GlStateManager.popMatrix();
    GlStateManager.popMatrix();
    RenderHelper.disableStandardItemLighting();
    GlStateManager.disableRescaleNormal();
    GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
    GlStateManager.disableTexture2D();
    GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
  }
  
  public static void drawEntityOnScreen(int posX, int posY, float scale, EntityLivingBase ent, float alpha) {
    GlStateManager.enableDepth();
    GlStateManager.disableBlend();
    GlStateManager.enableColorMaterial();
    GlStateManager.pushMatrix();
    GlStateManager.translate(posX, posY, 100.0F);
    GlStateManager.scale(-scale, scale, scale);
    GlStateManager.rotate(45.0F, -1.0F, 0.0F, 0.0F);
    GlStateManager.rotate(45.0F, 0.0F, -1.0F, 0.0F);
    GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
    boolean render = ent.getAlwaysRenderNameTag();
    if (ent instanceof net.minecraft.entity.boss.EntityDragon)
      GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F); 
    RenderHelper.enableStandardItemLighting();
    GlStateManager.enableRescaleNormal();
    GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
    float f = ent.renderYawOffset;
    float f1 = ent.rotationYaw;
    float f2 = ent.rotationPitch;
    float f3 = ent.prevRotationYawHead;
    float f4 = ent.rotationYawHead;
    ent.renderYawOffset = 0.0F;
    ent.rotationYaw = 0.0F;
    ent.rotationPitch = 0.0F;
    ent.rotationYawHead = ent.rotationYaw;
    ent.prevRotationYawHead = ent.rotationYaw;
    ent.setAlwaysRenderNameTag(false);
    GlStateManager.translate(0.0F, 0.0F, 0.0F);
    RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
    rendermanager.setPlayerViewY(180.0F);
    rendermanager.setRenderShadow(false);
//    rendermanager.func_147939_a((Entity)ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
    rendermanager.doRenderEntity((Entity)ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
    rendermanager.setRenderShadow(true);
    ent.renderYawOffset = f;
    ent.rotationYaw = f1;
    ent.rotationPitch = f2;
    ent.prevRotationYawHead = f3;
    ent.rotationYawHead = f4;
    ent.setAlwaysRenderNameTag(render);
    GlStateManager.popMatrix();
    RenderHelper.disableStandardItemLighting();
    GlStateManager.disableRescaleNormal();
    GlStateManager.disableBlend();
    GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
    GlStateManager.disableTexture2D();
    GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    GlStateManager.disableDepth();
  }
  
  public static void drawEntityOnScreen(int posX, int posY, int scale, int mouseX, int mouseY, EntityLivingBase ent) {
    GlStateManager.enableColorMaterial();
    GlStateManager.pushMatrix();
    GlStateManager.translate(posX, posY, 100.0F);
    GlStateManager.scale(-scale, scale, scale);
    GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
    float f = ent.renderYawOffset;
    float f1 = ent.rotationYaw;
    float f2 = ent.rotationPitch;
    float f3 = ent.prevRotationYawHead;
    float f4 = ent.rotationYawHead;
    ent.renderYawOffset = (float)Math.atan((mouseX / 40.0F)) * 20.0F;
    ent.rotationYaw = (float)Math.atan((mouseX / 40.0F)) * 40.0F;
    ent.rotationPitch = -((float)Math.atan((mouseY / 40.0F))) * 20.0F;
    ent.rotationYawHead = ent.rotationYaw;
    ent.prevRotationYawHead = ent.rotationYaw;
    GlStateManager.translate(0.0F, 0.0F, 0.0F);
    RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
    rendermanager.setPlayerViewY(180.0F);
    rendermanager.setRenderShadow(false);
    rendermanager.doRenderEntity((Entity)ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
    rendermanager.setRenderShadow(true);
    ent.renderYawOffset = f;
    ent.rotationYaw = f1;
    ent.rotationPitch = f2;
    ent.prevRotationYawHead = f3;
    ent.rotationYawHead = f4;
    GlStateManager.popMatrix();
    RenderHelper.disableStandardItemLighting();
    GlStateManager.disableRescaleNormal();
    GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
    GlStateManager.disableTexture2D();
    GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
  }
  
  public static void openWebLink(String address) {
    try {
      openWebLink(new URI(address));
    } catch (Exception exception) {}
  }
  
  public static void openWebLink(URI uri) {
    try {
      Class<?> clazz = Class.forName("java.awt.Desktop");
      Object object = clazz.getMethod("getDesktop", new Class[0]).invoke(null);
      clazz.getMethod("browse", new Class[] { URI.class }).invoke(object, uri);
    } catch (Throwable throwable) {}
  }
  
  public static void playClick() {
    Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));//func_147674_a
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mclib\client\gu\\utils\GuiUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */