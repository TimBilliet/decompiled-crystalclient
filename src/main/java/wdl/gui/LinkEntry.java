package wdl.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

class LinkEntry extends TextEntry {
    private final String link;

    private final int textWidth;

    private final int linkWidth;

    public LinkEntry(Minecraft mc, String text, String link) {
        super(mc, text, 5592575);
        this.link = link;
        this.textWidth = mc.fontRendererObj.getStringWidth(text);
        this.linkWidth = mc.fontRendererObj.getStringWidth(link);
    }

    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
        if (y < 0)
            return;
        super.drawEntry(slotIndex, x, y, listWidth, slotHeight, mouseX, mouseY, isSelected);
        int relativeX = mouseX - x;
        int relativeY = mouseY - y;
        if (relativeX >= 0 && relativeX <= this.textWidth && relativeY >= 0 && relativeY <= slotHeight) {
            int drawX = mouseX - 2;
            if (drawX + this.linkWidth + 4 > listWidth + x)
                drawX = listWidth + x - 4 + this.linkWidth;
            Gui.drawRect(drawX, mouseY - 2, drawX + this.linkWidth + 4, mouseY + this.mc.fontRendererObj.FONT_HEIGHT + 2, -2147483648);
            Utils.drawStringWithShadow(this.link, drawX + 2, mouseY, 16777215);
        }
    }

    public boolean mousePressed(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
        if (relativeX >= 0 && relativeX <= this.textWidth) {
            Utils.openLink(this.link);
            return true;
        }
        return false;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\gui\LinkEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */