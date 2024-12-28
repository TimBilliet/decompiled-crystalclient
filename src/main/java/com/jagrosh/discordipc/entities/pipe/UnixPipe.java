package com.jagrosh.discordipc.entities.pipe;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.entities.Packet;
import org.json.JSONException;
import org.json.JSONObject;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

public class UnixPipe extends Pipe {
  private final AFUNIXSocket socket;
  
  UnixPipe(IPCClient ipcClient, String location) throws IOException {
    super(ipcClient);
    this.socket = AFUNIXSocket.newInstance();
    this.socket.connect((SocketAddress)new AFUNIXSocketAddress(new File(location)));
  }
  
  public Packet read() throws IOException, JSONException {
    InputStream is = this.socket.getInputStream();
    while (is.available() == 0 && this.status == PipeStatus.CONNECTED) {
      try {
        Thread.sleep(50L);
      } catch (InterruptedException interruptedException) {}
    } 
    if (this.status == PipeStatus.DISCONNECTED)
      throw new IOException("Disconnected!"); 
    if (this.status == PipeStatus.CLOSED)
      return new Packet(Packet.OpCode.CLOSE, null); 
    byte[] d = new byte[8];
    is.read(d);
    ByteBuffer bb = ByteBuffer.wrap(d);
    Packet.OpCode op = Packet.OpCode.values()[Integer.reverseBytes(bb.getInt())];
    d = new byte[Integer.reverseBytes(bb.getInt())];
    is.read(d);
    Packet p = new Packet(op, new JSONObject(new String(d)));
    if (this.listener != null)
      this.listener.onPacketReceived(this.ipcClient, p); 
    return p;
  }
  
  public void write(byte[] b) throws IOException {
    this.socket.getOutputStream().write(b);
  }
  
  public void close() throws IOException {
    send(Packet.OpCode.CLOSE, new JSONObject());
    this.status = PipeStatus.CLOSED;
    this.socket.close();
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\jagrosh\discordipc\entities\pipe\UnixPipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */