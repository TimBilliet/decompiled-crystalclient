package co.crystaldev.client.feature.impl.all;

import co.crystaldev.client.Client;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.tick.ClientTickEvent;
import co.crystaldev.client.feature.annotations.ReloadRenderers;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.Toggle;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Module;
//import co.crystaldev.client.handler.NotificationHandler;
import co.crystaldev.client.handler.NotificationHandler;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;

@ModuleInfo(name = "Farming", description = "Helpful features for farming crops", category = Category.ALL)
public class Farming extends Module implements IRegistrable {
  @Toggle(label = "Avoid Breaking Bottom Cane")
  public boolean avoidBreakingBottomCane = true;
  
  @ReloadRenderers
  @Toggle(label = "Cactus Mode")
  public boolean cactusMode = false;
  
  @ReloadRenderers
  @Toggle(label = "Disable Cactus Rendering")
  public boolean disableCactusRendering = false;
  
  @ReloadRenderers
  @Toggle(label = "Disable String Rendering")
  public boolean disableStringRendering = false;
  
  private static Farming INSTANCE;
  
  private int currentChunkX;
  
  private int currentChunkZ;
  
  private long lastCaneMessage;
  
  private long lastChunkMessage;
  
  public Farming() {
    INSTANCE = this;
  }
  
  public boolean onClickBlock(BlockPos loc) {
    boolean flag = false;
    if (this.enabled && this.avoidBreakingBottomCane) {
      Block block = this.mc.theWorld.getBlockState(loc).getBlock();
      if (block instanceof net.minecraft.block.BlockReed) {
        Block blockBelow = this.mc.theWorld.getBlockState(loc.down()).getBlock();
        if (!(blockBelow instanceof net.minecraft.block.BlockReed)) {
          flag = true;
          if (System.currentTimeMillis() - this.lastCaneMessage > 5000L) {
            this.lastCaneMessage = System.currentTimeMillis();
            NotificationHandler.addNotification("Farming Module", "The client has prevented you from breaking a bottom cane block");
          } 
        } 
      } 
    } 
    return flag;
  }
  
  public void enable() {
    super.enable();
    if (Client.isCallingFromMainThread())
      this.mc.renderGlobal.loadRenderers(); 
  }
  
  public void disable() {
    super.disable();
    if (Client.isCallingFromMainThread())
      this.mc.renderGlobal.loadRenderers(); 
  }
  
  public static Farming getInstance() {
    return INSTANCE;
  }
  
  public void registerEvents() {
    EventBus.register(this, ClientTickEvent.Post.class, ev -> {
          if (this.cactusMode && this.mc.thePlayer != null && (this.mc.thePlayer.chunkCoordX != this.currentChunkX || this.mc.thePlayer.chunkCoordZ != this.currentChunkZ)) {
            this.currentChunkX = this.mc.thePlayer.chunkCoordX;
            this.currentChunkZ = this.mc.thePlayer.chunkCoordZ;
            this.mc.renderGlobal.loadRenderers();
            if (System.currentTimeMillis() - this.lastChunkMessage > 120000L) {
              NotificationHandler.addNotification("Farming Module", "Chunks were reloaded due to you entering a new chunk and the toggle 'Cactus Mode' being enabled", 10000L);
              this.lastChunkMessage = System.currentTimeMillis();
            } 
          } 
        });
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\all\Farming.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */