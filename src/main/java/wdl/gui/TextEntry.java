package wdl.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;

class TextEntry implements GuiListExtended.IGuiListEntry {
    private final String text;

    private final int color;

    protected final Minecraft mc;

    public TextEntry(Minecraft mc, String text) {
        this(mc, text, 1048575);
    }

    public TextEntry(Minecraft mc, String text, int color) {
        this.mc = mc;
        this.text = text;
        this.color = color;
    }

    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
        if (y < 0)
            return;
        Utils.drawStringWithShadow(this.text, x, y + 1, this.color);
    }

    public boolean mousePressed(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
        return false;
    }

    public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
    }

    public void setSelected(int slotIndex, int p_178011_2_, int p_178011_3_) {
    }
}
