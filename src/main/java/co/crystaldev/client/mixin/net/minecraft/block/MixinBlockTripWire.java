package co.crystaldev.client.mixin.net.minecraft.block;

import co.crystaldev.client.feature.settings.ClientOptions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTripWire;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({BlockTripWire.class})
public abstract class MixinBlockTripWire extends Block {
    public MixinBlockTripWire(Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);
    }

    public int colorMultiplier(IBlockAccess blockAccess, BlockPos pos, int renderPass) {
        return (ClientOptions.getInstance()).redString ? 16711680 : super.colorMultiplier(blockAccess, pos, renderPass);//func_180662_a
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\block\MixinBlockTripWire.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */