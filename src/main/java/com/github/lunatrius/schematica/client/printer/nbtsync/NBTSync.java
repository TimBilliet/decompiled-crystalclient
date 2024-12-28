package com.github.lunatrius.schematica.client.printer.nbtsync;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public abstract class NBTSync {
  protected final Minecraft minecraft = Minecraft.getMinecraft();
  
  public abstract boolean execute(EntityPlayer paramEntityPlayer, World paramWorld1, BlockPos paramBlockPos1, World paramWorld2, BlockPos paramBlockPos2);
  
  public final <T extends net.minecraft.network.INetHandler> boolean sendPacket(Packet<T> packet) {
    NetHandlerPlayClient netHandler = this.minecraft.getNetHandler();
    if (netHandler == null)
      return false; 
    netHandler.addToSendQueue(packet);
    return true;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\printer\nbtsync\NBTSync.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */