package co.crystaldev.client.cache;

import co.crystaldev.client.util.task.UsernameTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UsernameCache {
  private static UsernameCache INSTANCE = null;
  
  private final Map<UUID, UsernameTask> cache;
  
  public UsernameCache() {
    INSTANCE = this;
    this.cache = new ConcurrentHashMap<>();
  }
  
  public String getUsername(UUID uuid) {
    if (uuid == null)
      return "< Invalid UUID provided >"; 
    UsernameTask task = this.cache.computeIfAbsent(uuid, id -> {
          UsernameTask usernameTask = new UsernameTask(id);
          Thread thread = new Thread((Runnable)usernameTask);
          thread.setDaemon(true);
          thread.start();
          return usernameTask;
        });
    return task.isFetching() ? "Fetching IGN..." : task.getUsername();
  }
  
  public static UsernameCache getInstance() {
    return (INSTANCE == null) ? new UsernameCache() : INSTANCE;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\cache\UsernameCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */