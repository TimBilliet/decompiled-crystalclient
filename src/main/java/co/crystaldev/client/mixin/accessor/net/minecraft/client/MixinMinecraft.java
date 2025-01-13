package co.crystaldev.client.mixin.accessor.net.minecraft.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.util.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({Minecraft.class})
public interface MixinMinecraft {
    @Accessor("mcDefaultResourcePack")
    DefaultResourcePack getMcDefaultResourcePack();

    @Accessor("session")
    void setSession(Session paramSession);

    @Accessor("mcThread")
    Thread getMcThread();

    @Accessor("fullscreen")
    void setFullScreen(boolean paramBoolean);

    @Accessor("currentServerData")
    void setCurrentServerData(ServerData paramServerData);

    @Accessor("tempDisplayWidth")
    int getTempDisplayWidth();

    @Accessor("tempDisplayHeight")
    int getTempDisplayHeight();

    @Accessor("modelManager")
    ModelManager getModelManager();

    @Invoker("updateFramebufferSize")
    void callUpdateFramebufferSize();

    @Invoker("resize")
    void callResize(int paramInt1, int paramInt2);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\accessor\net\minecraft\client\MixinMinecraft.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */