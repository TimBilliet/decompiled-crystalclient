package com.github.timmekeclient.mixin.net.minecraft.block;

import com.github.timmekeclient.feature.impl.mechanic.NoLag;
import net.minecraft.block.BlockHopper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin({BlockHopper.class})
public abstract class MixinBlockHopper {
    @Overwrite
    public int getRenderType() {
        return ((NoLag.getInstance()).enabled && (NoLag.getInstance()).hideHoppers) ? -1 : 3;
    }
}
