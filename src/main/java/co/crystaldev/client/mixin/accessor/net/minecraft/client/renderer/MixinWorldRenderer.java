package co.crystaldev.client.mixin.accessor.net.minecraft.client.renderer;

import net.minecraft.client.renderer.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({WorldRenderer.class})
public interface MixinWorldRenderer {
    @Accessor("isDrawing")
    boolean isDrawing();
}
