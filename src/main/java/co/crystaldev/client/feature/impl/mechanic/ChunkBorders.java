package co.crystaldev.client.feature.impl.mechanic;

import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.render.RenderWorldEvent;
import co.crystaldev.client.feature.annotations.properties.*;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Dropdown;
import co.crystaldev.client.feature.base.Module;
import co.crystaldev.client.gui.GuiOptions;
import co.crystaldev.client.shader.ShaderManager;
import co.crystaldev.client.shader.chroma.ChromaScreenShader;
import co.crystaldev.client.util.ColorObject;
import co.crystaldev.client.util.RenderUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@ModuleInfo(name = "Chunk Borders", description = "Shows the boundaries between chunks", category = Category.MECHANIC)
public class ChunkBorders extends Module implements IRegistrable {
    @DropdownMenu(label = "Mode", values = {"Vertical", "Horizontal", "Both"}, defaultValues = {"Horizontal"})
    public Dropdown<String> mode;

    @Slider(label = "Radius", placeholder = "{value} chunks", minimum = 2.0D, maximum = 10.0D, standard = 3.0D, integers = true)
    public int distance = 2;

    @Slider(label = "Y Offset", placeholder = "{value} blocks", minimum = -10.0D, maximum = 10.0D, standard = 0.0D, integers = true)
    public int offset = 0;

    @Slider(label = "Y-Level", placeholder = "Y:{value}", minimum = 1.0D, maximum = 256.0D, standard = 1.0D, integers = true)
    public int lockY = 1;

    @Toggle(label = "Y-Level Lock")
    public boolean lockYLevel = false;

    @Keybind(label = "Toggle Keybinding")
    public KeyBinding keybind = new KeyBinding("crystalclient.key.toggle_chunk_borders", 0, "Crystal Client");

    @PageBreak(label = "Line Customization")
    @Slider(label = "Line Width", placeholder = "{value}px", minimum = 1.0D, maximum = 10.0D, standard = 2.0D, integers = true)
    public int lineWidth = 2;

    @Colour(label = "Color")
    public ColorObject color = GuiOptions.getInstance().getColorObject((GuiOptions.getInstance()).mainColor, 255);

    private static ChunkBorders INSTANCE;

    private final Tessellator tessellator = Tessellator.getInstance();

    private final WorldRenderer worldRenderer = this.tessellator.getWorldRenderer();

    public ChunkBorders() {
        INSTANCE = this;
        this.enabled = false;
        this.toggleKeyBinding = this.keybind;
    }

    public void configPostInit() {
        super.configPostInit();
        setOptionVisibility("Y Offset", f -> !((String) this.mode.getCurrentSelection()).equalsIgnoreCase("vertical"));
        setOptionVisibility("Y-Level", f -> !((String) this.mode.getCurrentSelection()).equalsIgnoreCase("vertical"));
        setOptionVisibility("Y-Level Lock", f -> !((String) this.mode.getCurrentSelection()).equalsIgnoreCase("vertical"));
    }

    private void drawVertical(double x, double z) {
        this.worldRenderer.begin(1, DefaultVertexFormats.POSITION);
        this.worldRenderer.pos(x, 0.0D, z).endVertex();
        this.worldRenderer.pos(x, 300.0D, z).endVertex();
        this.worldRenderer.pos(x + 16.0D, 0.0D, z).endVertex();
        this.worldRenderer.pos(x + 16.0D, 286.0D, z).endVertex();
        this.worldRenderer.pos(x, 0.0D, z + 16.0D).endVertex();
        this.worldRenderer.pos(x, 300.0D, z + 16.0D).endVertex();
        this.worldRenderer.pos(x + 16.0D, 0.0D, z + 16.0D).endVertex();
        this.worldRenderer.pos(x + 16.0D, 286.0D, z + 16.0D).endVertex();
        this.tessellator.draw();
    }

    private void drawHorizontal(double x, double y, double z) {
        this.worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        this.worldRenderer.pos(x, y, z).endVertex();
        this.worldRenderer.pos(x + 16.0D, y, z).endVertex();
        this.worldRenderer.pos(x + 16.0D, y, z + 16.0D).endVertex();
        this.worldRenderer.pos(x, y, z + 16.0D).endVertex();
        this.tessellator.draw();
        this.worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        this.worldRenderer.pos(x, y, z + 16.0D).endVertex();
        this.worldRenderer.pos(x, y, z).endVertex();
        this.tessellator.draw();
    }

    public static ChunkBorders getInstance() {
        return INSTANCE;
    }

    public void registerEvents() {
        EventBus.register(this, RenderWorldEvent.Post.class, ev -> {
            EntityPlayerSP entityPlayerSP = this.mc.thePlayer;
            float partialTicks = ev.partialTicks;
            double inChunkPosX = ((Entity) entityPlayerSP).lastTickPosX + (((Entity) entityPlayerSP).posX - ((Entity) entityPlayerSP).lastTickPosX) * partialTicks;
            double inChunkPosY = ((Entity) entityPlayerSP).lastTickPosY + (((Entity) entityPlayerSP).posY - ((Entity) entityPlayerSP).lastTickPosY) * partialTicks;
            double inChunkPosZ = ((Entity) entityPlayerSP).lastTickPosZ + (((Entity) entityPlayerSP).posZ - ((Entity) entityPlayerSP).lastTickPosZ) * partialTicks;
            GL11.glPushMatrix();
            GL11.glDisable(3553);
            GL11.glEnable(3042);
            GL11.glEnable(2848);
            GL11.glBlendFunc(770, 771);
            GL11.glLineWidth(this.lineWidth);
            GL11.glTranslated(0.0D, -inChunkPosY, 0.0D);
            if (this.color.isChroma())
                ShaderManager.getInstance().enableShader(ChromaScreenShader.class);
            RenderUtils.setGlColor((Color) this.color);
            double offsetX = (((Entity) entityPlayerSP).chunkCoordX << 4) - inChunkPosX;
            double offsetZ = (((Entity) entityPlayerSP).chunkCoordZ << 4) - inChunkPosZ;
            double y = inChunkPosY + this.offset;
            String mode = (String) this.mode.getCurrentSelection();
            for (int chunkZ = -this.distance; chunkZ <= this.distance; chunkZ++) {
                for (int chunkX = -this.distance; chunkX <= this.distance; chunkX++) {
                    double x = offsetX - (16 * chunkX);
                    double z = offsetZ - (16 * chunkZ);
                    switch (mode) {
                        case "Vertical":
                            drawVertical(x, z);
                            break;
                        case "Horizontal":
                            drawHorizontal(x, this.lockYLevel ? this.lockY : y, z);
                            break;
                        case "Both":
                            drawVertical(x, z);
                            drawHorizontal(x, this.lockYLevel ? this.lockY : y, z);
                            break;
                    }
                }
            }
            ShaderManager.getInstance().disableShader();
            GL11.glEnable(3553);
            GL11.glDisable(3042);
            GL11.glDisable(2848);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPopMatrix();
        });
    }
}