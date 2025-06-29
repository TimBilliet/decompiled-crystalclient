package com.github.timmekeclient.renderer;

import com.github.timmekeclient.feature.impl.mechanic.NoLag;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.init.Blocks;

public class RenderTNTPrimed extends net.minecraft.client.renderer.entity.RenderTNTPrimed {

    Minecraft mc;
    IBlockState state;

    public RenderTNTPrimed(RenderManager p_i46134_1_) {
        super(p_i46134_1_);
        mc = Minecraft.getMinecraft();
        state = Blocks.tnt.getDefaultState();
    }


    @Override
    public void doRender(EntityTNTPrimed entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (entity.isDead || NoLag.getInstance().hideTnt)
            return;
        BlockRendererDispatcher blockRendererDispatcher = mc.getBlockRendererDispatcher();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + 0.5D, z);
        float f2 = (1.0F - (entity.fuse - partialTicks + 1.0F) / 100.0F) * 0.8F;
        bindEntityTexture(entity);
        GlStateManager.translate(-0.5F, -0.5F, 0.5F);
        blockRendererDispatcher.renderBlockBrightness(this.state, 1.0F);
        GlStateManager.translate(0.0F, 0.0F, 1.0F);
        GlStateManager.popMatrix();
        renderName(entity, x, y, z);
    }
}

