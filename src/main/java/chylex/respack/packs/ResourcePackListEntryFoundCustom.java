package chylex.respack.packs;

import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.client.resources.ResourcePackListEntryFound;
import net.minecraft.client.resources.ResourcePackRepository;

import java.util.List;

public class ResourcePackListEntryFoundCustom extends ResourcePackListEntryFound {
    private final ResourcePackRepository.Entry resourcePackEntry;

    public ResourcePackListEntryFoundCustom(GuiScreenResourcePacks resourcePacksGUIIn, ResourcePackRepository.Entry p_i45053_2_) {
        super(resourcePacksGUIIn, p_i45053_2_);
        this.resourcePackEntry = p_i45053_2_;
    }

    public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int mouseXOffset, int mouseYOffset) {
        if (func_148310_d() && mouseXOffset <= 32) {
            if (func_148309_e()) {
                this.resourcePacksGUI.markChanged();
                this.resourcePacksGUI.getListContaining((ResourcePackListEntry) this).remove(this);
                this.resourcePacksGUI.getSelectedResourcePacks().add(0, this);
                return true;
            }
            if (mouseXOffset < 16 && func_148308_f()) {
                this.resourcePacksGUI.getListContaining((ResourcePackListEntry) this).remove(this);
                this.resourcePacksGUI.getAvailableResourcePacks().add(0, this);
                this.resourcePacksGUI.markChanged();
                return true;
            }
            if (mouseXOffset > 16 && mouseYOffset < 16 && func_148314_g()) {
                List<ResourcePackListEntry> list1 = this.resourcePacksGUI.getListContaining((ResourcePackListEntry) this);
                int k = list1.indexOf(this);
                list1.remove(this);
                list1.add(k - 1, this);
                this.resourcePacksGUI.markChanged();
                return true;
            }
            if (mouseXOffset > 16 && mouseYOffset > 16 && func_148307_h()) {
                List<ResourcePackListEntry> list = this.resourcePacksGUI.getListContaining((ResourcePackListEntry) this);
                int i = list.indexOf(this);
                list.remove(this);
                list.add(i + 1, this);
                this.resourcePacksGUI.markChanged();
                return true;
            }
        }
        return false;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\chylex\chylex.respack\packs\ResourcePackListEntryFoundCustom.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */