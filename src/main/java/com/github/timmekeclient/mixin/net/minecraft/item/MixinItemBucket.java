package com.github.timmekeclient.mixin.net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ItemBucket.class})
public abstract class MixinItemBucket extends Item {
    @Inject(method = {"<init>"}, at = {@At("RETURN")})
    public void constructorTail(Block containedBlock, CallbackInfo ci) {
        this.maxStackSize = 64;
    }

    @Inject(method = {"onItemRightClick"}, cancellable = true, at = {@At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;triggerAchievement(Lnet/minecraft/stats/StatBase;)V", ordinal = 2, shift = At.Shift.AFTER)})
    public void onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, CallbackInfoReturnable<ItemStack> ci) {
        if (itemStackIn.stackSize > 1) {
            if (!playerIn.inventory.addItemStackToInventory(new ItemStack(Items.bucket)))
                playerIn.dropPlayerItemWithRandomChoice(new ItemStack(Items.bucket), false);
            ci.setReturnValue(new ItemStack(itemStackIn.getItem(), itemStackIn.stackSize - 1));
        }
    }
}
