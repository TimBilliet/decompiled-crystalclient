package co.crystaldev.client.mixin.accessor.net.minecraft.nbt;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.io.DataOutput;
import java.io.IOException;

@Mixin({NBTTagCompound.class})
public interface MixinNBTTagCompound {
  @Invoker("writeEntry")
  static void callWriteEntry(String name, NBTBase data, DataOutput output) throws IOException {
    throw new AssertionError();
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\accessor\net\minecraft\nbt\MixinNBTTagCompound.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */