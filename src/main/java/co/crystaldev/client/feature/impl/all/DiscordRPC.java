package co.crystaldev.client.feature.impl.all;

import co.crystaldev.client.Client;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.init.SessionUpdateEvent;
import co.crystaldev.client.event.impl.network.ServerConnectEvent;
import co.crystaldev.client.event.impl.network.ServerDisconnectEvent;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.Toggle;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Module;
import co.crystaldev.client.handler.RPCHandler;

@ModuleInfo(name = "Discord RPC", description = "Displays your current in-game status on Discord", category = Category.ALL)
public class DiscordRPC extends Module implements IRegistrable {
  @Toggle(label = "Show IGN")
  public boolean showIgn = true;

  @Toggle(label = "Show Server")
  public boolean showServer = true;

  private final RPCHandler rpcHandler;

  private static DiscordRPC INSTANCE;

  public DiscordRPC() {
    INSTANCE = this;
    this.rpcHandler = RPCHandler.getInstance();
  }

  public void configPostInit() {
    super.configPostInit();
    onUpdate();
    if (this.enabled)
      this.rpcHandler.start();
  }

  public void toggle() {
    super.toggle();
    if (this.enabled) {
      this.rpcHandler.start();
    } else {
      this.rpcHandler.stop();
    }
  }

  public void onUpdate() {
    if (this.showServer) {
      String server = Client.formatConnectedServerIp();
      this.rpcHandler.setStateLine((this.mc.theWorld == null) ? "Main Menu" : ((this.mc.isSingleplayer() ? "Playing " : "IP: ") + server));
    } else {
      this.rpcHandler.setStateLine("Minecraft 1.8.9");
    }
    this.rpcHandler.setDetailsLine(!this.showIgn ? "In-Game" : ((this.mc.getSession() != null) ? ("IGN: " + this.mc.getSession().getUsername()) : null));
  }

  public static DiscordRPC getInstance() {
    return INSTANCE;
  }

  public void registerEvents() {
    EventBus.register(this, ServerDisconnectEvent.class, ev -> {
          if (this.showServer) {
            this.rpcHandler.setStateLine("Main Menu");
          } else {
            this.rpcHandler.setStateLine("Minecraft 1.8.9");
          }
        });
    EventBus.register(this, ServerConnectEvent.class, ev -> {
          if (this.showServer) {
            this.rpcHandler.setStateLine(ev.isLocal() ? "Playing singleplayer" : ("IP: " + Client.formatConnectedServerIp()));
          } else {
            this.rpcHandler.setStateLine("Minecraft 1.8.9");
          }
        });
    EventBus.register(this, SessionUpdateEvent.class, ev -> this.rpcHandler.setDetailsLine(!this.showIgn ? "In-Game" : ((this.mc.getSession() != null) ? ("IGN: " + this.mc.getSession().getUsername()) : null)));
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\all\DiscordRPC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */