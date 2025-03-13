package com.github.timmekeclient.event.impl.init;

import com.github.timmekeclient.Client;
import com.github.timmekeclient.event.Event;

public class InitializationEvent extends Event {
    private final Client client;

    public Client getClient() {
        return this.client;
    }

    public InitializationEvent(Client client) {
        this.client = client;
    }
}
