package wdl;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.*;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;
import wdl.api.IWorldLoadListener;
import wdl.api.WDLApi;

public class WDLEvents {
  private static final Profiler profiler = (Minecraft.getMinecraft()).mcProfiler;
  
  public static void onWorldLoad(WorldClient world) {
    profiler.startSection("Core");
    if (WDL.minecraft.isIntegratedServerRunning())
      return; 
    if (WDL.downloading) {
      if (!WDL.saving) {
        WDLMessages.chatMessageTranslated(WDLMessageTypes.INFO, "wdl.messages.generalInfo.worldChanged", new Object[0]);
        WDL.worldLoadingDeferred = true;
        WDL.startSaveThread();
      } 
      profiler.endSection();
      return;
    } 
    boolean sameServer = WDL.loadWorld();
    profiler.endSection();
    for (WDLApi.ModInfo<IWorldLoadListener> info : (Iterable<WDLApi.ModInfo<IWorldLoadListener>>)WDLApi.getImplementingExtensions(IWorldLoadListener.class)) {
      profiler.startSection(info.id);
      ((IWorldLoadListener)info.mod).onWorldLoad(world, sameServer);
      profiler.endSection();
    } 
  }
  
  public static void onChunkNoLongerNeeded(Chunk unneededChunk) {
    if (!WDL.downloading)
      return; 
    if (unneededChunk == null)
      return; 
    WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_CHUNK_NO_LONGER_NEEDED, "wdl.messages.onChunkNoLongerNeeded.saved", new Object[] { Integer.valueOf(unneededChunk.xPosition), Integer.valueOf(unneededChunk.zPosition) });
    WDL.saveChunk(unneededChunk);
  }
  
  public static void onItemGuiOpened() {
    if (!WDL.downloading)
      return; 
    if (WDL.minecraft.objectMouseOver == null)
      return; 
    if (WDL.minecraft.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
      WDL.lastEntity = WDL.minecraft.objectMouseOver.entityHit;
    } else {
      WDL.lastEntity = null;
      WDL.lastClickedBlock = WDL.minecraft.objectMouseOver.getBlockPos();
    } 
  }
  
  public static boolean onItemGuiClosed() {
    if (!WDL.downloading)
      return true; 
    String saveName = "";
    if (WDL.thePlayer.ridingEntity != null && WDL.thePlayer.ridingEntity instanceof EntityHorse)
      if (WDL.windowContainer instanceof net.minecraft.inventory.ContainerHorseInventory) {
        EntityHorse horseInContainer = ReflectionUtils.<EntityHorse>stealAndGetField(WDL.windowContainer, EntityHorse.class);
        if (horseInContainer == WDL.thePlayer.ridingEntity) {
          EntityHorse entityHorse = (EntityHorse)WDL.thePlayer.ridingEntity;
          AnimalChest horseChest = new AnimalChest("HorseChest", (entityHorse.isChested() && (entityHorse.getHorseType() == 1 || entityHorse.getHorseType() == 2)) ? 17 : 2);
          WDL.saveContainerItems(WDL.windowContainer, (IInventory)horseChest, 0);
          horseChest.addInventoryChangeListener((IInvBasic)entityHorse);
          ReflectionUtils.stealAndSetField(entityHorse, AnimalChest.class, horseChest);
          WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_GUI_CLOSED_INFO, "wdl.messages.onGuiClosedInfo.savedRiddenHorse", new Object[0]);
          return true;
        } 
      }  
    if (WDL.lastEntity != null) {
      if (WDL.lastEntity instanceof EntityMinecartChest && WDL.windowContainer instanceof net.minecraft.inventory.ContainerChest) {
        EntityMinecartChest emcc = (EntityMinecartChest)WDL.lastEntity;
        for (int i = 0; i < emcc.getSizeInventory(); i++)
          emcc.setInventorySlotContents(i, WDL.windowContainer
              .getSlot(i).getStack()); 
        saveName = "storageMinecart";
      } else if (WDL.lastEntity instanceof EntityMinecartHopper && WDL.windowContainer instanceof net.minecraft.inventory.ContainerHopper) {
        EntityMinecartHopper emch = (EntityMinecartHopper)WDL.lastEntity;
        for (int i = 0; i < emch.getSizeInventory(); i++)
          emch.setInventorySlotContents(i, WDL.windowContainer
              .getSlot(i).getStack()); 
        saveName = "hopperMinecart";
      } else if (WDL.lastEntity instanceof EntityVillager && WDL.windowContainer instanceof net.minecraft.inventory.ContainerMerchant) {
        EntityVillager ev = (EntityVillager)WDL.lastEntity;
        MerchantRecipeList list = ((IMerchant)ReflectionUtils.<IMerchant>stealAndGetField(WDL.windowContainer, IMerchant.class)).getRecipes((EntityPlayer)WDL.thePlayer);
        ReflectionUtils.stealAndSetField(ev, MerchantRecipeList.class, list);
        saveName = "villager";
      } else if (WDL.lastEntity instanceof EntityHorse && WDL.windowContainer instanceof net.minecraft.inventory.ContainerHorseInventory) {
        EntityHorse entityHorse = (EntityHorse)WDL.lastEntity;
        AnimalChest horseChest = new AnimalChest("HorseChest", (entityHorse.isChested() && (entityHorse.getHorseType() == 1 || entityHorse.getHorseType() == 2)) ? 17 : 2);
        WDL.saveContainerItems(WDL.windowContainer, (IInventory)horseChest, 0);
        horseChest.addInventoryChangeListener((IInvBasic)entityHorse);
        ReflectionUtils.stealAndSetField(entityHorse, AnimalChest.class, horseChest);
        saveName = "horse";
      } else {
        return false;
      } 
      WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_GUI_CLOSED_INFO, "wdl.messages.onGuiClosedInfo.savedEntity." + saveName, new Object[0]);
      return true;
    } 
    TileEntity te = WDL.worldClient.getTileEntity(WDL.lastClickedBlock);
    if (te == null) {
      WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_GUI_CLOSED_WARNING, "wdl.messages.onGuiClosedWarning.couldNotGetTE", new Object[] { WDL.lastClickedBlock });
      return true;
    } 
    if (WDL.windowContainer instanceof net.minecraft.inventory.ContainerChest && te instanceof TileEntityChest) {
      if (WDL.windowContainer.inventorySlots.size() > 63) {
        BlockPos pos1 = WDL.lastClickedBlock;
        TileEntity te1 = te;
        BlockPos chestPos1 = null, chestPos2 = null;
        TileEntityChest chest1 = null, chest2 = null;
        BlockPos pos2 = pos1.add(0, 0, 1);
        TileEntity te2 = WDL.worldClient.getTileEntity(pos2);
        if (te2 instanceof TileEntityChest && ((TileEntityChest)te2)
          .getChestType() == ((TileEntityChest)te1)
          .getChestType()) {
          chest1 = (TileEntityChest)te1;
          chest2 = (TileEntityChest)te2;
          chestPos1 = pos1;
          chestPos2 = pos2;
        } 
        pos2 = pos1.add(0, 0, -1);
        te2 = WDL.worldClient.getTileEntity(pos2);
        if (te2 instanceof TileEntityChest && ((TileEntityChest)te2)
          .getChestType() == ((TileEntityChest)te1)
          .getChestType()) {
          chest1 = (TileEntityChest)te2;
          chest2 = (TileEntityChest)te1;
          chestPos1 = pos2;
          chestPos2 = pos1;
        } 
        pos2 = pos1.add(1, 0, 0);
        te2 = WDL.worldClient.getTileEntity(pos2);
        if (te2 instanceof TileEntityChest && ((TileEntityChest)te2)
          .getChestType() == ((TileEntityChest)te1)
          .getChestType()) {
          chest1 = (TileEntityChest)te1;
          chest2 = (TileEntityChest)te2;
          chestPos1 = pos1;
          chestPos2 = pos2;
        } 
        pos2 = pos1.add(-1, 0, 0);
        te2 = WDL.worldClient.getTileEntity(pos2);
        if (te2 instanceof TileEntityChest && ((TileEntityChest)te2)
          .getChestType() == ((TileEntityChest)te1)
          .getChestType()) {
          chest1 = (TileEntityChest)te2;
          chest2 = (TileEntityChest)te1;
          chestPos1 = pos2;
          chestPos2 = pos1;
        } 
        if (chest1 == null || chest2 == null || chestPos1 == null || chestPos2 == null) {
          WDLMessages.chatMessageTranslated(WDLMessageTypes.ERROR, "wdl.messages.onGuiClosedWarning.failedToFindDoubleChest", new Object[0]);
          return true;
        } 
        WDL.saveContainerItems(WDL.windowContainer, (IInventory)chest1, 0);
        WDL.saveContainerItems(WDL.windowContainer, (IInventory)chest2, 27);
        WDL.saveTileEntity(chestPos1, (TileEntity)chest1);
        WDL.saveTileEntity(chestPos2, (TileEntity)chest2);
        saveName = "doubleChest";
      } else {
        WDL.saveContainerItems(WDL.windowContainer, (IInventory)te, 0);
        WDL.saveTileEntity(WDL.lastClickedBlock, te);
        saveName = "singleChest";
      } 
    } else if (WDL.windowContainer instanceof net.minecraft.inventory.ContainerChest && te instanceof net.minecraft.tileentity.TileEntityEnderChest) {
      InventoryEnderChest inventoryEnderChest = WDL.thePlayer.getInventoryEnderChest();
      int inventorySize = inventoryEnderChest.getSizeInventory();
      int containerSize = WDL.windowContainer.inventorySlots.size();
      for (int i = 0; i < containerSize && i < inventorySize; i++)
        inventoryEnderChest.setInventorySlotContents(i, WDL.windowContainer
            .getSlot(i).getStack()); 
      saveName = "enderChest";
    } else if (WDL.windowContainer instanceof net.minecraft.inventory.ContainerBrewingStand && te instanceof net.minecraft.tileentity.TileEntityBrewingStand) {
      IInventory brewingInventory = ReflectionUtils.<IInventory>stealAndGetField(WDL.windowContainer, IInventory.class);
      WDL.saveContainerItems(WDL.windowContainer, (IInventory)te, 0);
      WDL.saveInventoryFields(brewingInventory, (IInventory)te);
      WDL.saveTileEntity(WDL.lastClickedBlock, te);
      saveName = "brewingStand";
    } else if (WDL.windowContainer instanceof net.minecraft.inventory.ContainerDispenser && te instanceof net.minecraft.tileentity.TileEntityDispenser) {
      WDL.saveContainerItems(WDL.windowContainer, (IInventory)te, 0);
      WDL.saveTileEntity(WDL.lastClickedBlock, te);
      saveName = "dispenser";
    } else if (WDL.windowContainer instanceof net.minecraft.inventory.ContainerFurnace && te instanceof net.minecraft.tileentity.TileEntityFurnace) {
      IInventory furnaceInventory = ReflectionUtils.<IInventory>stealAndGetField(WDL.windowContainer, IInventory.class);
      WDL.saveContainerItems(WDL.windowContainer, (IInventory)te, 0);
      WDL.saveInventoryFields(furnaceInventory, (IInventory)te);
      WDL.saveTileEntity(WDL.lastClickedBlock, te);
      saveName = "furnace";
    } else if (WDL.windowContainer instanceof net.minecraft.inventory.ContainerHopper && te instanceof net.minecraft.tileentity.TileEntityHopper) {
      WDL.saveContainerItems(WDL.windowContainer, (IInventory)te, 0);
      WDL.saveTileEntity(WDL.lastClickedBlock, te);
      saveName = "hopper";
    } else if (WDL.windowContainer instanceof ContainerBeacon && te instanceof TileEntityBeacon) {
      IInventory beaconInventory = ((ContainerBeacon)WDL.windowContainer).func_180611_e();//getTileEntity
      TileEntityBeacon savedBeacon = (TileEntityBeacon)te;
      WDL.saveContainerItems(WDL.windowContainer, (IInventory)savedBeacon, 0);
      WDL.saveInventoryFields(beaconInventory, (IInventory)savedBeacon);
      WDL.saveTileEntity(WDL.lastClickedBlock, te);
      saveName = "beacon";
    } else {
      return false;
    } 
    WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_GUI_CLOSED_INFO, "wdl.messages.onGuiClosedInfo.savedTileEntity." + saveName, new Object[0]);
    return true;
  }
  
  public static void onBlockEvent(BlockPos pos, Block block, int event, int param) {
    if (!WDL.downloading)
      return; 
    if (block == Blocks.noteblock) {
      TileEntityNote newTE = new TileEntityNote();
      newTE.note = (byte)(param % 25);
      WDL.worldClient.setTileEntity(pos, (TileEntity)newTE);
      WDL.saveTileEntity(pos, (TileEntity)newTE);
      WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_BLOCK_EVENT, "wdl.messages.onBlockEvent.noteblock", new Object[] { pos, 
            Integer.valueOf(param), newTE });
    } 
  }
  
  public static void onMapDataLoaded(int mapID, MapData mapData) {
    if (!WDL.downloading)
      return; 
    WDL.newMapDatas.put(Integer.valueOf(mapID), mapData);
    WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_MAP_SAVED, "wdl.messages.onMapSaved", new Object[] { Integer.valueOf(mapID) });
  }
  
  public static void onRemoveEntityFromWorld(Entity entity) {
    if (WDL.downloading && entity != null) {
      int threshold = EntityUtils.getEntityTrackDistance(entity);
      if (threshold < 0) {
        WDLMessages.chatMessageTranslated(WDLMessageTypes.REMOVE_ENTITY, "wdl.messages.removeEntity.allowingRemoveUnrecognizedDistance", new Object[] { entity });
        return;
      } 
      double distance = entity.getDistance(WDL.thePlayer.posX, entity.posY, WDL.thePlayer.posZ);
      if (distance > threshold) {
        WDLMessages.chatMessageTranslated(WDLMessageTypes.REMOVE_ENTITY, "wdl.messages.removeEntity.savingDistance", new Object[] { entity, 
              
              Double.valueOf(distance), Integer.valueOf(threshold) });
        entity
          .chunkCoordX = MathHelper.floor_double(entity.posX / 16.0D);
        entity
          .chunkCoordZ = MathHelper.floor_double(entity.posZ / 16.0D);
        WDL.newEntities.put(new ChunkCoordIntPair(entity.chunkCoordX, entity.chunkCoordZ), entity);
        return;
      } 
      WDLMessages.chatMessageTranslated(WDLMessageTypes.REMOVE_ENTITY, "wdl.messages.removeEntity.allowingRemoveDistance", new Object[] { entity, 
            
            Double.valueOf(distance), Integer.valueOf(threshold) });
    } 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\WDLEvents.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */