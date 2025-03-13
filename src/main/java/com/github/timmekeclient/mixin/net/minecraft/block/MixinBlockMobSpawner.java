package com.github.timmekeclient.mixin.net.minecraft.block;

import com.github.timmekeclient.feature.impl.mechanic.NoLag;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.block.material.Material;
import net.minecraft.util.EnumWorldBlockLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin({BlockMobSpawner.class})
public abstract class MixinBlockMobSpawner extends BlockContainer {
    protected MixinBlockMobSpawner(Material materialIn) {
        super(materialIn);
    }

    /**
     * @author
     */
    @Overwrite
    public boolean isOpaqueCube() {
        return (NoLag.getInstance() != null && NoLag.isEnabled((NoLag.getInstance()).fasterSpawnerRendering));
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return !isOpaqueCube() ? EnumWorldBlockLayer.CUTOUT_MIPPED : EnumWorldBlockLayer.SOLID;
    }

    public boolean isVisuallyOpaque() {
        return false;
    }
}
