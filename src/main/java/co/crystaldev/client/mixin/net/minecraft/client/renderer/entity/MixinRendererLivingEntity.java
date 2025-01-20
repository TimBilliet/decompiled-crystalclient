package co.crystaldev.client.mixin.net.minecraft.client.renderer.entity;

import co.crystaldev.client.Client;
import co.crystaldev.client.Resources;
import co.crystaldev.client.cosmetic.CosmeticPlayer;
import co.crystaldev.client.cosmetic.type.Color;
import co.crystaldev.client.duck.AbstractClientPlayerExt;
import co.crystaldev.client.duck.EntityExt;
import co.crystaldev.client.feature.impl.all.NametagEditor;
import co.crystaldev.client.feature.impl.combat.HitColor;
import co.crystaldev.client.feature.settings.ClientOptions;
import co.crystaldev.client.shader.ShaderManager;
import co.crystaldev.client.shader.chroma.ChromaScreenTexturedShader;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.enums.IconColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;
import java.nio.FloatBuffer;

@Mixin({RendererLivingEntity.class})
public abstract class MixinRendererLivingEntity<T extends EntityLivingBase> extends Render<T> {
    @Shadow
    protected FloatBuffer brightnessBuffer;

    protected MixinRendererLivingEntity(RenderManager renderManager) {
        super(renderManager);
    }

    @Inject(method = {"doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V"}, cancellable = true, at = {@At("HEAD")})
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (entity == null)
            ci.cancel();
    }

    @Inject(method = {"rotateCorpse"}, cancellable = true, at = {@At(value = "INVOKE", target = "Lnet/minecraft/util/EnumChatFormatting;getTextWithoutFormattingCodes(Ljava/lang/String;)Ljava/lang/String;", shift = At.Shift.AFTER)})
    protected void rotateCorpse(T bat, float p_77043_2_, float p_77043_3_, float partialTicks, CallbackInfo ci) {
        if (bat instanceof AbstractClientPlayerExt) {
            CosmeticPlayer cp = ((AbstractClientPlayerExt) bat).crystal$getCosmeticPlayer();
            if (System.currentTimeMillis() - Client.getLastHitTime() > 30000L && cp != null && cp.hasCloak() && "australia".equals(cp.getCloak().getName())) {
                GlStateManager.translate(0.0F, ((EntityLivingBase) bat).height + 0.1F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
                ci.cancel();
            }
        }
    }

    /**
     * @author Tim
     */
    @Overwrite(aliases = {"canRenderName"})
    protected boolean canRenderName(T entity) {
        EntityPlayerSP entityplayersp = (Minecraft.getMinecraft()).thePlayer;
        if (entity == null || !((EntityExt) entity).isShouldRenderNametag())
            return false;
        boolean flag = ((Minecraft.getMinecraft()).gameSettings.thirdPersonView > 0 && (ClientOptions.getInstance()).f5Nametags);
        if (entity instanceof EntityPlayer && (flag || entity.getEntityId() != entityplayersp.getEntityId())) {
            Team team = entity.getTeam();
            Team team1 = entityplayersp.getTeam();
            if (entity.isInvisible())
                return false;
            if (flag)
                return true;
            if (team != null) {
                Team.EnumVisible teamVisibility = team.getNameTagVisibility();
                switch (teamVisibility) {
                    case NEVER:
                        return false;
                    case HIDE_FOR_OTHER_TEAMS:
                        return (team1 == null || team.isSameTeam(team1));
                    case HIDE_FOR_OWN_TEAM:
                        return (team1 == null || !team.isSameTeam(team1));
                }
                return true;
            }
        }
        return (Minecraft.isGuiEnabled() && entity != this.renderManager.livingPlayer && !entity.isInvisibleToPlayer((EntityPlayer) entityplayersp) && ((EntityLivingBase) entity).riddenByEntity == null);
    }

    @Inject(method = {"renderName(Lnet/minecraft/entity/Entity;DDD)V"}, cancellable = true, at = {@At("HEAD")})
    public void renderEntityName(Entity entityIn, double x, double y, double z, CallbackInfo ci) {
        EntityLivingBase entityLivingBase = (EntityLivingBase) entityIn;
        ci.cancel();
        if (canRenderName((T) entityLivingBase)) {
            double d0 = entityLivingBase.getDistanceSqToEntity(this.renderManager.livingPlayer);
            float f = entityLivingBase.isSneaking() ? 32.0F : 64.0F;
            if (d0 < (f * f)) {
                String s = entityLivingBase.getDisplayName().getFormattedText();
                GlStateManager.alphaFunc(516, 0.1F);
                if (entityLivingBase.isSneaking()) {
                    boolean isCrystalClient = ((NametagEditor.getInstance()).enabled && (NametagEditor.getInstance()).showClientLogo && Client.isOnCrystalClient((Entity) entityLivingBase));
                    FontRenderer fontrenderer = getFontRendererFromRenderManager();
                    GlStateManager.pushMatrix();
                    GlStateManager.translate((float) x, (float) y + entityLivingBase.height + 0.5F - (entityLivingBase.isChild() ? (entityLivingBase.height / 2.0F) : 0.0F), (float) z);
                    GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(((Minecraft.getMinecraft()).gameSettings.thirdPersonView == 2) ? -this.renderManager.playerViewX : this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
                    GlStateManager.scale(-0.02666667F, -0.02666667F, 0.02666667F);
                    GlStateManager.translate(0.0F, 9.374999F, 0.0F);
                    GlStateManager.disableLighting();
                    GlStateManager.depthMask(false);
                    GlStateManager.enableBlend();
                    GlStateManager.disableTexture2D();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                    int i = fontrenderer.getStringWidth(s) / 2;
                    if (isCrystalClient)
                        i += 5;
                    Tessellator tessellator = Tessellator.getInstance();
                    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                    worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
                    worldrenderer.pos((-i - 1), -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                    worldrenderer.pos((-i - 1), 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                    worldrenderer.pos((i + 1), 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                    worldrenderer.pos((i + 1), -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                    tessellator.draw();
                    GlStateManager.enableTexture2D();
                    GlStateManager.depthMask(true);
                    int x1 = -fontrenderer.getStringWidth(s) / 2;
                    if (isCrystalClient)
                        x1 += 5;
                    if (isCrystalClient) {
                        CosmeticPlayer cp = ((AbstractClientPlayerExt) entityLivingBase).crystal$getCosmeticPlayer();
                        Color color = (cp == null) ? null : (Color) cp.getColor();
                        if (cp != null && color != null) {
                            IconColor iconColor = color.getIconColor();
                            if (iconColor == IconColor.CHROMA)
                                ShaderManager.getInstance().enableShader(ChromaScreenTexturedShader.class);
                            RenderUtils.setGlColor(iconColor.getColor(), 64);
                        } else {
                            RenderUtils.setGlColor(16777215, 64);
                        }
                        RenderUtils.drawCustomSizedResource(Resources.LOGO_WHITE, x1 - 10, -1, 9, 9);
                        GlStateManager.enableBlend();
                        ShaderManager.getInstance().disableShader();
                    }
                    fontrenderer.drawString(s, x1, 0.0F, 553648127, ((NametagEditor.getInstance()).enabled && (NametagEditor.getInstance()).textShadow));
                    GlStateManager.enableLighting();
                    GlStateManager.disableBlend();
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.popMatrix();
                } else {
                    renderOffsetLivingLabel((T) entityLivingBase, x, y, z, s, 0.02666667F, d0);
                }
            }
        }
    }

    @Inject(method = {"setBrightness"}, at = {@At(value = "INVOKE", target = "Ljava/nio/FloatBuffer;put(F)Ljava/nio/FloatBuffer;", ordinal = 3, shift = At.Shift.AFTER)})
    public void colorizeHit(T f2, float f3, boolean f4, CallbackInfoReturnable<Boolean> cir) {
        HitColor instance = HitColor.getInstance();
        if (instance.enabled) {
            this.brightnessBuffer.position(0);
            java.awt.Color color = instance.color.isChroma() ? RenderUtils.getCurrentChromaColor() : (java.awt.Color) instance.color;
            this.brightnessBuffer.put(color.getRed() / 255.0F);
            this.brightnessBuffer.put(color.getGreen() / 255.0F);
            this.brightnessBuffer.put(color.getBlue() / 255.0F);
            this.brightnessBuffer.put(instance.color.getAlpha() / 255.0F);
        }
    }
}