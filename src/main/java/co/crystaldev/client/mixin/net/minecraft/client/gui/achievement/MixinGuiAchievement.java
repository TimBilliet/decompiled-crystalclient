package co.crystaldev.client.mixin.net.minecraft.client.gui.achievement;

import co.crystaldev.client.feature.settings.ClientOptions;
import net.minecraft.client.gui.achievement.GuiAchievement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiAchievement.class})
public abstract class MixinGuiAchievement {
  @Inject(method = {"updateAchievementWindow"}, at = {@At("HEAD")}, cancellable = true)
  private void disableAchievements(CallbackInfo ci) {
    if ((ClientOptions.getInstance()).disableAchievements)
      ci.cancel(); 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\gui\achievement\MixinGuiAchievement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */