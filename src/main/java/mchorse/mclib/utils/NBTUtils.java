package mchorse.mclib.utils;

import co.crystaldev.client.util.javax.Vector3f;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.EncoderException;
import net.minecraft.nbt.*;

import java.io.DataInput;
import java.io.IOException;

public class NBTUtils {
    public static void readFloatList(NBTTagList list, float[] array) {
        int count = Math.min(array.length, list.tagCount());
        for (int i = 0; i < count; i++)
            array[i] = list.getFloatAt(i);
    }

    public static NBTTagList writeFloatList(NBTTagList list, float[] array) {
        for (int i = 0; i < array.length; i++)
            list.appendTag((NBTBase) new NBTTagFloat(array[i]));
        return list;
    }

    public static void readFloatList(NBTTagList list, Vector3f vector) {
        if (list.tagCount() != 3)
            return;
        vector.x = list.getFloatAt(0);
        vector.y = list.getFloatAt(1);
        vector.z = list.getFloatAt(2);
    }

    public static NBTTagList writeFloatList(NBTTagList list, Vector3f vector) {
        list.appendTag((NBTBase) new NBTTagFloat(vector.x));
        list.appendTag((NBTBase) new NBTTagFloat(vector.y));
        list.appendTag((NBTBase) new NBTTagFloat(vector.z));
        return list;
    }

    public static NBTTagCompound readInfiniteTag(ByteBuf buf) {
        int i = buf.readerIndex();
        byte b0 = buf.readByte();
        if (b0 == 0)
            return null;
        buf.readerIndex(i);
        try {
            return CompressedStreamTools.read((DataInput) new ByteBufInputStream(buf), NBTSizeTracker.INFINITE);
        } catch (IOException ioexception) {
            throw new EncoderException(ioexception);
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\NBTUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */