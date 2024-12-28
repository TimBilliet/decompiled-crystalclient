package co.crystaldev.client.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ByteBufWrapper {
  private final ByteBuf buf;
  
  public ByteBufWrapper(ByteBuf buf) {
    this.buf = buf;
  }
  
  public void writeByteArray(byte[] bytes) {
    this.buf.writeBytes(bytes);
  }
  
  public byte[] readByteArray() {
    int len = readVarInt();
    byte[] buffer = new byte[len];
    this.buf.readBytes(buffer);
    return buffer;
  }
  
  public void writeVarInt(int b) {
    while ((b & 0xFFFFFF80) != 0) {
      this.buf.writeByte(b & 0x7F | 0x80);
      b >>>= 7;
    } 
    this.buf.writeByte(b);
  }
  
  public int readVarInt() {
    int i = 0;
    int chunk = 0;
    while (true) {
      byte b = this.buf.readByte();
      i |= (b & Byte.MAX_VALUE) << chunk++ * 7;
      if (chunk > 5)
        throw new RuntimeException("VarInt too big"); 
      if ((b & 0x80) != 128)
        return i; 
    } 
  }
  
  public void writeString(String s) {
    byte[] arr = s.getBytes(StandardCharsets.UTF_8);
    writeVarInt(arr.length);
    this.buf.writeBytes(arr);
  }
  
  public String readString() {
    int len = readVarInt();
    byte[] buffer = new byte[len];
    this.buf.readBytes(buffer);
    return new String(buffer, StandardCharsets.UTF_8);
  }
  
  public void writeUUID(UUID uuid) {
    this.buf.writeLong(uuid.getMostSignificantBits());
    this.buf.writeLong(uuid.getLeastSignificantBits());
  }
  
  public UUID readUUID() {
    long mostSigBits = this.buf.readLong();
    long leastSigBits = this.buf.readLong();
    return new UUID(mostSigBits, leastSigBits);
  }
  
  public void writeBool(boolean b) {
    String s = String.valueOf(b);
    byte[] arr = s.getBytes(StandardCharsets.UTF_8);
    writeVarInt(arr.length);
    this.buf.writeBytes(arr);
  }
  
  public boolean readBool() {
    int len = readVarInt();
    byte[] buffer = new byte[len];
    this.buf.readBytes(buffer);
    return Boolean.parseBoolean(new String(buffer, StandardCharsets.UTF_8));
  }
  
  public ItemStack readItemStack() {
    try {
      PacketBuffer buffer = new PacketBuffer(this.buf);
      return buffer.readItemStackFromBuffer();
    } catch (IOException ex) {
      return null;
    } 
  }
  
  public void writeItemStack(ItemStack stack) {
    PacketBuffer buffer = new PacketBuffer(this.buf);
    buffer.writeItemStackToBuffer(stack);
  }
  
  public ByteBuf buf() {
    return this.buf;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\ByteBufWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */