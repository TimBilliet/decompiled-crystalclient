package co.crystaldev.client.util;

import net.minecraft.event.ClickEvent;
import net.minecraft.util.IChatComponent;

import java.util.function.Consumer;

public class CallbackClickEvent extends ClickEvent {
    private final Consumer<IChatComponent> consumer;

    public Consumer<IChatComponent> getConsumer() {
        return this.consumer;
    }

    public CallbackClickEvent(Consumer<IChatComponent> consumer) {
        super(null, null);
        this.consumer = consumer;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\CallbackClickEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */