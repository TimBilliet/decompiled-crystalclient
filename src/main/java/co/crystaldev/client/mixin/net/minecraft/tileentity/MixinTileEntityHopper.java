package co.crystaldev.client.mixin.net.minecraft.tileentity;

import net.minecraft.block.BlockHopper;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({TileEntityHopper.class})
public abstract class MixinTileEntityHopper extends TileEntity {
  @Shadow
  public abstract boolean isOnTransferCooldown();
  
  @Shadow
  protected abstract boolean isEmpty();
  
  @Shadow
  protected abstract boolean transferItemsOut();
  
  @Shadow
  protected abstract boolean isFull();
  
  @Shadow
  public static boolean captureDroppedItems(IHopper p_145891_0_) {
    return false;
  }
  
  @Shadow
  public abstract void setTransferCooldown(int paramInt);
  
  /**
   * @author
   */
  @Overwrite
  public boolean updateHopper() {
    if (this.worldObj != null && this.worldObj.isRemote)
      return false; 
    if (this.worldObj != null) {
      if (!isOnTransferCooldown() && BlockHopper.isEnabled(getBlockMetadata())) {
        boolean flag = false;
        if (!isEmpty())
          flag = transferItemsOut(); 
        if (!isFull())
          flag = (captureDroppedItems((IHopper)this) || flag); 
        if (flag) {
          setTransferCooldown(8);
          markDirty();
          return true;
        } 
      } 
      return false;
    } 
    return false;
  }
}
