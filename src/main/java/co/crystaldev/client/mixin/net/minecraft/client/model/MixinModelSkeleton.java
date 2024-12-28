package co.crystaldev.client.mixin.net.minecraft.client.model;

import net.minecraft.client.model.ModelSkeleton;
import net.minecraft.client.model.ModelZombie;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ModelSkeleton.class})
public abstract class MixinModelSkeleton extends ModelZombie {

  public void func_178718_a(float scale) {
    this.bipedRightArm.rotationPointX++;
    this.bipedRightArm.postRender(scale);
    this.bipedRightArm.rotationPointX--;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\model\MixinModelSkeleton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */