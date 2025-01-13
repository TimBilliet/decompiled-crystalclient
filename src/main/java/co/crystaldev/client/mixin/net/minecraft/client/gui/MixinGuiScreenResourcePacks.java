package co.crystaldev.client.mixin.net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiScreenResourcePacks.class})
public abstract class MixinGuiScreenResourcePacks {
    @Inject(method = {"actionPerformed"}, at = {@At(value = "INVOKE", target = "Ljava/util/Collections;reverse(Ljava/util/List;)V", remap = false)})
    private void clearHandles(CallbackInfo ci) {
        ResourcePackRepository repository = Minecraft.getMinecraft().getResourcePackRepository();
        for (ResourcePackRepository.Entry entry : repository.getRepositoryEntries()) {
            IResourcePack current = repository.getResourcePackInstance();
            if (current == null || !entry.getResourcePackName().equals(current.getPackName()))
                entry.closeResourcePack();
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\gui\MixinGuiScreenResourcePacks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */