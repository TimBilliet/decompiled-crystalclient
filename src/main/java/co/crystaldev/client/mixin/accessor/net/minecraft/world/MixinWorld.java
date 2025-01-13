package co.crystaldev.client.mixin.accessor.net.minecraft.world;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Set;

@Mixin({World.class})
public interface MixinWorld {
    @Accessor("activeChunkSet")
    Set<ChunkCoordIntPair> getActiveChunkSet();

    @Accessor("tileEntitiesToBeRemoved")
    List<TileEntity> getTileEntitiesToBeRemoved();
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\accessor\net\minecraft\world\MixinWorld.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */