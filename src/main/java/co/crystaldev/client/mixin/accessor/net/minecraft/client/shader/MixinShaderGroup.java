package co.crystaldev.client.mixin.accessor.net.minecraft.client.shader;

import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin({ShaderGroup.class})
public interface MixinShaderGroup {
  @Accessor("listShaders")
  List<Shader> getListShaders();
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\accessor\net\minecraft\client\shader\MixinShaderGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */