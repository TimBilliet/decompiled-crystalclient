package co.crystaldev.client.group.provider;

import co.crystaldev.client.Client;
import co.crystaldev.client.cache.SkinCache;
import co.crystaldev.client.cache.UsernameCache;
import co.crystaldev.client.feature.impl.hud.MapWriter;
import co.crystaldev.client.feature.settings.GroupOptions;
import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.group.objects.GroupMember;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.resources.CachedSkin;
import mapwriter.MapWriterMod;
import mapwriter.api.IMwChunkOverlay;
import mapwriter.api.IMwDataProvider;
import mapwriter.map.MapView;
import mapwriter.map.mapmode.MapMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class GroupMapProvider implements IMwDataProvider {
  private static GroupMapProvider INSTANCE;

  private final Minecraft mc = Minecraft.getMinecraft();

  private String name;

  public GroupMapProvider() {
    INSTANCE = this;
  }

  public void onDraw(MapView mapview, MapMode mapmode) {
    if (GroupManager.getSelectedGroup() != null && (GroupOptions.getInstance()).showMembersOnMap && (!(GroupOptions.getInstance()).onlyShowOnBigMap || this.mc.currentScreen instanceof co.crystaldev.client.gui.screens.ScreenMapWriter))
      for (GroupMember member : GroupManager.getSelectedGroup().getMembers()) {
        if (member.getStatus() == null || !mapview.isBlockWithinView(member.getStatus().getX(), member.getStatus().getZ(), false) ||
          member.getLastStatusUpdate() + 30000L < System.currentTimeMillis() || member.getUuid().equals(Client.getUniqueID()))
          continue;
        double scale = mapview.getDimensionScaling(mapview.getDimension());
        Point2D.Double point = mapmode.getClampedScreenXY(mapview, member.getStatus().getX() * scale, member.getStatus().getZ() * scale);
        CachedSkin skin = SkinCache.getInstance().getCachedSkin(member.getUuid());
        int width = 8;
        int height = 8;
        GlStateManager.pushMatrix();
        float angle = mapmode.config.rotate ? (MapWriterMod.getInstance()).mapRotationDegrees : 0.0F;
        GlStateManager.translate(point.x, point.y, 0.0D);
        GlStateManager.translate(-0.5F, -0.5F, 0.0F);
        if ((MapWriter.getInstance()).rotate && (MapWriter.getInstance()).circular)
          GlStateManager.rotate(-angle, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(0.5F, 0.5F, 0.0F);
        RenderUtils.drawCustomSizedResource(skin.getResourceLocation(), -width / 2.0D, -height / 2.0D, width, height);
        GlStateManager.scale(0.5F, 0.5F, 0.0F);
        RenderUtils.drawCenteredString(UsernameCache.getInstance().getUsername(member.getUuid()), 0.0F, 8.0F, 16777215);
        GlStateManager.popMatrix();
      }
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getStatusString(int dim, int bX, int bY, int bZ) {
    return null;
  }

  public ArrayList<IMwChunkOverlay> getChunksOverlay(int dim, double centerX, double centerZ, double minX, double minZ, double maxX, double maxZ) {
    return null;
  }

  public void onMiddleClick(int dim, int bX, int bZ, MapView mapview) {}

  public void onDimensionChanged(int dimension, MapView mapview) {}

  public void onMapCenterChanged(double vX, double vZ, MapView mapview) {}

  public void onZoomChanged(int level, MapView mapview) {}

  public void onOverlayActivated(MapView mapview) {}

  public void onOverlayDeactivated(MapView mapview) {}

  public boolean onMouseInput(MapView mapview, MapMode mapmode) {
    return false;
  }

  public static GroupMapProvider getInstance() {
    return (INSTANCE == null) ? new GroupMapProvider() : INSTANCE;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\group\provider\GroupMapProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */