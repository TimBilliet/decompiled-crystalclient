package wdl.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import wdl.WDL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiWDL extends GuiScreen {
    private String displayedTooltip = null;

    private class GuiWDLButtonList extends GuiListExtended {
        private final List<IGuiListEntry> entries;

        public GuiWDLButtonList() {
            super(GuiWDL.this.mc, GuiWDL.this.width, GuiWDL.this.height, 39, GuiWDL.this.height - 32, 20);
            this.entries = new ArrayList<IGuiListEntry>() {

            };
        }

        private class ButtonEntry implements IGuiListEntry {
            private final GuiButton button;

            private final GuiScreen toOpen;

            private final String tooltip;

            public ButtonEntry(String key, GuiScreen toOpen) {
                this.button = new GuiButton(0, 0, 0, I18n.format("wdl.gui.wdl." + key + ".name", new Object[0]));
                this.toOpen = toOpen;
                this.tooltip = I18n.format("wdl.gui.wdl." + key + ".description", new Object[0]);
            }

            public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {
            }

            public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
                this.button.xPosition = GuiWDL.this.width / 2 - 100;
                this.button.yPosition = y;
                this.button.drawButton(GuiWDLButtonList.this.mc, mouseX, mouseY);
                if (this.button.isMouseOver())
                    GuiWDL.this.displayedTooltip = this.tooltip;
            }

            public boolean mousePressed(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
                if (this.button.mousePressed(GuiWDLButtonList.this.mc, x, y)) {
                    GuiWDLButtonList.this.mc.displayGuiScreen(this.toOpen);
                    this.button.playPressSound(GuiWDLButtonList.this.mc.getSoundHandler());
                    return true;
                }
                return false;
            }

            public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
            }
        }

        public IGuiListEntry getListEntry(int index) {
            return this.entries.get(index);
        }

        protected int getSize() {
            return this.entries.size();
        }
    }

    private String title = "";

    private final GuiScreen parent;

    private GuiTextField worldname;

    private GuiWDLButtonList list;

    public GuiWDL(GuiScreen parent) {
        this.parent = parent;
    }

    public void initGui() {
        if (WDL.isMultiworld && WDL.worldName.isEmpty()) {
            this.mc.displayGuiScreen(new GuiWDLMultiworldSelect(
                    I18n.format("wdl.gui.multiworldSelect.title.changeOptions", new Object[0]), new GuiWDLMultiworldSelect.WorldSelectionCallback() {
                public void onWorldSelected(String selectedWorld) {
                    WDL.worldName = selectedWorld;
                    WDL.isMultiworld = true;
                    WDL.propsFound = true;
                    WDL.worldProps = WDL.loadWorldProps(selectedWorld);
                    GuiWDL.this.mc.displayGuiScreen(GuiWDL.this);
                }

                public void onCancel() {
                    GuiWDL.this.mc.displayGuiScreen(null);
                }
            }));
            return;
        }
        if (!WDL.propsFound) {
            this.mc.displayGuiScreen(new GuiWDLMultiworld(new GuiWDLMultiworld.MultiworldCallback() {
                public void onSelect(boolean enableMutliworld) {
                    WDL.isMultiworld = enableMutliworld;
                    if (WDL.isMultiworld) {
                        GuiWDL.this.mc.displayGuiScreen(new GuiWDLMultiworldSelect(
                                I18n.format("wdl.gui.multiworldSelect.title.changeOptions", new Object[0]), new GuiWDLMultiworldSelect.WorldSelectionCallback() {
                            public void onWorldSelected(String selectedWorld) {
                                WDL.worldName = selectedWorld;
                                WDL.isMultiworld = true;
                                WDL.propsFound = true;
                                WDL.worldProps = WDL.loadWorldProps(selectedWorld);
                                GuiWDL.this.mc.displayGuiScreen(GuiWDL.this);
                            }

                            public void onCancel() {
                                GuiWDL.this.mc.displayGuiScreen(null);
                            }
                        }));
                    } else {
                        WDL.baseProps.setProperty("LinkedWorlds", "");
                        WDL.saveProps();
                        WDL.propsFound = true;
                        GuiWDL.this.mc.displayGuiScreen(GuiWDL.this);
                    }
                }

                public void onCancel() {
                    GuiWDL.this.mc.displayGuiScreen(null);
                }
            }));
            return;
        }
        this.buttonList.clear();
        this.title = I18n.format("wdl.gui.wdl.title", new Object[]{WDL.baseFolderName
                .replace('@', ':')});
        if (WDL.baseProps.getProperty("ServerName").isEmpty())
            WDL.baseProps.setProperty("ServerName", WDL.getServerName());
        this.worldname = new GuiTextField(42, this.fontRendererObj, this.width / 2 - 155, 19, 150, 18);
        this.worldname.setText(WDL.baseProps.getProperty("ServerName"));
        this.buttonList.add(new GuiButton(100, this.width / 2 - 100, this.height - 29,
                I18n.format("gui.done", new Object[0])));
        this.list = new GuiWDLButtonList();
    }

    protected void actionPerformed(GuiButton button) {
        if (!button.enabled)
            return;
        if (button.id == 100)
            this.mc.displayGuiScreen(this.parent);
    }

    public void onGuiClosed() {
        if (this.worldname != null) {
            WDL.baseProps.setProperty("ServerName", this.worldname.getText());
            WDL.saveProps();
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.list.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.worldname.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.list.handleMouseInput();
    }

    protected void mouseMovedOrUp(int mouseX, int mouseY, int state) {
        if (this.list.mouseReleased(mouseX, mouseY, state))
            return;
        super.mouseClickMove(mouseX, mouseY, state, 0);
//    super.mouseMovedOrUp(mouseX, mouseY, state);
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.worldname.textboxKeyTyped(typedChar, keyCode);
    }

    public void updateScreen() {
        this.worldname.updateCursorCounter();
        super.updateScreen();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        this.displayedTooltip = null;
        this.list.drawScreen(mouseX, mouseY, partialTicks);
        drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 8, 16777215);
        String name = I18n.format("wdl.gui.wdl.worldname", new Object[0]);
        drawString(this.fontRendererObj, name, this.worldname.xPosition - this.fontRendererObj
                .getStringWidth(name + " "), 26, 16777215);
        this.worldname.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
        Utils.drawGuiInfoBox(this.displayedTooltip, this.width, this.height, 48);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\gui\GuiWDL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */