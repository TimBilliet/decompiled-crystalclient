package wdl.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;
import wdl.EntityUtils;
import wdl.WDL;

import java.io.IOException;
import java.util.Set;

public class GuiWDLEntityRangePresets extends GuiScreen implements GuiYesNoCallback {
  private final GuiScreen parent;
  
  private GuiButton vanillaButton;
  
  private GuiButton spigotButton;
  
  private GuiButton serverButton;
  
  private GuiButton cancelButton;
  
  public GuiWDLEntityRangePresets(GuiScreen parent) {
    this.parent = parent;
  }
  
  public void initGui() {
    int y = this.height / 4;
    this
      .vanillaButton = new GuiButton(0, this.width / 2 - 100, y, I18n.format("wdl.gui.rangePresets.vanilla", new Object[0]));
    y += 22;
    this
      .spigotButton = new GuiButton(1, this.width / 2 - 100, y, I18n.format("wdl.gui.rangePresets.spigot", new Object[0]));
    y += 22;
    this
      .serverButton = new GuiButton(2, this.width / 2 - 100, y, I18n.format("wdl.gui.rangePresets.server", new Object[0]));
    this.buttonList.add(this.vanillaButton);
    this.buttonList.add(this.spigotButton);
    this.buttonList.add(this.serverButton);
    y += 28;
    this
      .cancelButton = new GuiButton(100, this.width / 2 - 100, this.height - 29, I18n.format("gui.cancel", new Object[0]));
    this.buttonList.add(this.cancelButton);
  }
  
  protected void actionPerformed(GuiButton button) throws IOException {
    if (!button.enabled)
      return; 
    if (button.id < 3) {
      String lower, upper = I18n.format("wdl.gui.rangePresets.upperWarning", new Object[0]);
      if (button.id == 0) {
        lower = I18n.format("wdl.gui.rangePresets.vanilla.warning", new Object[0]);
      } else if (button.id == 1) {
        lower = I18n.format("wdl.gui.rangePresets.spigot.warning", new Object[0]);
      } else if (button.id == 2) {
        lower = I18n.format("wdl.gui.rangePresets.server.warning", new Object[0]);
      } else {
        throw new Error("Button.id should never be negative.");
      } 
      this.mc.displayGuiScreen((GuiScreen)new GuiYesNo(this, upper, lower, button.id));
    } 
    if (button.id == 100)
      this.mc.displayGuiScreen(this.parent); 
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    Utils.drawListBackground(23, 32, 0, 0, this.height, this.width);
    drawCenteredString(this.fontRendererObj, 
        I18n.format("wdl.gui.rangePresets.title", new Object[0]), this.width / 2, 8, 16777215);
    String infoText = null;
    if (this.vanillaButton.isMouseOver()) {
      infoText = I18n.format("wdl.gui.rangePresets.vanilla.description", new Object[0]);
    } else if (this.spigotButton.isMouseOver()) {
      infoText = I18n.format("wdl.gui.rangePresets.spigot.description", new Object[0]);
    } else if (this.serverButton.isMouseOver()) {
      infoText = I18n.format("wdl.gui.rangePresets.server.description", new Object[0]) + "\n\n";
      if (this.serverButton.enabled) {
        infoText = infoText + I18n.format("wdl.gui.rangePresets.server.installed", new Object[0]);
      } else {
        infoText = infoText + I18n.format("wdl.gui.rangePresets.server.notInstalled", new Object[0]);
      } 
    } else if (this.cancelButton.isMouseOver()) {
      infoText = I18n.format("wdl.gui.rangePresets.cancel.description", new Object[0]);
    } 
    if (infoText != null)
      Utils.drawGuiInfoBox(infoText, this.width, this.height, 48); 
    super.drawScreen(mouseX, mouseY, partialTicks);
  }
  
  public void confirmClicked(boolean result, int id) {
    if (result) {
      Set<String> entities = EntityUtils.getEntityTypes();
      if (id == 0) {
        for (String entity : entities)
          WDL.worldProps.setProperty("Entity." + entity + ".TrackDistance", 
              Integer.toString(
                EntityUtils.getDefaultEntityRange(entity))); 
      } else if (id == 1) {
        for (String entity : entities) {
          Class<?> c = (Class)EntityUtils.stringToClassMapping.get(entity);
          if (c == null)
            continue; 
          WDL.worldProps.setProperty("Entity." + entity + ".TrackDistance", 
              Integer.toString(
                EntityUtils.getDefaultSpigotEntityRange(c)));
        } 
      } else if (id == 2) {
        for (String entity : entities)
          WDL.worldProps.setProperty("Entity." + entity + ".TrackDistance", 
              Integer.toString(32)); 
      } 
    } 
    this.mc.displayGuiScreen(this.parent);
  }
  
  public void onGuiClosed() {
    WDL.saveProps();
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\gui\GuiWDLEntityRangePresets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */