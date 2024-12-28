package co.crystaldev.client.feature.base;

import co.crystaldev.client.Client;
import co.crystaldev.client.Config;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.feature.annotations.Hidden;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.Toggle;
import co.crystaldev.client.handler.NotificationHandler;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;

public abstract class Module {
    @Toggle(label = "Enabled")
    @Hidden
    public boolean enabled = true;

    @Toggle(label = "Hoisted")
    @Hidden
    public boolean hoisted = false;

    private final transient UUID UNIQUE_ID = UUID.randomUUID();

    public final String name;

    public final String[] nameAliases;

    public final String description;

    public final Category category;

    public final boolean newMod;

    public int priority = 1000;

    public ResourceLocation icon;

    public KeyBinding toggleKeyBinding = null;

    public boolean forceDisabled = false;

    public boolean wasEnabled = false;

    public boolean canBeDisabled = true;

    private int configurableOptions;

    private final Map<String, Predicate<Field>> optionVisibility;

    public final Minecraft mc = Minecraft.getMinecraft();

    public final Client client = Client.getInstance();

    public Module() {
        ModuleInfo info = getClass().<ModuleInfo>getAnnotation(ModuleInfo.class);
        this.name = info.name();
        this.nameAliases = info.nameAliases();
        this.description = info.description();
        this.category = info.category();
        this.newMod = info.isNew();
        this.icon = getIcon();
        this.optionVisibility = new HashMap<>();
    }

    private ResourceLocation getIcon() {
        ResourceLocation resource = new ResourceLocation("crystalclient", "gui/module_icons/" + getSanitizedName() + ".png");
        try {
            Minecraft.getMinecraft().getResourceManager().getResource(resource);
            return resource;
        } catch (IOException ex) {
            System.out.println("foutje bij getIcon resourcelocation " + getSanitizedName());
            return null;
        }
    }

    public void init() {}

    public void enable() {
        if (this.forceDisabled)
            return;
        if (!this.enabled) {
            this.enabled = true;
            EventBus.register(this);
            onUpdate();
        }
    }

    public void disable() {
        if (!this.canBeDisabled) {
            if (!this.enabled) {
                this.enabled = true;
                EventBus.register(this);
            }
        } else if (this.enabled) {
            this.enabled = false;
            EventBus.unregister(this);
            onUpdate();
        }
    }

    public void toggle() {
        if (!this.canBeDisabled) {
            if (!this.enabled) {
                this.enabled = true;
                EventBus.register(this);
            }
            return;
        }
        if (this.forceDisabled) {
            NotificationHandler.addNotification(this.name + " is disabled by the current server");
            if (this.enabled)
                disable();
            return;
        }
        if (this.enabled) {
            disable();
        } else {
            enable();
        }
    }

    public void onModuleToggle() {
        Client.sendMessage(getToggleMessage(this.name, this.enabled), true);
    }

    public String getToggleMessage(String name, boolean state) {
        return String.format("%s has been %s", name, state ? "&aenabled" : "&cdisabled");
    }

    public String getSanitizedName() {
        return this.name.toLowerCase().replaceAll("\\W+", "_");
    }

    public boolean isConfigurable() {
        return (this.configurableOptions > 0);
    }

    public void configPreInit() {}

    public void configPostInit() {
        for (Field field : getClass().getFields()) {
            if (!field.isAnnotationPresent(Hidden.class))
                for (Annotation annotation : field.getDeclaredAnnotations()) {
                    if (Config.getInstance().getNameFromAnnotation(annotation) != null)
                        this.configurableOptions++;
                }
        }
    }

    public void onUpdate() {}

    public Field getFieldFromOption(String option) {
        String sanitizedName = Config.getInstance().format(option);
        for (Field field : getClass().getDeclaredFields()) {
            for (Annotation annotation : field.getDeclaredAnnotations()) {
                String formatted = Config.getInstance().getNameFromAnnotation(annotation);
                if (formatted != null &&
                        sanitizedName.equals(Config.getInstance().format(formatted)))
                    return field;
            }
        }
        return null;
    }

    public boolean isOptionVisible(String option) {
        Predicate<Field> predicate = this.optionVisibility.get(Config.getInstance().format(option));
        if (predicate != null)
            return predicate.test(getFieldFromOption(option));
        return true;
    }

    public void setOptionVisibility(String option, Predicate<Field> predicate) {
        this.optionVisibility.put(Config.getInstance().format(option), predicate);
    }

    public void setForceDisabled(boolean forceDisabled) {
        this.forceDisabled = forceDisabled;
        if (this.forceDisabled) {
            this.wasEnabled = this.enabled;
            disable();
        } else {
            if (this.wasEnabled)
                enable();
            this.wasEnabled = false;
        }
    }

    public boolean getDefaultForceDisabledState() {
        return false;
    }

    public boolean equals(Object object) {
        if (object instanceof Module)
            return ((Module)object).UNIQUE_ID.equals(this.UNIQUE_ID);
        return false;
    }
}
