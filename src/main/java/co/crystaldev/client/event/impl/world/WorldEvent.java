package co.crystaldev.client.event.impl.world;

import co.crystaldev.client.event.Event;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.world.World;

public class WorldEvent extends Event {
  public final World world;
  
  public WorldEvent(World world) {
    this.world = world;
  }
  
  public static class Load extends WorldEvent {
    public final NetHandlerPlayClient netHandler;
    
    public Load(World world, NetHandlerPlayClient netHandler) {
      super(world);
      this.netHandler = netHandler;
    }
  }
  
  public static class Unload extends WorldEvent {
    public Unload(World world) {
      super(world);
    }
  }
  
  public static class SchematicLoad extends Load {
    public SchematicLoad(World world, NetHandlerPlayClient netHandler) {
      super(world, netHandler);
    }
  }
  
  public static class SchematicUnload extends Unload {
    public SchematicUnload(World world) {
      super(world);
    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\impl\world\WorldEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */