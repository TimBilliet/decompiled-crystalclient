package co.crystaldev.client.mixin.net.minecraft.client.renderer;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.IntBuffer;

@Mixin({WorldRenderer.class})
public abstract class MixinWorldRenderer {
    @Shadow
    private IntBuffer rawIntBuffer;

    @Shadow
    private VertexFormat vertexFormat;

    @Inject(method = {"finishDrawing"}, at = {@At(value = "INVOKE", target = "Ljava/nio/ByteBuffer;limit(I)Ljava/nio/Buffer;", remap = false)})
    private void resetBuffer(CallbackInfo ci) {
        this.rawIntBuffer.position(0);
    }

    @Inject(method = {"endVertex"}, at = {@At("HEAD")})
    private void adjustBuffer(CallbackInfo ci) {
        this.rawIntBuffer.position(this.rawIntBuffer.position() + this.vertexFormat.getIntegerSize());
    }
}
