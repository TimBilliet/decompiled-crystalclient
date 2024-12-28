package co.crystaldev.client.mixin.net.minecraft.block;

import co.crystaldev.client.feature.settings.ClientOptions;
import co.crystaldev.client.util.ColorObject;
import co.crystaldev.client.util.RenderUtils;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin({BlockRedstoneWire.class})
public abstract class MixinBlockRedstoneWire {
  @Inject(method = {"colorMultiplier(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/BlockPos;I)I"}, cancellable = true, at = {@At("HEAD")})
  private void applyRedstoneColor(IBlockAccess worldIn, BlockPos pos, int renderPass, CallbackInfoReturnable<Integer> cir) {
    if ((ClientOptions.getInstance()).useRedstoneColor) {
      float[] hsv = new float[3];
      ColorObject colorObj = (ClientOptions.getInstance()).redstoneColor;
      Color color = colorObj.isChroma() ? RenderUtils.getCurrentChromaColor() : (Color)colorObj;
      Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
      IBlockState block = worldIn.getBlockState(pos);
      int power = ((Integer)block.getValue((IProperty)BlockRedstoneWire.POWER)).intValue();
      float decay = Math.max(0.3F, power / 15.0F);
      cir.setReturnValue(Integer.valueOf(Color.getHSBColor(hsv[0], hsv[1], decay).getRGB()));
    } 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\block\MixinBlockRedstoneWire.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */