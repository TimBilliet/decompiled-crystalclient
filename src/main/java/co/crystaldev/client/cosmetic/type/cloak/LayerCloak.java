package co.crystaldev.client.cosmetic.type.cloak;

import co.crystaldev.client.cosmetic.CosmeticPlayer;
import co.crystaldev.client.cosmetic.base.Cosmetic;
import co.crystaldev.client.duck.AbstractClientPlayerExt;
import co.crystaldev.client.gui.screens.ScreenCosmetics;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class LayerCloak implements LayerRenderer<AbstractClientPlayer> {
    private static final ModelCloak model = new ModelCloak();

    public void doRenderLayer(AbstractClientPlayer entity, float limbSwing, float prevLimbSwing, float partialTicks, float rotate, float yaw, float pitch, float scale) {
        CosmeticPlayer player = ((AbstractClientPlayerExt) entity).crystal$getCosmeticPlayer();
        if (entity.hasPlayerInfo() && !entity.isInvisible() && player.hasCloak()) {
            Cosmetic cloak = player.getCloak();
            cloak.bindTexture();
            GL11.glPushMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glTranslatef(0.0F, entity.isChild() ? 0.725F : 0.0F, entity.isChild() ? 0.0625F : 0.125F);
            if (entity.isChild())
                GL11.glScalef(0.5F, 0.5F, 0.5F);
            double d0 = entity.prevChasingPosX + (entity.chasingPosX - entity.prevChasingPosX) * partialTicks - entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks;
            double d1 = entity.prevChasingPosY + (entity.chasingPosY - entity.prevChasingPosY) * partialTicks - entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks;
            double d2 = entity.prevChasingPosZ + (entity.chasingPosZ - entity.prevChasingPosZ) * partialTicks - entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks;
            float f = entity.prevRenderYawOffset + (entity.renderYawOffset - entity.prevRenderYawOffset) * partialTicks;
            double d3 = MathHelper.sin(f * 3.1415927F / 180.0F);
            double d4 = -MathHelper.cos(f * 3.1415927F / 180.0F);
            float f1 = (float) d1 * 10.0F;
            f1 = MathHelper.clamp_float(f1, -6.0F, 32.0F);
            float f2 = (float) (d0 * d3 + d2 * d4) * 100.0F;
            float f3 = (float) (d0 * d4 - d2 * d3) * 100.0F;
            if (f2 < 0.0F)
                f2 = 0.0F;
            if (f2 > 165.0F)
                f2 = 165.0F;
            if (f1 < -5.0F)
                f1 = -5.0F;
            float f4 = entity.prevCameraYaw + (entity.cameraYaw - entity.prevCameraYaw) * partialTicks;
            f1 += MathHelper.sin((entity.prevDistanceWalkedModified + (entity.distanceWalkedModified - entity.prevDistanceWalkedModified) * partialTicks) * 6.0F) * 32.0F * f4;
            if (entity.isSneaking()) {
                f1 += 25.0F;
                GL11.glTranslatef(0.0F, 0.142F, entity.isChild() ? 0.0F : -0.0178F);
            }
            if (ScreenCosmetics.isRenderingPlayer()) {
                GL11.glRotatef(3.0F, 1.0F, 0.0F, 0.0F);
            } else {
                GL11.glRotatef(6.0F + f2 / 2.0F + f1, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(f3 / 2.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-f3 / 2.0F, 0.0F, 1.0F, 0.0F);
            }
            GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            model.render(0.0625F);
            GL11.glPopMatrix();
        }
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}