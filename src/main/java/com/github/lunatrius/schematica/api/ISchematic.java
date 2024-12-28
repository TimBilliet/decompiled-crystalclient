package com.github.lunatrius.schematica.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;

import java.util.List;

public interface ISchematic {
  IBlockState getBlockState(BlockPos paramBlockPos);
  
  boolean setBlockState(BlockPos paramBlockPos, IBlockState paramIBlockState);
  
  TileEntity getTileEntity(BlockPos paramBlockPos);
  
  List<TileEntity> getTileEntities();
  
  void setTileEntity(BlockPos paramBlockPos, TileEntity paramTileEntity);
  
  void removeTileEntity(BlockPos paramBlockPos);
  
  List<Entity> getEntities();
  
  void addEntity(Entity paramEntity);
  
  void removeEntity(Entity paramEntity);
  
  ItemStack getIcon();
  
  void setIcon(ItemStack paramItemStack);
  
  int getWidth();
  
  int getLength();
  
  int getHeight();
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\api\ISchematic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */