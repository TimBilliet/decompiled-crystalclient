package org.newsclub.net.unix;

import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

public final class AFUNIXSocket extends Socket {
  static String loadedLibrary;
  
  private static Integer capabilities = null;
  
  AFUNIXSocketImpl impl;
  
  AFUNIXSocketAddress addr;
  
  private final AFUNIXSocketFactory socketFactory;
  
  private final Closeables closeables = new Closeables();
  
  private AFUNIXSocket(AFUNIXSocketImpl impl, AFUNIXSocketFactory factory) throws IOException {
    super(impl);
    this.socketFactory = factory;
    if (factory == null)
      setIsCreated(); 
  }
  
  private void setIsCreated() throws IOException {
    try {
      NativeUnixSocket.setCreated(this);
    } catch (LinkageError e) {
      throw new IOException("Couldn't load native library", e);
    } 
  }
  
  public static AFUNIXSocket newInstance() throws IOException {
    return newInstance(null);
  }
  
  static AFUNIXSocket newInstance(AFUNIXSocketFactory factory) throws IOException {
    AFUNIXSocketImpl impl = new AFUNIXSocketImpl.Lenient();
    AFUNIXSocket instance = new AFUNIXSocket(impl, factory);
    instance.impl = impl;
    return instance;
  }
  
  public static AFUNIXSocket newStrictInstance() throws IOException {
    AFUNIXSocketImpl impl = new AFUNIXSocketImpl();
    AFUNIXSocket instance = new AFUNIXSocket(impl, null);
    instance.impl = impl;
    return instance;
  }
  
  public static AFUNIXSocket connectTo(AFUNIXSocketAddress addr) throws IOException {
    AFUNIXSocket socket = newInstance();
    socket.connect(addr);
    return socket;
  }
  
  public void bind(SocketAddress bindpoint) throws IOException {
    super.bind(bindpoint);
    this.addr = (AFUNIXSocketAddress)bindpoint;
  }
  
  public void connect(SocketAddress endpoint) throws IOException {
    connect(endpoint, 0);
  }
  
  public void connect(SocketAddress endpoint, int timeout) throws IOException {
    if (endpoint == null)
      throw new IllegalArgumentException("connect: The address can't be null"); 
    if (timeout < 0)
      throw new IllegalArgumentException("connect: timeout can't be negative"); 
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    if (!(endpoint instanceof AFUNIXSocketAddress)) {
      if (this.socketFactory != null && 
        endpoint instanceof InetSocketAddress) {
        InetSocketAddress isa = (InetSocketAddress)endpoint;
        String hostname = isa.getHostString();
        if (this.socketFactory.isHostnameSupported(hostname))
          endpoint = this.socketFactory.addressFromHost(hostname, isa.getPort()); 
      } 
      if (!(endpoint instanceof AFUNIXSocketAddress))
        throw new IllegalArgumentException("Can only connect to endpoints of type " + AFUNIXSocketAddress.class
            .getName() + ", got: " + endpoint); 
    } 
    this.impl.connect(endpoint, timeout);
    this.addr = (AFUNIXSocketAddress)endpoint;
    NativeUnixSocket.setBound(this);
    NativeUnixSocket.setConnected(this);
  }
  
  public String toString() {
    if (isConnected())
      return "AFUNIXSocket[fd=" + this.impl.getFD() + ";addr=" + this.addr.toString() + "]"; 
    return "AFUNIXSocket[unconnected]";
  }
  
  public static boolean isSupported() {
    return NativeUnixSocket.isLoaded();
  }
  
  public static String getVersion() {
    try {
      return NativeLibraryLoader.getJunixsocketVersion();
    } catch (IOException e) {
      return null;
    } 
  }
  
  public static String getLoadedLibrary() {
    return loadedLibrary;
  }
  
  public AFUNIXSocketCredentials getPeerCredentials() throws IOException {
    if (isClosed() || !isConnected())
      throw new SocketException("Not connected"); 
    return this.impl.getPeerCredentials();
  }
  
  public boolean isClosed() {
    return (super.isClosed() || (isConnected() && !this.impl.getFD().valid()));
  }
  
  public int getAncillaryReceiveBufferSize() {
    return this.impl.getAncillaryReceiveBufferSize();
  }
  
  public void setAncillaryReceiveBufferSize(int size) {
    this.impl.setAncillaryReceiveBufferSize(size);
  }
  
  public void ensureAncillaryReceiveBufferSize(int minSize) {
    this.impl.ensureAncillaryReceiveBufferSize(minSize);
  }
  
  public FileDescriptor[] getReceivedFileDescriptors() throws IOException {
    return this.impl.getReceivedFileDescriptors();
  }
  
  public void clearReceivedFileDescriptors() {
    this.impl.clearReceivedFileDescriptors();
  }
  
  public void setOutboundFileDescriptors(FileDescriptor... fdescs) throws IOException {
    this.impl.setOutboundFileDescriptors(fdescs);
  }
  
  private static synchronized int getCapabilities() {
    if (capabilities == null)
      if (!isSupported()) {
        capabilities = Integer.valueOf(0);
      } else {
        capabilities = Integer.valueOf(NativeUnixSocket.capabilities());
      }  
    return capabilities.intValue();
  }
  
  public static boolean supports(AFUNIXSocketCapability capability) {
    return ((getCapabilities() & capability.getBitmask()) != 0);
  }
  
  public synchronized void close() throws IOException {
    IOException superException = null;
    try {
      super.close();
    } catch (IOException e) {
      superException = e;
    } 
    this.closeables.close(superException);
  }
  
  public void addCloseable(Closeable closeable) {
    this.closeables.add(closeable);
  }
  
  public void removeCloseable(Closeable closeable) {
    this.closeables.remove(closeable);
  }
  
  public static void main(String[] args) {
    System.out.print("AFUNIXSocket.isSupported(): ");
    System.out.flush();
    System.out.println(isSupported());
    for (AFUNIXSocketCapability cap : AFUNIXSocketCapability.values()) {
      System.out.print(cap + ": ");
      System.out.flush();
      System.out.println(supports(cap));
    } 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\org\newsclub\ne\\unix\AFUNIXSocket.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */