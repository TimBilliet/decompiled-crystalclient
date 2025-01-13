package wdl.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import wdl.WDL;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GuiWDLMultiworldSelect extends GuiTurningCameraBase {
    private final WorldSelectionCallback callback;

    private final String title;

    private GuiButton cancelBtn;

    private GuiButton acceptBtn;

    private GuiTextField newNameField;

    private GuiTextField searchField;

    private GuiButton newWorldButton;

    private boolean showNewWorldTextBox;

    private final List<MultiworldInfo> linkedWorlds;

    private final List<MultiworldInfo> linkedWorldsFiltered;

    private MultiworldInfo selectedMultiWorld;

    private class WorldGuiButton extends GuiButton {
        public WorldGuiButton(int offset, int x, int y, int width, int height) {
            super(offset, x, y, width, height, "");
        }

        public WorldGuiButton(int offset, int x, int y, String worldName, String displayName) {
            super(offset, x, y, "");
        }

        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            MultiworldInfo info = getWorldInfo();
            if (info == null) {
                this.displayString = "";
                this.enabled = false;
            } else {
                this.displayString = info.displayName;
                this.enabled = true;
            }
            if (info != null && info == GuiWDLMultiworldSelect.this.selectedMultiWorld)
                drawRect(this.xPosition - 2, this.yPosition - 2, this.xPosition + this.width + 2, this.yPosition + this.height + 2, -16744704);
            super.drawButton(mc, mouseX, mouseY);
        }

        public MultiworldInfo getWorldInfo() {
            int location = GuiWDLMultiworldSelect.this.index + this.id;
            if (location < 0)
                return null;
            if (location >= GuiWDLMultiworldSelect.this.linkedWorldsFiltered.size())
                return null;
            return GuiWDLMultiworldSelect.this.linkedWorldsFiltered.get(location);
        }
    }

    private static class MultiworldInfo {
        public final String folderName;

        public final String displayName;

        private List<String> description;

        public MultiworldInfo(String folderName, String displayName) {
            this.folderName = folderName;
            this.displayName = displayName;
        }

        public List<String> getDescription() {
            if (this.description == null) {
                this.description = new ArrayList<>();
                this.description.add("Defined dimensions:");
                File savesFolder = new File(WDL.minecraft.mcDataDir, "saves");
                File world = new File(savesFolder, WDL.getWorldFolderName(this.folderName));
                File[] subfolders = world.listFiles();
                if (subfolders != null)
                    for (File subfolder : subfolders) {
                        if (subfolder.listFiles() != null)
                            if ((subfolder.listFiles()).length != 0)
                                if (subfolder.getName().equals("region")) {
                                    this.description.add(" * Overworld (#0)");
                                } else if (subfolder.getName().startsWith("DIM")) {
                                    String dimension = subfolder.getName().substring(3);
                                    if (dimension.equals("-1")) {
                                        this.description.add(" * Nether (#-1)");
                                    } else if (dimension.equals("1")) {
                                        this.description.add(" * The End (#1)");
                                    } else {
                                        this.description.add(" * #" + dimension);
                                    }
                                }
                    }
            }
            return this.description;
        }
    }

    private int index = 0;

    private GuiButton nextButton;

    private GuiButton prevButton;

    private int numWorldButtons;

    private String searchText = "";

    public GuiWDLMultiworldSelect(String title, WorldSelectionCallback callback) {
        this.title = title;
        this.callback = callback;
        String[] worldNames = WDL.baseProps.getProperty("LinkedWorlds").split("\\|");
        this.linkedWorlds = new ArrayList<>();
        for (String worldName : worldNames) {
            if (worldName != null && !worldName.isEmpty()) {
                Properties props = WDL.loadWorldProps(worldName);
                if (props.containsKey("WorldName")) {
                    String displayName = props.getProperty("WorldName", worldName);
                    this.linkedWorlds.add(new MultiworldInfo(worldName, displayName));
                }
            }
        }
        this.linkedWorldsFiltered = new ArrayList<>();
        this.linkedWorldsFiltered.addAll(this.linkedWorlds);
    }

    public void initGui() {
        super.initGui();
        this.numWorldButtons = (this.width - 50) / 155;
        if (this.numWorldButtons < 1)
            this.numWorldButtons = 1;
        int offset = (this.numWorldButtons * 155 + 45) / 2;
        int y = this.height - 49;
        this
                .cancelBtn = new GuiButton(-1, this.width / 2 - 155, this.height - 25, 150, 20, I18n.format("gui.cancel", new Object[0]));
        this.buttonList.add(this.cancelBtn);
        this
                .acceptBtn = new GuiButton(-2, this.width / 2 + 5, this.height - 25, 150, 20, I18n.format("wdl.gui.multiworldSelect.done", new Object[0]));
        this.acceptBtn.enabled = (this.selectedMultiWorld != null);
        this.buttonList.add(this.acceptBtn);
        this.prevButton = new GuiButton(-4, this.width / 2 - offset, y, 20, 20, "<");
        this.buttonList.add(this.prevButton);
        for (int i = 0; i < this.numWorldButtons; i++)
            this.buttonList.add(new WorldGuiButton(i, this.width / 2 - offset + i * 155 + 25, y, 150, 20));
        this.nextButton = new GuiButton(-5, this.width / 2 - offset + 25 + this.numWorldButtons * 155, y, 20, 20, ">");
        this.buttonList.add(this.nextButton);
        this
                .newWorldButton = new GuiButton(-3, this.width / 2 - 155, 29, 150, 20, I18n.format("wdl.gui.multiworldSelect.newName", new Object[0]));
        this.buttonList.add(this.newWorldButton);
        this.newNameField = new GuiTextField(40, this.fontRendererObj, this.width / 2 - 155, 29, 150, 20);
        this.searchField = new GuiTextField(41, this.fontRendererObj, this.width / 2 + 5, 29, 150, 20);
        this.searchField.setText(this.searchText);
    }

    protected void actionPerformed(GuiButton button) {
        if (button.enabled)
            if (button instanceof WorldGuiButton) {
                this.selectedMultiWorld = ((WorldGuiButton) button).getWorldInfo();
                this.acceptBtn.enabled = (this.selectedMultiWorld != null);
            } else if (button.id == -1) {
                this.callback.onCancel();
            } else if (button.id == -2) {
                this.callback.onWorldSelected(this.selectedMultiWorld.folderName);
            } else if (button.id == -3) {
                this.showNewWorldTextBox = true;
            } else if (button.id == -4) {
                this.index--;
            } else if (button.id == -5) {
                this.index++;
            }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.showNewWorldTextBox)
            this.newNameField.mouseClicked(mouseX, mouseY, mouseButton);
        this.searchField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1)
            this.callback.onCancel();
        super.keyTyped(typedChar, keyCode);
        if (this.showNewWorldTextBox) {
            this.newNameField.textboxKeyTyped(typedChar, keyCode);
            if (keyCode == 28) {
                String newName = this.newNameField.getText();
                if (newName != null && !newName.isEmpty()) {
                    addMultiworld(newName);
                    this.newNameField.setText("");
                    this.showNewWorldTextBox = false;
                }
            }
        }
        if (this.searchField.textboxKeyTyped(typedChar, keyCode)) {
            this.searchText = this.searchField.getText();
            rebuildFilteredWorlds();
        }
    }

    public void updateScreen() {
        this.newNameField.updateCursorCounter();
        this.searchField.updateCursorCounter();
        super.updateScreen();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.index >= this.linkedWorlds.size() - this.numWorldButtons) {
            this.index = this.linkedWorlds.size() - this.numWorldButtons;
            this.nextButton.enabled = false;
        } else {
            this.nextButton.enabled = true;
        }
        if (this.index <= 0) {
            this.index = 0;
            this.prevButton.enabled = false;
        } else {
            this.prevButton.enabled = true;
        }
        Utils.drawBorder(53, 53, 0, 0, this.height, this.width);
        drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 8, 16777215);
        drawCenteredString(this.fontRendererObj,
                I18n.format("wdl.gui.multiworldSelect.subtitle", new Object[0]), this.width / 2, 18, 16711680);
        if (this.showNewWorldTextBox)
            this.newNameField.drawTextBox();
        this.searchField.drawTextBox();
        if (this.searchField.getText().isEmpty() && !this.searchField.isFocused())
            drawString(this.fontRendererObj,
                    I18n.format("wdl.gui.multiworldSelect.search", new Object[0]), this.searchField.xPosition + 4, this.searchField.yPosition + 6, 9474192);
        this.newWorldButton.visible = !this.showNewWorldTextBox;
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawMultiworldDescription();
    }

    private void addMultiworld(String worldName) {
        String folderName = worldName;
        char[] unsafeChars = "\\/:*?\"<>|.".toCharArray();
        for (char unsafeChar : unsafeChars)
            folderName = folderName.replace(unsafeChar, '_');
        Properties worldProps = new Properties(WDL.baseProps);
        worldProps.setProperty("WorldName", worldName);
        String linkedWorldsProp = WDL.baseProps.getProperty("LinkedWorlds");
        linkedWorldsProp = linkedWorldsProp + "|" + folderName;
        WDL.baseProps.setProperty("LinkedWorlds", linkedWorldsProp);
        WDL.saveProps(folderName, worldProps);
        this.linkedWorlds.add(new MultiworldInfo(folderName, worldName));
        rebuildFilteredWorlds();
    }

    private void rebuildFilteredWorlds() {
        String searchFilter = this.searchText.toLowerCase();
        this.linkedWorldsFiltered.clear();
        for (MultiworldInfo info : this.linkedWorlds) {
            if (info.displayName.toLowerCase().contains(searchFilter))
                this.linkedWorldsFiltered.add(info);
        }
    }

    private void drawMultiworldDescription() {
        if (this.selectedMultiWorld == null)
            return;
        String title = "Info about " + this.selectedMultiWorld.displayName;
        List<String> description = this.selectedMultiWorld.getDescription();
        int maxWidth = this.fontRendererObj.getStringWidth(title);
        for (String line : description) {
            int width = this.fontRendererObj.getStringWidth(line);
            if (width > maxWidth)
                maxWidth = width;
        }
        drawRect(2, 61, 5 + maxWidth + 3, this.height - 61, -2147483648);
        drawString(this.fontRendererObj, title, 5, 64, 16777215);
        int y = 64 + this.fontRendererObj.FONT_HEIGHT;
        for (String s : description) {
            drawString(this.fontRendererObj, s, 5, y, 16777215);
            y += this.fontRendererObj.FONT_HEIGHT;
        }
    }

    public static interface WorldSelectionCallback {
        void onCancel();

        void onWorldSelected(String param1String);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\gui\GuiWDLMultiworldSelect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */