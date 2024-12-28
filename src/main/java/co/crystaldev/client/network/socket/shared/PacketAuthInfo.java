package co.crystaldev.client.network.socket.shared;

import co.crystaldev.client.Reference;
import co.crystaldev.client.network.ByteBufWrapper;
import co.crystaldev.client.network.INetHandler;
import co.crystaldev.client.network.Packet;
import co.crystaldev.client.util.BrokenHash;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.io.IOException;
import java.util.UUID;

public class PacketAuthInfo extends Packet {
  private static final Minecraft mc = Minecraft.getMinecraft();
  
  private static final YggdrasilAuthenticationService yas = new YggdrasilAuthenticationService(mc.getProxy(), UUID.randomUUID().toString());
  
  private static final YggdrasilMinecraftSessionService ymss = (YggdrasilMinecraftSessionService)yas.createMinecraftSessionService();
  
  private String hash;
  
  public void write(ByteBufWrapper out) throws IOException {}
  
  public void read(ByteBufWrapper in) throws IOException {
    this.hash = in.readString();
  }
  
  public void process(INetHandler handler) {
    Session session = mc.getSession();
    Thread thread = new Thread(() -> {
          try {
            ymss.joinServer(session.getProfile(), session.getToken(), BrokenHash.hash(this.hash));
            handler.sendPacket(this);
          } catch (AuthenticationException ex) {
            Reference.LOGGER.error("Unable to authenticate");
          } 
        });
    thread.setDaemon(true);
    thread.start();
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\socket\shared\PacketAuthInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */