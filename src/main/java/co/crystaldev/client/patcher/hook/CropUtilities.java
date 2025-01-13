package co.crystaldev.client.patcher.hook;

import co.crystaldev.client.mixin.accessor.net.minecraft.block.MixinBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CropUtilities {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static final AxisAlignedBB[] CARROT_POTATO_BOX = new AxisAlignedBB[]{new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.1875D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.3125D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.375D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.4375D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5625D, 1.0D)};

    public static final AxisAlignedBB[] WHEAT_BOX = new AxisAlignedBB[]{new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.375D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.625D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)};

    public static final AxisAlignedBB[] NETHER_WART_BOX = new AxisAlignedBB[]{new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.3125D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.6875D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D)};

    public static void updateCropsMaxY(World world, BlockPos pos, Block block) {
        IBlockState blockState = world.getBlockState(pos);
        Integer ageValue = blockState.getValue(BlockCrops.AGE);
        MixinBlock accessor = (MixinBlock) block;
        if (mc.isIntegratedServerRunning()) {
            accessor.setMaxY((blockState
                    .getBlock() instanceof net.minecraft.block.BlockPotato || blockState.getBlock() instanceof net.minecraft.block.BlockCarrot) ?
                    (CARROT_POTATO_BOX[ageValue]).maxY :
                    (WHEAT_BOX[ageValue]).maxY);
            return;
        }
        accessor.setMaxY(0.25D);
    }

    public static void updateWartMaxY(World world, BlockPos pos, Block block) {
        ((MixinBlock) block).setMaxY(
                mc.isIntegratedServerRunning() ?
                        (NETHER_WART_BOX[world.getBlockState(pos).getValue(BlockNetherWart.AGE)]).maxY : 0.25D);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\patcher\hook\CropUtilities.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */