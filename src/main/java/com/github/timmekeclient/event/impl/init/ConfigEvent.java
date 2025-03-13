package com.github.timmekeclient.event.impl.init;

import com.github.timmekeclient.Config;
import com.github.timmekeclient.event.Event;
import com.github.timmekeclient.feature.base.Module;

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
