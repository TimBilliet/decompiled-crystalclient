package com.github.lunatrius.schematica.handler.client;

import co.crystaldev.client.event.SubscribeEvent;
import co.crystaldev.client.event.impl.player.InputEvent;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import com.github.lunatrius.schematica.reference.Reference;
import com.github.lunatrius.schematica.util.Hooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3i;
import net.minecraft.world.World;

public class InputHandler {
  public static InputHandler INSTANCE;
  
  private final Minecraft minecraft = Minecraft.getMinecraft();
  
  public InputHandler() {
    INSTANCE = this;
  }
  
  @SubscribeEvent
  private void onMouseClick(InputEvent.Mouse event) {
    handlePickBlock();
  }
  
  private void handlePickBlock() {
    KeyBinding keyPickBlock = this.minecraft.gameSettings.keyBindPickBlock;
    if (keyPickBlock.isPressed())
      try {
        SchematicWorld schematic = ClientProxy.currentSchematic.schematic;
        boolean revert = true;
        if (schematic != null && schematic.isRendering)
          revert = pickBlock(schematic, ClientProxy.movingObjectPosition); 
        if (revert)
          KeyBinding.onTick(keyPickBlock.getKeyCode()); 
      } catch (Exception e) {
        Reference.logger.error("Could not pick block!", e);
      }  
  }
  
  private boolean pickBlock(SchematicWorld schematic, MovingObjectPosition objectMouseOver) {
    boolean revert = false;
    if (objectMouseOver != null) {
      EntityPlayerSP player = this.minecraft.thePlayer;
      if (objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.MISS)
        revert = true; 
      MovingObjectPosition mcObjectMouseOver = this.minecraft.objectMouseOver;
      if (mcObjectMouseOver != null && mcObjectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && 
        mcObjectMouseOver.getBlockPos().subtract((Vec3i)schematic.position).equals(objectMouseOver.getBlockPos()))
        return true; 
      if (!Hooks.onPickBlock(objectMouseOver, (EntityPlayer)player, (World)schematic))
        return revert; 
      if (player.capabilities.isCreativeMode) {
        int slot = player.inventoryContainer.inventorySlots.size() - 9 + player.inventory.currentItem;
        this.minecraft.playerController.sendSlotPacket(player.inventory.getStackInSlot(player.inventory.currentItem), slot);
      } 
    } 
    return revert;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\handler\client\InputHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */