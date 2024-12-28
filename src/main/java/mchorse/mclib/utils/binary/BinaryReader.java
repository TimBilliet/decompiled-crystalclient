package mchorse.mclib.utils.binary;

import java.io.IOException;
import java.io.InputStream;

public abstract class BinaryReader {
  public byte[] buf = new byte[4];
  
  public static int b2i(byte b0, byte b1, byte b2, byte b3) {
    return b0 & 0xFF | (b1 & 0xFF) << 8 | (b2 & 0xFF) << 16 | (b3 & 0xFF) << 24;
  }
  
  public int fourChars(char c0, char c1, char c2, char c3) {
    return c3 << 24 & 0xFF000000 | c2 << 16 & 0xFF0000 | c1 << 8 & 0xFF00 | c0 & 0xFF;
  }
  
  public int fourChars(String string) throws Exception {
    char[] chars = string.toCharArray();
    if (chars.length != 4)
      throw new Exception("Given string '" + string + "'"); 
    return fourChars(chars[0], chars[1], chars[2], chars[3]);
  }
  
  public String readFourString(InputStream stream) throws Exception {
    stream.read(this.buf);
    return new String(this.buf);
  }
  
  public int readInt(InputStream stream) throws Exception {
    if (stream.read(this.buf) < 4)
      throw new IOException(); 
    return b2i(this.buf[0], this.buf[1], this.buf[2], this.buf[3]);
  }
  
  public int readShort(InputStream stream) throws Exception {
    if (stream.read(this.buf, 0, 2) < 2)
      throw new IOException(); 
    return b2i(this.buf[0], this.buf[1], (byte)0, (byte)0);
  }
  
  public void skip(InputStream stream, long bytes) throws Exception {
    while (bytes > 0L)
      bytes -= stream.skip(bytes); 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\binary\BinaryReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */