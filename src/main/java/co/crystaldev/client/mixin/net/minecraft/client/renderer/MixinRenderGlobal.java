package co.crystaldev.client.mixin.net.minecraft.client.renderer;

import co.crystaldev.client.duck.RenderGlobalExt;
import net.minecraft.client.renderer.RenderGlobal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({RenderGlobal.class})
public abstract class MixinRenderGlobal implements RenderGlobalExt {
    @Shadow
    private int countEntitiesRendered;

    @Shadow
    private int countEntitiesTotal;

    public String getHudEntityCount() {
        return String.format("%s/%s", this.countEntitiesRendered, this.countEntitiesTotal);
    }
}
