package chylex.respack.packs;

import chylex.respack.gui.GuiCustomResourcePacks;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.resources.ResourcePackRepository;

public abstract class ResourcePackListEntryCustom extends ResourcePackListEntryFoundCustom {
    public ResourcePackListEntryCustom(GuiCustomResourcePacks ownerScreen) {
        super((GuiScreenResourcePacks) ownerScreen, (ResourcePackRepository.Entry) null);
    }

    public abstract void bindResourcePackIcon();

    public abstract String getResourcePackDescription();

    public abstract String getResourcePackName();

    public boolean func_148310_d() {
        return super.func_148310_d();
    }

    public boolean func_148307_h() {
        return super.func_148307_h();
    }

    public boolean func_148308_f() {
        return super.func_148308_f();
    }

    public boolean func_148309_e() {
        return super.func_148309_e();
    }

    public boolean func_148314_g() {
        return super.func_148314_g();
    }
}
