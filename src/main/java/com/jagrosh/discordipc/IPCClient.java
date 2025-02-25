package com.jagrosh.discordipc;

import com.jagrosh.discordipc.entities.DiscordBuild;
import com.jagrosh.discordipc.entities.Packet;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.User;
import com.jagrosh.discordipc.entities.pipe.Pipe;
import com.jagrosh.discordipc.entities.pipe.PipeStatus;
import com.jagrosh.discordipc.entities.pipe.listener.PipeCreationListener;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import org.json.JSONObject;

import java.io.Closeable;
import java.io.IOException;
import java.lang.management.ManagementFactory;

public final class IPCClient implements Closeable {
    private final long clientId;

    private volatile Pipe pipe;

    private IPCListener listener = null;

    private Thread readThread = null;

    private User user = null;

    public IPCClient(long clientId) {
        this.clientId = clientId;
    }

    private static int getPID() {
        String pr = ManagementFactory.getRuntimeMXBean().getName();
        return Integer.parseInt(pr.substring(0, pr.indexOf('@')));
    }

    public void setListener(IPCListener listener) {
        this.listener = listener;
        if (this.pipe != null)
            this.pipe.setListener(listener);
    }

    public void connect(DiscordBuild... preferredOrder) throws NoDiscordClientException {
        checkConnected(false);
        this.pipe = null;
        PipeCreationListener pipeCreationListener = user -> this.user = user;
        this.pipe = Pipe.openPipe(this, pipeCreationListener, this.clientId, preferredOrder);
        if (this.listener != null)
            this.listener.onReady(this, this.user);
        startReading();
    }

    public void sendRichPresence(RichPresence presence) {
        checkConnected(true);
        this.pipe.send(Packet.OpCode.FRAME, (new JSONObject())
                .put("cmd", "SET_ACTIVITY")
                .put("args", (new JSONObject())
                        .put("pid", getPID())
                        .put("activity", (presence == null) ? null : presence.toJson())));
    }

    public void subscribe(Event sub) {
        checkConnected(true);
        if (!sub.isSubscribable())
            throw new IllegalStateException("Cannot subscribe to " + sub + " event!");
        this.pipe.send(Packet.OpCode.FRAME, (new JSONObject())
                .put("cmd", "SUBSCRIBE")
                .put("evt", sub.name()));
    }

    public PipeStatus getStatus() {
        if (this.pipe == null)
            return PipeStatus.UNINITIALIZED;
        return this.pipe.getStatus();
    }

    public void close() {
        checkConnected(true);
        try {
            this.pipe.close();
        } catch (IOException e) {
            System.err.println("Failed to close pipe: " + e);
        }
    }

    public DiscordBuild getDiscordBuild() {
        if (this.pipe == null)
            return null;
        return this.pipe.getDiscordBuild();
    }

    private void checkConnected(boolean connected) {
        if (connected && getStatus() != PipeStatus.CONNECTED)
            throw new IllegalStateException(String.format("IPCClient (ID: %d) is not connected!", new Object[]{Long.valueOf(this.clientId)}));
        if (!connected && getStatus() == PipeStatus.CONNECTED)
            throw new IllegalStateException(String.format("IPCClient (ID: %d) is already connected!", new Object[]{Long.valueOf(this.clientId)}));
    }

    private void startReading() {
        this.readThread = new Thread(() -> {
            try {
                Packet p;
                while ((p = this.pipe.read()).getOp() != Packet.OpCode.CLOSE) {
                    JSONObject json = p.getJson();
                    if (this.listener != null && json.has("cmd") && json.getString("cmd").equals("DISPATCH"))
                        try {
                            JSONObject u;
                            User user;
                            JSONObject data = json.getJSONObject("data");
                            switch (Event.of(json.getString("evt"))) {
                                case ACTIVITY_JOIN:
                                    this.listener.onActivityJoin(this, data.getString("secret"));
                                case ACTIVITY_SPECTATE:
                                    this.listener.onActivitySpectate(this, data.getString("secret"));
                                case ACTIVITY_JOIN_REQUEST:
                                    u = data.getJSONObject("user");
                                    user = new User(u.getString("username"), u.getString("discriminator"), Long.parseLong(u.getString("id")), u.optString("avatar", null));
                                    this.listener.onActivityJoinRequest(this, data.optString("secret", null), user);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }
                this.pipe.setStatus(PipeStatus.DISCONNECTED);
                if (this.listener != null)
                    this.listener.onClose(this, p.getJson());
            } catch (IOException | org.json.JSONException e) {
                e.printStackTrace();
                this.pipe.setStatus(PipeStatus.DISCONNECTED);
                if (this.listener != null)
                    this.listener.onDisconnect(this, e);
            }
        });
        this.readThread.start();
    }

    public enum Event {
        NULL(false),
        READY(false),
        ERROR(false),
        ACTIVITY_JOIN(true),
        ACTIVITY_SPECTATE(true),
        ACTIVITY_JOIN_REQUEST(true),
        UNKNOWN(false);

        private final boolean subscribable;

        Event(boolean subscribable) {
            this.subscribable = subscribable;
        }

        static Event of(String str) {
            if (str == null)
                return NULL;
            for (Event s : values()) {
                if (s != UNKNOWN && s.name().equalsIgnoreCase(str))
                    return s;
            }
            return UNKNOWN;
        }

        public boolean isSubscribable() {
            return this.subscribable;
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\jagrosh\discordipc\IPCClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */