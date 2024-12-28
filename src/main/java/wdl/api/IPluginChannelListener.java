package wdl.api;

import net.minecraft.client.multiplayer.WorldClient;

public interface IPluginChannelListener extends IWDLMod {
  void onPluginChannelPacket(WorldClient paramWorldClient, String paramString, byte[] paramArrayOfbyte);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\api\IPluginChannelListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */