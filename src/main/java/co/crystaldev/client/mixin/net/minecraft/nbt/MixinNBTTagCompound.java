package co.crystaldev.client.mixin.net.minecraft.nbt;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({NBTTagCompound.class})
public abstract class MixinNBTTagCompound {
    @Inject(method = {"setTag"}, at = {@At("HEAD")})
    private void failFast(String key, NBTBase value, CallbackInfo ci) {
        if (value == null)
            throw new IllegalArgumentException(String.format("NBT value with key: %s is invalid", new Object[]{key}));
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\nbt\MixinNBTTagCompound.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */