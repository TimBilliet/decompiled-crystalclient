package co.crystaldev.client.event.impl.init;

import co.crystaldev.client.event.Event;
import net.minecraft.client.renderer.texture.TextureMap;

public class TextureStitchEvent extends Event {
  public final TextureMap map;
  
  public TextureStitchEvent(TextureMap map) {
    this.map = map;
  }
  
  public static class Pre extends TextureStitchEvent {
    public Pre(TextureMap map) {
      super(map);
    }
  }
  
  public static class Post extends TextureStitchEvent {
    public Post(TextureMap map) {
      super(map);
    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\impl\init\TextureStitchEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */