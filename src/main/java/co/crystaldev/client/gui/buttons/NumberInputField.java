package co.crystaldev.client.gui.buttons;

import co.crystaldev.client.Resources;
import net.minecraft.util.MathHelper;

public class NumberInputField extends TextInputField {
    private final boolean valueStepButtons;

    private final int defaultValue;

    private final int minValue;

    private final int maxValue;

    private final ResourceButton stepUp;

    private final ResourceButton stepDown;

    private int stepButtonClicked = -1;

    private long lastUpdate = 0L;

    public NumberInputField(int id, int x, int y, int width, int height, int defaultValue, boolean valueStepButtons) {
        this(id, x, y, width, height, defaultValue, -2147483648, 2147483647, valueStepButtons);
    }

    public NumberInputField(int id, int x, int y, int width, int height, int defaultValue, int minValue, int maxValue, boolean valueStepButtons) {
        super(id, x, y, width, height, String.valueOf(defaultValue));
        setValidInputPattern("^-?\\d+$");
        setMaxLength(Math.max(String.valueOf(minValue).length(), String.valueOf(maxValue).length()));
        this.text = getPlaceholderText();
        this.valueStepButtons = valueStepButtons;
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        int bSize = this.height / 2;
        this.stepUp = new ResourceButton(-1, this.x + this.width - bSize, this.y, bSize, bSize, Resources.CHEVRON_UP);
        this.stepDown = new ResourceButton(-1, this.x + this.width - bSize, this.y + bSize, bSize, bSize, Resources.CHEVRON_DOWN);
        this.stepDown.drawBackground = false;
        this.stepDown.iconSize = bSize;
        this.stepUp.drawBackground = false;
        this.stepUp.iconSize = bSize;
        this.stepUp.iconColor.setColor2(this.opts.mainColor);
        this.stepDown.iconColor.setColor2(this.opts.mainColor);
    }

    public NumberInputField(int id, int x, int y, int width, int height, int defaultValue) {
        this(id, x, y, width, height, defaultValue, -2147483648, 2147483647, false);
    }

    public NumberInputField(int id, int x, int y, int width, int height, int defaultValue, int minValue, int maxValue) {
        this(id, x, y, width, height, defaultValue, minValue, maxValue, false);
    }

    public void onUpdate() {
        int bSize = this.height / 2;
        this.stepUp.y = this.y;
        this.stepDown.y = this.y + bSize;
    }

    public boolean onKeyTyped(char typedChar, int keyCode) {
        if (this.typing && this.enabled && keyCode == 12 && this.text.isEmpty()) {
            this.caret.insertText("-0");
            this.caret.removeText();
            return true;
        }
        return super.onKeyTyped(typedChar, keyCode);
    }

    public void onInteract(int mouseX, int mouseY, int mouseButton) {
        if (!this.enabled)
            return;
        if (mouseButton == 2) {
            setValue(this.defaultValue);
            return;
        }
        if (this.valueStepButtons && this.stepUp.isHovered(mouseX, mouseY)) {
            setValue(getValue() + 1);
            this.stepButtonClicked = 0;
            this.lastUpdate = System.currentTimeMillis();
        } else if (this.valueStepButtons && this.stepDown.isHovered(mouseX, mouseY)) {
            setValue(getValue() - 1);
            this.stepButtonClicked = 1;
            this.lastUpdate = System.currentTimeMillis();
        } else {
            super.onInteract(mouseX, mouseY, mouseButton);
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        this.stepButtonClicked = -1;
    }

    public void drawButton(int mouseX, int mouseY, boolean hovered) {
        super.drawButton(mouseX, mouseY, hovered);
        if (this.valueStepButtons) {
            this.stepUp.drawButton(mouseX, mouseY, (this.enabled && this.stepUp.isHovered(mouseX, mouseY)));
            this.stepDown.drawButton(mouseX, mouseY, (this.enabled && this.stepDown.isHovered(mouseX, mouseY)));
            if (this.enabled && this.stepButtonClicked != -1 && System.currentTimeMillis() - this.lastUpdate > 125L)
                if ((this.stepButtonClicked == 0 && this.stepUp.isHovered(mouseX, mouseY)) || (this.stepButtonClicked == 1 && this.stepDown.isHovered(mouseX, mouseY))) {
                    setValue(getValue() + ((this.stepButtonClicked == 0) ? 1 : -1));
                    this.lastUpdate = System.currentTimeMillis();
                } else {
                    this.stepButtonClicked = -1;
                }
        }
    }

    public void setValue(int value) {
        setText(Integer.toString(MathHelper.clamp_int(value, this.minValue, this.maxValue)));
    }

    public int getValue() {
        if (this.text.equals("-"))
            return 0;
        return MathHelper.clamp_int(this.text.isEmpty() ? Integer.parseInt(this.placeholderText) : Integer.parseInt(this.text), this.minValue, this.maxValue);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\buttons\NumberInputField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */