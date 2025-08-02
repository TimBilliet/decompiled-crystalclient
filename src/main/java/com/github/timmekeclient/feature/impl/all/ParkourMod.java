package com.github.timmekeclient.feature.impl.all;

import com.github.timmekeclient.event.EventBus;
import com.github.timmekeclient.event.IRegistrable;
import com.github.timmekeclient.event.impl.tick.ClientTickEvent;
import com.github.timmekeclient.feature.annotations.properties.Keybind;
import com.github.timmekeclient.feature.annotations.properties.ModuleInfo;
import com.github.timmekeclient.feature.base.Category;
import com.github.timmekeclient.feature.base.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.AxisAlignedBB;

import java.util.stream.Stream;

@ModuleInfo(name = "Parkour Mod", description = "Jumps at the end of a block", category = Category.ALL)

public class ParkourMod extends Module implements IRegistrable {
    @Keybind(label = "Toggle Keybinding")
    public KeyBinding keybind = new KeyBinding("timmekeclient.key.parkourmod", 0, "Timmeke_ Client");

    public ParkourMod() {
        enabled = false;
        toggleKeyBinding = keybind;
    }
    @Override
    public void registerEvents() {
        EventBus.register(this, ClientTickEvent.Post.class, ev -> {
            if (mc.thePlayer == null || !mc.thePlayer.onGround || mc.gameSettings.keyBindJump.isPressed()) {
                return;
            }
            if (mc.thePlayer.isSneaking() || mc.gameSettings.keyBindSneak.isPressed()) {
                return;
            }
            AxisAlignedBB playerBB = mc.thePlayer.getEntityBoundingBox();
            AxisAlignedBB adjustedBB = playerBB.offset(0, -0.5, 0).expand(-0.001,0,-0.001);
            Stream<AxisAlignedBB> collisions = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, adjustedBB).stream();
            if(collisions.findAny().isPresent())
                return;
            mc.thePlayer.jump();
        });
    }
}
