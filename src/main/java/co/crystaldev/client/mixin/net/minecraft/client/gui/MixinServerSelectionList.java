package co.crystaldev.client.mixin.net.minecraft.client.gui;

import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.gui.ServerSelectionList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin({ServerSelectionList.class})
public abstract class MixinServerSelectionList {
    @Shadow
    @Final
    private List<ServerListEntryNormal> serverListInternet;

    @Shadow
    @Final
    private GuiListExtended.IGuiListEntry lanScanEntry;

    @Inject(method = {"getListEntry"}, at = {@At("HEAD")}, cancellable = true)
    private void resolveIndexOutOfBounds(int index, CallbackInfoReturnable<GuiListExtended.IGuiListEntry> ci) {
        if (index > this.serverListInternet.size())
            ci.setReturnValue(this.lanScanEntry);
    }
}
