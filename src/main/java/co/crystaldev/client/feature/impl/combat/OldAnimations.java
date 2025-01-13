package co.crystaldev.client.feature.impl.combat;

import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.render.RenderTickEvent;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.PageBreak;
import co.crystaldev.client.feature.annotations.properties.Toggle;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Module;
import co.crystaldev.client.mixin.accessor.net.minecraft.client.entity.MixinEntityLivingBase;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.WorldServer;

@ModuleInfo(name = "1.7 Visuals", description = "Revert certain visuals and animations to how they were in 1.7", category = Category.COMBAT)
public class OldAnimations extends Module implements IRegistrable {
    @Toggle(label = "Punch During Usage")
    public boolean punchDuringUsage = true;

    @PageBreak(label = "Animations")
    @Toggle(label = "Block Hitting")
    public boolean revertBlocking = true;

    @Toggle(label = "Rod Position")
    public boolean revertFishingRod = true;

    @Toggle(label = "Sneaking")
    public boolean revertSneaking = true;

    @Toggle(label = "Health Flash")
    public boolean revertHealthFlash = true;

    @Toggle(label = "Camera Shake")
    public boolean revertCameraShake = false;

    @PageBreak(label = "Visuals")
    @Toggle(label = "Red Armor On Hit")
    public boolean redArmorOnHit = true;

    private static OldAnimations INSTANCE;

    public OldAnimations() {
        INSTANCE = this;
    }

    private void swingNoPacket() {
        EntityPlayerSP self = this.mc.thePlayer;

        if (!self.isSwingInProgress || self.swingProgressInt >= ((MixinEntityLivingBase) self).callGetArmSwingAnimationEnd() / 2 || self.swingProgressInt < 0) {
            self.swingProgressInt = -1;
            self.isSwingInProgress = true;
            if (self.worldObj instanceof WorldServer)
                ((WorldServer) self.worldObj).getEntityTracker().sendToAllTrackingEntity((Entity) self, (Packet) new S0BPacketAnimation((Entity) self, 0));
        }
    }

    public static OldAnimations getInstance() {
        return INSTANCE;
    }

    //veranderd
    public void registerEvents() {
        EventBus.register(this, RenderTickEvent.Pre.class, ev -> {
            if (this.punchDuringUsage) {
                boolean flag = (this.mc.gameSettings.keyBindAttack.isKeyDown() && this.mc.gameSettings.keyBindUseItem.isKeyDown());
                if (flag && this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && this.mc.thePlayer != null && this.mc.thePlayer.getItemInUseCount() > 0)
                    swingNoPacket();
            }
        });
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\combat\OldAnimations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */