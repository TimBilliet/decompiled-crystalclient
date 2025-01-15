package co.crystaldev.client.cosmetic.type.wings;

import co.crystaldev.client.cosmetic.CosmeticPlayer;
import co.crystaldev.client.cosmetic.base.Cosmetic;
import co.crystaldev.client.duck.AbstractClientPlayerExt;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import org.lwjgl.opengl.GL11;

public class LayerWings implements LayerRenderer<AbstractClientPlayer> {
    private static final ModelWings model = new ModelWings();

    public void doRenderLayer(AbstractClientPlayer entity, float limbSwing, float prevLimbSwing, float partialTicks, float rotate, float yaw, float pitch, float scale) {
        CosmeticPlayer player = ((AbstractClientPlayerExt) entity).crystal$getCosmeticPlayer();
        if (entity.hasPlayerInfo() && !entity.isInvisible() && player.hasWings()) {
            Cosmetic wings = player.getWings();
            wings.bindTexture();
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, 0.0F, 0.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glScalef(0.7F, 0.7F, 0.7F);
            GL11.glTranslatef(0.0F, 0.1F, 0.0F);
            GL11.glTranslatef(0.0F, 0.0F, 0.2F);
            if (entity.isSneaking())
                GL11.glTranslatef(0.0F, entity.isChild() ? 0.0F : 0.145F, -0.0178F);
            model.render(0.0625F);
            GL11.glCullFace(1029);
            GL11.glDisable(2884);
            GL11.glPopMatrix();
        }
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}