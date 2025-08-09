package com.github.timmekeclient.feature.impl.all;

import com.github.timmekeclient.event.EventBus;
import com.github.timmekeclient.event.IRegistrable;
import com.github.timmekeclient.event.impl.render.RenderBlockHighlightEvent;
import com.github.timmekeclient.feature.annotations.properties.Colour;
import com.github.timmekeclient.feature.annotations.properties.DropdownMenu;
import com.github.timmekeclient.feature.annotations.properties.ModuleInfo;
import com.github.timmekeclient.feature.annotations.properties.Slider;
import com.github.timmekeclient.feature.base.Category;
import com.github.timmekeclient.feature.base.Dropdown;
import com.github.timmekeclient.feature.base.Module;
import com.github.timmekeclient.shader.ShaderManager;
import com.github.timmekeclient.shader.chroma.ChromaScreenShader;
import com.github.timmekeclient.util.ColorObject;
import com.github.timmekeclient.util.RenderUtils;
import net.minecraft.block.*;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.opengl.GL11;

@ModuleInfo(name = "Block Overlay", description = "Customize the default Minecraft block highlighting", category = Category.ALL)
public class BlockOverlay extends Module implements IRegistrable {
    @DropdownMenu(label = "Overlay Mode", values = {"Outline", "Fill"}, defaultValues = {"Outline"}, limitlessSelections = true)
    public Dropdown<String> mode;

    @Slider(label = "Outline Width", placeholder = "{value}px", minimum = 1.0D, maximum = 10.0D, standard = 3.0D, integers = true)
    public int lineWidth = 3;

    @Colour(label = "Color")
    public ColorObject color = new ColorObject(255, 85, 85, 100);

    public BlockOverlay() {
        this.enabled = false;
    }

    public void registerEvents() {
        EventBus.register(this, RenderBlockHighlightEvent.class, ev -> {
            if ((ev.getTarget()).typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                if (this.mode.isEmpty())
                    return;
                ev.setCancelled(true);
                GL11.glPushMatrix();
                GL11.glDisable(3553);
                GL11.glEnable(2848);
                GL11.glBlendFunc(770, 771);
                GL11.glDepthMask(true);
                GL11.glLineWidth(this.lineWidth);
                BlockPos pos = ev.getTarget().getBlockPos();
                Block b = mc.theWorld.getBlockState(pos).getBlock();
                b.setBlockBoundsBasedOnState(mc.theWorld, pos);
                AxisAlignedBB bb = RenderUtils.normalize(b.getSelectedBoundingBox(this.mc.theWorld, pos).expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D));
                if (this.color.isChroma())
                    ShaderManager.getInstance().enableShader(ChromaScreenShader.class);
                if (this.mode.isSelected("Outline")) {
                    RenderUtils.setGlColor(this.color, 255);
                    RenderGlobal.drawSelectionBoundingBox(bb);
                }
                if (this.mode.isSelected("Fill")) {
                    RenderUtils.setGlColor(this.color);
                    RenderUtils.drawFilledBoundingBox(bb);
                }
                ShaderManager.getInstance().disableShader();
                GL11.glEnable(3553);
                GL11.glDisable(2848);
                GL11.glLineWidth(1.0F);
                GL11.glDepthMask(true);
                GL11.glColor4d(255.0D, 255.0D, 255.0D, 255.0D);
                GL11.glPopMatrix();
            }
        });
    }
}