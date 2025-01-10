package co.crystaldev.client.event.impl.player;

import co.crystaldev.client.event.Cancellable;
import co.crystaldev.client.event.Event;

public class InputEvent extends Event {
  @Cancellable
  public static class Key extends InputEvent {
    private final int keyCode;
    
    private final char characterTyped;
    
    private final boolean keyDown;
    
    public Key(int keyCode, char characterTyped, boolean keyDown) {
      this.keyCode = keyCode;
      this.characterTyped = characterTyped;
      this.keyDown = keyDown;
    }
    
    public int getKeyCode() {
      return this.keyCode;
    }
    
    public char getCharacterTyped() {
      return this.characterTyped;
    }
    
    public boolean isKeyDown() {
      return this.keyDown;
    }
  }
  
  @Cancellable
  public static class Mouse extends InputEvent {

    public final int x = org.lwjgl.input.Mouse.getEventX();
    
    public final int y = org.lwjgl.input.Mouse.getEventY();
    
    public final int dx = org.lwjgl.input.Mouse.getEventDX();
    
    public final int dy = org.lwjgl.input.Mouse.getEventDY();
    
    public final int dWheel = org.lwjgl.input.Mouse.getEventDWheel();
    
    public final int button = org.lwjgl.input.Mouse.getEventButton();
    
    public final boolean buttonState = org.lwjgl.input.Mouse.getEventButtonState();
    
    public final long nanoseconds = org.lwjgl.input.Mouse.getEventNanoseconds();
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\impl\player\InputEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */