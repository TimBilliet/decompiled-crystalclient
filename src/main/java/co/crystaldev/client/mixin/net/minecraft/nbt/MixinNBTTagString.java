package co.crystaldev.client.mixin.net.minecraft.nbt;

import net.minecraft.nbt.NBTTagString;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({NBTTagString.class})
public abstract class MixinNBTTagString {
    @Shadow
    private String data;

    @Unique
    private String dataCache;

    @Inject(method = {"read"}, at = {@At("HEAD")})
    private void emptyDataCache(CallbackInfo ci) {
        this.dataCache = null;
    }

    @Overwrite
    public String toString() {
        if (this.dataCache == null)
            this.dataCache = "\"" + this.data.replace("\"", "\\\"") + "\"";
        return this.dataCache;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\nbt\MixinNBTTagString.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */