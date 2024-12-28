package co.crystaldev.client.gui.buttons;

import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.gui.screens.ScreenMacros;
import co.crystaldev.client.handler.MacroHandler;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.ScissorManager;
import co.crystaldev.client.util.objects.FadingColor;
import net.minecraft.client.settings.GameSettings;

import java.util.ArrayList;
import java.util.List;

public class MacroBuilderButton extends Button {
    private final TextInputField nameInput;

    private final TextInputField actionInput;

    private final KeySelectorButton keySelector;

    private final MenuButton createButton;

    private final List<Button> subButtons = new ArrayList<>();

    private final FadingColor background;

    public MacroBuilderButton(int x, int y, int width, int height) {
        super(-1, x, y, width, height);
//    int bWidth = (this.x + this.width - 4 - this.x + this.width / 2 + this.width / 4 + 4) / 2 - 2;
        int bWidth = (this.width / 4 - 8) / 2 - 2;
        this.subButtons.add(this.nameInput = new TextInputField(-1, this.x + 4, this.y + this.height / 2 - 9, this.x + this.width / 4 - this.x - 8, 18, "Macro Name") {

        });
        this.subButtons.add(this.actionInput = new TextInputField(-1, this.x + this.width / 4, this.y + this.height / 2 - 9, this.width / 2, 18, "Macro Action") {

        });
        System.out.println("new subbutton keyselector:  + x?: " + this.actionInput.width + " width?: " + bWidth);
        this.subButtons.add(this.keySelector = new KeySelectorButton(-1, this.actionInput.x + this.actionInput.width + 4, this.y + this.height / 2 - 9, bWidth, 18, 0));
        this.subButtons.add(this.createButton = new CreateButton(-1, this.keySelector.x + bWidth + 4, this.y + this.height / 2 - 9, bWidth, 18));
        this.background = new FadingColor(this.opts.neutralButtonBackground, this.opts.hoveredButtonBackground);
    }

    public void onUpdate() {
        for (Button button : this.subButtons)
            button.y = this.y + this.height / 2 - 9;
    }

    public void drawButton(int mouseX, int mouseY, boolean hovered) {
        this.nameInput.setScissorPane(this.scissorPane);
        this.actionInput.setScissorPane(this.scissorPane);
        this.background.fade(hovered);
        ScissorManager.getInstance().push(this.scissorPane);
        RenderUtils.drawRoundedRect(this.x, this.y, (this.x + this.width), (this.y + this.height), 9.0D, this.background.getCurrentColor().getRGB());
        for (Button button : this.subButtons)
            button.drawButton(mouseX, mouseY, button.isHovered(mouseX, mouseY));
        ScissorManager.getInstance().pop(this.scissorPane);
    }

    public void mouseDown(Screen screen, int mouseX, int mouseY, int mouseButton) {
        super.mouseDown(screen, mouseX, mouseY, mouseButton);
        for (Button button : this.subButtons) {
            button.mouseDown(screen, mouseX, mouseY, mouseButton);
            if (button.isHovered(mouseX, mouseY))
                button.onInteract(mouseX, mouseY, mouseButton);
        }
    }

    public boolean onKeyTyped(char key, int keyCode) {
        boolean res = true;
        for (Button button : this.subButtons) {
            if (!button.onKeyTyped(key, keyCode))
                res = false;
        }
        return res;
    }

    private boolean canCreate() {
        return (!this.nameInput.getText().isEmpty() && !this.actionInput.getText().isEmpty());
    }

    public void onInteract(int mouseX, int mouseY, int mouseButton) {
        super.onInteract(mouseX, mouseY, mouseButton);
        if (this.createButton.isHovered(mouseX, mouseY) && canCreate()) {
            MacroHandler.getInstance().addMacro(this.nameInput.getText(), this.actionInput.getText(), this.keySelector.keycode);
            ((ScreenMacros) this.mc.currentScreen).initMacros();
        }
    }

    private class CreateButton extends MenuButton {
        private final FadingColor outlineColor;

        public CreateButton(int id, int x, int y, int width, int height) {
            super(id, x, y, width, height, "Create");
            this.outlineColor = new FadingColor(this.opts.mainDisabled, this.opts.mainColor);
            this.fontRenderer = Fonts.NUNITO_SEMI_BOLD_16;
        }

        public void drawButton(int mouseX, int mouseY, boolean hovered) {
            this.outlineColor.fade(this.enabled = MacroBuilderButton.this.canCreate());
            super.outlineColor = this.outlineColor.getCurrentColor().getRGB();
            super.drawButton(mouseX, mouseY, hovered);
        }
    }

    public static class KeySelectorButton extends Button {
        private boolean selecting = false;

        private int keycode;

        private final FadingColor background;

        private final FadingColor text;

        public int getKeycode() {
            return this.keycode;
        }

        public KeySelectorButton(int id, int x, int y, int width, int height, int keycode) {
            super(id, x, y, width, height);
            this.keycode = keycode;
            this.background = new FadingColor(this.opts.neutralButtonBackground, this.opts.hoveredButtonBackground);
            this.text = new FadingColor(this.opts.neutralTextColor, this.opts.hoveredTextColor);
            this.fontRenderer = Fonts.NUNITO_SEMI_BOLD_16;
        }

        public void drawButton(int mouseX, int mouseY, boolean hovered) {
            this.background.fade((hovered || this.selecting));
            this.text.fade((hovered || this.selecting));
//      System.out.println("hoogte: " + this.height + "breedte: " + this.width);
            RenderUtils.drawRoundedRect(this.x, this.y, (this.x + this.width), (this.y + this.height), 9.0D, this.background.getCurrentColor().getRGB());

//      RenderUtils.drawRoundedRect(this.x, this.y, (this.x + this.width), (this.y + this.height), 9.0D, this.background.getCurrentColor().getRGB());
            String s = this.selecting ? "..." : ((this.keycode == 0) ? "NONE" : GameSettings.getKeyDisplayString(this.keycode));
            this.fontRenderer.drawCenteredString(s, this.x + this.width / 2, this.y + this.height / 2, this.text.getCurrentColor().getRGB());
        }

        public void onInteract(int mouseX, int mouseY, int mouseButton) {
            super.onInteract(mouseX, mouseY, mouseButton);
            if (mouseButton == 0)
                this.selecting = !this.selecting;
        }

        public void mouseDown(Screen currentScreen, int mouseX, int mouseY, int mouseButton) {
            if (this.selecting && mouseButton != 0 && mouseButton != 1) {
                this.keycode = -100 + mouseButton;
                this.selecting = false;
            }
        }

        public boolean onKeyTyped(char key, int code) {
            if (this.selecting) {
                this.keycode = (code == 1) ? 0 : code;
                this.selecting = false;
                return false;
            }
            return true;
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\buttons\MacroBuilderButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */