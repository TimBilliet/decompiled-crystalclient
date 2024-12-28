package com.github.lunatrius.core.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiScreenBase extends GuiScreen {
  protected final GuiScreen parentScreen;
  
  protected List<GuiTextField> textFields = new ArrayList<>();
  
  public GuiScreenBase() {
    this((GuiScreen)null);
  }
  
  public GuiScreenBase(GuiScreen parentScreen) {
    this.parentScreen = parentScreen;
  }
  
  public boolean doesGuiPauseGame() {
    return false;
  }
  
  public void initGui() {
    this.buttonList.clear();
    this.textFields.clear();
  }
  
  protected void mouseClicked(int mouseX, int mouseY, int mouseEvent) throws IOException {
    for (GuiButton button : this.buttonList) {
      if (button instanceof GuiNumericField) {
        GuiNumericField numericField = (GuiNumericField)button;
        numericField.mouseClicked(mouseX, mouseY, mouseEvent);
      } 
    } 
    for (GuiTextField textField : this.textFields)
      textField.mouseClicked(mouseX, mouseY, mouseEvent); 
    super.mouseClicked(mouseX, mouseY, mouseEvent);
  }
  
  protected void keyTyped(char character, int code) throws IOException {
    if (code == 1) {
      this.mc.displayGuiScreen(this.parentScreen);
      return;
    } 
    for (GuiButton button : this.buttonList) {
      if (button instanceof GuiNumericField) {
        GuiNumericField numericField = (GuiNumericField)button;
        numericField.keyTyped(character, code);
        if (numericField.isFocused())
          actionPerformed(numericField); 
      } 
    } 
    for (GuiTextField textField : this.textFields)
      textField.textboxKeyTyped(character, code); 
    super.keyTyped(character, code);
  }
  
  public void updateScreen() {
    super.updateScreen();
    for (GuiButton button : this.buttonList) {
      if (button instanceof GuiNumericField) {
        GuiNumericField numericField = (GuiNumericField)button;
        numericField.updateCursorCounter();
      } 
    } 
    for (GuiTextField textField : this.textFields)
      textField.updateCursorCounter(); 
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    super.drawScreen(mouseX, mouseY, partialTicks);
    for (GuiTextField textField : this.textFields)
      textField.drawTextBox(); 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\core\client\gui\GuiScreenBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */