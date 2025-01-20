package co.crystaldev.client.mixin.net.minecraft.world.chunk.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.DataInputStream;
import java.io.IOException;

@Mixin({AnvilChunkLoader.class})
public abstract class MixinAnvilChunkLoader {
    //  @Inject(method = {"loadChunk"}, locals = LocalCapture.CAPTURE_FAILEXCEPTION, at = {@At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompressedStreamTools;read(Ljava/io/DataInputStream;)Lnet/minecraft/nbt/NBTTagCompound;", shift = At.Shift.AFTER)})
//  private void loadChunk(World worldIn, int x, int z, CallbackInfoReturnable<Chunk> ci, ChunkCoordIntPair chunkcoordintpair, NBTTagCompound nbttagcompound, DataInputStream datainputstream) throws IOException {
//    datainputstream.close();
//  }
    @Inject(method = {"loadChunk__Async"}, locals = LocalCapture.CAPTURE_FAILEXCEPTION, at = {@At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompressedStreamTools;read(Ljava/io/DataInputStream;)Lnet/minecraft/nbt/NBTTagCompound;", shift = At.Shift.AFTER)})
    private void loadChunk(World worldIn, int x, int z, CallbackInfoReturnable<Chunk> ci, ChunkCoordIntPair chunkcoordintpair, NBTTagCompound nbttagcompound, DataInputStream datainputstream) throws IOException {
        datainputstream.close();
    }
}