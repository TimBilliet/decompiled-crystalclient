package co.crystaldev.client.feature.impl.all;

import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.render.RenderBlockHighlightEvent;
import co.crystaldev.client.feature.annotations.properties.Colour;
import co.crystaldev.client.feature.annotations.properties.DropdownMenu;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.Slider;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Dropdown;
import co.crystaldev.client.feature.base.Module;
import co.crystaldev.client.shader.chroma.ChromaScreenShader;
import co.crystaldev.client.shader.ShaderManager;
import co.crystaldev.client.util.ColorObject;
import co.crystaldev.client.util.RenderUtils;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.opengl.GL11;

import java.awt.*;

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
                IBlockState state = this.mc.theWorld.getBlockState(pos);
                Block b = state.getBlock();
                Block superBlock = b;
                if(b.getCollisionBoundingBox(this.mc.theWorld, pos, state) == null) {
                    superBlock = new Block(b.getMaterial());
                    superBlock.setBlockBounds((float) b.getBlockBoundsMinX(), (float) b.getBlockBoundsMinY(), (float) b.getBlockBoundsMinZ(), (float) b.getBlockBoundsMaxX(), (float) b.getBlockBoundsMaxY(), (float) b.getBlockBoundsMaxZ());
                } else {
                    if(b instanceof BlockStairs) {
                         superBlock.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                    } else {
                        superBlock.setBlockBoundsBasedOnState(mc.theWorld, pos);
                    }
                }
                AxisAlignedBB bb = RenderUtils.normalize(superBlock.getCollisionBoundingBox(this.mc.theWorld, pos, state).expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D));
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