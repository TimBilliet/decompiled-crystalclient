package com.github.timmekeclient.feature.impl.all;

import com.github.timmekeclient.Client;
import com.github.timmekeclient.event.EventBus;
import com.github.timmekeclient.event.IRegistrable;
import com.github.timmekeclient.event.impl.init.SessionUpdateEvent;
import com.github.timmekeclient.event.impl.network.ServerConnectEvent;
import com.github.timmekeclient.event.impl.network.ServerDisconnectEvent;
import com.github.timmekeclient.feature.annotations.properties.ModuleInfo;
import com.github.timmekeclient.feature.annotations.properties.Toggle;
import com.github.timmekeclient.feature.base.Category;
import com.github.timmekeclient.feature.base.Module;
import com.github.timmekeclient.handler.RPCHandler;

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
