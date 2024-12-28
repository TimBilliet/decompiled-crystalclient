package com.github.lunatrius.core.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.*;

public class MBlockPos extends BlockPos {
  public int x;
  
  public int y;
  
  public int z;
  
  public MBlockPos() {
    this(0, 0, 0);
  }
  
  public MBlockPos(Entity source) {
    this(source.posX, source.posY, source.posZ);
  }
  
  public MBlockPos(Vec3 source) {
    this(source.xCoord, source.yCoord, source.zCoord);
  }
  
  public MBlockPos(Vec3i source) {
    this(source.getX(), source.getY(), source.getZ());
  }
  
  public MBlockPos(double x, double y, double z) {
    this(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
  }
  
  public MBlockPos(int x, int y, int z) {
    super(0, 0, 0);
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  public MBlockPos set(Entity source) {
    return set(source.posX, source.posY, source.posZ);
  }
  
  public MBlockPos set(Vec3 source) {
    return set(source.xCoord, source.yCoord, source.zCoord);
  }
  
  public MBlockPos set(Vec3i source) {
    return set(source.getX(), source.getY(), source.getZ());
  }
  
  public MBlockPos set(double x, double y, double z) {
    return set(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
  }
  
  public MBlockPos set(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
    return this;
  }
  
  public MBlockPos add(Vec3i vec) {
    return add(vec.getX(), vec.getY(), vec.getZ());
  }
  
  public MBlockPos add(double x, double y, double z) {
    return add(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
  }
  
  public MBlockPos add(int x, int y, int z) {
    return new MBlockPos(this.x + x, this.y + y, this.z + z);
  }
  
  public MBlockPos multiply(int factor) {
    return new MBlockPos(this.x * factor, this.y * factor, this.z * factor);
  }
  
  public MBlockPos subtract(Vec3i vec) {
    return subtract(vec.getX(), vec.getY(), vec.getZ());
  }
  
  public MBlockPos subtract(double x, double y, double z) {
    return subtract(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
  }
  
  public MBlockPos subtract(int x, int y, int z) {
    return new MBlockPos(this.x - x, this.y - y, this.z - z);
  }
  
  public MBlockPos up() {
    return up(1);
  }
  
  public MBlockPos up(int n) {
    return offset(EnumFacing.UP, n);
  }
  
  public MBlockPos down() {
    return down(1);
  }
  
  public MBlockPos down(int n) {
    return offset(EnumFacing.DOWN, n);
  }
  
  public MBlockPos north() {
    return north(1);
  }
  
  public MBlockPos north(int n) {
    return offset(EnumFacing.NORTH, n);
  }
  
  public MBlockPos south() {
    return south(1);
  }
  
  public MBlockPos south(int n) {
    return offset(EnumFacing.SOUTH, n);
  }
  
  public MBlockPos west() {
    return west(1);
  }
  
  public MBlockPos west(int n) {
    return offset(EnumFacing.WEST, n);
  }
  
  public MBlockPos east() {
    return east(1);
  }
  
  public MBlockPos east(int n) {
    return offset(EnumFacing.EAST, n);
  }
  
  public MBlockPos offset(EnumFacing facing) {
    return offset(facing, 1);
  }
  
  public MBlockPos offset(EnumFacing facing, int n) {
    return new MBlockPos(this.x + facing.getFrontOffsetX() * n, this.y + facing.getFrontOffsetY() * n, this.z + facing.getFrontOffsetZ() * n);
  }
  
  public MBlockPos crossProduct(Vec3i vec) {
    return new MBlockPos(this.y * vec.getZ() - this.z * vec.getY(), this.z * vec.getX() - this.x * vec.getZ(), this.x * vec.getY() - this.y * vec.getX());
  }
  
  public int getX() {
    return this.x;
  }
  
  public int getY() {
    return this.y;
  }
  
  public int getZ() {
    return this.z;
  }
  
  public String toString() {
    return "X: " + getX() + ", Y: " + getY() + ", Z: " + getZ();
  }
  
  @Deprecated
  public static Iterable<MBlockPos> getAllInRange(BlockPos from, BlockPos to) {
    return BlockPosHelper.getAllInBox(from, to);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\cor\\util\MBlockPos.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */