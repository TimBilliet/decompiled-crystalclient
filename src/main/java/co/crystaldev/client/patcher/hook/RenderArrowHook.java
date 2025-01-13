package co.crystaldev.client.patcher.hook;

import co.crystaldev.client.feature.impl.mechanic.NoLag;
import co.crystaldev.client.mixin.accessor.net.minecraft.entity.projectile.MixinEntityArrow;
import net.minecraft.entity.projectile.EntityArrow;

public class RenderArrowHook {
    public static boolean cancelRendering(EntityArrow entity) {
        boolean grounded = ((MixinEntityArrow) entity).getInGround();
        return (NoLag.isEnabled((NoLag.getInstance()).disableGroundArrows) && grounded);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\patcher\hook\RenderArrowHook.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */