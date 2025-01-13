package co.crystaldev.client.event.impl.init;

import co.crystaldev.client.event.Event;
import co.crystaldev.client.feature.base.Module;

import java.lang.reflect.Field;

public class ModuleOptionUpdateEvent extends Event {
    private final Module module;

    private final Field field;

    private final String optionName;

    public ModuleOptionUpdateEvent(Module module, Field field, String optionName) {
        this.module = module;
        this.field = field;
        this.optionName = optionName;
    }

    public String toString() {
        return "ModuleOptionUpdateEvent(module=" + getModule() + ", field=" + getField() + ", optionName=" + getOptionName() + ")";
    }

    public Module getModule() {
        return this.module;
    }

    public Field getField() {
        return this.field;
    }

    public String getOptionName() {
        return this.optionName;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\impl\init\ModuleOptionUpdateEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */