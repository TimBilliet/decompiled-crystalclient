package co.crystaldev.client.mixin.net.minecraft.client.renderer;

import co.crystaldev.client.feature.impl.all.Farming;
import co.crystaldev.client.feature.impl.factions.CannonView;
import co.crystaldev.client.feature.impl.mechanic.NoLag;
import co.crystaldev.client.util.LocationUtils;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockFluidRenderer;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

@Mixin({BlockRendererDispatcher.class})
public abstract class MixinBlockRendererDispatcher {
    @Shadow
    @Final
    private BlockModelRenderer blockModelRenderer;

    @Shadow
    @Final
    private BlockFluidRenderer fluidRenderer;

    private static final IBlockState DOUBLE_STONE_SLAB_STATE = Block.getStateById(43);

    private static final IBlockState DOUBLE_STONE_SLAB_2_STATE = Block.getStateById(181);

    private static final IBlockState DOUBLE_WOODEN_SLAB_STATE = Block.getStateById(125);

    @Unique
    private static final Set<Block> FOLIAGE_TO_BLOCK = Sets.newHashSet(Blocks.red_flower, (Block) Blocks.yellow_flower, (Block) Blocks.double_plant, (Block) Blocks.tallgrass);

    @Shadow
    public abstract IBakedModel getModelFromBlockState(IBlockState paramIBlockState, IBlockAccess paramIBlockAccess, BlockPos paramBlockPos);
//  public abstract IBakedModel func_175022_a(IBlockState paramIBlockState, IBlockAccess paramIBlockAccess, BlockPos paramBlockPos);

    /**
     * @author Tim
     */
    @Overwrite(aliases = {"renderBlock"})
    public boolean renderBlock(IBlockState state, BlockPos pos, IBlockAccess blockAccess, WorldRenderer wr) {
        if ((Farming.getInstance()).enabled) {
            boolean cacMode = (Farming.getInstance()).cactusMode;
            boolean disCac = (Farming.getInstance()).disableCactusRendering;
            boolean disStr = (Farming.getInstance()).disableStringRendering;
            boolean isCac = state.getBlock() instanceof net.minecraft.block.BlockCactus;
            boolean isStr = state.getBlock() instanceof net.minecraft.block.BlockTripWire;
            if ((cacMode && (isCac || isStr) && !LocationUtils.isBlockInSameChunk(pos, (Entity) (Minecraft.getMinecraft()).thePlayer)) || (disCac && isCac) || (disStr && isStr))
                return false;
        }
        if (NoLag.isEnabled((NoLag.getInstance()).hideFoliage) && FOLIAGE_TO_BLOCK.contains(state.getBlock()))
            return false;
        try {
            IBakedModel ibakedmodel;
            int i = state.getBlock().getRenderType();
            if (i == -1)
                return false;
            switch (i) {
                case 1:
                    return this.fluidRenderer.renderFluid(blockAccess, state, pos, wr);
                case 3:
                    if ((CannonView.getInstance()).enabled) {
                        if (!CannonView.BLOCKS.contains(state.getBlock().getClass()))
                            return false;
//            IBakedModel iBakedModel = func_175022_a(state, blockAccess, pos);
                        IBakedModel iBakedModel = getModelFromBlockState(state, blockAccess, pos);
                        return this.blockModelRenderer.renderModel(blockAccess, iBakedModel, state, pos, wr, false);
                    }
                    ibakedmodel = getModelFromBlockState(state, blockAccess, pos);//func_175022_a
                    return this.blockModelRenderer.renderModel(blockAccess, ibakedmodel, state, pos, wr);//func_178259_a
            }
            return false;
        } catch (Throwable throwable) {
            return false;
        }
    }

    @Redirect(method = {"renderBlock"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BlockRendererDispatcher;getModelFromBlockState(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/BlockPos;)Lnet/minecraft/client/resources/model/IBakedModel;"))
    private IBakedModel cancelSlabRender(BlockRendererDispatcher instance, IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        if (NoLag.isEnabled((NoLag.getInstance()).disableSlabRendering)) {
            Block block = state.getBlock();
            if (NoLag.isSlab(block) && NoLag.isSlab(worldIn.getBlockState(pos.up()).getBlock()))
                if (block instanceof net.minecraft.block.BlockHalfStoneSlab) {
                    state = DOUBLE_STONE_SLAB_STATE;
                } else if (block instanceof net.minecraft.block.BlockHalfStoneSlabNew) {
                    state = DOUBLE_STONE_SLAB_2_STATE;
                } else {
                    state = DOUBLE_WOODEN_SLAB_STATE;
                }
        }
//    return instance.func_175022_a(state, worldIn, pos);
        return instance.getModelFromBlockState(state, worldIn, pos);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\renderer\MixinBlockRendererDispatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */