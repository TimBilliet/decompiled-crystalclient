package co.crystaldev.client.mixin.net.minecraft.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.LazyLoadBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({LazyLoadBase.class})
public abstract class MixinLazyLoadBase<T> {
  @Shadow
  private boolean isLoaded;
  
  @Shadow
  private T value;
  
  @Shadow
  protected abstract T load();
  
  @Overwrite
  public T getValue() {
    synchronized (Minecraft.class) {
      if (!this.isLoaded) {
        this.isLoaded = true;
        this.value = load();
      } 
      return this.value;
    } 
  }
}

