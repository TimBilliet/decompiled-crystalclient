package co.crystaldev.client.mixin.net.minecraft.block;

import co.crystaldev.client.feature.impl.mechanic.NoLag;
import net.minecraft.block.BlockHopper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin({BlockHopper.class})
public abstract class MixinBlockHopper {
  @Overwrite
  public int getRenderType() {
    return ((NoLag.getInstance()).enabled && (NoLag.getInstance()).hideHoppers) ? -1 : 3;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\block\MixinBlockHopper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */