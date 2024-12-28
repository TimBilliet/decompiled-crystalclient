package co.crystaldev.client.mixin.net.minecraft.tileentity;

import co.crystaldev.client.duck.TileEntityExt;
import co.crystaldev.client.feature.impl.mechanic.NoLag;
import co.crystaldev.client.handler.ModuleHandler;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin({TileEntity.class})
public abstract class MixinTileEntity implements TileEntityExt {
  @Shadow
  protected boolean tileEntityInvalid;
  
  @Unique
  private boolean crystal$shouldBeRemoved = false;
  
  @Unique
  private long crystal$removalTick = -1L;
  
  /**
   * @author
   */
  @Overwrite
  public double getMaxRenderDistanceSquared() {
    return (NoLag.getInstance()).enabled ? Math.pow((NoLag.getInstance()).tileEntityRenderDistance, 2.0D) : 4096.0D;
  }
  
  /**
   * @author
   */
  @Overwrite(aliases = {"isInvalid"})
  public boolean isInvalid() {
//    return (this.tileEntityInvalid || super.crystal$shouldBeRemoved());
    return (this.tileEntityInvalid);
  }
  
  public boolean crystal$shouldBeRemoved() {
    return (this.crystal$shouldBeRemoved && ModuleHandler.getTotalTicks() > this.crystal$removalTick);
  }
  
  public void crystal$setShouldBeRemoved(boolean shouldBeRemoved) {
    this.crystal$shouldBeRemoved = shouldBeRemoved;
  }
  
  public void crystal$setRemovalTick(long tick) {
    this.crystal$removalTick = tick;
  }
}
