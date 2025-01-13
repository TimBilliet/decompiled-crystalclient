package co.crystaldev.client.mixin.net.minecraft.client.multiplayer;

import net.minecraft.client.multiplayer.ServerAddress;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.net.IDN;

@Mixin({ServerAddress.class})
public abstract class MixinServerAddress {
    @Shadow
    @Final
    private String ipAddress;

    /**
     * @author
     */
    @Overwrite
    public String getIP() {
        try {
            return IDN.toASCII(this.ipAddress);
        } catch (IllegalArgumentException ex) {
            return "";
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\multiplayer\MixinServerAddress.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */