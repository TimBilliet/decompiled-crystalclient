package co.crystaldev.client.mixin.net.minecraft.client.resources;

import co.crystaldev.client.Reference;
import net.minecraft.client.resources.DefaultResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Set;

@Mixin({DefaultResourcePack.class})
public abstract class MixinDefaultResourcePack {
  @Overwrite
  public Set<String> getResourceDomains() {
    return Reference.RESOURCE_DOMAINS;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\resources\MixinDefaultResourcePack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */