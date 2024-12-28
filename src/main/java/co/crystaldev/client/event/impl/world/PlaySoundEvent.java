package co.crystaldev.client.event.impl.world;

import co.crystaldev.client.event.Cancellable;
import co.crystaldev.client.event.Event;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.util.ResourceLocation;

@Cancellable
public class PlaySoundEvent extends Event {
  public final SoundManager manager;
  
  public final String name;
  
  public final ResourceLocation location;
  
  public final SoundCategory category;
  
  public PlaySoundEvent(SoundManager manager, ResourceLocation location, SoundCategory category) {
    this.manager = manager;
    this.location = location;
    this.name = this.location.getResourcePath();
    this.category = category;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\impl\world\PlaySoundEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */