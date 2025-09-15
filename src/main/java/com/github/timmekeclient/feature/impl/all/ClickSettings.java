package com.github.timmekeclient.feature.impl.all;

import com.github.timmekeclient.event.EventBus;
import com.github.timmekeclient.event.IRegistrable;
import com.github.timmekeclient.event.impl.tick.ClientTickEvent;
import com.github.timmekeclient.feature.annotations.HoverOverlay;
import com.github.timmekeclient.feature.annotations.properties.ModuleInfo;
import com.github.timmekeclient.feature.annotations.properties.Toggle;
import com.github.timmekeclient.feature.base.Category;
import com.github.timmekeclient.feature.base.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;

@ModuleInfo(name = "Click Settings", description = "A collection of features relating to clicking", category = Category.ALL)
public class ClickSettings extends Module implements IRegistrable {

    @HoverOverlay("Or use /click right to start/stop holding right click")
    @Toggle(label = "Hold Right Click")
    public boolean holdRightClick = false;

    @HoverOverlay("Or use /click left to start/stop holding left click")
    @Toggle(label = "Hold Left Click")
    public boolean holdLeftClick = false;

    private static ClickSettings INSTANCE;

    public ClickSettings() {
        INSTANCE = this;
    }

    public static ClickSettings getInstance() {
        return INSTANCE;
    }

    @Override
    public void registerEvents() {
        EventBus.register(this, ClientTickEvent.Post.class, ev -> {
            if (mc.thePlayer == null || mc.playerController == null || mc.theWorld == null || mc.thePlayer.getHeldItem() == null)
                return;
            if (holdLeftClick)
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), true);

            if (holdRightClick) {
                MovingObjectPosition mop = mc.thePlayer.rayTrace(5.0D,1);
                if(mop == null || mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
                    return;
                BlockPos pos = mop.getBlockPos();
                mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), pos, mop.sideHit, mop.hitVec);
            }
        });
    }
}
