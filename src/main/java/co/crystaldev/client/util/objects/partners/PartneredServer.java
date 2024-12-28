package co.crystaldev.client.util.objects.partners;

import com.google.gson.annotations.SerializedName;
import net.minecraft.client.multiplayer.ServerData;

public class PartneredServer {
  @SerializedName("ip")
  public final String ip;
  
  @SerializedName("name")
  public final String name;
  
  public PartneredServer(String ip, String name) {
    this.ip = ip;
    this.name = name;
  }
  
  public ServerData getData() {
    return new PartneredServerData(this.name, this.ip, false);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\objects\partners\PartneredServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */