package com.github.lunatrius.schematica.client.printer.nbtsync;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class NBTSyncCommandBlock extends NBTSync {
  public boolean execute(EntityPlayer player, World schematic, BlockPos pos, World mcWorld, BlockPos mcPos) {
    TileEntity tileEntity = schematic.getTileEntity(pos);
    TileEntity mcTileEntity = mcWorld.getTileEntity(mcPos);
    if (tileEntity instanceof TileEntityCommandBlock && mcTileEntity instanceof TileEntityCommandBlock) {
      CommandBlockLogic commandBlockLogic = ((TileEntityCommandBlock)tileEntity).getCommandBlockLogic();
      CommandBlockLogic mcCommandBlockLogic = ((TileEntityCommandBlock)mcTileEntity).getCommandBlockLogic();
      if (!commandBlockLogic.getCommand().equals(mcCommandBlockLogic.getCommand())) {
        PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
//        packetBuffer.writeByte(mcCommandBlockLogic.getCommandBlockType());
        packetBuffer.writeByte(mcCommandBlockLogic.getSuccessCount());
//        mcCommandBlockLogic.fillInInfo((ByteBuf)packetBuffer);
        mcCommandBlockLogic.func_145757_a(packetBuffer);
        packetBuffer.writeString(commandBlockLogic.getCommand());
        packetBuffer.writeBoolean(mcCommandBlockLogic.shouldTrackOutput());
        return sendPacket(new C17PacketCustomPayload("MC|AdvCdm", packetBuffer));
      } 
    } 
    return false;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\printer\nbtsync\NBTSyncCommandBlock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */