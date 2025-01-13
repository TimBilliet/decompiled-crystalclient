package wdl.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import wdl.WDL;
import wdl.WorldBackup;

import java.io.IOException;

public class GuiWDLBackup extends GuiScreen {
    private final GuiScreen parent;

    private final String description;

    private WorldBackup.WorldBackupType backupType;

    public GuiWDLBackup(GuiScreen parent) {
        this.parent = parent;
        this

                .description = I18n.format("wdl.gui.backup.description1", new Object[0]) + "\n\n" + I18n.format("wdl.gui.backup.description2", new Object[0]) + "\n\n" + I18n.format("wdl.gui.backup.description3", new Object[0]);
    }

    public void initGui() {
        this.backupType = WorldBackup.WorldBackupType.match(WDL.baseProps
                .getProperty("Backup", "ZIP"));
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, 32,
                getBackupButtonText()));
        this.buttonList.add(new GuiButton(100, this.width / 2 - 100, this.height - 29,
                I18n.format("gui.done", new Object[0])));
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (!button.enabled)
            return;
        if (button.id == 0) {
            switch (this.backupType) {
                case NONE:
                    this.backupType = WorldBackup.WorldBackupType.FOLDER;
                    break;
                case FOLDER:
                    this.backupType = WorldBackup.WorldBackupType.ZIP;
                    break;
                case ZIP:
                    this.backupType = WorldBackup.WorldBackupType.NONE;
                    break;
            }
            button.displayString = getBackupButtonText();
        } else if (button.id == 100) {
            this.mc.displayGuiScreen(this.parent);
        }
    }

    private String getBackupButtonText() {
        return I18n.format("wdl.gui.backup.backupMode", new Object[]{this.backupType
                .getDescription()});
    }

    public void onGuiClosed() {
        WDL.baseProps.setProperty("Backup", this.backupType.name());
        WDL.saveProps();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Utils.drawListBackground(23, 32, 0, 0, this.height, this.width);
        drawCenteredString(this.fontRendererObj,
                I18n.format("wdl.gui.backup.title", new Object[0]), this.width / 2, 8, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
        Utils.drawGuiInfoBox(this.description, this.width - 50, 3 * this.height / 5, this.width, this.height, 48);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\gui\GuiWDLBackup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */