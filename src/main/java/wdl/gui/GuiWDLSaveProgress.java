package wdl.gui;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import wdl.WorldBackup;

import java.io.IOException;

public class GuiWDLSaveProgress extends GuiTurningCameraBase implements WorldBackup.IBackupProgressMonitor {
  private final String title;
  
  private volatile String majorTaskMessage = "";
  
  private volatile String minorTaskMessage = "";
  
  private volatile int majorTaskNumber;
  
  private final int majorTaskCount;
  
  private volatile int minorTaskProgress;
  
  private volatile int minorTaskMaximum;
  
  private volatile boolean doneWorking = false;
  
  public GuiWDLSaveProgress(String title, int taskCount) {
    this.title = title;
    this.majorTaskCount = taskCount;
    this.majorTaskNumber = 0;
  }
  
  public void startMajorTask(String message, int minorTaskMaximum) {
    this.majorTaskMessage = message;
    this.majorTaskNumber++;
    this.minorTaskMessage = "";
    this.minorTaskProgress = 0;
    this.minorTaskMaximum = minorTaskMaximum;
  }
  
  public void setMinorTaskProgress(String message, int progress) {
    this.minorTaskMessage = message;
    this.minorTaskProgress = progress;
  }
  
  public void setMinorTaskProgress(int progress) {
    this.minorTaskProgress = progress;
  }
  
  public void setDoneWorking() {
    this.doneWorking = true;
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    if (this.doneWorking) {
      this.mc.displayGuiScreen(null);
    } else {
      Utils.drawBorder(32, 32, 0, 0, this.height, this.width);
      String majorTaskInfo = this.majorTaskMessage;
      if (this.majorTaskCount > 1)
        majorTaskInfo = I18n.format("wdl.gui.saveProgress.progressInfo", new Object[] { this.majorTaskMessage, 
              
              Integer.valueOf(this.majorTaskNumber), Integer.valueOf(this.majorTaskCount) }); 
      String minorTaskInfo = this.minorTaskMessage;
      if (this.minorTaskMaximum > 1)
        majorTaskInfo = I18n.format("wdl.gui.saveProgress.progressInfo", new Object[] { this.minorTaskMessage, 
              
              Integer.valueOf(this.minorTaskProgress), Integer.valueOf(this.minorTaskMaximum) }); 
      drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 8, 16777215);
      drawCenteredString(this.fontRendererObj, majorTaskInfo, this.width / 2, 100, 16777215);
      if (this.minorTaskMaximum > 0) {
        drawProgressBar(110, 84, 89, this.majorTaskNumber * this.minorTaskMaximum + this.minorTaskProgress, (this.majorTaskCount + 1) * this.minorTaskMaximum);
      } else {
        drawProgressBar(110, 84, 89, this.majorTaskNumber, this.majorTaskCount);
      } 
      drawCenteredString(this.fontRendererObj, minorTaskInfo, this.width / 2, 130, 16777215);
      drawProgressBar(140, 64, 69, this.minorTaskProgress, this.minorTaskMaximum);
      super.drawScreen(mouseX, mouseY, partialTicks);
    } 
  }
  
  private void drawProgressBar(int y, int emptyV, int filledV, int progress, int maximum) {
    if (maximum == 0)
      return; 
    this.mc.getTextureManager().bindTexture(Gui.icons);
    int fullWidth = 182;
    int currentWidth = progress * 182 / maximum;
    int height = 5;
    int x = this.width / 2 - 91;
    int u = 0;
    drawTexturedModalRect(x, y, 0, emptyV, 182, 5);
    drawTexturedModalRect(x, y, 0, filledV, currentWidth, 5);
  }
  
  protected void keyTyped(char typedChar, int keyCode) throws IOException {}
  
  public void setNumberOfFiles(int num) {
    this.minorTaskMaximum = num;
  }
  
  public void onNextFile(String name) {
    this.minorTaskProgress++;
    this.minorTaskMessage = I18n.format("wdl.saveProgress.backingUp.file", new Object[] { name });
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\gui\GuiWDLSaveProgress.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */