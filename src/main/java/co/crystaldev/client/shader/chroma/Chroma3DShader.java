package co.crystaldev.client.shader.chroma;

import co.crystaldev.client.feature.settings.ClientOptions;
import co.crystaldev.client.shader.UniformType;
import co.crystaldev.client.util.LocationUtils;
import net.minecraft.util.Vec3;

public class Chroma3DShader extends ChromaShader {
    private float alpha = 1.0F;

    public Chroma3DShader() throws Exception {
        super("chroma_3d");
    }

    protected void registerUniforms() {
        super.registerUniforms();
        registerUniform(UniformType.VEC3, "playerWorldPosition", () -> {
            Vec3 viewPosition = LocationUtils.getViewPosition();
            return new Float[]{(float) viewPosition.xCoord, (float) viewPosition.yCoord, (float) viewPosition.zCoord};
        });
        registerUniform(UniformType.FLOAT, "alpha", () -> this.alpha);
        registerUniform(UniformType.FLOAT, "brightness", () -> (float) (ClientOptions.getInstance()).chromaBrightness);
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\shader\chroma\Chroma3DShader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */