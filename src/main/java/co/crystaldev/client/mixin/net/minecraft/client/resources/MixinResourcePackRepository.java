package co.crystaldev.client.mixin.net.minecraft.client.resources;

import net.minecraft.client.resources.ResourcePackRepository;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin({ResourcePackRepository.class})
public abstract class MixinResourcePackRepository {
    @Shadow
    @Final
    private File dirServerResourcepacks;

    @Inject(method = {"deleteOldServerResourcesPacks"}, at = {@At("HEAD")})
    private void deleteOldServerResourcesPacks(CallbackInfo ci) {
        if (!this.dirServerResourcepacks.exists())
            this.dirServerResourcepacks.mkdirs();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\resources\MixinResourcePackRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */