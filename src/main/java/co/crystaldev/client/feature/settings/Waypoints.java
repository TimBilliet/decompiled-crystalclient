package co.crystaldev.client.feature.settings;

import co.crystaldev.client.Client;
import co.crystaldev.client.cache.UsernameCache;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.render.RenderWorldEvent;
import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.group.objects.GroupMember;
import co.crystaldev.client.handler.WaypointHandler;
import co.crystaldev.client.shader.ShaderManager;
import co.crystaldev.client.shader.chroma.ChromaScreenShader;
import co.crystaldev.client.util.ColorObject;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.Vec3d;
import co.crystaldev.client.util.objects.Waypoint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3i;

import java.awt.*;

public class Waypoints implements IRegistrable {
    private static final ColorObject BLACK = new ColorObject(0, 0, 0, 255);

    private final Minecraft mc = Minecraft.getMinecraft();

    private void renderWaypoint(Waypoint wp) {
        Vec3d vec = RenderUtils.normalize(wp.getPos());
        double maxDistance = this.mc.gameSettings.renderDistanceChunks * 12.0D;
        double distance = wp.distanceTo((Vec3i) this.mc.thePlayer.getPosition());
        String text = wp.getName() + " [" + Math.round(distance) + "m]";
        int width = this.mc.fontRendererObj.getStringWidth(text) / 2;
        int tagAlpha = (distance < 16.0D) ? ((distance >= 4.0D) ? (int) Math.round(180.0D * (distance - 4.0D) / 12.0D) : 0) : 180;
        int textAlpha = (distance < 16.0D) ? ((distance >= 4.0D) ? (int) Math.round(255.0D * (distance - 4.0D) / 12.0D) : 0) : 255;
        int beamAlpha = (distance < 32.0D) ? ((distance >= 6.0D) ? (int) Math.round(140.0D * (distance - 6.0D) / 26.0D) : 0) : 140;
        if (distance > maxDistance) {
            vec = new Vec3d(vec.x / distance * maxDistance, vec.y / distance * maxDistance, vec.z / distance * maxDistance);
            distance = maxDistance;
        }
        float size = ((float) distance * 0.1F + 1.0F) * 0.0185F;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        if (distance < maxDistance) {
            RenderUtils.setGlColor((Color) wp.getColor().setAlpha(beamAlpha));
            if (wp.getColor().isChroma())
                ShaderManager.getInstance().enableShader(ChromaScreenShader.class);
            int x = wp.getPos().getX();
            int z = wp.getPos().getZ();
            RenderUtils.drawFilledBoundingBox(
                    RenderUtils.normalize((new AxisAlignedBB(x, 0.0D, z, (x + 1), 255.0D, (z + 1))).expand(-0.3D, 0.0D, -0.3D)));
            ShaderManager.getInstance().disableShader();
        }
        GlStateManager.translate(vec.x, vec.y, vec.z);
        GlStateManager.rotate(-(this.mc.getRenderManager()).playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((this.mc.getRenderManager()).playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-size, -size, -size);
        GlStateManager.disableDepth();
        int alpha = wp.getColor().getAlpha();
        RenderUtils.drawBorderedRect((-width - 4), (this.mc.fontRendererObj.FONT_HEIGHT + 4), (width + 4), -4.0F, 2.0F, wp.getColor().setAlpha(tagAlpha), BLACK
                .setAlpha(tagAlpha));
        if (textAlpha > 4)
            RenderUtils.drawString(text, -width, 0, wp.getColor(), false);
        wp.getColor().setAlpha(alpha);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.resetColor();
        GlStateManager.popMatrix();
        if (wp.isExpired())
            WaypointHandler.getInstance().removeWaypoint(wp);
    }

    public void registerEvents() {
        EventBus.register(this, RenderWorldEvent.Post.class, ev -> {
            if (this.mc.thePlayer != null && this.mc.theWorld != null) {
                for (Waypoint wp : WaypointHandler.getInstance().getRegisteredWaypoints()) {
                    if (!wp.isVisible() || !wp.isSameWorld())
                        continue;
                    renderWaypoint(wp);
                }
                if (GroupManager.getSelectedGroup() != null)
                    for (GroupMember member : GroupManager.getSelectedGroup().getMembers()) {
                        if (member.getPingLocation() != null)
                            renderWaypoint(new Waypoint(UsernameCache.getInstance().getUsername(member.getUuid()) + "'s Location", Client.formatConnectedServerIp(), member.getPingLocation()));
                    }
            }
        });
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\settings\Waypoints.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */