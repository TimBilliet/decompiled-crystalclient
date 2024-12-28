package org.newsclub.net.unix;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Locale;

public final class AFUNIXSocketAddress extends InetSocketAddress {
  private static final long serialVersionUID = 1L;
  
  private final byte[] bytes;
  
  public AFUNIXSocketAddress(File socketFile) throws IOException {
    this(socketFile, 0);
  }
  
  public AFUNIXSocketAddress(File socketFile, int port) throws IOException {
    this(socketFile.getCanonicalPath().getBytes(Charset.defaultCharset()), port);
  }
  
  public AFUNIXSocketAddress(byte[] socketAddress) throws IOException {
    this(socketAddress, 0);
  }
  
  public AFUNIXSocketAddress(byte[] socketAddress, int port) throws IOException {
    super(InetAddress.getLoopbackAddress(), 0);
    if (port != 0)
      NativeUnixSocket.setPort1(this, port); 
    if (socketAddress.length == 0)
      throw new SocketException("Illegal address length: " + socketAddress.length); 
    this.bytes = (byte[])socketAddress.clone();
  }
  
  public static AFUNIXSocketAddress inAbstractNamespace(String name) throws IOException {
    return inAbstractNamespace(name, 0);
  }
  
  public static AFUNIXSocketAddress inAbstractNamespace(String name, int port) throws IOException {
    byte[] bytes = name.getBytes(Charset.defaultCharset());
    byte[] addr = new byte[bytes.length + 1];
    System.arraycopy(bytes, 0, addr, 1, bytes.length);
    return new AFUNIXSocketAddress(addr, port);
  }
  
  byte[] getBytes() {
    return this.bytes;
  }
  
  private static String prettyPrint(byte[] data) {
    int dataLength = data.length;
    StringBuilder sb = new StringBuilder(dataLength + 16);
    for (int i = 0; i < dataLength; i++) {
      byte c = data[i];
      if (c >= 32 && c < Byte.MAX_VALUE) {
        sb.append((char)c);
      } else {
        sb.append("\\x");
        sb.append(String.format(Locale.ENGLISH, "%02x", new Object[] { Byte.valueOf(c) }));
      } 
    } 
    return sb.toString();
  }
  
  public String toString() {
    return getClass().getName() + "[port=" + getPort() + ";path=" + prettyPrint(this.bytes) + "]";
  }
  
  public String getPath() {
    byte[] by = getPathAsBytes();
    for (int i = 1; i < by.length; i++) {
      byte b = by[i];
      if (b == 0) {
        by[i] = 64;
      } else if (b < 32 || b == Byte.MAX_VALUE) {
        by[i] = 46;
      } 
    } 
    return new String(by, Charset.defaultCharset());
  }
  
  public byte[] getPathAsBytes() {
    return (byte[])this.bytes.clone();
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\org\newsclub\ne\\unix\AFUNIXSocketAddress.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */