package co.crystaldev.client.event.impl.player;

import co.crystaldev.client.event.Event;
import net.minecraft.util.MovementInput;

public class MovementInputUpdateEvent extends Event {
    public final MovementInput input;

    public MovementInputUpdateEvent(MovementInput input) {
        this.input = input;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\impl\player\MovementInputUpdateEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */