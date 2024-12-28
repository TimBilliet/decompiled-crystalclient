package mapwriter.map;

import co.crystaldev.client.feature.impl.hud.MapWriter;
import mapwriter.MapWriterMod;
import mapwriter.api.IMwChunkOverlay;
import mapwriter.api.MwAPI;
import mapwriter.map.mapmode.MapMode;
import mapwriter.util.Reference;
import mapwriter.util.Render;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class MapRenderer {
  private final MapWriterMod mapWriterMod;
  
  private final MapMode mapMode;
  
  private final MapView mapView;
  
  private final Minecraft mc = Minecraft.getMinecraft();
  
  public Point2D.Double playerArrowScreenPos = new Point2D.Double(0.0D, 0.0D);
  
  private long lastRequestTime = 0L;
  
  private MapViewRequest lastRequest = null;
  
  private int lastZoom = -1000;
  
  public MapRenderer(MapWriterMod mapWriterMod, MapMode mapMode, MapView mapView) {
    this.mapWriterMod = mapWriterMod;
    this.mapMode = mapMode;
    this.mapView = mapView;
  }
  
  private void drawMap() {
    double u, v, w, h;
    int regionZoomLevel = Math.max(0, this.mapView.getZoomLevel());
    double tSize = this.mapWriterMod.textureSize;
    double zoomScale = (1 << regionZoomLevel);
    if (!this.mapMode.config.circular && this.mapView.getZoomLevel() >= 0) {
      u = Math.round(this.mapView.getMinX() / zoomScale) / tSize % 1.0D;
      v = Math.round(this.mapView.getMinZ() / zoomScale) / tSize % 1.0D;
      w = Math.round(this.mapView.getWidth() / zoomScale) / tSize;
      h = Math.round(this.mapView.getHeight() / zoomScale) / tSize;
    } else {
      double tSizeInBlocks = tSize * zoomScale;
      u = this.mapView.getMinX() / tSizeInBlocks % 1.0D;
      v = this.mapView.getMinZ() / tSizeInBlocks % 1.0D;
      w = this.mapView.getWidth() / tSizeInBlocks;
      h = this.mapView.getHeight() / tSizeInBlocks;
    } 
    GlStateManager.pushMatrix();
    if (this.mapMode.config.rotate && this.mapMode.config.circular)
      GlStateManager.rotate(this.mapWriterMod.mapRotationDegrees, 0.0F, 0.0F, 1.0F); 
    if (this.mapMode.config.circular)
      Render.setCircularStencil(0.0D, 0.0D, this.mapMode.h / 2.0D); 
    MapViewRequest req = requestView();
    String backgroundMode = (MapWriter.getInstance()).backgroundMode;
    if (!backgroundMode.equalsIgnoreCase("none")) {
      double bu1 = 0.0D;
      double bu2 = 1.0D;
      double bv1 = 0.0D;
      double bv2 = 1.0D;
      if (backgroundMode.equalsIgnoreCase("planning")) {
        double bSize = tSize / 256.0D;
        bu1 = u * bSize;
        bu2 = (u + w) * bSize;
        bv1 = v * bSize;
        bv2 = (v + h) * bSize;
      } 
      this.mapWriterMod.mc.getTextureManager().bindTexture(Reference.backgroundTexture);
      Render.setColourWithAlphaPercent(16777215, this.mapMode.config.alphaPercent);
      Render.drawTexturedRect(this.mapMode.x, this.mapMode.y, this.mapMode.w, this.mapMode.h, bu1, bv1, bu2, bv2);
    } else {
      Render.setColourWithAlphaPercent(0, this.mapMode.config.alphaPercent);
      Render.drawRect(this.mapMode.x, this.mapMode.y, this.mapMode.w, this.mapMode.h);
    } 
    if (this.mapWriterMod.mapTexture.isLoaded(req)) {
      this.mapWriterMod.mapTexture.bind();
      Render.resetColour();
      Render.drawTexturedRect(this.mapMode.x, this.mapMode.y, this.mapMode.w, this.mapMode.h, u, v, u + w, v + h);
    } 
    MwAPI.getEnabledDataProviders().forEach(provider -> {
          ArrayList<IMwChunkOverlay> overlays = provider.getChunksOverlay(this.mapView.getDimension(), this.mapView.getX(), this.mapView.getZ(), this.mapView.getMinX(), this.mapView.getMinZ(), this.mapView.getMaxX(), this.mapView.getMaxZ());
          if (overlays != null)
            for (IMwChunkOverlay overlay : overlays) {
              GlStateManager.pushMatrix();
              overlay.draw(this.mapMode, this.mapView);
              GlStateManager.popMatrix();
            }  
          GlStateManager.pushMatrix();
          provider.onDraw(this.mapView, this.mapMode);
          GlStateManager.popMatrix();
        });
    if (this.mapMode.config.circular)
      Render.disableStencil(); 
    GlStateManager.popMatrix();
  }
  
  private MapViewRequest requestView() {
    MapViewRequest req = new MapViewRequest(this.mapView);
    if (this.lastZoom == -1000)
      this.lastZoom = this.mapView.getZoomLevel(); 
    boolean flag = (this.lastZoom != this.mapView.getZoomLevel());
    if (this.lastRequest != null) {
      int xMinDiff = Math.abs(req.xMin - this.lastRequest.xMin);
      int xMaxDiff = Math.abs(req.xMax - this.lastRequest.xMax);
      int zMinDiff = Math.abs(req.zMin - this.lastRequest.zMin);
      int zMaxDiff = Math.abs(req.zMax - this.lastRequest.zMax);
      flag = (flag || xMinDiff > 16 || xMaxDiff > 16 || zMinDiff > 16 || zMaxDiff > 16);
    } 
    long currentMs = System.currentTimeMillis();
    if (currentMs - this.lastRequestTime > 5000L || this.lastRequest == null || flag) {
      this.mapWriterMod.mapTexture.requestView(req, this.mapWriterMod.executor, this.mapWriterMod.regionManager);
      this.lastRequest = req;
      this.lastRequestTime = currentMs;
      this.lastZoom = this.mapView.getZoomLevel();
    } else {
      req = this.lastRequest;
    } 
    return req;
  }
  
  private void drawBorder() {
    if (this.mapMode.config.circular) {
      this.mapWriterMod.mc.getTextureManager().bindTexture(Reference.roundMapTexture);
    } else {
      this.mapWriterMod.mc.getTextureManager().bindTexture(Reference.squareMapTexture);
    } 
    Render.setColour(-1);
    Render.drawTexturedRect(this.mapMode.x / 0.75D, this.mapMode.y / 0.75D, this.mapMode.w / 0.75D, this.mapMode.h / 0.75D, 0.0D, 0.0D, 1.0D, 1.0D);
  }
  
  private void drawPlayerArrow() {
    GlStateManager.pushMatrix();
    double scale = this.mapView.getDimensionScaling(this.mapWriterMod.playerDimension);
    Point2D.Double p = this.mapMode.getClampedScreenXY(this.mapView, this.mapWriterMod.playerX * scale, this.mapWriterMod.playerZ * scale);
    this.playerArrowScreenPos.setLocation(p.x + this.mapMode.xTranslation, p.y + this.mapMode.yTranslation);
    GlStateManager.translate(p.x, p.y, 0.0D);
    if (!this.mapMode.config.rotate || !this.mapMode.config.circular)
      GlStateManager.rotate(-this.mapWriterMod.mapRotationDegrees, 0.0F, 0.0F, 1.0F); 
    double arrowSize = this.mapMode.config.playerArrowSize;
    Render.setColour(-1);
    this.mapWriterMod.mc.getTextureManager().bindTexture(Reference.playerArrowTexture);
    Render.drawTexturedRect(-arrowSize, -arrowSize, arrowSize * 2.0D, arrowSize * 2.0D, 0.0D, 0.0D, 1.0D, 1.0D);
    GlStateManager.popMatrix();
  }
  
  private void drawIcons() {
    GlStateManager.pushMatrix();
    if (this.mapMode.config.rotate && this.mapMode.config.circular)
      GlStateManager.rotate(this.mapWriterMod.mapRotationDegrees, 0.0F, 0.0F, 1.0F); 
    if (this.mapMode.config.rotate && this.mapMode.config.circular) {
      double y = this.mapMode.h / 2.0D;
      double arrowSize = this.mapMode.config.playerArrowSize;
      Render.setColour(-1);
      this.mapWriterMod.mc.getTextureManager().bindTexture(Reference.northArrowTexture);
      Render.drawTexturedRect(-arrowSize, -y - arrowSize * 2.0D, arrowSize * 2.0D, arrowSize * 2.0D, 0.0D, 0.0D, 1.0D, 1.0D);
    } 
    GlStateManager.popMatrix();
    drawPlayerArrow();
  }
  
  private void drawStatusText() {
    int textX = this.mapMode.textX;
    int textY = this.mapMode.textY;
    if (!(MapWriter.getInstance()).coordsMode.equalsIgnoreCase("disabled")) {
      GlStateManager.pushMatrix();
      GlStateManager.translate(textX, textY, 0.0F);
      if ((MapWriter.getInstance()).coordsMode.equalsIgnoreCase("small"))
        GlStateManager.scale(0.5F, 0.5F, 1.0F); 
      Render.drawCentredString(0, 0, this.mapMode.textColor, "%d, %d, %d", new Object[] { Integer.valueOf(this.mapWriterMod.playerXInt), Integer.valueOf(this.mapWriterMod.playerYInt), Integer.valueOf(this.mapWriterMod.playerZInt) });
      GlStateManager.popMatrix();
    } 
  }
  
  public void draw() {
    boolean isGui = (this.mc.currentScreen instanceof co.crystaldev.client.gui.screens.ScreenMapWriter || this.mapMode instanceof mapwriter.map.mapmode.LargeMapMode);
    this.mapMode.update();
    this.mapView.setMapWH(this.mapMode);
    this.mapView.setTextureSize(this.mapWriterMod.textureSize);
    GlStateManager.pushMatrix();
    GlStateManager.loadIdentity();
    GlStateManager.resetColor();
    ScaledResolution res = (MapWriter.getInstance()).scaledResolution;
    GlStateManager.scale(2.0D / res.getScaleFactor(), 2.0D / res.getScaleFactor(), 1.0D);
    MapWriter hudModule = MapWriter.getInstance();
    if (!isGui)
      GlStateManager.scale(hudModule.scale, hudModule.scale, hudModule.scale); 
    GlStateManager.translate(this.mapMode.xTranslation, this.mapMode.yTranslation, -2000.0D);
    drawMap();
    if (this.mapMode.config.borderMode)
      drawBorder(); 
    drawIcons();
    GlStateManager.enableDepth();
    GlStateManager.popMatrix();
    if (!isGui)
      drawStatusText(); 
  }
  
  public MapMode getMapMode() {
    return this.mapMode;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwriter\map\MapRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */