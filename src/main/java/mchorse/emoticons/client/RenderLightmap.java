package mchorse.emoticons.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class RenderLightmap extends RendererLivingEntity<EntityLivingBase> {
    private static RenderLightmap instance;

    public static void create() {
        instance = new RenderLightmap(Minecraft.getMinecraft().getRenderManager(), null, 0.0F);
    }

    public static boolean canRenderNamePlate(EntityLivingBase entity) {
        return instance.canRenderName(entity);
    }

    public static boolean set(EntityLivingBase entity, float partialTicks) {
        return instance.setBrightness(entity, partialTicks, true);
    }

    public static void unset() {
        instance.unsetBrightness();
    }

    public RenderLightmap(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
        super(renderManagerIn, modelBaseIn, shadowSizeIn);
    }

    protected int getColorMultiplier(EntityLivingBase entitylivingbaseIn, float lightBrightness, float partialTickTime) {
        return 0;
    }

    protected ResourceLocation getEntityTexture(EntityLivingBase entity) {
        return null;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\client\RenderLightmap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */