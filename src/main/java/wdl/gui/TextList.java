package wdl.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;

import java.util.ArrayList;
import java.util.List;

class TextList extends GuiListExtended {
    public final int topMargin;

    public final int bottomMargin;

    private final List<IGuiListEntry> entries;

    public TextList(Minecraft mc, int width, int height, int topMargin, int bottomMargin) {
        super(mc, width, height, topMargin, height - bottomMargin, mc.fontRendererObj.FONT_HEIGHT + 1);
        this.topMargin = topMargin;
        this.bottomMargin = bottomMargin;
        this.entries = new ArrayList<>();
    }

    public IGuiListEntry getListEntry(int index) {
        return this.entries.get(index);
    }

    protected int getSize() {
        return this.entries.size();
    }

    protected int getScrollBarX() {
        return this.width - 10;
    }

    public int getListWidth() {
        return this.width - 18;
    }

    public void addLine(String text) {
        List<String> lines = Utils.wordWrap(text, getListWidth());
        for (String line : lines)
            this.entries.add(new TextEntry(this.mc, line, 16777215));
    }

    public void addBlankLine() {
        this.entries.add(new TextEntry(this.mc, "", 16777215));
    }

    public void addLinkLine(String text, String URL) {
        List<String> lines = Utils.wordWrap(text, getListWidth());
        for (String line : lines)
            this.entries.add(new LinkEntry(this.mc, line, URL));
    }

    public void clearLines() {
        this.entries.clear();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\gui\TextList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */