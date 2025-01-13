package com.github.lunatrius.schematica.client.printer.registry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public interface IValidPlayerFacing {
    boolean isValid(IBlockState paramIBlockState, EntityPlayer paramEntityPlayer, BlockPos paramBlockPos, World paramWorld);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\printer\registry\IValidPlayerFacing.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */