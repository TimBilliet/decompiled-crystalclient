package co.crystaldev.client.event.data;

import co.crystaldev.client.Reference;
import co.crystaldev.client.event.Event;

import java.lang.reflect.Method;

public class ReflectiveEventData extends AbstractEventData {
    public final Method target;

    public ReflectiveEventData(Object source, Method target, byte priority) {
        super(source, priority);
        this.target = target;
    }

    public void call(Object source, Event event) {
        try {
            this.target.invoke(source, new Object[]{event});
        } catch (IllegalAccessException | java.lang.reflect.InvocationTargetException ex) {
            Reference.LOGGER.error("Error calling event (Class: {} / Method: {})", new Object[]{source.getClass().getSimpleName(), this.target.getName(), ex});
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\data\ReflectiveEventData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */