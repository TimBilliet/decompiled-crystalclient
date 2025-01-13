package co.crystaldev.client.gui.screens.groups;

import co.crystaldev.client.Client;
import co.crystaldev.client.feature.annotations.Hidden;
import co.crystaldev.client.feature.annotations.properties.*;
import co.crystaldev.client.feature.settings.GroupOptions;
import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.group.objects.Group;
import co.crystaldev.client.group.objects.enums.Rank;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Pane;
import co.crystaldev.client.gui.buttons.Divider;
import co.crystaldev.client.gui.buttons.MenuButton;
import co.crystaldev.client.gui.buttons.settings.*;
import co.crystaldev.client.util.ColorObject;
import net.minecraft.client.settings.KeyBinding;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SectionSettings extends GroupSection {
    protected SectionSettings(Pane pane) {
        super(pane);
    }

    public void init() {
        super.init();
        int x = this.pane.x + 20;
        int y = this.pane.y + 10;
        int w = this.pane.width - 40;
        int h = 18;
        final GroupOptions module = GroupOptions.getInstance();
        final Pane scissor = this.pane.scale(getScaledScreen());
        List<Field> declaredFields = (List<Field>) Arrays.<Field>stream(module.getClass().getFields()).filter(field -> ((field.getAnnotations()).length > 0)).collect(Collectors.toList());
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent((Class) Hidden.class))
                continue;
            try {
                for (Annotation annotation : field.getDeclaredAnnotations()) {
                    if (annotation instanceof Toggle) {
                        Toggle toggle = (Toggle) annotation;
                        addButton((Button) new ToggleButton(-1, x, y, w, h, toggle.label(), field.getBoolean(module)) {

                        });
                        y += h + 4;
                    } else if (annotation instanceof Keybind) {
                        Keybind keybind = (Keybind) annotation;
                        addButton((Button) new KeybindButton(-1, x, y, w, h, keybind.label(), (KeyBinding) field.get(module)) {

                        });
                        y += h + 4;
                    } else if (annotation instanceof Colour) {
                        Colour colour = (Colour) annotation;
                        addButton((Button) new ColorPicker(-1, x, y, w, h, colour.label(), (ColorObject) field.get(module), colour.isTextRender()) {

                        });
                        y += h + 4;
                    } else if (annotation instanceof Selector) {
                        Selector selector = (Selector) annotation;
                        addButton((Button) new SelectorButton(-1, x, y, w, h, selector.label(), (String) field.get(module), selector.values()) {

                        });
                        y += h + 4;
                    } else if (annotation instanceof Slider) {
                        Slider slider = (Slider) annotation;
                        if (slider.integers()) {
                            addButton((Button) new SliderButton(-1, x, y, w, h, slider.label(), slider.placeholder(), field
                                    .getInt(module), (int) slider.minimum(), (int) slider.maximum(), (int) slider.standard()) {

                            });
                        } else {
                            addButton((Button) new SliderButton(-1, x, y, w, h, slider.label(), slider.placeholder(), field
                                    .getDouble(module), slider.minimum(), slider.maximum(), slider.standard()) {

                            });
                        }
                        y += h + 4;
                    } else if (annotation instanceof PageBreak) {
                        PageBreak pageBreak = (PageBreak) annotation;
                        y += 6;
                        addButton((Button) new Divider(x, y + 3, pageBreak.label()) {

                        });
                        y += h + 18;
                    }
                }
            } catch (IllegalAccessException illegalAccessException) {
            }
        }
        addButton((Button) new MenuButton(-1, x, y, w, h, "Clear Schematic Cache") {

        });
        y += h + 4;
        final Group sg = GroupManager.getSelectedGroup();
        if (sg.getRank(Client.getUniqueID()) == Rank.LEADER)
            addButton((Button) new MenuButton(-1, x, y, w, h, "Delete group") {

            });
        this.pane.updateMaxScroll(this, 0);
        this.pane.addScrollbarToScreen(this);
    }

    public void draw(int mouseX, int mouseY, float partialTicks) {
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\screens\groups\SectionSettings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */