package co.crystaldev.client.mixin.net.minecraft.client.renderer.block.statemap;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.BlockStateMapper;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Set;

@Mixin({BlockStateMapper.class})
public abstract class MixinBlockStateMapper {
    @Shadow
    private Set<Block> setBuiltInBlocks;

    @Shadow
    private Map<Block, IStateMapper> blockStateMap;

    /**
     * @author
     */
    @Overwrite(aliases = {"putAllStateModelLocations"})
    public Map<IBlockState, ModelResourceLocation> putAllStateModelLocations() {
        Map<IBlockState, ModelResourceLocation> map = Maps.newIdentityHashMap();
        for (Block block : Block.blockRegistry) {
            if (block == Blocks.chest || block == Blocks.trapped_chest || !this.setBuiltInBlocks.contains(block))
                map.putAll(Objects.firstNonNull(this.blockStateMap.get(block), new DefaultStateMapper()).putStateModelLocations(block));
        }
        return map;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\renderer\block\statemap\MixinBlockStateMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */