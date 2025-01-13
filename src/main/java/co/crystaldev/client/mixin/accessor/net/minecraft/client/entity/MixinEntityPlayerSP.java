package co.crystaldev.client.mixin.accessor.net.minecraft.client.entity;

import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({EntityPlayerSP.class})
public interface MixinEntityPlayerSP {
    @Accessor("sprintToggleTimer")
    int getSprintToggleTimer();

    @Accessor("sprintToggleTimer")
    void setSprintToggleTimer(int paramInt);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\accessor\net\minecraft\client\entity\MixinEntityPlayerSP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */