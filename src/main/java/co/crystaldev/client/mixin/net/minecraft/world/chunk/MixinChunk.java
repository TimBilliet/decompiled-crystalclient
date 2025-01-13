package co.crystaldev.client.mixin.net.minecraft.world.chunk;

import co.crystaldev.client.patcher.hook.ChunkHook;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin({Chunk.class})
public abstract class MixinChunk {
    /**
     * @author
     */
    @Overwrite
    public IBlockState getBlockState(BlockPos pos) {
        return ChunkHook.getBlockState((Chunk) (Object) this, pos);
    }

    @ModifyArg(method = {"setBlockState"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;relightBlock(III)V", ordinal = 0), index = 1)
    private int subtractOneFromY(int y) {
        return y - 1;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\world\chunk\MixinChunk.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */