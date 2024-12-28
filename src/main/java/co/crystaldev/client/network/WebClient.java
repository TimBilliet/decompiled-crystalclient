//package co.crystaldev.client.network;
//
//import co.crystaldev.client.event.EventBus;
//import co.crystaldev.client.network.socket.NetHandlerClient;
//import co.crystaldev.client.network.socket.client.PacketServerConnection;
//import net.minecraft.client.Minecraft;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.java_websocket.client.WebSocketClient;
//import org.java_websocket.handshake.ServerHandshake;
//
//import java.net.URI;
//import java.nio.ByteBuffer;
//import java.util.Map;
//
//public class WebClient extends WebSocketClient {
//  public static final Logger logger = LogManager.getLogger("WebSocket");
//
//  private static WebClient INSTANCE;
//
//  private final NetHandlerClient handler;
//
//  public NetHandlerClient getHandler() {
//    return this.handler;
//  }
//
//  public WebClient(URI uri, Map<String, String> httpHeaders) {
//    super(uri, httpHeaders);
//    EventBus.register(this.handler = new NetHandlerClient());
//    INSTANCE = this;
//  }
//
//  public void onOpen(ServerHandshake handshake) {
//    logger.info("Connection established (Code: " + handshake.getHttpStatus() + " | Message: " + handshake.getHttpStatusMessage() + ")");
//    if ((Minecraft.getMinecraft()).theWorld != null)
//      this.handler.sendPacket((Packet)new PacketServerConnection(PacketServerConnection.Type.JOIN));
//  }
//
//  public void onClose(int code, String reason, boolean remote) {
//    logger.info("Connection closed by " + (remote ? "client" : "server") + " (Code: " + code + " | Reason: " + reason + ")");
//  }
//
//  public void onMessage(String message) {
//    logger.info("Server sent: " + message);
//  }
//
//  public void onMessage(ByteBuffer message) {
//    this.handler.handlePacket(message.array());
//  }
//
//  public void onError(Exception ex) {
//    logger.error("Error occurred in socket connection", ex);
//  }
//
//  public static WebClient getInstance() {
//    return INSTANCE;
//  }
//}
//
//
///* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\WebClient.class
// * Java compiler version: 8 (52.0)
// * JD-Core Version:       1.1.3
// */