package co.crystaldev.client.mixin.net.minecraft.client.renderer.entity;

import co.crystaldev.client.Client;
import co.crystaldev.client.Resources;
import co.crystaldev.client.cosmetic.CosmeticPlayer;
import co.crystaldev.client.cosmetic.type.Color;
import co.crystaldev.client.duck.AbstractClientPlayerExt;
import co.crystaldev.client.shader.ShaderManager;
import co.crystaldev.client.shader.chroma.ChromaScreenTexturedShader;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.feature.impl.all.NametagEditor;
import co.crystaldev.client.util.enums.IconColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({Render.class})
public abstract class MixinRender<T extends Entity> {
    @Shadow
    @Final
    protected RenderManager renderManager;

    @Shadow
    public abstract FontRenderer getFontRendererFromRenderManager();

    /**
     * @author
     */
    @Overwrite(aliases = {"renderLivingLabel"})
    protected void renderLivingLabel(T entityIn, String str, double x, double y, double z, int maxDistance) {
        double d0 = entityIn.getDistanceSqToEntity(this.renderManager.livingPlayer);
        if (d0 <= (maxDistance * maxDistance)) {
            boolean isNameTag = (entityIn instanceof net.minecraft.entity.player.EntityPlayer && str.contains(entityIn.getCommandSenderEntity().getName()));
            boolean isCrystalClient = isNameTag && (NametagEditor.getInstance()).enabled && (NametagEditor.getInstance()).showCrystalClientLogo && Client.isOnCrystalClient(entityIn);
            boolean isOrbitClient = isNameTag && NametagEditor.getInstance().enabled && NametagEditor.getInstance().showOrbitClientLogo && Client.isOnOrbitClient(entityIn);
            FontRenderer fontrenderer = getFontRendererFromRenderManager();
            float f = 1.6F;
            float f1 = 0.016666668F * f;
            GlStateManager.pushMatrix();
            GlStateManager.translate((float) x, (float) y + ((Entity) entityIn).height + 0.5F - ((entityIn instanceof EntityLivingBase && ((EntityLivingBase) entityIn).isChild()) ? (((Entity) entityIn).height / 2.0F) : 0.0F), (float) z);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(((Minecraft.getMinecraft()).gameSettings.thirdPersonView == 2) ? -this.renderManager.playerViewX : this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
            GlStateManager.scale(-f1, -f1, f1);
            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            int j = fontrenderer.getStringWidth(str) / 2;
            if (isCrystalClient || isOrbitClient)
                j += 5;
            GlStateManager.disableTexture2D();
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            worldrenderer.pos((-j - 1), -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            worldrenderer.pos((-j - 1), 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            worldrenderer.pos((j + 1), 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            worldrenderer.pos((j + 1), -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            tessellator.draw();
            GlStateManager.enableTexture2D();
            int x1 = -fontrenderer.getStringWidth(str) / 2;
            if (isCrystalClient || isOrbitClient) {
                x1 += 5;
                CosmeticPlayer cp = ((AbstractClientPlayerExt) entityIn).crystal$getCosmeticPlayer();
                Color color = (cp == null) ? null : (Color) cp.getColor();
                if (cp != null && color != null) {
                    IconColor iconColor = color.getIconColor();
                    RenderUtils.setGlColor(iconColor.getColor(), 32);
                    if (iconColor == IconColor.CHROMA)
                        ShaderManager.getInstance().enableShader(ChromaScreenTexturedShader.class);
                } else {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 0.25F);
                }
                if(isCrystalClient)
                    RenderUtils.drawCustomSizedResource(Resources.LOGO_WHITE, x1 - 10, -1, 9, 9);
                else RenderUtils.drawCustomSizedResource(Resources.LOGO_ORBIT_ORIGINAL, x1 - 10, -1, 9, 9);
                ShaderManager.getInstance().disableShader();
                GlStateManager.enableBlend();
                GlStateManager.resetColor();
            }
            fontrenderer.drawString(str, x1, 0.0F, 553648127, ((NametagEditor.getInstance()).enabled && (NametagEditor.getInstance()).textShadow));
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            if (isCrystalClient || isOrbitClient) {
                CosmeticPlayer cp = ((AbstractClientPlayerExt) entityIn).crystal$getCosmeticPlayer();
                Color color = (cp == null) ? null : (Color) cp.getColor();
                if (cp != null && color != null) {
                    IconColor iconColor = color.getIconColor();
                    RenderUtils.setGlColor(iconColor.getColor(), 255);
                    if (iconColor == IconColor.CHROMA)
                        ShaderManager.getInstance().enableShader(ChromaScreenTexturedShader.class);
                } else {
                    RenderUtils.resetColor();
                }
                if(isCrystalClient)
                    RenderUtils.drawCustomSizedResource(Resources.LOGO_WHITE, x1 - 10, -1, 9, 9);
                else
                    RenderUtils.drawCustomSizedResource(Resources.LOGO_ORBIT_ORIGINAL, x1 - 10, -1, 9, 9);
                ShaderManager.getInstance().disableShader();
                GlStateManager.enableBlend();
                GlStateManager.resetColor();
            }
            fontrenderer.drawString(str, x1, 0.0F, -1, ((NametagEditor.getInstance()).enabled && (NametagEditor.getInstance()).textShadow));
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
    }
}