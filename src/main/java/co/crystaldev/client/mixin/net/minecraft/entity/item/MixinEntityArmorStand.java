package co.crystaldev.client.mixin.net.minecraft.entity.item;

import co.crystaldev.client.duck.EntityArmorStandExt;
import net.minecraft.entity.item.EntityArmorStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({EntityArmorStand.class})
public abstract class MixinEntityArmorStand implements EntityArmorStandExt {
    @Unique
    private boolean crystal$isInBlock = false;

    @Unique
    private long crystal$lastBlockCheck = 0L;

    public void crystal$setIsInBlock(boolean inBlock) {
        this.crystal$isInBlock = inBlock;
    }

    public boolean crystal$isInBlock() {
        return this.crystal$isInBlock;
    }

    public void crystal$setLastBlockCheck(long lastCheck) {
        this.crystal$lastBlockCheck = lastCheck;
    }

    public long crystal$getLastBlockCheck() {
        return this.crystal$lastBlockCheck;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\entity\item\MixinEntityArmorStand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */