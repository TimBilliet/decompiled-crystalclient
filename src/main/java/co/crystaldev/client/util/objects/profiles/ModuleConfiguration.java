package co.crystaldev.client.util.objects.profiles;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class ModuleConfiguration {
    @SerializedName("module_name")
    private final String name;

    @SerializedName("settings")
    private final JsonObject config;

    public ModuleConfiguration(String name, JsonObject config) {
        this.name = name;
        this.config = config;
    }

    public String getName() {
        return this.name;
    }

    public JsonObject getConfig() {
        return this.config;
    }

    public String toString() {
        return String.format("ModuleConfiguration { mod = %s, settings = %d }", new Object[]{this.name, Integer.valueOf(this.config.entrySet().size())});
    }
}
