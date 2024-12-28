package co.crystaldev.client.mixin.net.minecraft.command;

import net.minecraft.command.CommandHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin({CommandHandler.class})
public abstract class MixinCommandHandler {
  @ModifyArg(method = {"executeCommand"}, at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;", remap = false))
  private Object returnLowerCase(Object s) {
    if (s instanceof String)
      return ((String)s).toLowerCase(); 
    return s;
  }
  
  @ModifyArg(method = {"registerCommand"}, at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", remap = false), index = 0)
  private Object putLowerCase(Object s) {
    if (s instanceof String)
      return ((String)s).toLowerCase(); 
    return s;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\command\MixinCommandHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */