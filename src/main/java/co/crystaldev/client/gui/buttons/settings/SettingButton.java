package co.crystaldev.client.gui.buttons.settings;

import co.crystaldev.client.Reference;
import co.crystaldev.client.event.impl.init.ModuleOptionUpdateEvent;
import co.crystaldev.client.feature.base.Module;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.screens.ScreenSettings;

import java.lang.reflect.Field;
import java.util.function.Consumer;

public abstract class SettingButton<T> extends Button {
  protected T currentValue;

  protected Object settingObject;

  protected Field settingField;

  public T getCurrentValue() {
    return this.currentValue;
  }

  private Consumer<T> onStateChange = null;

  private T lastValue;

  public Consumer<T> getOnStateChange() {
    return this.onStateChange;
  }

  public void setOnStateChange(Consumer<T> onStateChange) {
    this.onStateChange = onStateChange;
  }

  public SettingButton(int id, int x, int y, int width, int height, String displayText, T currentValue) {
    super(id, x, y, width, height, displayText);
    this.currentValue = currentValue;
    this.lastValue = currentValue;
  }

  public void assignField(Object object, Field field) {
    this.settingObject = object;
    this.settingField = field;
  }

  protected void save() {
    if (this.settingObject != null && this.settingField != null)
      try {
        this.settingField.set(this.settingObject, this.currentValue);
        if (this.settingObject instanceof Module)
          (new ModuleOptionUpdateEvent((Module)this.settingObject, this.settingField, this.displayText)).call();
      } catch (IllegalAccessException ex) {
        Reference.LOGGER.error("Unable to assign field to value", ex);
      }
  }

  protected T setValue(T newValue) {
    this.currentValue = newValue;
    save();
    return this.currentValue;
  }

  public void onInteract(int mouseX, int mouseY, int mouseButton) {
    super.onInteract(mouseX, mouseY, mouseButton);
    this.lastValue = this.currentValue;
  }

  public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    super.mouseReleased(mouseX, mouseY, mouseButton);
    if (this.lastValue != this.currentValue) {
      this.lastValue = this.currentValue;
      if (this.onStateChange != null)
        this.onStateChange.accept(this.currentValue);
      if (this.mc.currentScreen instanceof ScreenSettings)
        ((ScreenSettings)this.mc.currentScreen).getModule().onUpdate();
    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\buttons\settings\SettingButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */