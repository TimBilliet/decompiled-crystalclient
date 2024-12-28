package co.crystaldev.client.mixin.net.minecraft.block;

import net.minecraft.block.BlockRedstoneTorch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.WeakHashMap;

@Mixin({BlockRedstoneTorch.class})
public abstract class MixinBlockRedstoneTorch {
  @Shadow
  private static Map toggles = new WeakHashMap<>();
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\block\MixinBlockRedstoneTorch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */