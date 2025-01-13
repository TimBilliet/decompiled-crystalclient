package chylex.respack.packs;

import chylex.respack.gui.GuiCustomResourcePacks;
import chylex.respack.gui.GuiUtils;
import net.minecraft.util.ResourceLocation;

import java.io.File;

public class ResourcePackListEntryFolder extends ResourcePackListEntryCustom {
    private static final ResourceLocation folderResource = new ResourceLocation("crystalclient", "gui/textures/better_resource_packs/folder.png");

    private final GuiCustomResourcePacks ownerScreen;

    public final File folder;

    public final String folderName;

    public final boolean isUp;

    public ResourcePackListEntryFolder(GuiCustomResourcePacks ownerScreen, File folder) {
        super(ownerScreen);
        this.ownerScreen = ownerScreen;
        this.folder = folder;
        this.folderName = folder.getName();
        this.isUp = false;
    }

    public ResourcePackListEntryFolder(GuiCustomResourcePacks ownerScreen, File folder, boolean isUp) {
        super(ownerScreen);
        this.ownerScreen = ownerScreen;
        this.folder = folder;
        this.folderName = "..";
        this.isUp = isUp;
    }

    public void bindResourcePackIcon() {
        this.mc.getTextureManager().bindTexture(folderResource);
    }

    public String getResourcePackName() {
        return this.folderName;
    }

    public String getResourcePackDescription() {
        return this.isUp ? "(Back)" : "(Folder)";
    }

    public boolean mousePressed(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_) {
        this.ownerScreen.moveToFolder(this.folder);
        return true;
    }

    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
        GuiUtils.renderFolderEntry(this, x, y, isSelected);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\chylex\chylex.respack\packs\ResourcePackListEntryFolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */