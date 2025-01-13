package co.crystaldev.client.mixin.net.minecraft.client.renderer.entity;

import co.crystaldev.client.feature.impl.mechanic.PerspectiveMod;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityTNTPrimed;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin({RenderManager.class})
public abstract class MixinRenderManager {
    @Shadow
    public Map<Class<? extends Entity>, Render<? extends Entity>> entityRenderMap;

    @Inject(method = {"<init>"}, at = {@At("RETURN")})
    public void onConstruct(TextureManager renderEngineIn, RenderItem itemRendererIn, CallbackInfo ci) {
        this.entityRenderMap.put(EntityTNTPrimed.class, new RenderTNTPrimed((RenderManager) (Object) this));
        this.entityRenderMap.put(EntityFallingBlock.class, new RenderFallingBlock((RenderManager) (Object) this));
    }

    @Inject(method = {"doRenderEntity"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/Render;doRender(Lnet/minecraft/entity/Entity;DDDFF)V", shift = At.Shift.BEFORE)})
    public void doRender(CallbackInfoReturnable<Boolean> ci) {
        GlStateManager.enableDepth();
    }

    @Redirect(method = {"cacheActiveRenderInfo"}, at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/RenderManager;playerViewX:F", opcode = 181))
    private void cacheActiveRenderInfo$playerViewX(RenderManager renderManager, float value) {
        renderManager.playerViewX = (PerspectiveMod.getInstance()).perspectiveToggled ? (PerspectiveMod.getInstance()).cameraPitch : value;
    }

    @Redirect(method = {"cacheActiveRenderInfo"}, at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/RenderManager;playerViewY:F", opcode = 181))
    private void cacheActiveRenderInfo$playerViewY(RenderManager renderManager, float value) {
        renderManager.playerViewY = (PerspectiveMod.getInstance()).perspectiveToggled ? (PerspectiveMod.getInstance()).cameraYaw : value;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\renderer\entity\MixinRenderManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */