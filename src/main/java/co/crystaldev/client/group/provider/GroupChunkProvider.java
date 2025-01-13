package co.crystaldev.client.group.provider;

import co.crystaldev.client.Client;
import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.group.objects.ChunkHighlight;
import co.crystaldev.client.group.objects.ChunkHighlightGrid;
import co.crystaldev.client.group.objects.Group;
import co.crystaldev.client.group.objects.enums.HighlightedChunk;
import co.crystaldev.client.util.objects.dataprovider.GroupChunkHighlight;
import co.crystaldev.client.util.objects.dataprovider.HoveredChunkHighlight;
import mapwriter.api.IMwChunkOverlay;
import mapwriter.api.IMwDataProvider;
import mapwriter.map.MapView;
import mapwriter.map.mapmode.FullscreenMapMode;
import mapwriter.map.mapmode.MapMode;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

public class GroupChunkProvider implements IMwDataProvider {
    private final ArrayList<IMwChunkOverlay> chunkOverlays = new ArrayList<>();

    private final int hoveredColor = (new Color(255, 255, 255, 100)).getRGB();

    private boolean wasFullscreen = false;

    private String name;

    private boolean awaitingUpdate = false;

    public void setAwaitingUpdate(boolean awaitingUpdate) {
        this.awaitingUpdate = awaitingUpdate;
    }

    public ArrayList<IMwChunkOverlay> getChunksOverlay(int dim, double centerX, double centerZ, double minX, double minZ, double maxX, double maxZ) {
        return this.chunkOverlays;
    }

    public void onDraw(MapView mapview, MapMode mapmode) {
        updateChunkOverlay(mapmode);
    }

    public void updateChunkOverlay(MapMode mapmode) {
        if (this.awaitingUpdate) {
            this.awaitingUpdate = false;
            this.chunkOverlays.removeIf(o -> o instanceof GroupChunkHighlight);
            Group sg = GroupManager.getSelectedGroup();
            if (sg != null) {
                String server = Client.formatConnectedServerIp();
                if (server == null)
                    return;
                if (sg.getHighlightedChunks() == null)
                    return;
                for (Map.Entry<String, ChunkHighlightGrid> entry : (Iterable<Map.Entry<String, ChunkHighlightGrid>>) sg.getHighlightedChunks().entrySet()) {
                    if (server.endsWith(entry.getKey())) {
                        ChunkHighlightGrid grid = entry.getValue();
                        for (ChunkHighlight chunk : grid.getHighlightedChunks())
                            this.chunkOverlays.add(new GroupChunkHighlight(chunk.getX(), chunk.getZ(),
                                    (chunk.getType() == HighlightedChunk.COLOR) ? Integer.parseInt(chunk.getData()) : 16777215,
                                    (chunk.getType() == HighlightedChunk.TEXT) ? chunk.getData() : ""));
                        break;
                    }
                }
            }
            this.chunkOverlays.sort(Comparator.comparing(c -> Boolean.valueOf(c instanceof HoveredChunkHighlight)));
        }
        if (mapmode instanceof FullscreenMapMode) {
            FullscreenMapMode fmm = (FullscreenMapMode) mapmode;
            if (!this.wasFullscreen) {
                this.wasFullscreen = true;
                this.chunkOverlays.add(new HoveredChunkHighlight(fmm.getChunkX(), fmm.getChunkZ(), this.hoveredColor));
            } else {
                for (IMwChunkOverlay overlay : this.chunkOverlays) {
                    if (overlay instanceof HoveredChunkHighlight) {
                        HoveredChunkHighlight chunkOverlay = (HoveredChunkHighlight) overlay;
                        chunkOverlay.setCoordinates(fmm.getChunkX(), fmm.getChunkZ());
                    }
                }
            }
        } else if (this.wasFullscreen) {
            this.wasFullscreen = false;
            this.chunkOverlays.removeIf(overlay -> overlay instanceof HoveredChunkHighlight);
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

    public void onMiddleClick(int dim, int bX, int bZ, MapView mapview) {
    }

    public void onDimensionChanged(int dimension, MapView mapview) {
    }

    public void onMapCenterChanged(double vX, double vZ, MapView mapview) {
    }

    public void onZoomChanged(int level, MapView mapview) {
    }

    public void onOverlayActivated(MapView mapview) {
    }

    public void onOverlayDeactivated(MapView mapview) {
    }

    public boolean onMouseInput(MapView mapview, MapMode mapmode) {
        return false;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\group\provider\GroupChunkProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */