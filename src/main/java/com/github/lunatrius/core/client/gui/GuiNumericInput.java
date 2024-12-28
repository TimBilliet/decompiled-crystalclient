package com.github.lunatrius.core.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;

public class GuiNumericInput extends GuiButton {
  private static final int DEFAULT_VALUE = 0;
  
  private static final int BUTTON_WIDTH = 12;
  
  private final GuiTextField guiTextField;
  
  private final String label;
  
  private String previous = String.valueOf(0);
  
  private int minimum = Integer.MIN_VALUE;
  
  private int maximum = Integer.MAX_VALUE;
  
  private boolean wasFocused = false;
  
  public GuiNumericInput(FontRenderer fontRenderer, String label, int id, int x, int y, int width, int height) {
    super(id, x, y, width, height, "");
    this.guiTextField = new GuiTextField(0, fontRenderer, x + 1, y + 1, width - 24 - 2, height - 2);
    this.label = label;
    setValue(0);
  }
  
  public boolean mousePressed(Minecraft minecraft, int x, int y) {
    if (this.wasFocused && !this.guiTextField.isFocused()) {
      this.wasFocused = false;
      return true;
    } 
    this.wasFocused = this.guiTextField.isFocused();
    return false;
  }
  
  public void drawButton(Minecraft minecraft, int x, int y) {
    if (this.visible) {
      drawCenteredString(minecraft.fontRendererObj, this.label, this.xPosition - 3 - minecraft.fontRendererObj.getStringWidth(this.label), this.yPosition + this.height / 2 - minecraft.fontRendererObj.FONT_HEIGHT / 2, 16777215);
      this.guiTextField.drawTextBox();
    } 
  }
  
  public void mouseClicked(int x, int y, int action) {
    Minecraft minecraft = Minecraft.getMinecraft();
    this.guiTextField.mouseClicked(x, y, action);
  }
  
  public boolean keyTyped(char character, int code) {
    if (!this.guiTextField.isFocused())
      return false; 
    int cursorPositionOld = this.guiTextField.getCursorPosition();
    this.guiTextField.textboxKeyTyped(character, code);
    String text = this.guiTextField.getText();
    int cursorPositionNew = this.guiTextField.getCursorPosition();
    if (text.length() == 0 || text.equals("-"))
      return true; 
    try {
      long value = Long.parseLong(text);
      boolean outOfRange = false;
      if (value > this.maximum) {
        value = this.maximum;
        outOfRange = true;
      } else if (value < this.minimum) {
        value = this.minimum;
        outOfRange = true;
      } 
      text = String.valueOf(value);
      if (!text.equals(this.previous) || outOfRange) {
        this.guiTextField.setText(String.valueOf(value));
        this.guiTextField.setCursorPosition(cursorPositionNew);
      } 
      this.previous = text;
      return true;
    } catch (NumberFormatException nfe) {
      this.guiTextField.setText(this.previous);
      this.guiTextField.setCursorPosition(cursorPositionOld);
      return false;
    } 
  }
  
  public void updateCursorCounter() {
    this.guiTextField.updateCursorCounter();
  }
  
  public boolean isFocused() {
    return this.guiTextField.isFocused();
  }
  
  public void setPosition(int x, int y) {
    this.guiTextField.xPosition = x + 1;
    this.guiTextField.yPosition = y + 1;
  }
  
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
    this.guiTextField.setEnabled(enabled);
  }
  
  public void setMinimum(int minimum) {
    this.minimum = minimum;
  }
  
  public int getMinimum() {
    return this.minimum;
  }
  
  public void setMaximum(int maximum) {
    this.maximum = maximum;
  }
  
  public int getMaximum() {
    return this.maximum;
  }
  
  public void setBounds(int minimum, int maximum) {
    this.minimum = minimum;
    this.maximum = maximum;
  }
  
  public void setValue(int value) {
    if (value > this.maximum) {
      value = this.maximum;
    } else if (value < this.minimum) {
      value = this.minimum;
    } 
    this.guiTextField.setText(String.valueOf(value));
  }
  
  public int getValue() {
    String text = this.guiTextField.getText();
    if (text.length() == 0 || text.equals("-"))
      return 0; 
    return Integer.parseInt(text);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\core\client\gui\GuiNumericInput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */