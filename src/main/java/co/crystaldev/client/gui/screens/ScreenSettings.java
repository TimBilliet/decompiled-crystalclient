package co.crystaldev.client.gui.screens;

import co.crystaldev.client.Config;
import co.crystaldev.client.Reference;
import co.crystaldev.client.feature.annotations.Hidden;
import co.crystaldev.client.feature.annotations.HoverOverlay;
import co.crystaldev.client.feature.annotations.properties.*;
import co.crystaldev.client.feature.base.Dropdown;
import co.crystaldev.client.feature.base.Module;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.ButtonHoverOverlay;
import co.crystaldev.client.gui.Pane;
import co.crystaldev.client.gui.buttons.Divider;
import co.crystaldev.client.gui.buttons.settings.*;
import co.crystaldev.client.util.ColorObject;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;

import java.awt.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class ScreenSettings extends ScreenBase {
    private final Module module;

    protected int x;

    protected int x1;

    protected int y;

    protected int w;

    protected int w1;

    protected int h;

    public Module getModule() {
        return this.module;
    }

    public ScreenSettings(Module module, GuiScreen parent) {
        this.module = module;
        this.parent = parent;
    }

    public void init() {
        super.init();
        this.content.setScrollIf(b -> b.hasAttribute("config_option"));
        initSettings();
        ensureOptionVisibility();
    }

    public void onGuiClosed() {
        super.onGuiClosed();
        Config.getInstance().saveModuleConfig(this.module);
    }

    public void keyTyped(char charTyped, int keyCode) {
        super.keyTyped(charTyped, keyCode);
        ensureOptionVisibility();
    }

    public void onButtonInteract(Button button, int mouseX, int mouseY, int mouseButton) {
        super.onButtonInteract(button, mouseX, mouseY, mouseButton);
        if (!(button instanceof co.crystaldev.client.gui.buttons.ScrollBarButton))
            ensureOptionVisibility();
    }

    public void draw(int mouseX, int mouseY, float partialTicks) {
        super.draw(mouseX, mouseY, partialTicks);
        int size = Fonts.NUNITO_SEMI_BOLD_20.getStringHeight() + Fonts.NUNITO_SEMI_BOLD_18.getStringHeight();
        int half = this.header.height / 2 - Fonts.NUNITO_SEMI_BOLD_20.getStringHeight() / 2;
        int y = this.header.y + this.header.height / 2 - size / 2;
        Fonts.NUNITO_SEMI_BOLD_20.drawString(this.module.name, this.header.x + this.header.width - Fonts.NUNITO_SEMI_BOLD_20

                .getStringWidth(this.module.name) - half, y, Color.WHITE
                .getRGB());
        Fonts.NUNITO_SEMI_BOLD_18.drawString(this.module.description, this.header.x + this.header.width - Fonts.NUNITO_SEMI_BOLD_18

                .getStringWidth(this.module.description) - half, y + Fonts.NUNITO_SEMI_BOLD_20
                .getStringHeight(), Color.WHITE.getRGB());
        this.content.scroll(this, mouseX, mouseY);
    }

    public void ensureOptionVisibility() {
        int x = this.content.x + 14;
        int x1 = this.content.x + this.content.width / 2 + 7;
        int w = this.content.width - 28;
        int w1 = w / 2 - 7;
        for (Button button : this.buttons) {
            if (!button.hasAttribute("config_option"))
                continue;
            boolean half = (button.width == w1);
            if (!this.module.isOptionVisible(button.displayText)) {
                if (button.visible) {
                    button.visible = false;
                    if (!half || moveHalfButtons(button, x, x1))
                        moveButtonsUnder(button, -(button.height + 3));
                }
                continue;
            }
            if (!button.visible) {
                button.visible = true;
                if (!half || moveHalfButtons(button, x, x1))
                    moveButtonsUnder(button, button.height + 3);
            }
        }
        this.content.updateMaxScroll(this, 5);
        this.content.addScrollbarToScreen(this, this.content.y + 5);
    }

    public void initSettings() {
        removeButton(b -> b.hasAttribute("config_option"));
        this.x = this.content.x + 14;
        this.x1 = this.content.x + this.content.width / 2 + 7;
        this.y = this.content.y + 5;
        this.w = this.content.width - 28;
        this.w1 = this.w / 2 - 7;
        this.h = 18;
        addSettingsButtons();
    }

    protected void addSettingsButtons() {
        boolean wasHalf = false;
        Pane scissor = this.content.scale(getScaledScreen());
        for (Field field : this.module.getClass().getFields()) {
            if (!field.isAnnotationPresent(Hidden.class))
                try {
                    for (Annotation annotation : field.getDeclaredAnnotations()) {
                        if (Config.getInstance().isConfigAnnotation(annotation) && !Config.getInstance().isAnnotationInvalid(annotation, this.module)) {
                            boolean canBeHalf = (annotation instanceof Keybind || annotation instanceof Toggle);
                            if (wasHalf && !canBeHalf)
                                this.y += this.h + 4;
                            if (annotation instanceof Toggle) {
                                Toggle toggle = (Toggle) annotation;
                                ToggleButton toggleButton = new ToggleButton(-1, wasHalf ? this.x1 : this.x, this.y, this.w1, this.h, toggle.label(), field.getBoolean(this.module));
                                addButton(toggleButton, b -> {
                                    b.addAttribute("config_option");
                                    b.setScissorPane(scissor);
                                    b.assignField(this.module, field);
                                    checkButton(field, b);
                                });
                                if (wasHalf)
                                    this.y += this.h + 4;
                            } else if (annotation instanceof Keybind) {
                                Keybind keybind = (Keybind) annotation;
                                addButton(new KeybindButton(-1, wasHalf ? this.x1 : this.x, this.y, this.w1, this.h, keybind.label(), (KeyBinding) field.get(this.module)), b -> {
                                    b.addAttribute("config_option");
                                    b.setScissorPane(scissor);
                                    b.assignField(this.module, field);
                                    checkButton(field, b);
                                });
                                if (wasHalf)
                                    this.y += this.h + 4;
                            } else if (annotation instanceof Colour) {
                                Colour colour = (Colour) annotation;
                                addButton(new ColorPicker(-1, this.x, this.y, this.w, this.h, colour.label(), (ColorObject) field.get(this.module), colour.isTextRender()), b -> {
                                    b.addAttribute("config_option");
                                    b.setScissorPane(scissor);
                                    b.assignField(this.module, field);
                                    checkButton(field, b);
                                });
                                this.y += this.h + 4;
                            } else if (annotation instanceof Slider) {
                                Slider slider = (Slider) annotation;
                                if (slider.integers()) {
                                    addButton(new SliderButton(-1, this.x, this.y, this.w, this.h, slider.label(), slider.placeholder(), field
                                            .getInt(this.module), (int) slider.minimum(), (int) slider.maximum(), (int) slider.standard()), b -> {
                                        b.addAttribute("config_option");
                                        b.setScissorPane(scissor);
                                        b.assignField(this.module, field);
                                        checkButton(field, b);
                                    });
                                } else {
                                    addButton(new SliderButton(-1, this.x, this.y, this.w, this.h, slider.label(), slider.placeholder(), field
                                            .getDouble(this.module), slider.minimum(), slider.maximum(), slider.standard()), b -> {
                                        b.addAttribute("config_option");
                                        b.setScissorPane(scissor);
                                        b.assignField(this.module, field);
                                        checkButton(field, b);
                                    });
                                }
                                this.y += this.h + 4;
                            } else if (annotation instanceof Selector) {
                                Selector selector = (Selector) annotation;
                                addButton(new SelectorButton(-1, this.x, this.y, this.w, this.h, selector.label(), (String) field.get(this.module), selector.values()), b -> {
                                    b.addAttribute("config_option");
                                    b.setScissorPane(scissor);
                                    b.assignField(this.module, field);
                                    checkButton(field, b);
                                });
                                this.y += this.h + 4;
                            } else if (annotation instanceof DropdownMenu) {
                                DropdownMenu menu = (DropdownMenu) annotation;
                                Dropdown<?> dropdown = (Dropdown) field.get(this.module);
                                addButton(new LabelWithDropdownButton(-1, this.x, this.y, this.w, this.h, menu.label(), dropdown), b -> {
                                    b.addAttribute("config_option");
                                    b.setScissorPane(scissor);
                                    b.assignField(this.module, field);
                                    checkButton(field, b);
                                });
                                this.y += this.h + 4;
                            } else if (annotation instanceof PageBreak) {
                                PageBreak pageBreak = (PageBreak) annotation;
                                if (this.y != this.content.y + 5 || !(this.module instanceof co.crystaldev.client.feature.base.HudModule)) {
                                    this.y += 6;
                                    addButton((Button) new Divider(this.x, this.y + 3, pageBreak.label()), b -> {
                                        b.setScissorPane(scissor);
                                        b.addAttribute("config_option");
                                    });
                                    this.y += this.h + 18;
                                }
                            }
                            wasHalf = (canBeHalf && !wasHalf);
                        }
                    }
                } catch (IllegalAccessException ex) {
                    Reference.LOGGER.error("Unable to read field value", ex);
                }
        }
        this.content.updateMaxScroll(this, 5);
        this.content.addScrollbarToScreen(this, this.content.y + 5);
    }

    private void checkButton(Field field, Button button) {
        if (!field.isAnnotationPresent(HoverOverlay.class))
            return;
        HoverOverlay overlay = field.<HoverOverlay>getAnnotation(HoverOverlay.class);
        button.setHoverOverlay(new ButtonHoverOverlay(overlay.value(), Fonts.NUNITO_REGULAR_16));
    }

    private boolean hasSideButton(Button button) {
        for (Button side : this.buttons) {
            if (side.visible && button.y == side.y && button.width == side.width && !button.equals(side) && side.hasAttribute("config_option"))
                return true;
        }
        return false;
    }

    private boolean moveHalfButtons(Button button, int x, int x1) {
        if (!hasSideButton(button))
            return true;
        for (Button side : this.buttons) {
            if (side.width == button.width && button.y == side.y && !button.equals(side) && side.hasAttribute("config_option") &&
                    side.initialX == x1)
                side.x = button.visible ? x1 : x;
        }
        return false;
    }

    private void moveButtonsUnder(Button button, int amount) {
        for (Button under : this.buttons) {
            if (under.initialY > button.initialY && under.hasAttribute("config_option")) {
                under.initialY += amount;
                under.y += amount;
            }
        }
    }
}