package co.crystaldev.client.util;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import org.apache.commons.lang3.Validate;

public class ByteBufUtils {
  public static int varIntByteCount(int toCount) {
    return ((toCount & 0xFFFFFF80) == 0) ? 1 : (((toCount & 0xFFFFC000) == 0) ? 2 : (((toCount & 0xFFE00000) == 0) ? 3 : (((toCount & 0xF0000000) == 0) ? 4 : 5)));
  }
  
  public static int readVarInt(ByteBuf buf, int maxSize) {
    byte b0;
    Validate.isTrue((maxSize < 6 && maxSize > 0), "Varint length is between 1 and 5, not %d", maxSize);
    int i = 0;
    int j = 0;
    do {
      b0 = buf.readByte();
      i |= (b0 & Byte.MAX_VALUE) << j++ * 7;
      if (j > maxSize)
        throw new RuntimeException("VarInt too big"); 
    } while ((b0 & 0x80) == 128);
    return i;
  }
  
  public static void writeVarInt(ByteBuf to, int toWrite, int maxSize) {
    Validate.isTrue((varIntByteCount(toWrite) <= maxSize), "Integer is too big for %d bytes", maxSize);
    while ((toWrite & 0xFFFFFF80) != 0) {
      to.writeByte(toWrite & 0x7F | 0x80);
      toWrite >>>= 7;
    } 
    to.writeByte(toWrite);
  }
  
  public static String readUTF8String(ByteBuf from) {
    int len = readVarInt(from, 2);
    String str = from.toString(from.readerIndex(), len, Charsets.UTF_8);
    from.readerIndex(from.readerIndex() + len);
    return str;
  }
  
  public static void writeUTF8String(ByteBuf to, String string) {
    byte[] utf8Bytes = string.getBytes(Charsets.UTF_8);
    Validate.isTrue((varIntByteCount(utf8Bytes.length) < 3), "The string is too long for this encoding.", new Object[0]);
    writeVarInt(to, utf8Bytes.length, 2);
    to.writeBytes(utf8Bytes);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\ByteBufUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */