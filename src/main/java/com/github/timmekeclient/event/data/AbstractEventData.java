package com.github.timmekeclient.event.data;

import com.github.timmekeclient.event.Event;

public abstract class AbstractEventData {
    public final Object source;

    public final byte priority;

    public AbstractEventData(Object source, byte priority) {
        this.source = source;
        this.priority = priority;
    }

    public abstract void call(Object paramObject, Event paramEvent);
}
