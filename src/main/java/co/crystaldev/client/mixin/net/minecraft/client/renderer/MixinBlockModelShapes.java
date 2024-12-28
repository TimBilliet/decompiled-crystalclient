package co.crystaldev.client.mixin.net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin({BlockModelShapes.class})
public abstract class MixinBlockModelShapes {
  @Shadow
  public abstract void registerBlockWithStateMapper(Block paramBlock, IStateMapper paramIStateMapper);
  
  @Inject(method = {"registerAllBlocks"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BlockModelShapes;registerBuiltInBlocks([Lnet/minecraft/block/Block;)V", shift = At.Shift.AFTER)})
  private void registerAllBlocks(CallbackInfo ci) {
    registerBlockWithStateMapper((Block)Blocks.chest, (IStateMapper)new StateMapperBase() {
          protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
            Map<IProperty, Comparable> map = Maps.newLinkedHashMap((Map)state.getProperties());
            return new ModelResourceLocation((ResourceLocation)Block.blockRegistry.getNameForObject(state.getBlock()), getPropertyString(map));
          }
        });
    registerBlockWithStateMapper(Blocks.trapped_chest, (IStateMapper)new StateMapperBase() {
          protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
            Map<IProperty, Comparable> map = Maps.newLinkedHashMap((Map)state.getProperties());
            return new ModelResourceLocation((ResourceLocation)Block.blockRegistry.getNameForObject(state.getBlock()), getPropertyString(map));
          }
        });
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\client\renderer\MixinBlockModelShapes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */