//package co.crystaldev.client.feature.impl.all;
//
//import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
//import co.crystaldev.client.feature.annotations.properties.Toggle;
//import co.crystaldev.client.feature.base.Category;
//import co.crystaldev.client.feature.base.HudModule;
//import co.crystaldev.client.util.enums.AnchorRegion;
//import co.crystaldev.client.util.objects.ModulePosition;
//import net.minecraft.client.network.NetHandlerPlayClient;
//
//import java.lang.ref.WeakReference;
//
//@ModuleInfo(name = "Replay Mod", description = "Record, relive and share your experience.", category = Category.ALL)
//public class ReplayModFeature extends HudModule {
//  @Toggle(label = "Show Recording Indicator")
//  public boolean showRecordingIndicator = true;
//
//  private static ReplayModFeature INSTANCE;
//
//  public WeakReference<NetHandlerPlayClient> netHandler = null;
//
//  public ReplayModFeature() {
//    this.enabled = true;
//    this.priority *= 2;
//    this.position = new ModulePosition(AnchorRegion.TOP_LEFT, 5.0F, 5.0F);
//    INSTANCE = this;
//  }
//
//  public void draw() {
//    if (this.showRecordingIndicator);
//  }
//
//  public static ReplayModFeature getInstance() {
//    return INSTANCE;
//  }
//}
//
//
///* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\all\ReplayModFeature.class
// * Java compiler version: 8 (52.0)
// * JD-Core Version:       1.1.3
// */