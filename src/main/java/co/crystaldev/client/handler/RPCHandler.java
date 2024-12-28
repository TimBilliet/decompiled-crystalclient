package co.crystaldev.client.handler;

import co.crystaldev.client.Reference;
import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.User;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import org.json.JSONObject;

import java.time.OffsetDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RPCHandler implements IPCListener {
  private static final long APPLICATION_ID = 798721912616124416L;

  private static final long UPDATE_PERIOD = 120000L;

  private static final RPCHandler INSTANCE = new RPCHandler();

  private final Executor executor = Executors.newSingleThreadScheduledExecutor();

  private String stateLine;

  private String detailsLine;

  private IPCClient client;

  private OffsetDateTime startTimestamp;

  private Timer updateTimer;

  private boolean connected;

  public void start() {
    this.executor.execute(() -> {
          try {
            if (isActive())
              return;
            this.startTimestamp = OffsetDateTime.now();
            this.client = new IPCClient(798721912616124416L);
            this.client.setListener(this);
            try {
              this.client.connect(new com.jagrosh.discordipc.entities.DiscordBuild[0]);
              updatePresence();
            } catch (NoDiscordClientException ex) {
              Reference.LOGGER.error("No Discord Client detected", (Throwable)ex);
            }
          } catch (Exception ex) {
            Reference.LOGGER.error("DiscordRPC has thrown an unexpected error while trying to start", ex);
          }
        });
  }

  public void stop() {
    if (isActive()) {
      this.client.close();
      this.connected = false;
    }
  }

  public boolean isActive() {
    return (this.client != null && this.connected);
  }

  public void updatePresence() {
    try {
      RichPresence.Builder builder = (new RichPresence.Builder()).setState(this.stateLine).setDetails(this.detailsLine).setStartTimestamp(this.startTimestamp).setLargeImage("image", String.format("v%s by Crystal Development", new Object[] { "1.1.16-projectassfucker" }));
      if (this.client != null)
        this.client.sendRichPresence(builder.build());
    } catch (Exception ex) {
      Reference.LOGGER.error("Failed to update DiscordRPC", ex);
    }
  }

  public void setStateLine(String state) {
    this.stateLine = state;
    if (isActive())
      updatePresence();
  }

  public void setDetailsLine(String details) {
    this.detailsLine = details;
    if (isActive())
      updatePresence();
  }

  public void onReady(IPCClient client, User user) {
    this.connected = true;
    this.updateTimer = new Timer();
    this.updateTimer.schedule(new TimerTask() {
          public void run() {
            RPCHandler.this.updatePresence();
          }
        },  0L, 120000L);
  }

  public void onClose(IPCClient client, JSONObject json) {
    this.client = null;
    this.connected = false;
    cancelTimer();
  }

  public void onDisconnect(IPCClient client, Throwable t) {
    this.client = null;
    this.connected = false;
    cancelTimer();
  }

  private void cancelTimer() {
    if (this.updateTimer != null) {
      this.updateTimer.cancel();
      this.updateTimer = null;
    }
  }

  public static RPCHandler getInstance() {
    return INSTANCE;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\handler\RPCHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */