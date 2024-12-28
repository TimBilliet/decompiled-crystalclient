package co.crystaldev.client.mixin.accessor.net.minecraft.util;

import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({EnumFacing.class})
public interface MixinEnumFacing {
  @Accessor("VALUES")
  static EnumFacing[] getValues() {
    throw new AssertionError();
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\accessor\net\minecraf\\util\MixinEnumFacing.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */