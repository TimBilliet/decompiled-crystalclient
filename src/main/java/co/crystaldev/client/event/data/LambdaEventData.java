package co.crystaldev.client.event.data;

import co.crystaldev.client.event.Event;

import java.util.function.Consumer;

public class LambdaEventData extends AbstractEventData {
    public final Consumer<Event> consumer;

    public LambdaEventData(Object source, Consumer<Event> consumer, byte priority) {
        super(source, priority);
        this.consumer = consumer;
    }

    public void call(Object source, Event event) {
        if (this.consumer != null)
            this.consumer.accept(event);
    }
}