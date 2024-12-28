package co.crystaldev.client.shader.chroma;

import co.crystaldev.client.Client;
import co.crystaldev.client.feature.settings.ClientOptions;
import co.crystaldev.client.handler.ModuleHandler;
import co.crystaldev.client.shader.Shader;
import co.crystaldev.client.shader.UniformType;
import net.minecraft.client.Minecraft;

public abstract class ChromaShader extends Shader {
  public ChromaShader(String shaderName) throws Exception {
    super(shaderName, shaderName);
  }
  
  protected void registerUniforms() {
    registerUniform(UniformType.FLOAT, "chromaSize", () -> (ClientOptions.getInstance()).chromaSize * (Minecraft.getMinecraft()).displayWidth / 100.0F);
    registerUniform(UniformType.FLOAT, "timeOffset", () -> {
          float ticks = (float)ModuleHandler.getTotalTicks() + (Client.getTimer()).renderPartialTicks;
          float chromaSpeed = (ClientOptions.getInstance()).chromaSpeed / 360.0F;
          return ticks * chromaSpeed;
        });
    registerUniform(UniformType.FLOAT, "saturation", () -> (float) (ClientOptions.getInstance()).chromaSaturation);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\shader\chroma\ChromaShader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */