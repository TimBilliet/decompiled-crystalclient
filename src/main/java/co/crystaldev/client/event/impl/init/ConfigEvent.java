package co.crystaldev.client.event.impl.init;

import co.crystaldev.client.Config;
import co.crystaldev.client.event.Event;
import co.crystaldev.client.feature.base.Module;

public class ConfigEvent extends Event {
    private final Config config;

    public Config getConfig() {
        return this.config;
    }

    public ConfigEvent(Config config) {
        this.config = config;
    }

    public static class Save extends ConfigEvent {
        public Save(Config config) {
            super(config);
        }
    }

    public static class ModuleSave extends ConfigEvent {
        private final Module module;

        public Module getModule() {
            return this.module;
        }

        private ModuleSave(Config config, Module module) {
            super(config);
            this.module = module;
        }

        public static class Pre extends ModuleSave {
            public Pre(Config config, Module module) {
                super(config, module);
            }
        }

        public static class Post extends ModuleSave {
            public Post(Config config, Module module) {
                super(config, module);
            }
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\impl\init\ConfigEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */