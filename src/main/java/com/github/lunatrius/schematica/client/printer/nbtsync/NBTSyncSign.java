package com.github.lunatrius.schematica.client.printer.nbtsync;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

import java.util.Arrays;

public class NBTSyncSign extends NBTSync {
    public boolean execute(EntityPlayer player, World schematic, BlockPos pos, World mcWorld, BlockPos mcPos) {
        TileEntity tileEntity = schematic.getTileEntity(pos);
        TileEntity mcTileEntity = mcWorld.getTileEntity(mcPos);
        if (tileEntity instanceof TileEntitySign && mcTileEntity instanceof TileEntitySign) {
            IChatComponent[] signText = ((TileEntitySign) tileEntity).signText;
            IChatComponent[] mcSignText = ((TileEntitySign) mcTileEntity).signText;
            if (!Arrays.equals((Object[]) signText, (Object[]) mcSignText))
                return sendPacket(new C12PacketUpdateSign(mcPos, signText));
        }
        return false;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\printer\nbtsync\NBTSyncSign.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */