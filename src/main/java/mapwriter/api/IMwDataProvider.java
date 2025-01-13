package mapwriter.api;

import mapwriter.map.MapView;
import mapwriter.map.mapmode.MapMode;

import java.util.ArrayList;

public interface IMwDataProvider {
    ArrayList<IMwChunkOverlay> getChunksOverlay(int paramInt, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6);

    String getName();

    void setName(String paramString);

    String getStatusString(int paramInt1, int paramInt2, int paramInt3, int paramInt4);

    void onMiddleClick(int paramInt1, int paramInt2, int paramInt3, MapView paramMapView);

    void onDimensionChanged(int paramInt, MapView paramMapView);

    void onMapCenterChanged(double paramDouble1, double paramDouble2, MapView paramMapView);

    void onZoomChanged(int paramInt, MapView paramMapView);

    void onOverlayActivated(MapView paramMapView);

    void onOverlayDeactivated(MapView paramMapView);

    void onDraw(MapView paramMapView, MapMode paramMapMode);

    @Deprecated
    boolean onMouseInput(MapView paramMapView, MapMode paramMapMode);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwriter\api\IMwDataProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */