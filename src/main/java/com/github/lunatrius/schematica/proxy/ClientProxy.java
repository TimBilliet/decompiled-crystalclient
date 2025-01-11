package com.github.lunatrius.schematica.proxy;

import co.crystaldev.client.Client;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.feature.impl.factions.Schematica;
import co.crystaldev.client.feature.settings.ClientOptions;
import co.crystaldev.client.gui.screens.schematica.ScreenSchematicControl;
import co.crystaldev.client.util.objects.Schematic;
import co.crystaldev.client.util.objects.Transformation;
import com.github.lunatrius.core.util.MBlockPos;
import com.github.lunatrius.core.util.vector.Vector3d;
import com.github.lunatrius.schematica.api.ISchematic;
import com.github.lunatrius.schematica.client.printer.SchematicPrinter;
import com.github.lunatrius.schematica.client.renderer.RenderSchematic;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.handler.ConfigurationHandler;
import com.github.lunatrius.schematica.handler.QueueTickHandler;
import com.github.lunatrius.schematica.handler.client.*;
import com.github.lunatrius.schematica.reference.Reference;
import com.github.lunatrius.schematica.util.LoadedSchematic;
import com.github.lunatrius.schematica.world.schematic.SchematicFormat;
import com.github.lunatrius.schematica.world.schematic.SchematicUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ClientProxy extends CommonProxy {
  public static boolean isRenderingGuide = false;
  
  public static boolean isPendingReset = false;
  
  public static final List<LoadedSchematic> loadedSchematics = new LinkedList<>(Arrays.asList(new LoadedSchematic[] { new LoadedSchematic(), new LoadedSchematic(), new LoadedSchematic() }));
  
  public static LoadedSchematic currentSchematic = loadedSchematics.get(0);
  
  public static final Vector3d playerPosition = new Vector3d();
  
  public static EnumFacing orientation = null;
  
  public static int rotationRender = 0;
  
  public static final MBlockPos pointA = new MBlockPos();
  
  public static final MBlockPos pointB = new MBlockPos();
  
  public static final MBlockPos pointMin = new MBlockPos();
  
  public static final MBlockPos pointMax = new MBlockPos();
  
  public static MovingObjectPosition movingObjectPosition = null;
  
  private static final Minecraft MINECRAFT = Minecraft.getMinecraft();
  
  public boolean awaitingChange = false;
  
  public static boolean moveToPlayer = true;
  
  public void onClientTick() {
    if (this.awaitingChange) {
      this.awaitingChange = false;
      RenderSchematic.INSTANCE.setWorldAndLoadRenderers(currentSchematic.schematic);
      SchematicPrinter.INSTANCE.setSchematic(currentSchematic.schematic);
      if (moveToPlayer)
        moveSchematicToPlayer(currentSchematic.schematic, (Schematica.getInstance()).loadAtY1); 
      moveToPlayer = true;
      if (MINECRAFT.currentScreen instanceof ScreenSchematicControl)
        ((ScreenSchematicControl)MINECRAFT.currentScreen).init(); 
      Client.sendMessage("Finished loading schematic", true);
    } 
  }
  
  public static void setPlayerData(EntityPlayer player, float partialTicks) {
    playerPosition.x = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
    playerPosition.y = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
    playerPosition.z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
    orientation = getOrientation(player);
    rotationRender = MathHelper.floor_double((player.rotationYaw / 90.0F)) & 0x3;
  }
  
  private static EnumFacing getOrientation(EntityPlayer player) {
    if (player.rotationPitch > 45.0F)
      return EnumFacing.DOWN; 
    if (player.rotationPitch < -45.0F)
      return EnumFacing.UP; 
    switch (MathHelper.floor_double(player.rotationYaw / 90.0D + 0.5D) & 0x3) {
      case 0:
        return EnumFacing.SOUTH;
      case 1:
        return EnumFacing.WEST;
      case 2:
        return EnumFacing.NORTH;
      case 3:
        return EnumFacing.EAST;
    } 
    return null;
  }
  
  public static void updatePoints() {
    pointMin.x = Math.min(pointA.x, pointB.x);
    pointMin.y = Math.min(pointA.y, pointB.y);
    pointMin.z = Math.min(pointA.z, pointB.z);
    pointMax.x = Math.max(pointA.x, pointB.x);
    pointMax.y = Math.max(pointA.y, pointB.y);
    pointMax.z = Math.max(pointA.z, pointB.z);
  }
  
  public static void movePointToPlayer(MBlockPos point) {
    point.x = (int)Math.floor(playerPosition.x);
    point.y = (int)Math.floor(playerPosition.y);
    point.z = (int)Math.floor(playerPosition.z);
    switch (rotationRender) {
      case 0:
        point.x--;
        point.z++;
        break;
      case 1:
        point.x--;
        point.z--;
        break;
      case 2:
        point.x++;
        point.z--;
        break;
      case 3:
        point.x++;
        point.z++;
        break;
    } 
  }
  
  public static void moveSchematicToPlayer(SchematicWorld schematic) {
    moveSchematicToPlayer(schematic, false);
  }
  
  public static void moveSchematicToPlayer(SchematicWorld schematic, boolean initial) {
    Schematica.getInstance().clearTracerLists();
    if (schematic != null) {
      MBlockPos position = schematic.position;
      position.x = (int)Math.floor(playerPosition.x);
      position.y = initial ? 1 : (int)Math.floor(playerPosition.y);
      position.z = (int)Math.floor(playerPosition.z);
      switch (rotationRender) {
        case 0:
          position.x -= schematic.getWidth();
          position.z++;
          break;
        case 1:
          position.x -= schematic.getWidth();
          position.z -= schematic.getLength();
          break;
        case 2:
          position.x++;
          position.z -= schematic.getLength();
          break;
        case 3:
          position.x++;
          position.z++;
          break;
      } 
    } 
  }
  
  public void init() {
    super.init();
    preInit();
    registerEvents();
    postInit();
  }
  
  public void registerEvents() {
    EventBus.register(InputHandler.INSTANCE);
    EventBus.register(TickHandler.INSTANCE);
    EventBus.register(QueueTickHandler.INSTANCE);
    EventBus.register(RenderTickHandler.INSTANCE);
    EventBus.register(ConfigurationHandler.INSTANCE);
    EventBus.register(GuiHandler.INSTANCE);
    EventBus.register(RenderSchematic.INSTANCE);
    EventBus.register(WorldHandler.INSTANCE);
  }
  
  public void unregisterEvents() {
    EventBus.unregister(InputHandler.INSTANCE);
    EventBus.unregister(TickHandler.INSTANCE);
    EventBus.unregister(QueueTickHandler.INSTANCE);
    EventBus.unregister(RenderTickHandler.INSTANCE);
    EventBus.unregister(ConfigurationHandler.INSTANCE);
    EventBus.unregister(GuiHandler.INSTANCE);
    EventBus.unregister(RenderSchematic.INSTANCE);
    EventBus.unregister(WorldHandler.INSTANCE);
  }
  
  public void preInit() {}
  
  public void postInit() {
    resetSettings();
  }
  
  public File getDataDirectory() {
    File file = MINECRAFT.mcDataDir;
    try {
      return file.getCanonicalFile();
    } catch (IOException e) {
      Reference.logger.debug("Could not canonize path!", e);
      return file;
    } 
  }
  
  public void resetSettings() {
    super.resetSettings();
    SchematicPrinter.INSTANCE.setEnabled(true);
    unloadSchematic();
    playerPosition.set(0.0D, 0.0D, 0.0D);
    orientation = null;
    rotationRender = 0;
    pointA.set(0, 0, 0);
    pointB.set(0, 0, 0);
    updatePoints();
  }
  
  public void unloadSchematic() {
    if (currentSchematic.schematic != null && currentSchematic.currentFile != null) {
      JsonArray transformations = new JsonArray();
      for (Transformation t : currentSchematic.transformations) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", t.getType().toString());
        jsonObject.addProperty("direction", t.getDirection().toString());
        jsonObject.addProperty("x", t.getX());
        jsonObject.addProperty("y", t.getY());
        jsonObject.addProperty("z", t.getZ());
        transformations.add((JsonElement)jsonObject);
      } 
      JsonObject obj = new JsonObject();
      obj.addProperty("name", currentSchematic.currentFile.getName().replaceAll("\\..+", ""));
      obj.addProperty("uploadedAt", System.currentTimeMillis());
      obj.addProperty("x", currentSchematic.schematic.position.x);
      obj.addProperty("y", currentSchematic.schematic.position.y);
      obj.addProperty("z", currentSchematic.schematic.position.z);
      obj.add("transformations", (JsonElement)transformations);
      Schematic schematic = new Schematic(currentSchematic.currentFile, null, currentSchematic.currentFile.getName().split("\\.")[0], obj);
      ClientOptions.getInstance().addSchematicToHistory(schematic);
    } 
    Schematica.getInstance().clearTracerLists();
    currentSchematic.replaceHistory.clear();
    currentSchematic.schematic = null;
    currentSchematic.currentFile = null;
    currentSchematic.transformations.clear();
    currentSchematic.totalBlocks = 0;
    RenderSchematic.INSTANCE.setWorldAndLoadRenderers(null);
    SchematicPrinter.INSTANCE.setSchematic(null);
  }

  public boolean loadSchematic(EntityPlayer player, File directory, String filename) {
    Schematica.getInstance().clearTracerLists();
    currentSchematic.replaceHistory.clear();
    ISchematic schematic = SchematicFormat.readFromFile(directory, filename);
    if (schematic == null)
      return false;
    currentSchematic.currentFile = new File(directory, filename);
    currentSchematic.transformations.clear();
    try {
      NBTTagCompound compound = SchematicUtil.readTagCompoundFromFile(currentSchematic.currentFile);
      byte[] arrayOfByte = compound.getByteArray("Blocks");
      for (byte b : arrayOfByte) {
        if (b != 0) {
          currentSchematic.totalBlocks++;
        }
      }
    } catch (IOException ex) {
      Reference.logger.error("Unable to read tag compound", ex);
    }
    System.out.println("reading blocks complete");
    SchematicWorld world = new SchematicWorld(schematic);
    Reference.logger.debug("Loaded {} [w:{},h:{},l:{}]", filename, world.getWidth(), world.getHeight(), world.getLength());
    currentSchematic.schematic = world;
    world.isRendering = true;
    this.awaitingChange = true;
    System.out.println("returning in loadschematic");
    return true;
  }
  
  public boolean isPlayerQuotaExceeded(EntityPlayer player) {
    return false;
  }
  
  public File getPlayerSchematicDirectory(EntityPlayer player, boolean privateDirectory) {
    return ConfigurationHandler.schematicDirectory;
  }
}