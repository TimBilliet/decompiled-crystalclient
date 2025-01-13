package co.crystaldev.client;


import co.crystaldev.client.event.impl.init.ConfigEvent;
import co.crystaldev.client.feature.annotations.properties.Colour;
import co.crystaldev.client.feature.annotations.properties.DropdownMenu;
import co.crystaldev.client.feature.annotations.properties.Keybind;
import co.crystaldev.client.feature.annotations.properties.Position;
import co.crystaldev.client.feature.annotations.properties.Property;
import co.crystaldev.client.feature.annotations.properties.Selector;
import co.crystaldev.client.feature.annotations.properties.Slider;
import co.crystaldev.client.feature.annotations.properties.Toggle;
import co.crystaldev.client.feature.base.Dropdown;
import co.crystaldev.client.feature.base.Module;
import co.crystaldev.client.feature.settings.ClientOptions;
import co.crystaldev.client.feature.settings.GroupOptions;
import co.crystaldev.client.handler.ModuleHandler;
import co.crystaldev.client.feature.settings.ClientOptions;
import co.crystaldev.client.feature.settings.GroupOptions;
import co.crystaldev.client.handler.ModuleHandler;
import co.crystaldev.client.util.ColorObject;
import co.crystaldev.client.util.FileUtils;
import co.crystaldev.client.util.objects.ModulePosition;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Config {
    private static Config INSTANCE;

    private final File configDir;

    public Config(File configDir) {
        INSTANCE = this;
        this.configDir = configDir;
        if (!this.configDir.exists() &&
                !this.configDir.mkdirs())
            throw new RuntimeException("Unable to create configuration directory at " + this.configDir);
        loadModuleConfig();
    }

    public void loadConfig(String fileName, Object object) {
        try {
            File configFile = new File(this.configDir, format(fileName) + ".json");
            if (!configFile.exists()) {
                if (object instanceof Module)
                    ((Module) object).configPreInit();
                saveConfig(fileName, object);
                if (object instanceof Module)
                    ((Module) object).configPostInit();
            } else {
                JsonObject base;
                StringBuilder json = new StringBuilder();
                try (Stream<String> stream = Files.lines(configFile.toPath())) {
                    stream.forEach(s -> json.append(s).append("\n"));
                } catch (IOException ex) {
                    Reference.LOGGER.error("Error loading config", ex);
                }
                try {
                    base = (JsonObject) Reference.GSON_PRETTY.fromJson(json.toString(), JsonObject.class);
                } catch (RuntimeException ex) {
                    Reference.LOGGER.error("Configuration failed to load (JSON syntax error) - Object: {}", object, ex);
                    configFile.renameTo(new File(this.configDir, format(fileName) + ".json.broken"));
                    saveConfig(fileName, object);
                    return;
                }
                loadFromJsonObject(base, object);
            }
        } catch (IllegalArgumentException | NullPointerException | java.io.UncheckedIOException ex) {
            Reference.LOGGER.error("Exception thrown while loading configuration.", ex);
        }
        saveConfig(fileName, object);
    }

    public void saveConfig(String fileName, Object object) {
        File configFile = new File(this.configDir, format(fileName) + ".json");
        if (!configFile.exists())
            try {
                if (!configFile.createNewFile()) {
                    Reference.LOGGER.error("Unable to create configuration file.");
                    return;
                }
            } catch (IOException ex) {
                Reference.LOGGER.error("Exception thrown while saving configuration", ex);
            }
        if (object instanceof Module)
            (new ConfigEvent.ModuleSave.Pre(this, (Module) object)).call();
        JsonObject obj = saveObjectToJson(object);
        if (obj == null)
            return;
        try {
            FileWriter writer = new FileWriter(configFile);
            writer.write(Reference.GSON_PRETTY.toJson(obj));
            writer.close();
        } catch (IOException ex) {
            Reference.LOGGER.error("Unable to write to config file " + configFile.getAbsolutePath(), ex);
        }
        if (object instanceof Module)
            (new ConfigEvent.ModuleSave.Post(this, (Module) object)).call();
    }

    public void loadFromJsonObject(JsonObject base, Object object) {
        loadFromJsonObject(getFileNameFromObject(object), base, object);
    }

    public void loadFromJsonObject(String fileName, JsonObject base, Object object) {
        if (object instanceof Module)
            ((Module) object).configPreInit();
        try {
            for (Field field : object.getClass().getFields()) {
                try {
                    for (Annotation annotation : field.getDeclaredAnnotations()) {
                        String formatted = format(getNameFromAnnotation(annotation));
                        String prefix = getPrefixFromAnnotation(annotation);
                        if (prefix != null && base.has(formatted = prefix + formatted) && !isAnnotationInvalid(annotation, object))
                            if (annotation instanceof Toggle) {
                                field.setBoolean(object, base.get(formatted).getAsBoolean());
                            } else if (annotation instanceof Slider) {
                                if (((Slider) annotation).integers()) {
                                    field.setInt(object, base.get(formatted).getAsInt());
                                } else {
                                    field.setDouble(object, base.get(formatted).getAsDouble());
                                }
                            } else if (annotation instanceof Selector) {
                                field.set(object, base.get(formatted).getAsString());
                            } else if (annotation instanceof Property) {
                                field.set(object, base.get(formatted).getAsString());
                            } else if (annotation instanceof Colour) {
                                try {
                                    ColorObject color = (ColorObject) Reference.GSON.fromJson(base.get(formatted).getAsJsonObject(), ColorObject.class);
                                    field.set(object, color);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (annotation instanceof Position) {
                                ModulePosition pos = Reference.GSON.fromJson(base.get(formatted).getAsJsonObject(), ModulePosition.class);
                                field.set(object, pos);
                            } else if (annotation instanceof DropdownMenu) {
                                Dropdown<?> dropdown = Reference.GSON.fromJson(base.get(formatted).getAsJsonObject(), Dropdown.class);
                                dropdown.copy((DropdownMenu) annotation);
                                field.set(object, dropdown);
                            }
                    }
                } catch (AssertionError ex) {
                    Reference.LOGGER.error("Unable to serialize {} in object {} (instance of {})", field
                            .getName(), (fileName == null) ? "null" : fileName, object.getClass().getName(), ex);
                } catch (IllegalAccessException ex) {
                    Reference.LOGGER.error("Unable to access field value {} in object {} (instance of {})", field
                            .getName(), (fileName == null) ? "null" : fileName, object.getClass().getName(), ex);
                }
            }
        } catch (IllegalArgumentException | NullPointerException | java.io.UncheckedIOException ex) {
            Reference.LOGGER.error("Exception thrown while loading configuration.", ex);
        }
        if (fileName != null)
            saveConfig(fileName, object);
        if (object instanceof Module)
            ((Module) object).configPostInit();
    }

    public JsonObject saveObjectToJson(Object object) {
        JsonObject obj = null;
        int attempts = 0;
        while (obj == null || !FileUtils.isValidJson(Reference.GSON.toJson(obj))) {
            obj = _saveObjectToJson(object);
            if (attempts > 10) {
                Reference.LOGGER.info("Unable to save object to JSON object, Class: {}, toString: {}", object
                        .getClass().getSimpleName(), object.toString());
                return null;
            }
            attempts++;
        }
        return obj;
    }

    private JsonObject _saveObjectToJson(Object object) {
        JsonObject obj = new JsonObject();
        for (Field field : object.getClass().getFields()) {
            try {
                for (Annotation annotation : field.getDeclaredAnnotations()) {
                    String name = format(getNameFromAnnotation(annotation));
                    String prefix = getPrefixFromAnnotation(annotation);
                    if (prefix != null && !isAnnotationInvalid(annotation, object)) {
                        name = prefix + name;
                        if (annotation instanceof Toggle) {
                            obj.addProperty(name, (Boolean) field.get(object));
                        } else if (annotation instanceof Slider) {
                            if (((Slider) annotation).integers()) {
                                obj.addProperty(name, (Integer) field.get(object));
                            } else {
                                obj.addProperty(name, (Double) field.get(object));
                            }
                        } else if (annotation instanceof Selector) {
                            obj.addProperty(name, (String) field.get(object));
                        } else if (annotation instanceof Property) {
                            obj.addProperty(name, (String) field.get(object));
                        } else if (annotation instanceof Colour) {
                            ColorObject c = (ColorObject) field.get(object);
                            obj.add(name, Reference.GSON.fromJson(Reference.GSON.toJson(c), JsonObject.class));
                        } else if (annotation instanceof Position) {
                            ModulePosition pos = (ModulePosition) field.get(object);
                            obj.add(name, Reference.GSON.fromJson(Reference.GSON.toJson(pos), JsonObject.class));
                        } else if (annotation instanceof DropdownMenu) {
                            Dropdown<?> dropdown = (Dropdown) field.get(object);
                            if (dropdown == null) {
                                field.set(object, dropdown = new Dropdown((DropdownMenu) annotation));
                                dropdown.setDefault();
                            }
                            obj.add(name, Reference.GSON.fromJson(Reference.GSON.toJson(dropdown, Dropdown.class), JsonObject.class));
                        }
                    }
                }
            } catch (IllegalAccessException ex) {
                Reference.LOGGER.error("Unable to access field value {} in object {} (instance of {})", field
                        .getName(), getFileNameFromObject(object), object.getClass().getName(), ex);
            }
        }
        return obj;
    }

    public void loadModuleConfig() {
        for (Module module : ModuleHandler.getModules())
            loadConfig(module.name, module);
        loadConfig(format((ClientOptions.getInstance()).name), ClientOptions.getInstance());
        loadConfig("group_options", GroupOptions.getInstance());
    }

    public void saveModuleConfig(Module module) {
        saveConfig(format(module.name), module);
    }

    public void saveModuleConfig() {
        for (Module module : ModuleHandler.getModules())
            saveModuleConfig(module);
        saveModuleConfig((Module) ClientOptions.getInstance());
        saveConfig("group_options", GroupOptions.getInstance());
        (new ConfigEvent.Save(this)).call();
    }

    private String getPrefixFromAnnotation(Annotation a) {
        if (a instanceof Toggle)
            return "tgl_";
        if (a instanceof Colour)
            return "clr_";
        if (a instanceof Selector)
            return "slc_";
        if (a instanceof Property)
            return "prop_";
        if (a instanceof Slider)
            return "sld_";
        if (a instanceof Position)
            return "pos_";
        if (a instanceof DropdownMenu)
            return "drd_";
        return null;
    }

    public boolean isConfigAnnotation(Annotation a) {
        return (a instanceof Toggle || a instanceof Colour || a instanceof Selector || a instanceof Property || a instanceof Slider || a instanceof Position || a instanceof Keybind || a instanceof DropdownMenu || a instanceof co.crystaldev.client.feature.annotations.properties.PageBreak);
    }

    public String getNameFromAnnotation(Annotation a) {
        if (a instanceof Toggle)
            return ((Toggle) a).label();
        if (a instanceof Colour)
            return ((Colour) a).label();
        if (a instanceof Selector)
            return ((Selector) a).label();
        if (a instanceof Property)
            return ((Property) a).label();
        if (a instanceof Slider)
            return ((Slider) a).label();
        if (a instanceof Position)
            return ((Position) a).cfg();
        if (a instanceof Keybind)
            return ((Keybind) a).label();
        if (a instanceof DropdownMenu)
            return ((DropdownMenu) a).label();
        return null;
    }

    public boolean isAnnotationInvalid(Annotation a, Object object) {
        List<Class<? extends Annotation>> requires = new ArrayList<>();
        if (a instanceof Toggle) {
            requires.addAll(Arrays.asList(((Toggle) a).requires()));
        } else if (a instanceof Colour) {
            requires.addAll(Arrays.asList(((Colour) a).requires()));
        } else if (a instanceof Selector) {
            requires.addAll(Arrays.asList(((Selector) a).requires()));
        } else if (a instanceof Property) {
            requires.addAll(Arrays.asList(((Property) a).requires()));
        } else if (a instanceof Slider) {
            requires.addAll(Arrays.asList(((Slider) a).requires()));
        } else if (a instanceof Keybind) {
            requires.addAll(Arrays.asList(((Keybind) a).requires()));
        } else if (a instanceof DropdownMenu) {
            requires.addAll(Arrays.asList(((DropdownMenu) a).requires()));
        }
        if (requires.isEmpty())
            return false;
        for (Class<? extends Annotation> clazz : requires) {
            if (object.getClass().isAnnotationPresent(clazz))
                return false;
        }
        return true;
    }

    private String getFileNameFromObject(Object obj) {
        if (obj instanceof Module)
            return format(((Module) obj).name);
        if (obj instanceof GroupOptions)
            return "group_options";
        return null;
    }

    public String format(String stringIn) {
        if (stringIn == null)
            return null;
        return FileUtils.sanitizeFileName(stringIn.toLowerCase().replaceAll("\\W+", "_"));
    }

    public static Config getInstance() {
        return INSTANCE;
    }
}
