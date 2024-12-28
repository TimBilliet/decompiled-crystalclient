package com.github.lunatrius.core.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3i;

import java.util.Iterator;

public class BlockPosHelper {
  public static Iterable<MBlockPos> getAllInBox(BlockPos from, BlockPos to) {
    return getAllInBox(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ());
  }
  
  public static Iterable<MBlockPos> getAllInBox(int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
    final BlockPos posMin = new BlockPos(Math.min(fromX, toX), Math.min(fromY, toY), Math.min(fromZ, toZ));
    final BlockPos posMax = new BlockPos(Math.max(fromX, toX), Math.max(fromY, toY), Math.max(fromZ, toZ));
    return new Iterable<MBlockPos>() {
        public Iterator<MBlockPos> iterator() {
          return (Iterator<MBlockPos>)new AbstractIterator<MBlockPos>() {
              private MBlockPos pos = null;
              
              private int x;
              
              private int y;
              
              private int z;
              
              protected MBlockPos computeNext() {
                if (this.pos == null) {
                  this.x = posMin.getX();
                  this.y = posMin.getY();
                  this.z = posMin.getZ();
                  this.pos = new MBlockPos(this.x, this.y, this.z);
                  return this.pos;
                } 
                if (this.pos.equals(posMax))
                  return (MBlockPos)endOfData(); 
                if (this.x < posMax.getX()) {
                  this.x++;
                } else if (this.y < posMax.getY()) {
                  this.x = posMin.getX();
                  this.y++;
                } else if (this.z < posMax.getZ()) {
                  this.x = posMin.getX();
                  this.y = posMin.getY();
                  this.z++;
                } 
                this.pos.x = this.x;
                this.pos.y = this.y;
                this.pos.z = this.z;
                return this.pos;
              }
            };
        }
      };
  }
  
  public static Iterable<MBlockPos> getAllInBoxXZY(BlockPos from, BlockPos to) {
    return getAllInBoxXZY(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ());
  }
  
  public static Iterable<MBlockPos> getAllInBoxXZY(int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
    final BlockPos posMin = new BlockPos(Math.min(fromX, toX), Math.min(fromY, toY), Math.min(fromZ, toZ));
    final BlockPos posMax = new BlockPos(Math.max(fromX, toX), Math.max(fromY, toY), Math.max(fromZ, toZ));
    return new Iterable<MBlockPos>() {
        public Iterator<MBlockPos> iterator() {
          return (Iterator<MBlockPos>)new AbstractIterator<MBlockPos>() {
              private MBlockPos pos = null;
              
              private int x;
              
              private int y;
              
              private int z;
              
              protected MBlockPos computeNext() {
                if (this.pos == null) {
                  this.x = posMin.getX();
                  this.y = posMin.getY();
                  this.z = posMin.getZ();
                  this.pos = new MBlockPos(this.x, this.y, this.z);
                  return this.pos;
                } 
                if (this.pos.equals(posMax))
                  return (MBlockPos)endOfData(); 
                if (this.x < posMax.getX()) {
                  this.x++;
                } else if (this.z < posMax.getZ()) {
                  this.x = posMin.getX();
                  this.z++;
                } else if (this.y < posMax.getY()) {
                  this.x = posMin.getX();
                  this.z = posMin.getZ();
                  this.y++;
                } 
                this.pos.x = this.x;
                this.pos.y = this.y;
                this.pos.z = this.z;
                return this.pos;
              }
            };
        }
      };
  }
  
  public static Iterable<MBlockPos> getAllInBoxXZYFromFacing(EnumFacing facing, int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
    Preconditions.checkArgument((facing.getAxis() != EnumFacing.Axis.Y), "Facing must be horizontal");
    final EnumFacing opposite = facing.getOpposite(), rotated = facing.rotateY();
    int xMin = Math.min(fromX, toX), xMax = Math.max(fromX, toX);
    int yMin = Math.min(fromY, toY), yMax = Math.max(fromY, toY);
    int zMin = Math.min(fromZ, toZ), zMax = Math.max(fromZ, toZ);
    MBlockPos startPos = null, endPos = null;
    final Vec3i moveVecX = rotated.getDirectionVec(), moveVecZ = opposite.getDirectionVec();
    switch (facing) {
      case NORTH:
        startPos = new MBlockPos(xMin, yMin, zMin);
        endPos = new MBlockPos(xMax, yMax, zMax);
        break;
      case SOUTH:
        startPos = new MBlockPos(xMax, yMin, zMax);
        endPos = new MBlockPos(xMin, yMax, zMin);
        break;
      case EAST:
        startPos = new MBlockPos(xMax, yMin, zMin);
        endPos = new MBlockPos(xMin, yMax, zMax);
        break;
      case WEST:
        startPos = new MBlockPos(xMin, yMin, zMax);
        endPos = new MBlockPos(xMax, yMax, zMin);
        break;
    } 
    final MBlockPos posMin = startPos;
    final MBlockPos posMax = endPos;
    return new Iterable<MBlockPos>() {
        public Iterator<MBlockPos> iterator() {
          return (Iterator<MBlockPos>)new AbstractIterator<MBlockPos>() {
              private MBlockPos pos = null;
              
              protected MBlockPos computeNext() {
                if (this.pos == null) {
                  this.pos = new MBlockPos(posMin.getX(), posMin.getY(), posMin.getZ());
                  return this.pos;
                } 
                if (this.pos.equals(posMax))
                  return (MBlockPos)endOfData(); 
                if (BlockPosHelper.isLessThan(rotated.getAxis(), rotated.getAxisDirection(), this.pos, posMax)) {
                  this.pos = this.pos.add(moveVecX);
                } else if (BlockPosHelper.isLessThan(opposite.getAxis(), opposite.getAxisDirection(), this.pos, posMax)) {
                  if (opposite.getAxis() == EnumFacing.Axis.Z) {
                    this.pos.x = posMin.getX();
                  } else {
                    this.pos.z = posMin.getZ();
                  } 
                  this.pos = this.pos.add(moveVecZ);
                } else if (this.pos.y < posMax.y) {
                  this.pos.x = posMin.x;
                  this.pos.z = posMin.z;
                  this.pos.y++;
                } 
                return this.pos;
              }
            };
        }
      };
  }
  
  public static Iterable<MBlockPos> getAllInBoxYXZ(BlockPos from, BlockPos to) {
    return getAllInBoxYXZ(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ());
  }
  
  public static Iterable<MBlockPos> getAllInBoxYXZ(int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
    final BlockPos posMin = new BlockPos(Math.min(fromX, toX), Math.min(fromY, toY), Math.min(fromZ, toZ));
    final BlockPos posMax = new BlockPos(Math.max(fromX, toX), Math.max(fromY, toY), Math.max(fromZ, toZ));
    return new Iterable<MBlockPos>() {
        public Iterator<MBlockPos> iterator() {
          return (Iterator<MBlockPos>)new AbstractIterator<MBlockPos>() {
              private MBlockPos pos = null;
              
              private int x;
              
              private int y;
              
              private int z;
              
              protected MBlockPos computeNext() {
                if (this.pos == null) {
                  this.x = posMin.getX();
                  this.y = posMin.getY();
                  this.z = posMin.getZ();
                  this.pos = new MBlockPos(this.x, this.y, this.z);
                  return this.pos;
                } 
                if (this.pos.equals(posMax))
                  return (MBlockPos)endOfData(); 
                if (this.y < posMax.getY()) {
                  this.y++;
                } else if (this.x < posMax.getX()) {
                  this.y = posMin.getY();
                  this.x++;
                } else if (this.z < posMax.getZ()) {
                  this.y = posMin.getY();
                  this.x = posMin.getX();
                  this.z++;
                } 
                this.pos.x = this.x;
                this.pos.y = this.y;
                this.pos.z = this.z;
                return this.pos;
              }
            };
        }
      };
  }
  
  private static boolean isLessThan(EnumFacing.Axis axis, EnumFacing.AxisDirection direction, BlockPos checked, BlockPos checkedAgainst) {
    int val1 = (axis == EnumFacing.Axis.X) ? checked.getX() : checked.getZ();
    int val2 = (axis == EnumFacing.Axis.X) ? checkedAgainst.getX() : checkedAgainst.getZ();
    return (direction == EnumFacing.AxisDirection.POSITIVE) ? ((val1 < val2)) : ((val1 > val2));
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\cor\\util\BlockPosHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */