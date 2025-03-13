package com.github.timmekeclient.mixin.net.minecraft.block;

import com.github.timmekeclient.feature.impl.mechanic.NoLag;
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
