package co.crystaldev.client.mixin.accessor.net.minecraft.entity.projectile;

import net.minecraft.entity.projectile.EntityArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({EntityArrow.class})
public interface MixinEntityArrow {
  @Accessor
  boolean getInGround();
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\accessor\net\minecraft\entity\projectile\MixinEntityArrow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */