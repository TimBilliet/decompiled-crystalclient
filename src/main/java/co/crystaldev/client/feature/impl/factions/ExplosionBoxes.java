package co.crystaldev.client.feature.impl.factions;

import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.render.RenderWorldEvent;
import co.crystaldev.client.event.impl.world.ExplosionEvent;
import co.crystaldev.client.feature.annotations.properties.Colour;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.Slider;
import co.crystaldev.client.feature.annotations.properties.Toggle;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Module;
import co.crystaldev.client.shader.ShaderManager;
import co.crystaldev.client.shader.chroma.ChromaScreenShader;
import co.crystaldev.client.util.ColorObject;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.ExplosionBox;
import co.crystaldev.client.util.objects.Vec3d;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Iterator;

@ModuleInfo(name = "Explosion Boxes", description = "Displays an indicator of every explosion", category = Category.FACTIONS)
public class ExplosionBoxes extends Module implements IRegistrable {
    @Slider(label = "Timeout", placeholder = "{value}s", minimum = 1.0D, maximum = 30.0D, standard = 10.0D, integers = true)
    public int timeout = 10;

    @Slider(label = "Line Width", placeholder = "{value}px", minimum = 1.0D, maximum = 10.0D, standard = 3.0D, integers = true)
    public int lineWidth = 3;

    @Colour(label = "Color")
    public ColorObject color = new ColorObject(255, 85, 85, 125);

    @Toggle(label = "Render Sides")
    public boolean renderSides = true;

    @Toggle(label = "Use Center of Block")
    public boolean useCenter = false;

    private static ExplosionBoxes INSTANCE;

    private final ArrayDeque<ExplosionBox> boxes = new ArrayDeque<>();

    public ExplosionBoxes() {
        INSTANCE = this;
        this.enabled = false;
    }

    private void onRenderWorld(RenderWorldEvent.Post event) {
        if (!this.boxes.isEmpty()) {
            Iterator<ExplosionBox> iterator = this.boxes.iterator();
            while (iterator.hasNext()) {
                ExplosionBox box = iterator.next();
                if (box.expired()) {
                    iterator.remove();
                    continue;
                }
                GL11.glPushMatrix();
                GL11.glEnable(3042);
                GL11.glDisable(3553);
                GL11.glDisable(2929);
                GL11.glEnable(2848);
                GL11.glDepthMask(false);
                GL11.glBlendFunc(770, 771);
                GL11.glLineWidth(this.lineWidth);
                if (this.color.isChroma())
                    ShaderManager.getInstance().enableShader(ChromaScreenShader.class);
                RenderUtils.setGlColor((Color) this.color);
                AxisAlignedBB small = new AxisAlignedBB(box.pos.x + 0.5D - 0.1D, box.pos.y + 0.5D - 0.1D, box.pos.z + 0.5D - 0.1D, box.pos.x + 0.5D + 0.1D, box.pos.y + 0.5D + 0.1D, box.pos.z + 0.5D + 0.1D);
                AxisAlignedBB tntBox = RenderUtils.normalize(small);
                RenderGlobal.drawSelectionBoundingBox(tntBox);
                if (this.renderSides)
                    RenderUtils.drawFilledBoundingBox(tntBox);
                ShaderManager.getInstance().disableShader();
                GL11.glDisable(3042);
                GL11.glEnable(3553);
                GL11.glEnable(2929);
                GL11.glDisable(2848);
                GL11.glDepthMask(true);
                GL11.glColor4d(255.0D, 255.0D, 255.0D, 255.0D);
                GL11.glLineWidth(1.0F);
                GL11.glPopMatrix();
            }
        }
    }

    private void onExplosion(ExplosionEvent event) {
        if (event.size > 3.0F) {
            Vec3d pos = this.useCenter ? new Vec3d(MathHelper.floor_double(event.posX), MathHelper.floor_double(event.posY), MathHelper.floor_double(event.posZ)) : new Vec3d(event.posX - 0.5D, event.posY - 0.5D, event.posZ - 0.5D);
            for (ExplosionBox box : this.boxes) {
                if (box.pos.equals(pos))
                    return;
            }
            this.boxes.add(new ExplosionBox(pos));
        }
    }

    public static ExplosionBoxes getInstance() {
        return INSTANCE;
    }

    public void registerEvents() {
        EventBus.register(this, RenderWorldEvent.Post.class, this::onRenderWorld);
        EventBus.register(this, ExplosionEvent.class, this::onExplosion);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\factions\ExplosionBoxes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */