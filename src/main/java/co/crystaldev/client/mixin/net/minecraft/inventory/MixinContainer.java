package co.crystaldev.client.mixin.net.minecraft.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Container.class})
public abstract class MixinContainer {
    @Inject(method = {"putStackInSlot"}, at = {@At("HEAD")})
    private void playArmorBreakingSound(int slotId, ItemStack stack, CallbackInfo ci) {
        if (!(Minecraft.getMinecraft()).theWorld.isRemote || stack != null)
            return;
        Container container = (Container) (Object) this;
        if (slotId >= 5 && slotId <= 8 && container instanceof net.minecraft.inventory.ContainerPlayer) {
            Slot slot = container.getSlot(slotId);
            if (slot != null) {
                ItemStack slotStack = slot.getStack();
                if (slotStack != null && slotStack.getItem() instanceof net.minecraft.item.ItemArmor && slotStack.getItemDamage() > slotStack.getMaxDamage() - 2)
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.break")));
            }
        }
    }
}