package wdl.gui;

import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import wdl.MessageTypeCategory;
import wdl.WDL;
import wdl.WDLMessages;
import wdl.api.IWDLMessageType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiWDLMessages extends GuiScreen {
    private String hoveredButtonDescription = null;

    private final GuiScreen parent;

    private GuiMessageTypeList list;

    private GuiButton enableAllButton;

    private GuiButton resetButton;

    private class GuiMessageTypeList extends GuiListExtended {
        private final List<IGuiListEntry> entries;

        public GuiMessageTypeList() {
            super(GuiWDLMessages.this.mc, GuiWDLMessages.this.width, GuiWDLMessages.this.height, 39, GuiWDLMessages.this.height - 32, 20);
            this.entries = new ArrayList<IGuiListEntry>() {

            };
        }

        private class CategoryEntry implements IGuiListEntry {
            private final GuiButton button;

            private final MessageTypeCategory category;

            public CategoryEntry(MessageTypeCategory category) {
                this.category = category;
                this.button = new GuiButton(0, 0, 0, 80, 20, "");
            }

            public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {
            }

            public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
                GuiWDLMessages.this.drawCenteredString(GuiWDLMessages.this.fontRendererObj, this.category.getDisplayName(), GuiWDLMessages.this.width / 2 - 40, y + slotHeight - GuiMessageTypeList.this.mc.fontRendererObj.FONT_HEIGHT - 1, 16777215);
                this.button.xPosition = GuiWDLMessages.this.width / 2 + 20;
                this.button.yPosition = y;
                this.button.displayString = I18n.format("wdl.gui.messages.group." + WDLMessages.isGroupEnabled(this.category), new Object[0]);
                this.button.enabled = WDLMessages.enableAllMessages;
                this.button.drawButton(GuiMessageTypeList.this.mc, mouseX, mouseY);
            }

            public boolean mousePressed(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
                if (this.button.mousePressed(GuiMessageTypeList.this.mc, x, y)) {
                    WDLMessages.toggleGroupEnabled(this.category);
                    this.button.playPressSound(GuiMessageTypeList.this.mc.getSoundHandler());
                    return true;
                }
                return false;
            }

            public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
            }
        }

        private class MessageTypeEntry implements IGuiListEntry {
            private final GuiButton button;

            private final IWDLMessageType type;

            private final MessageTypeCategory category;

            public MessageTypeEntry(IWDLMessageType type, MessageTypeCategory category) {
                this.type = type;
                this.button = new GuiButton(0, 0, 0, type.toString());
                this.category = category;
            }

            public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {
            }

            public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
                this.button.xPosition = GuiWDLMessages.this.width / 2 - 100;
                this.button.yPosition = y;
                this.button.displayString = I18n.format("wdl.gui.messages.message." + WDLMessages.isEnabled(this.type), new Object[]{this.type.getDisplayName()});
                this.button.enabled = (WDLMessages.enableAllMessages && WDLMessages.isGroupEnabled(this.category));
                this.button.drawButton(GuiMessageTypeList.this.mc, mouseX, mouseY);
                if (this.button.isMouseOver())
                    GuiWDLMessages.this.hoveredButtonDescription = this.type.getDescription();
            }

            public boolean mousePressed(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
                if (this.button.mousePressed(GuiMessageTypeList.this.mc, x, y)) {
                    WDLMessages.toggleEnabled(this.type);
                    this.button.playPressSound(GuiMessageTypeList.this.mc.getSoundHandler());
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

    public GuiWDLMessages(GuiScreen parent) {
        this.parent = parent;
    }

    public void initGui() {
        this
                .enableAllButton = new GuiButton(100, this.width / 2 - 155, 18, 150, 20, getAllEnabledText());
        this.buttonList.add(this.enableAllButton);
        this
                .resetButton = new GuiButton(101, this.width / 2 + 5, 18, 150, 20, I18n.format("wdl.gui.messages.reset", new Object[0]));
        this.buttonList.add(this.resetButton);
        this.list = new GuiMessageTypeList();
        this.buttonList.add(new GuiButton(102, this.width / 2 - 100, this.height - 29,
                I18n.format("gui.done", new Object[0])));
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (!button.enabled)
            return;
        if (button.id == 100) {
            //WDLMessages.enableAllMessages ^= 0x1;
            WDL.baseProps.setProperty("Messages.enableAll",
                    Boolean.toString(WDLMessages.enableAllMessages));
            button.displayString = getAllEnabledText();
        } else if (button.id == 101) {
            this.mc.displayGuiScreen((GuiScreen) new GuiYesNo((GuiYesNoCallback) this,
                    I18n.format("wdl.gui.messages.reset.confirm.title", new Object[0]),
                    I18n.format("wdl.gui.messages.reset.confirm.subtitle", new Object[0]), 101));
        } else if (button.id == 102) {
            this.mc.displayGuiScreen(this.parent);
        }
    }

    public void confirmClicked(boolean result, int id) {
        if (result &&
                id == 101)
            WDLMessages.resetEnabledToDefaults();
        this.mc.displayGuiScreen(this);
    }

    public void onGuiClosed() {
        WDL.saveProps();
    }

    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.list.handleMouseInput();
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (this.list.mouseClicked(mouseX, mouseY, mouseButton))
            return;
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void mouseMovedOrUp(int mouseX, int mouseY, int state) {
        if (this.list.mouseReleased(mouseX, mouseY, state))
            return;
        super.mouseClickMove(mouseX, mouseY, state, 0);
//    super.mouseMovedOrUp(mouseX, mouseY, state);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.hoveredButtonDescription = null;
        drawDefaultBackground();
        this.list.drawScreen(mouseX, mouseY, partialTicks);
        drawCenteredString(this.fontRendererObj,
                I18n.format("wdl.gui.messages.message.title", new Object[0]), this.width / 2, 8, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (this.hoveredButtonDescription != null) {
            Utils.drawGuiInfoBox(this.hoveredButtonDescription, this.width, this.height, 48);
        } else if (this.enableAllButton.isMouseOver()) {
            Utils.drawGuiInfoBox(
                    I18n.format("wdl.gui.messages.all.description", new Object[0]), this.width, this.height, 48);
        } else if (this.resetButton.isMouseOver()) {
            Utils.drawGuiInfoBox(
                    I18n.format("wdl.gui.messages.reset.description", new Object[0]), this.width, this.height, 48);
        }
    }

    private String getAllEnabledText() {
        return I18n.format("wdl.gui.messages.all." + WDLMessages.enableAllMessages, new Object[0]);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\gui\GuiWDLMessages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */