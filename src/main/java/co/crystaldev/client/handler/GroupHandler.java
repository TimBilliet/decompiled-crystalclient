package co.crystaldev.client.handler;

import co.crystaldev.client.Client;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.network.ServerConnectEvent;
import co.crystaldev.client.event.impl.network.ServerDisconnectEvent;
import co.crystaldev.client.event.impl.player.InputEvent;
import co.crystaldev.client.event.impl.player.PlayerChatEvent;
import co.crystaldev.client.event.impl.render.RenderPlayerEvent;
import co.crystaldev.client.feature.settings.ClientOptions;
import co.crystaldev.client.feature.settings.GroupOptions;
import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.group.StatusUpdateTask;
import co.crystaldev.client.network.Packet;
import co.crystaldev.client.network.socket.client.group.PacketGroupChat;
import co.crystaldev.client.network.socket.client.group.PacketPingLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GroupHandler implements IRegistrable {
    private ScheduledFuture<?> statusUpdateFuture;

    public void registerEvents() {
        EventBus.register(this, ServerConnectEvent.class, ev -> this.statusUpdateFuture = Client.getInstance().getExecutor().scheduleAtFixedRate((Runnable) new StatusUpdateTask(), 0L, 3000L, TimeUnit.MILLISECONDS));
        EventBus.register(this, ServerDisconnectEvent.class, ev -> {
            this.statusUpdateFuture.cancel(true);
            this.statusUpdateFuture = null;
        });
        EventBus.register(this, RenderPlayerEvent.Pre.class, ev -> {
            if (GroupManager.getSelectedGroup() == null || GroupManager.getSelectedGroup().getFocusedId() == null)
                return;
            if (ev.player.getUniqueID().equals(GroupManager.getSelectedGroup().getFocusedId()))
                GL11.glColor4d(1.0D, 0.333D, 0.333D, 1.0D);
        });
        EventBus.register(this, InputEvent.Key.class, ev -> {
            if ((GroupOptions.getInstance()).groupChatToggle.isPressed()) {
                Client.sendMessage(ClientOptions.getInstance().getToggleMessage("Group Chat", (GroupOptions.getInstance()).groupChat = !(GroupOptions.getInstance()).groupChat), true);
            } else if (GroupManager.getSelectedGroup() != null && (GroupOptions.getInstance()).pingLocation.isPressed() && (Minecraft.getMinecraft()).thePlayer != null) {
                int x = MathHelper.floor_double((Minecraft.getMinecraft()).thePlayer.posX);
                int y = MathHelper.floor_double((Minecraft.getMinecraft()).thePlayer.posY);
                int z = MathHelper.floor_double((Minecraft.getMinecraft()).thePlayer.posZ);
                PacketPingLocation packet = new PacketPingLocation(x, y, z);
                Client.sendPacket((Packet) packet);
            }
        });
        EventBus.register(this, PlayerChatEvent.class, ev -> {
            if (!ev.message.startsWith("/") && (GroupOptions.getInstance()).groupChat && GroupManager.getSelectedGroup() != null) {
            PacketGroupChat packet = new PacketGroupChat(ev.message);
            Client.sendPacket((Packet)packet);
                ev.setCancelled(true);
            }
        });
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\handler\GroupHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */