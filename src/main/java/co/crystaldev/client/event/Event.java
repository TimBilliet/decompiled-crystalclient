package co.crystaldev.client.event;

import co.crystaldev.client.event.data.AbstractEventData;

import java.util.List;

public abstract class Event {
    private boolean cancelled = false;

    public Event call() {
        call(this);
        return this;
    }

    private static void call(Event event) {
        List<AbstractEventData> dataList = EventBus.get(event.getClass());
        if (dataList != null)
            dataList.forEach(data -> data.call(data.source, event));
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public boolean isCancellable() {
        return getClass().isAnnotationPresent(Cancellable.class);
    }

    public void setCancelled(boolean cancelled) {
        if (getClass().isAnnotationPresent(Cancellable.class)) {
            this.cancelled = cancelled;
        } else {
            throw new IllegalStateException("Attempted to cancel an non-cancellable event");
        }
    }

    public static final class Priority {
        public static final byte HIGHEST = 0;

        public static final byte HIGHER = 1;

        public static final byte NORMAL = 2;

        public static final byte LOWER = 3;

        public static final byte LOWEST = 5;

        public static final byte[] PRIORITIES = new byte[]{0, 1, 2, 3, 5};
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\Event.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */