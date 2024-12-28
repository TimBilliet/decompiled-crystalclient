package wdl.gui;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import wdl.WDL;

public abstract class GuiTurningCameraBase extends GuiScreen {
  private float yaw;
  
  private float yawNextTick;
  
  private int oldCameraMode;
  
  private boolean oldHideHud;
  
  private boolean oldShowDebug;
  
  private EntityPlayer.EnumChatVisibility oldChatVisibility;
  
  private EntityPlayerSP cam;
  
  private Entity oldRenderViewEntity;
  
  private boolean initializedCamera = false;
  
  private static final float ROTATION_SPEED = 1.0F;
  
  private static final float ROTATION_VARIANCE = 0.7F;
  
  public void initGui() {
    if (!this.initializedCamera) {
      if (WDL.thePlayer != null) {
        this
          .cam = new EntityPlayerSP(WDL.minecraft, (World)WDL.worldClient, WDL.thePlayer.sendQueue, WDL.thePlayer.getStatFileWriter());
        this.cam.setLocationAndAngles(WDL.thePlayer.posX, WDL.thePlayer.posY - WDL.thePlayer
            .getYOffset(), WDL.thePlayer.posZ, WDL.thePlayer.rotationYaw, 0.0F);
        this.yaw = this.yawNextTick = WDL.thePlayer.rotationYaw;
        this.oldCameraMode = WDL.minecraft.gameSettings.thirdPersonView;
        this.oldChatVisibility = WDL.minecraft.gameSettings.chatVisibility;
        WDL.minecraft.gameSettings.thirdPersonView = 0;
        WDL.minecraft.gameSettings.chatVisibility = EntityPlayer.EnumChatVisibility.HIDDEN;
        this.oldRenderViewEntity = WDL.minecraft.getRenderViewEntity();
      } 
      this.initializedCamera = true;
    } 
    if (this.cam != null)
      WDL.minecraft.setRenderViewEntity((Entity)this.cam); 
  }
  
  public void updateScreen() {
    this.yaw = this.yawNextTick;
    this
      
      .yawNextTick = this.yaw + 1.0F * (float)(1.0D + 0.699999988079071D * Math.cos((this.yaw + 45.0F) / 45.0D * Math.PI));
  }
  
  private double truncateDistanceIfBlockInWay(double camX, double camZ, double currentDistance) {
    Vec3 playerPos = WDL.thePlayer.getPositionVector().addVector(0.0D, WDL.thePlayer.getEyeHeight(), 0.0D);
    Vec3 offsetPos = new Vec3(WDL.thePlayer.posX - currentDistance * camX, WDL.thePlayer.posY + WDL.thePlayer.getEyeHeight(), WDL.thePlayer.posZ + camZ);
    if (this.mc.theWorld == null)
      return currentDistance - 0.25D; 
    for (int i = 0; i < 9; i++) {
      float offsetX = ((i & 0x1) != 0) ? -0.1F : 0.1F;
      float offsetY = ((i & 0x2) != 0) ? -0.1F : 0.1F;
      float offsetZ = ((i & 0x4) != 0) ? -0.1F : 0.1F;
      if (i == 8) {
        offsetX = 0.0F;
        offsetY = 0.0F;
        offsetZ = 0.0F;
      } 
      Vec3 from = playerPos.addVector(offsetX, offsetY, offsetZ);
      Vec3 to = offsetPos.addVector(offsetX, offsetY, offsetZ);
      MovingObjectPosition pos = this.mc.theWorld.rayTraceBlocks(from, to);
      if (pos != null) {
        double distance = pos.hitVec.distanceTo(playerPos);
        if (distance < currentDistance && distance > 0.0D)
          currentDistance = distance; 
      } 
    } 
    return currentDistance - 0.25D;
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    if (this.cam != null) {
      float yaw = this.yaw + (this.yawNextTick - this.yaw) * partialTicks;
      this.cam.prevRotationPitch = this.cam.rotationPitch = 0.0F;
      this.cam.prevRotationYaw = this.cam.rotationYaw = yaw;
      double x = Math.cos(yaw / 180.0D * Math.PI);
      double z = Math.sin((yaw - 90.0F) / 180.0D * Math.PI);
      double distance = truncateDistanceIfBlockInWay(x, z, 0.5D);
      this.cam.lastTickPosY = this.cam.prevPosY = this.cam.posY = WDL.thePlayer.posY;
      this.cam.lastTickPosX = this.cam.prevPosX = WDL.thePlayer.posX -= distance * x;
      this.cam.lastTickPosZ = this.cam.prevPosZ = WDL.thePlayer.posZ += distance * z;
    } 
    super.drawScreen(mouseX, mouseY, partialTicks);
  }
  
  public void onGuiClosed() {
    super.onGuiClosed();
    WDL.minecraft.gameSettings.thirdPersonView = this.oldCameraMode;
    WDL.minecraft.gameSettings.chatVisibility = this.oldChatVisibility;
    WDL.minecraft.setRenderViewEntity(this.oldRenderViewEntity);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\gui\GuiTurningCameraBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */