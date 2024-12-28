package co.crystaldev.client.util.objects.crumbs;

import co.crystaldev.client.duck.EntityExt;
import co.crystaldev.client.handler.ModuleHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.util.BlockPos;

public abstract class CrumbEntity {
  private final long created = System.currentTimeMillis();
  
  private final double x;
  
  private final double y;
  
  private final double z;
  
  private final Patchcrumb.Source source;
  
  private final BlockPos pos;
  
  public long getCreated() {
    return this.created;
  }
  
  public double getX() {
    return this.x;
  }
  
  public double getY() {
    return this.y;
  }
  
  public double getZ() {
    return this.z;
  }
  
  public Patchcrumb.Source getSource() {
    return this.source;
  }
  
  public BlockPos getPos() {
    return this.pos;
  }
  
  private long deathTick = -1L;
  
  public long getDeathTick() {
    return this.deathTick;
  }
  
  private boolean dirty = false;
  
  public boolean isDirty() {
    return this.dirty;
  }
  
  public void setDirty(boolean dirty) {
    this.dirty = dirty;
  }
  
  private boolean bypassSandCheck = false;
  
  public boolean isBypassSandCheck() {
    return this.bypassSandCheck;
  }
  
  public void setBypassSandCheck(boolean bypassSandCheck) {
    this.bypassSandCheck = bypassSandCheck;
  }
  
  private CrumbEntity(double x, double y, double z, Patchcrumb.Source source) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.source = source;
    this.pos = new BlockPos(this.x, this.y, this.z);
  }
  
  public boolean isDead() {
    return (this.deathTick > -1L);
  }
  
  public void setDead() {
    this.deathTick = ModuleHandler.getTotalTicks();
  }
  
  public long ticksSinceDeath() {
    return ModuleHandler.getTotalTicks() - this.deathTick;
  }
  
  public static class TNT extends CrumbEntity {
    private final EntityTNTPrimed tntPrimed;
    
    public EntityTNTPrimed getTntPrimed() {
      return this.tntPrimed;
    }
    
    public TNT(Entity entity) {
      super(entity.posX, ((EntityExt)entity).getInitialYLevel(), entity.posZ, Patchcrumb.Source.ENTITY);
      this.tntPrimed = (EntityTNTPrimed)entity;
    }
    
    public boolean isDead() {
      if (this.tntPrimed.isDead && !super.isDead())
        setDead(); 
      return (super.isDead() && ticksSinceDeath() > 4L);
    }
  }
  
  public static class FallingBlock extends CrumbEntity {
    private final EntityFallingBlock falling;
    
    public EntityFallingBlock getFalling() {
      return this.falling;
    }
    
    public FallingBlock(Entity entity) {
      super(entity.posX, ((EntityExt)entity).getInitialYLevel(), entity.posZ, Patchcrumb.Source.ENTITY);
      this.falling = (EntityFallingBlock)entity;
    }
    
    public boolean isDead() {
      if (this.falling.isDead && !super.isDead())
        setDead(); 
      return (super.isDead() && ticksSinceDeath() > 4L);
    }
  }
  
  public static class Explosion extends CrumbEntity {
    public Explosion(double x, double y, double z) {
      super(x, y, z, Patchcrumb.Source.EXPLOSION);
      setDead();
      setDirty(true);
    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\objects\crumbs\CrumbEntity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */