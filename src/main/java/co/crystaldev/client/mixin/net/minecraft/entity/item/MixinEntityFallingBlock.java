package co.crystaldev.client.mixin.net.minecraft.entity.item;

import co.crystaldev.client.duck.EntityExt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EntityFallingBlock.class})
public abstract class MixinEntityFallingBlock extends Entity {
    public MixinEntityFallingBlock(World worldIn) {
        super(worldIn);
    }

    @Inject(method = {"<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/block/state/IBlockState;)V"}, at = {@At("RETURN")})
    private void constructor(CallbackInfo ci) {
        ((EntityExt) this).setInitialYLevel(this.posY);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\entity\item\MixinEntityFallingBlock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */