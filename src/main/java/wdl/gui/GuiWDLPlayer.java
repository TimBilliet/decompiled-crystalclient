package wdl.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import wdl.WDL;

import java.io.IOException;

public class GuiWDLPlayer extends GuiScreen {
    private String title;

    private final GuiScreen parent;

    private GuiButton healthBtn;

    private GuiButton hungerBtn;

    private GuiButton playerPosBtn;

    private GuiButton pickPosBtn;

    private boolean showPosFields = false;

    private GuiNumericTextField posX;

    private GuiNumericTextField posY;

    private GuiNumericTextField posZ;

    private int posTextY;

    public GuiWDLPlayer(GuiScreen var1) {
        this.parent = var1;
    }

    public void initGui() {
        this.buttonList.clear();
        this.title = I18n.format("wdl.gui.player.title", new Object[]{WDL.baseFolderName
                .replace('@', ':')});
        int y = this.height / 4 - 15;
        this.healthBtn = new GuiButton(1, this.width / 2 - 100, y, getHealthText());
        this.buttonList.add(this.healthBtn);
        y += 22;
        this.hungerBtn = new GuiButton(2, this.width / 2 - 100, y, getHungerText());
        this.buttonList.add(this.hungerBtn);
        y += 22;
        this
                .playerPosBtn = new GuiButton(3, this.width / 2 - 100, y, getPlayerPosText());
        this.buttonList.add(this.playerPosBtn);
        y += 22;
        this.posTextY = y + 4;
        this.posX = new GuiNumericTextField(40, this.fontRendererObj, this.width / 2 - 87, y, 50, 16);
        this.posY = new GuiNumericTextField(41, this.fontRendererObj, this.width / 2 - 19, y, 50, 16);
        this.posZ = new GuiNumericTextField(42, this.fontRendererObj, this.width / 2 + 48, y, 50, 16);
        this.posX.setText(WDL.worldProps.getProperty("PlayerX"));
        this.posY.setText(WDL.worldProps.getProperty("PlayerY"));
        this.posZ.setText(WDL.worldProps.getProperty("PlayerZ"));
        this.posX.setMaxStringLength(7);
        this.posY.setMaxStringLength(7);
        this.posZ.setMaxStringLength(7);
        y += 18;
        this
                .pickPosBtn = new GuiButton(4, this.width / 2 - 0, y, 100, 20, I18n.format("wdl.gui.player.setPositionToCurrentPosition", new Object[0]));
        this.buttonList.add(this.pickPosBtn);
        upadatePlayerPosVisibility();
        this.buttonList.add(new GuiButton(100, this.width / 2 - 100, this.height - 29,
                I18n.format("gui.done", new Object[0])));
    }

    protected void actionPerformed(GuiButton var1) {
        if (var1.enabled)
            if (var1.id == 1) {
                cycleHealth();
            } else if (var1.id == 2) {
                cycleHunger();
            } else if (var1.id == 3) {
                cyclePlayerPos();
            } else if (var1.id == 4) {
                setPlayerPosToPlayerPosition();
            } else if (var1.id == 100) {
                this.mc.displayGuiScreen(this.parent);
            }
    }

    public void onGuiClosed() {
        if (this.showPosFields) {
            WDL.worldProps.setProperty("PlayerX", this.posX.getText());
            WDL.worldProps.setProperty("PlayerY", this.posY.getText());
            WDL.worldProps.setProperty("PlayerZ", this.posZ.getText());
        }
        WDL.saveProps();
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.showPosFields) {
            this.posX.mouseClicked(mouseX, mouseY, mouseButton);
            this.posY.mouseClicked(mouseX, mouseY, mouseButton);
            this.posZ.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.posX.textboxKeyTyped(typedChar, keyCode);
        this.posY.textboxKeyTyped(typedChar, keyCode);
        this.posZ.textboxKeyTyped(typedChar, keyCode);
    }

    public void updateScreen() {
        this.posX.updateCursorCounter();
        this.posY.updateCursorCounter();
        this.posZ.updateCursorCounter();
        super.updateScreen();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Utils.drawListBackground(23, 32, 0, 0, this.height, this.width);
        drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 8, 16777215);
        String tooltip = null;
        if (this.showPosFields) {
            drawString(this.fontRendererObj, "X:", this.width / 2 - 99, this.posTextY, 16777215);
            drawString(this.fontRendererObj, "Y:", this.width / 2 - 31, this.posTextY, 16777215);
            drawString(this.fontRendererObj, "Z:", this.width / 2 + 37, this.posTextY, 16777215);
            this.posX.drawTextBox();
            this.posY.drawTextBox();
            this.posZ.drawTextBox();
            if (Utils.isMouseOverTextBox(mouseX, mouseY, this.posX)) {
                tooltip = I18n.format("wdl.gui.player.positionTextBox.description", new Object[]{"X"});
            } else if (Utils.isMouseOverTextBox(mouseX, mouseY, this.posY)) {
                tooltip = I18n.format("wdl.gui.player.positionTextBox.description", new Object[]{"Y"});
            } else if (Utils.isMouseOverTextBox(mouseX, mouseY, this.posZ)) {
                tooltip = I18n.format("wdl.gui.player.positionTextBox.description", new Object[]{"Z"});
            }
            if (this.pickPosBtn.isMouseOver())
                tooltip = I18n.format("wdl.gui.player.setPositionToCurrentPosition.description", new Object[0]);
        }
        if (this.healthBtn.isMouseOver())
            tooltip = I18n.format("wdl.gui.player.health.description", new Object[0]);
        if (this.hungerBtn.isMouseOver())
            tooltip = I18n.format("wdl.gui.player.hunger.description", new Object[0]);
        if (this.playerPosBtn.isMouseOver())
            tooltip = I18n.format("wdl.gui.player.position.description", new Object[0]);
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (tooltip != null)
            Utils.drawGuiInfoBox(tooltip, this.width, this.height, 48);
    }

    private void cycleHealth() {
        String prop = WDL.baseProps.getProperty("PlayerHealth");
        if (prop.equals("keep")) {
            WDL.baseProps.setProperty("PlayerHealth", "20");
        } else if (prop.equals("20")) {
            WDL.baseProps.setProperty("PlayerHealth", "keep");
        }
        this.healthBtn.displayString = getHealthText();
    }

    private void cycleHunger() {
        String prop = WDL.baseProps.getProperty("PlayerFood");
        if (prop.equals("keep")) {
            WDL.baseProps.setProperty("PlayerFood", "20");
        } else if (prop.equals("20")) {
            WDL.baseProps.setProperty("PlayerFood", "keep");
        }
        this.hungerBtn.displayString = getHungerText();
    }

    private void cyclePlayerPos() {
        String prop = WDL.worldProps.getProperty("PlayerPos");
        if (prop.equals("keep")) {
            WDL.worldProps.setProperty("PlayerPos", "xyz");
        } else if (prop.equals("xyz")) {
            WDL.worldProps.setProperty("PlayerPos", "keep");
        }
        this.playerPosBtn.displayString = getPlayerPosText();
        upadatePlayerPosVisibility();
    }

    private String getHealthText() {
        String result = I18n.format("wdl.gui.player.health." + WDL.baseProps
                .getProperty("PlayerHealth"), new Object[0]);
        if (result.startsWith("wdl.gui.player.health."))
            result = I18n.format("wdl.gui.player.health.custom", new Object[]{WDL.baseProps
                    .getProperty("PlayerHealth")});
        return result;
    }

    private String getHungerText() {
        String result = I18n.format("wdl.gui.player.hunger." + WDL.baseProps
                .getProperty("PlayerFood"), new Object[0]);
        if (result.startsWith("wdl.gui.player.hunger."))
            result = I18n.format("wdl.gui.player.hunger.custom", new Object[]{WDL.baseProps
                    .getProperty("PlayerFood")});
        return result;
    }

    private void upadatePlayerPosVisibility() {
        this.showPosFields = WDL.worldProps.getProperty("PlayerPos").equals("xyz");
        this.pickPosBtn.visible = this.showPosFields;
    }

    private String getPlayerPosText() {
        return I18n.format("wdl.gui.player.position." + WDL.worldProps
                .getProperty("PlayerPos"), new Object[0]);
    }

    private void setPlayerPosToPlayerPosition() {
        this.posX.setValue((int) WDL.thePlayer.posX);
        this.posY.setValue((int) WDL.thePlayer.posY);
        this.posZ.setValue((int) WDL.thePlayer.posZ);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\gui\GuiWDLPlayer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */