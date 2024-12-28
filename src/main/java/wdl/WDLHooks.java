package wdl;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemMap;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;
import wdl.api.IBlockEventListener;
import wdl.api.IGuiHooksListener;
import wdl.api.WDLApi;
import wdl.gui.GuiWDL;

import java.util.List;

public class WDLHooks {
  private static final Profiler profiler = (Minecraft.getMinecraft()).mcProfiler;
  
  private static final int WDLs = 1464093811;
  
  private static final int WDLo = 1464093807;
  
  public static void onWorldClientTick(WorldClient sender) {
    try {
      profiler.startSection("wdl");
      ImmutableList immutableList = ImmutableList.copyOf(sender.playerEntities);
      if (sender != WDL.worldClient) {
        profiler.startSection("onWorldLoad");
        if (WDL.worldLoadingDeferred)
          return; 
        WDLEvents.onWorldLoad(sender);
        profiler.endSection();
      } else {
        profiler.startSection("inventoryCheck");
        if (WDL.downloading && WDL.thePlayer != null && 
          WDL.thePlayer.openContainer != WDL.windowContainer) {
          if (WDL.thePlayer.openContainer == WDL.thePlayer.inventoryContainer) {
            profiler.startSection("onItemGuiClosed");
            profiler.startSection("Core");
            boolean handled = WDLEvents.onItemGuiClosed();
            profiler.endSection();
            Container container = WDL.thePlayer.openContainer;
            if (WDL.lastEntity != null) {
              Entity entity = WDL.lastEntity;
              for (WDLApi.ModInfo<IGuiHooksListener> info : (Iterable<WDLApi.ModInfo<IGuiHooksListener>>)WDLApi.getImplementingExtensions(IGuiHooksListener.class)) {
                if (handled)
                  break; 
                profiler.startSection(info.id);
                handled = ((IGuiHooksListener)info.mod).onEntityGuiClosed(sender, entity, container);
                profiler.endSection();
              } 
              if (!handled)
                WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_GUI_CLOSED_WARNING, "wdl.messages.onGuiClosedWarning.unhandledEntity", new Object[] { entity }); 
            } else {
              BlockPos pos = WDL.lastClickedBlock;
              for (WDLApi.ModInfo<IGuiHooksListener> info : (Iterable<WDLApi.ModInfo<IGuiHooksListener>>)WDLApi.getImplementingExtensions(IGuiHooksListener.class)) {
                if (handled)
                  break; 
                profiler.startSection(info.id);
                handled = ((IGuiHooksListener)info.mod).onBlockGuiClosed(sender, pos, container);
                profiler.endSection();
              } 
              if (!handled)
                WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_GUI_CLOSED_WARNING, "wdl.messages.onGuiClosedWarning.unhandledTileEntity", new Object[] { pos, sender
                      
                      .getTileEntity(pos) }); 
            } 
            profiler.endSection();
          } else {
            profiler.startSection("onItemGuiOpened");
            profiler.startSection("Core");
            WDLEvents.onItemGuiOpened();
            profiler.endSection();
            profiler.endSection();
          } 
          WDL.windowContainer = WDL.thePlayer.openContainer;
        } 
        profiler.endSection();
      } 
      profiler.endSection();
    } catch (Throwable e) {
      WDL.crashed(e, "WDL mod: exception in onWorldClientTick event");
    } 
  }
  
  public static void onWorldClientDoPreChunk(WorldClient sender, int x, int z, boolean loading) {
    try {
      if (!WDL.downloading)
        return; 
      profiler.startSection("wdl");
      if (!loading) {
        profiler.startSection("onChunkNoLongerNeeded");
        Chunk c = sender.getChunkFromChunkCoords(x, z);
        profiler.startSection("Core");
        WDLEvents.onChunkNoLongerNeeded(c);
        profiler.endSection();
        profiler.endSection();
      } 
      profiler.endSection();
    } catch (Throwable e) {
      WDL.crashed(e, "WDL mod: exception in onWorldDoPreChunk event");
    } 
  }
  
  public static void onWorldClientRemoveEntityFromWorld(WorldClient sender, int eid) {
    try {
      if (!WDL.downloading)
        return; 
      profiler.startSection("wdl.onRemoveEntityFromWorld");
      Entity entity = sender.getEntityByID(eid);
      profiler.startSection("Core");
      WDLEvents.onRemoveEntityFromWorld(entity);
      profiler.endSection();
      profiler.endSection();
    } catch (Throwable e) {
      WDL.crashed(e, "WDL mod: exception in onWorldRemoveEntityFromWorld event");
    } 
  }
  
  public static void onNHPCHandleMaps(NetHandlerPlayClient sender, S34PacketMaps packet) {
    try {
      if (!Minecraft.getMinecraft().isCallingFromMinecraftThread())
        return; 
      if (!WDL.downloading)
        return; 
      profiler.startSection("wdl.onMapDataLoaded");
      int id = packet.getMapId();
      MapData mapData = ItemMap.loadMapData(packet.getMapId(), (World)WDL.worldClient);
      profiler.startSection("Core");
      WDLEvents.onMapDataLoaded(id, mapData);
      profiler.endSection();
      profiler.endSection();
    } catch (Throwable e) {
      WDL.crashed(e, "WDL mod: exception in onNHPCHandleMaps event");
    } 
  }
  
  public static void onNHPCHandleBlockAction(NetHandlerPlayClient sender, S24PacketBlockAction packet) {
    try {
      if (!Minecraft.getMinecraft().isCallingFromMinecraftThread())
        return; 
      if (!WDL.downloading)
        return; 
      profiler.startSection("wdl.onBlockEvent");
      BlockPos pos = packet.getBlockPosition();
      Block block = packet.getBlockType();
      int data1 = packet.getData1();
      int data2 = packet.getData2();
      profiler.startSection("Core");
      WDLEvents.onBlockEvent(pos, block, data1, data2);
      profiler.endSection();
      for (WDLApi.ModInfo<IBlockEventListener> info : (Iterable<WDLApi.ModInfo<IBlockEventListener>>)WDLApi.getImplementingExtensions(IBlockEventListener.class)) {
        profiler.startSection(info.id);
        ((IBlockEventListener)info.mod).onBlockEvent(WDL.worldClient, pos, block, data1, data2);
        profiler.endSection();
      } 
      profiler.endSection();
    } catch (Throwable e) {
      WDL.crashed(e, "WDL mod: exception in onNHPCHandleBlockAction event");
    } 
  }
  
  public static void injectWDLButtons(GuiIngameMenu gui, List<GuiButton> buttonList) {
    int y = (new ScaledResolution(Minecraft.getMinecraft())).getScaledHeight() - 20;
    GuiButton wdlDownload = new GuiButton(1464093811, 0, y, 170, 20, null);
    GuiButton wdlOptions = new GuiButton(1464093807, 170, y, 28, 20, I18n.format("wdl.gui.ingameMenu.settings", new Object[0]));
    if (WDL.minecraft.isIntegratedServerRunning()) {
      wdlDownload
        .displayString = I18n.format("wdl.gui.ingameMenu.downloadStatus.singlePlayer", new Object[0]);
      wdlDownload.enabled = false;
    } else if (WDL.saving) {
      wdlDownload
        .displayString = I18n.format("wdl.gui.ingameMenu.downloadStatus.saving", new Object[0]);
      wdlDownload.enabled = false;
      wdlOptions.enabled = false;
    } else if (WDL.downloading) {
      wdlDownload
        .displayString = I18n.format("wdl.gui.ingameMenu.downloadStatus.stop", new Object[0]);
    } else {
      wdlDownload
        .displayString = I18n.format("wdl.gui.ingameMenu.downloadStatus.start", new Object[0]);
    } 
    buttonList.add(wdlDownload);
    buttonList.add(wdlOptions);
  }
  
  public static void handleWDLButtonClick(GuiIngameMenu gui, GuiButton button) {
    if (!button.enabled)
      return; 
    if (button.id == 1464093811) {
      if (WDL.minecraft.isIntegratedServerRunning())
        return; 
      if (WDL.downloading) {
        WDL.stopDownload();
      } else {
        WDL.startDownload();
      } 
    } else if (button.id == 1464093807) {
      WDL.minecraft.displayGuiScreen((GuiScreen)new GuiWDL((GuiScreen)gui));
    } else if (button.id == 1) {
      WDL.stopDownload();
    } 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\WDLHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */