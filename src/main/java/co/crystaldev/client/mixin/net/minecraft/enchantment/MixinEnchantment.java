package co.crystaldev.client.mixin.net.minecraft.enchantment;

import co.crystaldev.client.feature.settings.ClientOptions;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.StatCollector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Enchantment.class})
public abstract class MixinEnchantment {
  @Shadow
  public abstract String getName();
  
  @Inject(method = {"getTranslatedName"}, at = {@At("HEAD")}, cancellable = true)
  private void translateToEnglish(int level, CallbackInfoReturnable<String> ci) {
    if ((ClientOptions.getInstance()).translateRomanNumerals)
      ci.setReturnValue(StatCollector.translateToLocal(getName()) + " " + level); 
  }
}

