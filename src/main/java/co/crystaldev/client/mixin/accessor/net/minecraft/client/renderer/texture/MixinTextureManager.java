package co.crystaldev.client.mixin.accessor.net.minecraft.client.renderer.texture;

import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin({TextureManager.class})
public interface MixinTextureManager {
  @Accessor("listTickables")
  List<ITickable> getListTickables();
  
  @Accessor("mapTextureObjects")
  Map<ResourceLocation, ITextureObject> getMapTextureObjects();
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\accessor\net\minecraft\client\renderer\texture\MixinTextureManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */