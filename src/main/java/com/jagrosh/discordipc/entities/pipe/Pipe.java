package com.jagrosh.discordipc.entities.pipe;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.DiscordBuild;
import com.jagrosh.discordipc.entities.Packet;
import com.jagrosh.discordipc.entities.User;
import com.jagrosh.discordipc.entities.pipe.listener.PipeCreationListener;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

public abstract class Pipe {
  private static final int VERSION = 1;
  
  private static final String[] unixPaths = new String[] { "XDG_RUNTIME_DIR", "TMPDIR", "TMP", "TEMP" };
  
  final IPCClient ipcClient;
  
  PipeStatus status = PipeStatus.CONNECTING;
  
  IPCListener listener;
  
  private DiscordBuild build;
  
  Pipe(IPCClient ipcClient) {
    this.ipcClient = ipcClient;
  }
  
  public static Pipe openPipe(IPCClient ipcClient, PipeCreationListener pipeCreationListener, long clientId, DiscordBuild... preferredOrder) throws NoDiscordClientException {
    if (preferredOrder == null || preferredOrder.length == 0)
      preferredOrder = new DiscordBuild[] { DiscordBuild.ANY }; 
    Pipe pipe = null;
    Pipe[] open = new Pipe[(DiscordBuild.values()).length];
    int i;
    for (i = 0; i < 10; i++) {
      try {
        String location = getPipeLocation(i);
        pipe = createPipe(ipcClient, location);
        pipe.send(Packet.OpCode.HANDSHAKE, (new JSONObject()).put("v", 1).put("client_id", Long.toString(clientId)));
        Packet p = pipe.read();
        JSONObject data = p.getJson().getJSONObject("data");
        JSONObject userObject = data.getJSONObject("user");
        pipe.build = DiscordBuild.from(data.getJSONObject("config").getString("api_endpoint"));
        if (pipe.build == preferredOrder[0] || DiscordBuild.ANY == preferredOrder[0]) {
          pipeCreationListener.onUserFound(new User(userObject
                .getString("username"), userObject
                .getString("discriminator"), 
                Long.parseLong(userObject.getString("id")), userObject
                .optString("avatar", null)));
          break;
        } 
        open[pipe.build.ordinal()] = pipe;
        open[DiscordBuild.ANY.ordinal()] = pipe;
        pipe.build = null;
        pipe = null;
      } catch (IOException|RuntimeException ignored) {
        pipe = null;
      } 
    } 
    if (pipe == null) {
      for (i = 1; i < preferredOrder.length; i++) {
        DiscordBuild cb = preferredOrder[i];
        if (open[cb.ordinal()] != null) {
          pipe = open[cb.ordinal()];
          open[cb.ordinal()] = null;
          if (cb == DiscordBuild.ANY) {
            for (int k = 0; k < open.length; k++) {
              if (open[k] == pipe) {
                pipe.build = DiscordBuild.values()[k];
                open[k] = null;
              } 
            } 
            break;
          } 
          pipe.build = cb;
          break;
        } 
      } 
      if (pipe == null)
        throw new NoDiscordClientException(); 
    } 
    for (i = 0; i < open.length; i++) {
      if (i != DiscordBuild.ANY.ordinal())
        if (open[i] != null)
          try {
            open[i].close();
          } catch (IOException ex) {
            ex.printStackTrace();
          }   
    } 
    pipe.status = PipeStatus.CONNECTED;
    return pipe;
  }
  
  private static Pipe createPipe(IPCClient ipcClient, String location) {
    String osName = System.getProperty("os.name").toLowerCase();
    if (osName.contains("win"))
      return new WindowsPipe(ipcClient, location); 
    if (osName.contains("linux") || osName.contains("mac"))
      try {
        return new UnixPipe(ipcClient, location);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }  
    throw new RuntimeException("Unsupported OS: " + osName);
  }
  
  private static String generateNonce() {
    return UUID.randomUUID().toString();
  }
  
  private static String getPipeLocation(int i) {
    if (System.getProperty("os.name").contains("Win"))
      return "\\\\?\\pipe\\discord-ipc-" + i; 
    String tmppath = null;
    for (String str : unixPaths) {
      tmppath = System.getenv(str);
      if (tmppath != null)
        break; 
    } 
    if (tmppath == null)
      tmppath = "/tmp"; 
    return tmppath + "/discord-ipc-" + i;
  }
  
  public void send(Packet.OpCode op, JSONObject data) {
    try {
      String nonce = generateNonce();
      Packet p = new Packet(op, data.put("nonce", nonce));
      write(p.toBytes());
      if (this.listener != null)
        this.listener.onPacketSent(this.ipcClient, p); 
    } catch (IOException ex) {
      this.status = PipeStatus.DISCONNECTED;
    } 
  }
  
  public abstract Packet read() throws IOException, JSONException;
  
  public abstract void write(byte[] paramArrayOfbyte) throws IOException;
  
  public PipeStatus getStatus() {
    return this.status;
  }
  
  public void setStatus(PipeStatus status) {
    this.status = status;
  }
  
  public void setListener(IPCListener listener) {
    this.listener = listener;
  }
  
  public abstract void close() throws IOException;
  
  public DiscordBuild getDiscordBuild() {
    return this.build;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\jagrosh\discordipc\entities\pipe\Pipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */