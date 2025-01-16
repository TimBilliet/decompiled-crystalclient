package co.crystaldev.client.mixin.net.minecraft.block;

import co.crystaldev.client.feature.impl.mechanic.NoLag;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.util.EnumWorldBlockLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin({BlockChest.class})
public abstract class MixinBlockChest extends BlockContainer {
    protected MixinBlockChest(Material materialIn) {
        super(materialIn);
    }

    /**
     * @author
     */
    @Overwrite
    public boolean isOpaqueCube() {
        return (NoLag.getInstance() != null && NoLag.isEnabled((NoLag.getInstance()).fasterChestRendering));
    }

    /**
     * @author
     */
    @Overwrite
    public int getRenderType() {
        return isOpaqueCube() ? 3 : 2;
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return !isOpaqueCube() ? EnumWorldBlockLayer.CUTOUT_MIPPED : EnumWorldBlockLayer.SOLID;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\block\MixinBlockChest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */