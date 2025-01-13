package co.crystaldev.client.mixin.net.minecraft.client.renderer.tileentity;

import co.crystaldev.client.feature.impl.mechanic.NoLag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityEndPortalRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.FloatBuffer;
import java.util.Random;

@Mixin({TileEntityEndPortalRenderer.class})
public abstract class MixinTileEntityEndPortalRenderer extends TileEntitySpecialRenderer<TileEntityEndPortal> {
    @Shadow
    @Final
    private static Random field_147527_e;

    @Shadow
    @Final
    private static ResourceLocation END_SKY_TEXTURE;

    @Shadow
    @Final
    private static ResourceLocation END_PORTAL_TEXTURE;

    @Shadow
    protected abstract FloatBuffer func_147525_a(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4);

    @Inject(method = {"renderTileEntityAt(Lnet/minecraft/tileentity/TileEntityEndPortal;DDDFI)V"}, at = {@At("HEAD")}, cancellable = true)
    private void cancelRendering(TileEntityEndPortal te, double x, double y, double z, float partialTicks, int destroyStage, CallbackInfo ci) {
        if (NoLag.isEnabled((NoLag.getInstance()).disableEndPortals)) {
            ci.cancel();
            return;
        }
        if (NoLag.isDisabled((NoLag.getInstance()).animatedEndPortal)) {
            renderReducedAnimationPortal(x, y, z);
            ci.cancel();
        }
    }

    public void renderReducedAnimationPortal(double x, double y, double z) {
        float f = (float) this.rendererDispatcher.entityX;
        float f1 = (float) this.rendererDispatcher.entityY;
        float f2 = (float) this.rendererDispatcher.entityZ;
        float f3 = 0.75F;
        float f4 = 65.0F;
        float f5 = 0.125F;
        float f6 = 0.1F;
        GlStateManager.disableLighting();
        GlStateManager.pushMatrix();
        bindTexture(END_SKY_TEXTURE);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        float f7 = (float) -(y + f3);
        float f8 = f7 + (float) (ActiveRenderInfo.getPosition()).yCoord;
        float f9 = f7 + f4 + (float) (ActiveRenderInfo.getPosition()).yCoord;
        float f10 = f8 / f9;
        f10 = (float) (y + f3) + f10;
        GlStateManager.translate(f, f10, f2);
        GlStateManager.texGen(GlStateManager.TexGen.S, 9217);
        GlStateManager.texGen(GlStateManager.TexGen.T, 9217);
        GlStateManager.texGen(GlStateManager.TexGen.R, 9217);
        GlStateManager.texGen(GlStateManager.TexGen.Q, 9216);
        GlStateManager.texGen(GlStateManager.TexGen.S, 9473, func_147525_a(1.0F, 0.0F, 0.0F, 0.0F));
        GlStateManager.texGen(GlStateManager.TexGen.T, 9473, func_147525_a(0.0F, 0.0F, 1.0F, 0.0F));
        GlStateManager.texGen(GlStateManager.TexGen.R, 9473, func_147525_a(0.0F, 0.0F, 0.0F, 1.0F));
        GlStateManager.texGen(GlStateManager.TexGen.Q, 9474, func_147525_a(0.0F, 1.0F, 0.0F, 0.0F));
        GlStateManager.enableTexGenCoord(GlStateManager.TexGen.S);
        GlStateManager.enableTexGenCoord(GlStateManager.TexGen.T);
        GlStateManager.enableTexGenCoord(GlStateManager.TexGen.R);
        GlStateManager.enableTexGenCoord(GlStateManager.TexGen.Q);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5890);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, (float) (Minecraft.getSystemTime() % 700000L) / 700000.0F, 0.0F);
        GlStateManager.scale(f5, f5, f5);
        GlStateManager.translate(0.5F, 0.5F, 0.0F);
        GlStateManager.rotate(0.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(-0.5F, -0.5F, 0.0F);
        GlStateManager.translate(-f, -f2, -f1);
        f8 = f7 + (float) (ActiveRenderInfo.getPosition()).yCoord;
        GlStateManager.translate((float) (ActiveRenderInfo.getPosition()).xCoord * f4 / f8, (float) (ActiveRenderInfo.getPosition()).zCoord * f4 / f8, -f1);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(x, y + f3, z).color(f6, f6, f6, 1.0F).endVertex();
        worldrenderer.pos(x, y + f3, z + 1.0D).color(f6, f6, f6, 1.0F).endVertex();
        worldrenderer.pos(x + 1.0D, y + f3, z + 1.0D).color(f6, f6, f6, 1.0F).endVertex();
        worldrenderer.pos(x + 1.0D, y + f3, z).color(f6, f6, f6, 1.0F).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        bindTexture(END_SKY_TEXTURE);
        GlStateManager.disableBlend();
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.S);
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.T);
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.R);
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.Q);
        GlStateManager.enableLighting();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\renderer\tileentity\MixinTileEntityEndPortalRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */