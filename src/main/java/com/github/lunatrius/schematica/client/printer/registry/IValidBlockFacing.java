package com.github.lunatrius.schematica.client.printer.registry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

import java.util.List;

public interface IValidBlockFacing {
  List<EnumFacing> getValidBlockFacings(List<EnumFacing> paramList, IBlockState paramIBlockState);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\printer\registry\IValidBlockFacing.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */