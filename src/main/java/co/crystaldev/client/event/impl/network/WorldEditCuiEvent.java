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
