package co.crystaldev.client.handler;

import co.crystaldev.client.Client;
import co.crystaldev.client.Reference;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.init.ShutdownEvent;
import co.crystaldev.client.event.impl.player.InputEvent;
import co.crystaldev.client.util.FileUtils;
import co.crystaldev.client.util.objects.Macro;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonArray;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class MacroHandler implements IRegistrable {
  private static final File macroFile = new File(Client.getClientRunDirectory(), "macros.json");

  private static MacroHandler INSTANCE;

  private final List<Macro> registeredMacros;

  public List<Macro> getRegisteredMacros() {
    return this.registeredMacros;
  }

  public MacroHandler() {
    INSTANCE = this;
    this.registeredMacros = new LinkedList<>();
    populateMacros();
  }

  public void deleteMacro(Macro macro) {
    this.registeredMacros.remove(macro);
    macro.setDeleted(true);
  }

  public void addMacro(String name, String action, int keybind) {
    this.registeredMacros.add(new Macro(name, action, keybind));
    saveMacros();
  }

  private void populateMacros() {
    if (macroFile.exists())
      try {
        this.registeredMacros.clear();
        FileReader fr = new FileReader(macroFile);
        this.registeredMacros.addAll(Reference.GSON_PRETTY.fromJson(fr, (new TypeToken<LinkedList<Macro>>() {

              }).getType()));
        fr.close();
      } catch (Exception ex) {
        Reference.LOGGER.error("An exception was thrown while populating macros.", ex);
      }
  }

  public void saveMacros() {
    if (this.registeredMacros != null && !this.registeredMacros.isEmpty()) {
      String json = null;
      while (json == null || !FileUtils.isValidJson(json, JsonArray.class))
        json = Reference.GSON.toJson(this.registeredMacros);
      try {
        FileWriter fileWriter = new FileWriter(macroFile);
        fileWriter.write(json);
        fileWriter.close();
      } catch (IOException ex) {
        Reference.LOGGER.error("An exception was thrown while saving to the macro file.", ex);
      }
    } else if (macroFile.exists()) {
      macroFile.delete();
    }
  }

  public static MacroHandler getInstance() {
    return INSTANCE;
  }

  public void registerEvents() {
    EventBus.register(this, ShutdownEvent.class, ev -> saveMacros());
    EventBus.register(this, InputEvent.Key.class, ev -> {
          if ((Minecraft.getMinecraft()).currentScreen != null || (Minecraft.getMinecraft()).theWorld == null)
            return;
          if (ev.isKeyDown() && this.registeredMacros != null)
            for (Macro macro : this.registeredMacros) {
              if (macro.isEnabled() && macro.getKeybinding() != 0 && macro.getKeybinding() == ev.getKeyCode())
                macro.execute();
            }
        });
    EventBus.register(this, InputEvent.Mouse.class, ev -> {
          if (ev.buttonState && this.registeredMacros != null)
            for (Macro macro : this.registeredMacros) {
              if (macro.isEnabled() && macro.getKeybinding() != 0 && macro.getKeybinding() == ev.button - 100)
                macro.execute();
            }
        });
  }
}
