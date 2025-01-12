package co.crystaldev.client.gui.buttons;

import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.gui.screens.ScreenMacros;
import co.crystaldev.client.handler.MacroHandler;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.ScissorManager;
import co.crystaldev.client.util.objects.FadingColor;
import co.crystaldev.client.util.objects.Macro;

import java.util.ArrayList;
import java.util.List;

public class MacroButton extends Button {
    private final Macro macro;

    private final TextInputField actionInput;

    private final MacroBuilderButton.KeySelectorButton keySelector;

    private final MenuButton removeButton;

    private final List<Button> subButtons = new ArrayList<>();

    private final FadingColor background;

    private final FadingColor text;

    private final FadingColor status;

    private final FadingColor status1;

    public MacroButton(final Macro macro, int x, int y, int width, int height) {
        super(-1, x, y, width, height, macro.getName());
        this.macro = macro;
//    int bWidth = (this.x + this.width - 4 - this.x + this.width / 2 + this.width / 4 + 4) / 2 - 2;
        int bWidth = (this.width / 4 - 8) / 2 - 2;
        this.subButtons.add(this.actionInput = new TextInputField(-1, this.x + this.width / 4, this.y + this.height / 2 - 9, this.width / 2, 18, "Macro Action") {

        });
        actionInput.setText(macro.getAction());
        this.subButtons.add(this
                .keySelector = new MacroBuilderButton.KeySelectorButton(-1, this.actionInput.x + this.actionInput.width + 4, this.y + this.height / 2 - 9, bWidth, 18, this.macro.getKeybinding()));
        this.subButtons.add(this.removeButton = new MenuButton(-1, this.keySelector.x + bWidth + 4, this.y + this.height / 2 - 9, bWidth, 18, "Delete"));
        removeButton.fontRenderer = Fonts.NUNITO_SEMI_BOLD_16;
        removeButton.textColor = new FadingColor(this.opts.secondaryColor, this.opts.secondaryColor);
        this.background = new FadingColor(this.opts.neutralButtonBackground, this.opts.hoveredButtonBackground);
        this.text = new FadingColor(this.opts.neutralTextColor, this.opts.hoveredTextColor);
        this.status = new FadingColor(this.opts.mainDisabled, this.opts.mainColor);
        this.status1 = new FadingColor(this.opts.secondaryDisabled, this.opts.secondaryColor);
    }

    public void onUpdate() {
        for (Button button : this.subButtons)
            button.y = this.y + this.height / 2 - 9;
    }

    public void drawButton(int mouseX, int mouseY, boolean hovered) {
        this.actionInput.setScissorPane(this.scissorPane);
        this.background.fade(hovered);
        this.text.fade(hovered);
        this.status.fade(this.macro.isEnabled());
        this.status1.fade(this.macro.isEnabled());
        ScissorManager.getInstance().push(this.scissorPane);
        RenderUtils.drawRoundedRectWithGradientBorder(this.x, this.y, (this.x + this.width), (this.y + this.height), 9.0D, 3.5F, this.status
                .getCurrentColor().getRGB(), this.status1.getCurrentColor().getRGB(), this.background.getCurrentColor().getRGB());
        Fonts.NUNITO_SEMI_BOLD_18.drawString(this.displayText, this.x + 5, this.y + this.height / 2 - Fonts.NUNITO_SEMI_BOLD_18.getStringHeight() / 2, this.text
                .getCurrentColor().getRGB());
        for (Button button : this.subButtons)
            button.drawButton(mouseX, mouseY, button.isHovered(mouseX, mouseY));
        ScissorManager.getInstance().pop(this.scissorPane);
    }

    public void mouseDown(Screen screen, int mouseX, int mouseY, int mouseButton) {
        super.mouseDown(screen, mouseX, mouseY, mouseButton);
        for (Button button : this.subButtons)
            button.mouseDown(screen, mouseX, mouseY, mouseButton);
    }

    public boolean onKeyTyped(char key, int keyCode) {
        boolean res = true;
        for (Button button : this.subButtons) {
            if (!button.onKeyTyped(key, keyCode))
                res = false;
        }
        return res;
    }

    public void onInteract(int mouseX, int mouseY, int mouseButton) {
        super.onInteract(mouseX, mouseY, mouseButton);
        if (this.removeButton.isHovered(mouseX, mouseY)) {
            MacroHandler.getInstance().deleteMacro(this.macro);
            ((ScreenMacros) this.mc.currentScreen).initMacros();
        } else {
            for (Button button : this.subButtons) {
                if (button.isHovered(mouseX, mouseY)) {
                    button.onInteract(mouseX, mouseY, mouseButton);
                    return;
                }
            }
            this.macro.setEnabled(!this.macro.isEnabled());
        }
    }

    public void onClose() {
        if (this.macro.isDeleted())
            return;
        if (!this.actionInput.getText().isEmpty())
            this.macro.setAction(this.actionInput.getText());
        this.macro.setKeybinding(this.keySelector.getKeycode());
    }
}