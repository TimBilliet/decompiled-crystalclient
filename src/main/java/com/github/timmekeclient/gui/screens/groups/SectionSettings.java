package com.github.timmekeclient.gui.screens.groups;

import com.github.timmekeclient.Client;
import com.github.timmekeclient.feature.annotations.Hidden;
import com.github.timmekeclient.feature.annotations.properties.*;
import com.github.timmekeclient.feature.settings.GroupOptions;
import com.github.timmekeclient.group.GroupManager;
import com.github.timmekeclient.group.objects.Group;
import com.github.timmekeclient.group.objects.enums.Rank;
import com.github.timmekeclient.gui.Button;
import com.github.timmekeclient.gui.Pane;
import com.github.timmekeclient.gui.buttons.Divider;
import com.github.timmekeclient.gui.buttons.MenuButton;
import com.github.timmekeclient.gui.buttons.settings.*;
import com.github.timmekeclient.gui.screens.screen_overlay.OverlayRemoveGroup;
import com.github.timmekeclient.util.ColorObject;
import com.github.timmekeclient.util.objects.FadingColor;
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
        List<Field> declaredFields = Arrays.<Field>stream(module.getClass().getFields()).filter(field -> ((field.getAnnotations()).length > 0)).collect(Collectors.toList());
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(Hidden.class))
                continue;
            try {
                for (Annotation annotation : field.getDeclaredAnnotations()) {
                    if (annotation instanceof Toggle) {
                        Toggle toggle = (Toggle) annotation;
                        addButton((Button) new ToggleButton(-1, x, y, w, h, toggle.label(), field.getBoolean(module)) {
                            {
                                this.assignField(module, field);
                                this.addAttribute("groupSection");
                                this.setScissorPane(scissor);
                            }
                        });
                        y += h + 4;
                    } else if (annotation instanceof Keybind) {
                        Keybind keybind = (Keybind) annotation;
                        addButton((Button) new KeybindButton(-1, x, y, w, h, keybind.label(), (KeyBinding) field.get(module)) {
                            {
                                this.addAttribute("groupSection");
                                this.setScissorPane(scissor);
                            }
                        });
                        y += h + 4;
                    } else if (annotation instanceof Colour) {
                        Colour colour = (Colour) annotation;
                        addButton((Button) new ColorPicker(-1, x, y, w, h, colour.label(), (ColorObject) field.get(module), colour.isTextRender()) {
                            {
                                this.assignField(module, field);
                                this.addAttribute("groupSection");
                                this.setScissorPane(scissor);
                            }
                        });
                        y += h + 4;
                    } else if (annotation instanceof Selector) {
                        Selector selector = (Selector) annotation;
                        addButton((Button) new SelectorButton(-1, x, y, w, h, selector.label(), (String) field.get(module), selector.values()) {
                            {
                                this.assignField(module, field);
                                this.addAttribute("groupSection");
                                this.setScissorPane(scissor);
                            }
                        });
                        y += h + 4;
                    } else if (annotation instanceof Slider) {
                        Slider slider = (Slider) annotation;
                        if (slider.integers()) {
                            addButton((Button) new SliderButton(-1, x, y, w, h, slider.label(), slider.placeholder(), field
                                    .getInt(module), (int) slider.minimum(), (int) slider.maximum(), (int) slider.standard()) {
                                {
                                    this.assignField(module, field);
                                    this.addAttribute("groupSection");
                                    this.setScissorPane(scissor);
                                }
                            });
                        } else {
                            addButton((Button) new SliderButton(-1, x, y, w, h, slider.label(), slider.placeholder(), field
                                    .getDouble(module), slider.minimum(), slider.maximum(), slider.standard()) {
                                {
                                    this.assignField(module, field);
                                    this.addAttribute("groupSection");
                                    this.setScissorPane(scissor);
                                }
                            });
                        }
                        y += h + 4;
                    } else if (annotation instanceof PageBreak) {
                        PageBreak pageBreak = (PageBreak) annotation;
                        y += 6;
                        addButton((Button) new Divider(x, y + 3, pageBreak.label()) {
                            {
                                setScissorPane(scissor);
                                this.addAttribute("config_option");

                            }
                        });
                        y += h + 18;
                    }
                }
            } catch (IllegalAccessException illegalAccessException) {
            }
        }
        addButton((Button) new MenuButton(-1, x, y, w, h, "Clear Schematic Cache") {
            {
                this.addAttribute("groupSection");
                this.setScissorPane(scissor);
//                this.setOnClick(() -> {
//                    File dir = SchematicHandler.getInstance().getSchematicDirectory();
//                    if (dir.exists()) {
//                        dir.delete();
//                        NotificationHandler.addNotification("Schematic cache has been cleared");
//                    } else {
//                        NotificationHandler.addNotification("Schematic cache is already empty");
//                    }

//                });
            }
        });
        y += h + 4;
        final Group sg = GroupManager.getSelectedGroup();
        if (sg.getRank(Client.getUniqueID()) == Rank.LEADER)
            addButton((Button) new MenuButton(-1, x, y, w, h, "Delete group") {
                {
                    setTextColor(new FadingColor(opts.secondaryRed, opts.mainRed));
                    addAttribute("groupSection");
                    setScissorPane(scissor);
                    onClick = () -> {
                        addOverlay(new OverlayRemoveGroup(sg));
                    };
                }
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