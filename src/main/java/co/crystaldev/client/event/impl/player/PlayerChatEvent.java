package co.crystaldev.client.event.impl.player;

import co.crystaldev.client.event.Cancellable;
import net.minecraft.entity.player.EntityPlayer;

@Cancellable
public class PlayerChatEvent extends PlayerEvent {
    public String message;

    public PlayerChatEvent(EntityPlayer player, String message) {
        super(player);
        this.message = message;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\impl\player\PlayerChatEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */