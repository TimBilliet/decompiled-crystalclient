package co.crystaldev.client.mixin.accessor.net.minecraft.block;

import net.minecraft.block.BlockRedstoneDiode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({BlockRedstoneDiode.class})
public interface MixinBlockRedstoneDiode {
    @Accessor("isRepeaterPowered")
    boolean getIsRepeaterPowered();
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\accessor\net\minecraft\block\MixinBlockRedstoneDiode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */