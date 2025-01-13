package co.crystaldev.client.event.impl.network;

import co.crystaldev.client.event.Event;

public class WorldEditCuiEvent extends Event {
    public final String type;

    public final String[] args;

    public WorldEditCuiEvent(String input) {
        String[] split = input.split("[|]");
        this.type = split[0];
        this.args = input.substring(this.type.length() + 1).split("[|]");
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\impl\network\WorldEditCuiEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */