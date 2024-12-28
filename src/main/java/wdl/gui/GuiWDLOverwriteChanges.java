package wdl.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import wdl.WDL;
import wdl.WDLMessageTypes;
import wdl.WDLMessages;
import wdl.WorldBackup;
import wdl.api.IWDLMessageType;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GuiWDLOverwriteChanges extends GuiTurningCameraBase implements WorldBackup.IBackupProgressMonitor {
  private volatile boolean backingUp;
  
  private volatile String backupData;
  
  private volatile int backupCount;
  
  private volatile int backupCurrent;
  
  private volatile String backupFile;
  
  private int infoBoxX;
  
  private int infoBoxY;
  
  private int infoBoxWidth;
  
  private int infoBoxHeight;
  
  private GuiButton backupAsZipButton;
  
  private GuiButton backupAsFolderButton;
  
  private GuiButton downloadNowButton;
  
  private GuiButton cancelButton;
  
  private final long lastSaved;
  
  private final long lastPlayed;
  
  private String title;
  
  private String footer;
  
  private String captionTitle;
  
  private String captionSubtitle;
  
  private String overwriteWarning1;
  
  private String overwriteWarning2;
  
  private String backingUpTitle;
  
  private class BackupThread extends Thread {
    private final DateFormat folderDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    
    private final boolean zip;
    
    public BackupThread(boolean zip) {
      this.zip = zip;
    }
    
    public void run() {
      try {
        String backupName = WDL.getWorldFolderName(WDL.worldName) + "_" + this.folderDateFormat.format(new Date()) + "_user" + (this.zip ? ".zip" : "");
        if (this.zip) {
          GuiWDLOverwriteChanges.this.backupData = 
            I18n.format("wdl.gui.overwriteChanges.backingUp.zip", new Object[] { backupName });
        } else {
          GuiWDLOverwriteChanges.this.backupData = 
            I18n.format("wdl.gui.overwriteChanges.backingUp.folder", new Object[] { backupName });
        } 
        File fromFolder = WDL.saveHandler.getWorldDirectory();
        File backupFile = new File(fromFolder.getParentFile(), backupName);
        if (backupFile.exists())
          throw new IOException("Backup target (" + backupFile + ") already exists!"); 
        if (this.zip) {
          WorldBackup.zipDirectory(fromFolder, backupFile, GuiWDLOverwriteChanges.this);
        } else {
          WorldBackup.copyDirectory(fromFolder, backupFile, GuiWDLOverwriteChanges.this);
        } 
      } catch (Exception e) {
        WDLMessages.chatMessageTranslated((IWDLMessageType)WDLMessageTypes.ERROR, "wdl.messages.generalError.failedToSetUpEntityUI", new Object[0]);
      } finally {
        GuiWDLOverwriteChanges.this.backingUp = false;
        WDL.overrideLastModifiedCheck = true;
        GuiWDLOverwriteChanges.this.mc.displayGuiScreen(null);
        WDL.startDownload();
      } 
    }
  }
  
  public GuiWDLOverwriteChanges(long lastSaved, long lastPlayed) {
    this.backingUp = false;
    this.backupData = "";
    this.backupFile = "";
    this.lastSaved = lastSaved;
    this.lastPlayed = lastPlayed;
  }
  
  public void initGui() {
    this.backingUp = false;
    this.title = I18n.format("wdl.gui.overwriteChanges.title", new Object[0]);
    if (this.lastSaved != -1L) {
      this.footer = I18n.format("wdl.gui.overwriteChanges.footer", new Object[] { Long.valueOf(this.lastSaved), Long.valueOf(this.lastPlayed) });
    } else {
      this.footer = I18n.format("wdl.gui.overwriteChanges.footerNeverSaved", new Object[] { Long.valueOf(this.lastPlayed) });
    } 
    this.captionTitle = I18n.format("wdl.gui.overwriteChanges.captionTitle", new Object[0]);
    this.captionSubtitle = I18n.format("wdl.gui.overwriteChanges.captionSubtitle", new Object[0]);
    this.overwriteWarning1 = I18n.format("wdl.gui.overwriteChanges.overwriteWarning1", new Object[0]);
    this.overwriteWarning2 = I18n.format("wdl.gui.overwriteChanges.overwriteWarning2", new Object[0]);
    this.backingUpTitle = I18n.format("wdl.gui.overwriteChanges.backingUp.title", new Object[0]);
    this.infoBoxWidth = this.fontRendererObj.getStringWidth(this.overwriteWarning1);
    this.infoBoxHeight = 132;
    if (this.infoBoxWidth < 200)
      this.infoBoxWidth = 200; 
    this.infoBoxY = 48;
    this.infoBoxX = this.width / 2 - this.infoBoxWidth / 2;
    int x = this.width / 2 - 100;
    int y = this.infoBoxY + 22;
    this
      .backupAsZipButton = new GuiButton(0, x, y, I18n.format("wdl.gui.overwriteChanges.asZip.name", new Object[0]));
    this.buttonList.add(this.backupAsZipButton);
    y += 22;
    this
      .backupAsFolderButton = new GuiButton(1, x, y, I18n.format("wdl.gui.overwriteChanges.asFolder.name", new Object[0]));
    this.buttonList.add(this.backupAsFolderButton);
    y += 22;
    this
      .downloadNowButton = new GuiButton(2, x, y, I18n.format("wdl.gui.overwriteChanges.startNow.name", new Object[0]));
    this.buttonList.add(this.downloadNowButton);
    y += 22;
    this
      .cancelButton = new GuiButton(3, x, y, I18n.format("wdl.gui.overwriteChanges.cancel.name", new Object[0]));
    this.buttonList.add(this.cancelButton);
    super.initGui();
  }
  
  protected void keyTyped(char typedChar, int keyCode) throws IOException {
    if (keyCode == 1)
      return; 
    super.keyTyped(typedChar, keyCode);
  }
  
  protected void actionPerformed(GuiButton button) throws IOException {
    if (this.backingUp)
      return; 
    if (button.id == 0) {
      this.backingUp = true;
      (new BackupThread(true)).start();
    } 
    if (button.id == 1) {
      this.backingUp = true;
      (new BackupThread(false)).start();
    } 
    if (button.id == 2) {
      WDL.overrideLastModifiedCheck = true;
      this.mc.displayGuiScreen(null);
      WDL.startDownload();
    } 
    if (button.id == 3)
      this.mc.displayGuiScreen(null); 
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    if (this.backingUp) {
      drawBackground(0);
      drawCenteredString(this.fontRendererObj, this.backingUpTitle, this.width / 2, this.height / 4 - 40, 16777215);
      drawCenteredString(this.fontRendererObj, this.backupData, this.width / 2, this.height / 4 - 10, 16777215);
      if (this.backupFile != null) {
        String text = I18n.format("wdl.gui.overwriteChanges.backingUp.progress", new Object[] { Integer.valueOf(this.backupCurrent), Integer.valueOf(this.backupCount), this.backupFile });
        drawCenteredString(this.fontRendererObj, text, this.width / 2, this.height / 4 + 10, 16777215);
      } 
    } else {
      drawDefaultBackground();
      Utils.drawBorder(32, 22, 0, 0, this.height, this.width);
      drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 8, 16777215);
      drawCenteredString(this.fontRendererObj, this.footer, this.width / 2, this.height - 8 - this.fontRendererObj.FONT_HEIGHT, 16777215);
      drawRect(this.infoBoxX - 5, this.infoBoxY - 5, this.infoBoxX + this.infoBoxWidth + 5, this.infoBoxY + this.infoBoxHeight + 5, -1342177280);
      drawCenteredString(this.fontRendererObj, this.captionTitle, this.width / 2, this.infoBoxY, 16777215);
      drawCenteredString(this.fontRendererObj, this.captionSubtitle, this.width / 2, this.infoBoxY + this.fontRendererObj.FONT_HEIGHT, 16777215);
      drawCenteredString(this.fontRendererObj, this.overwriteWarning1, this.width / 2, this.infoBoxY + 115, 16777215);
      drawCenteredString(this.fontRendererObj, this.overwriteWarning2, this.width / 2, this.infoBoxY + 115 + this.fontRendererObj.FONT_HEIGHT, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
      String tooltip = null;
      if (this.backupAsZipButton.isMouseOver()) {
        tooltip = I18n.format("wdl.gui.overwriteChanges.asZip.description", new Object[0]);
      } else if (this.backupAsFolderButton.isMouseOver()) {
        tooltip = I18n.format("wdl.gui.overwriteChanges.asFolder.description", new Object[0]);
      } else if (this.downloadNowButton.isMouseOver()) {
        tooltip = I18n.format("wdl.gui.overwriteChanges.startNow.description", new Object[0]);
      } else if (this.cancelButton.isMouseOver()) {
        tooltip = I18n.format("wdl.gui.overwriteChanges.cancel.description", new Object[0]);
      } 
      Utils.drawGuiInfoBox(tooltip, this.width, this.height, 48);
    } 
  }
  
  public void setNumberOfFiles(int num) {
    this.backupCount = num;
    this.backupCurrent = 0;
  }
  
  public void onNextFile(String name) {
    this.backupCurrent++;
    this.backupFile = name;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\gui\GuiWDLOverwriteChanges.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */