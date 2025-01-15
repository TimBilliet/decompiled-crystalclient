package co.crystaldev.client.cosmetic.type.wings;

import co.crystaldev.client.command.ThumbnailCommand;
import co.crystaldev.client.cosmetic.base.CosmeticModel;
import co.crystaldev.client.feature.settings.ClientOptions;
import co.crystaldev.client.gui.screens.ScreenCosmetics;
import co.crystaldev.client.util.MathUtils;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelWings extends CosmeticModel {
    private final ModelRenderer wing;

    private final ModelRenderer wingTip;

    public ModelWings() {
        setTextureOffset("wing.bone", 0, 0);
        setTextureOffset("wing.skin", -10, 8);
        setTextureOffset("wing.tip.bone", 0, 5);
        setTextureOffset("wing.tip.skin", -10, 18);
        this.wing = new ModelRenderer((ModelBase) this, "wing");
        this.wing.setTextureSize(30, 30);
        this.wing.setRotationPoint(-2.0F, 0.0F, 0.0F);
        this.wing.addBox("bone", -10.0F, -1.0F, -1.0F, 10, 2, 2);
        this.wing.addBox("skin", -10.0F, 0.0F, 0.5F, 10, 0, 10);
        this.wingTip = new ModelRenderer((ModelBase) this, "wing.tip");
        this.wingTip.setTextureSize(30, 30);
        this.wingTip.setRotationPoint(-10.0F, 0.0F, 0.0F);
        this.wingTip.addBox("bone", -10.0F, -0.5F, -0.5F, 10, 1, 1);
        this.wingTip.addBox("skin", -10.0F, 0.0F, 0.5F, 10, 0, 10);
        this.wing.addChild(this.wingTip);
    }

    public void render(float scale) {
        float f11 = (float) (ScreenCosmetics.isRenderingPlayer() ? 1000L : (System.currentTimeMillis() % 2000L)) / 2000.0F * 3.1415927F * 2.0F;
        for (int i = 0; i < 2; i++) {
            GL11.glEnable(2884);
            if (!(ClientOptions.getInstance()).newWingAnimation || ThumbnailCommand.isRendering() || ScreenCosmetics.isRenderingPlayer()) {
                this.wing.rotateAngleX = MathUtils.toRadians(-80.0F) - MathHelper.cos(f11) * 0.2F;
                this.wing.rotateAngleY = MathUtils.toRadians(20.0F) + MathHelper.sin(f11) * 0.4F;
                this.wing.rotateAngleZ = MathUtils.toRadians(20.0F);
                this.wingTip.rotateAngleZ = -(MathHelper.sin(f11 + 2.0F) + 0.5F) * 0.75F;
            } else {
                this.wing.rotateAngleX = -0.125F - (float) Math.cos(f11) * 0.2F;
                this.wing.rotateAngleY = 0.75F;
                this.wing.rotateAngleZ = (float) (Math.sin(f11) + 0.125D) * 0.8F;
                this.wingTip.rotateAngleZ = (float) (Math.sin((f11 + 2.0F)) + 0.5D) * 0.75F;
            }
            this.wing.render(scale);
            GL11.glScalef(-1.0F, 1.0F, 1.0F);
            if (i == 0)
                GL11.glCullFace(1028);
        }
    }
}