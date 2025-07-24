package com.github.timmekeclient.feature.impl.factions;

import com.github.timmekeclient.event.EventBus;
import com.github.timmekeclient.event.IRegistrable;
import com.github.timmekeclient.event.impl.render.RenderWorldEvent;
import com.github.timmekeclient.feature.annotations.properties.Colour;
import com.github.timmekeclient.feature.annotations.properties.ModuleInfo;
import com.github.timmekeclient.feature.annotations.properties.PageBreak;
import com.github.timmekeclient.feature.base.Category;
import com.github.timmekeclient.feature.base.Module;
import com.github.timmekeclient.gui.GuiOptions;
import com.github.timmekeclient.util.ColorObject;
import com.github.timmekeclient.util.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.List;

@ModuleInfo(name = "Boss Focus", description = "Draw a colored box around the boss mob", category = Category.FACTIONS)
public class BossFocus extends Module implements IRegistrable {

    @PageBreak(label = "Color")
    @Colour(label = "Box Color")
    public ColorObject boxColor = ColorObject.fromColor(GuiOptions.getInstance().getColor((GuiOptions.getInstance()).secondaryRed, 255));
    private final Set<String> bossNames = new HashSet<>(Arrays.asList("hades", "hermes", "ares", "helios", "santa", "hyperion"));

    private void drawBox(Entity entity, float partialTicks) {
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks - mc.getRenderManager().viewerPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks - mc.getRenderManager().viewerPosY;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks - mc.getRenderManager().viewerPosZ;
        AxisAlignedBB entityBox = entity.getEntityBoundingBox();
        AxisAlignedBB boundingBox = new AxisAlignedBB(entityBox.minX - entity.posX + x - 0.05D, entityBox.minY - entity.posY + y, entityBox.minZ - entity.posZ + z - 0.05D, entityBox.maxX - entity.posX + x + 0.05D, entityBox.maxY - entity.posY + y + 0.15D, entityBox.maxZ - entity.posZ + z + 0.05D);
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.disableTexture2D();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.resetColor();
        RenderUtils.setGlColor(boxColor);
        GL11.glLineWidth(4.0F);
        worldrenderer.begin(3, DefaultVertexFormats.POSITION);
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    @Override
    public void registerEvents() {
        EventBus.register(this, RenderWorldEvent.Post.class, ev -> {
            List<Entity> entities = mc.theWorld.getLoadedEntityList();
            for (Entity entity : entities) {
                if (entity instanceof EntityMob && bossNames.contains(entity.getCustomNameTag().toLowerCase(Locale.ROOT))) {
                    drawBox(entity, ev.partialTicks);
                }
            }
        });
    }
}
