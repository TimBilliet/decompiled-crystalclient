package co.crystaldev.client.event.data;

import co.crystaldev.client.event.Event;

public abstract class AbstractEventData {
    public final Object source;

    public final byte priority;

    public AbstractEventData(Object source, byte priority) {
        this.source = source;
        this.priority = priority;
    }

    public abstract void call(Object paramObject, Event paramEvent);
}
