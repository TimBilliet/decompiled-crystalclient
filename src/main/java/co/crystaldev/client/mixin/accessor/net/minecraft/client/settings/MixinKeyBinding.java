package co.crystaldev.client.mixin.accessor.net.minecraft.client.settings;

import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin({KeyBinding.class})
public interface MixinKeyBinding {
  @Accessor
  static List<KeyBinding> getKeybindArray() {
    throw new UnsupportedOperationException("Mixin failed to inject!");
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\accessor\net\minecraft\client\settings\MixinKeyBinding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */