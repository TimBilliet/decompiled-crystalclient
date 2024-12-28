package co.crystaldev.client.mixin.net.minecraft.item;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ItemStack.class})
public abstract class MixinItemStack {
  private String displayName;
  
  @Redirect(method = {"getTooltip"}, at = @At(value = "INVOKE", target = "Ljava/lang/Integer;toHexString(I)Ljava/lang/String;"))
  private String fixHexCrash(int i) {
    return String.format("%06X", new Object[] { Integer.valueOf(i) });
  }
  
  @Inject(method = {"getDisplayName"}, at = {@At("HEAD")}, cancellable = true)
  private void returnCachedDisplayName(CallbackInfoReturnable<String> cir) {
    if (this.displayName != null)
      cir.setReturnValue(this.displayName); 
  }
  
  @Inject(method = {"getDisplayName"}, at = {@At("RETURN")})
  private void cacheDisplayName(CallbackInfoReturnable<String> cir) {
    this.displayName = (String)cir.getReturnValue();
  }
  
  @Inject(method = {"setStackDisplayName"}, at = {@At("HEAD")})
  private void resetCachedDisplayName(String displayName, CallbackInfoReturnable<ItemStack> cir) {
    this.displayName = null;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\item\MixinItemStack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */