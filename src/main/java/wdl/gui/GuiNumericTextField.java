package wdl.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

class GuiNumericTextField extends GuiTextField {
    private String lastSafeText;

    public GuiNumericTextField(int id, FontRenderer fontRenderer, int x, int y, int width, int height) {
        super(id, fontRenderer, x, y, width, height);
        this.lastSafeText = "0";
        setText("0");
    }

    public void drawTextBox() {
        try {
            Integer.parseInt("0" + getText());
            this.lastSafeText = getText();
        } catch (NumberFormatException e) {
            setText(this.lastSafeText);
        }
        super.drawTextBox();
    }

    public int getValue() {
        try {
            return Integer.parseInt("0" + getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void setValue(int value) {
        String text = String.valueOf(value);
        this.lastSafeText = text;
        setText(text);
    }

    public String getText() {
        String text = super.getText();
        try {
            int value = Integer.parseInt("0" + text);
            return String.valueOf(value);
        } catch (NumberFormatException e) {
            setText(this.lastSafeText);
            return this.lastSafeText;
        }
    }

    public void setText(String text) {
        String value;
        try {
            value = String.valueOf(Integer.parseInt("0" + text));
        } catch (NumberFormatException e) {
            value = this.lastSafeText;
        }
        super.setText(value);
        this.lastSafeText = value;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\gui\GuiNumericTextField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */