package co.crystaldev.client.mixin.optifine.net.minecraft.src;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Pseudo
@Mixin(targets = {"net.minecraft.src.Config"})
public abstract class MixinConfig {
    @Inject(method = {"drawFps"}, cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT, remap = false, at = {@At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/renderer/RenderGlobal;getCountTileEntitiesRendered()I")})
    @Dynamic("OptiFine")
    private static void simplifyFpsCounter(CallbackInfo ci, int fps, String updates, int renderersActive, int entities, int tileEntities) {
        String fpsStr = fps + " fps, C: " + renderersActive + ", E: " + entities + "+" + tileEntities + ", U: " + updates;
        (Minecraft.getMinecraft()).fontRendererObj.drawString(fpsStr, 2, 2, -2039584);
        ci.cancel();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\optifine\net\minecraft\src\MixinConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */