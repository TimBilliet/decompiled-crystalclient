package co.crystaldev.client.event.impl.init;

import co.crystaldev.client.Client;
import co.crystaldev.client.event.Event;

public class InitializationEvent extends Event {
    private final Client client;

    public Client getClient() {
        return this.client;
    }

    public InitializationEvent(Client client) {
        this.client = client;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\impl\init\InitializationEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */