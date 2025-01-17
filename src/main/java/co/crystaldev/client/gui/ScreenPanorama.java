package co.crystaldev.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

public abstract class ScreenPanorama extends Screen {
    public static long panoramaTimer;

    private ResourceLocation backgroundTexture;

    private final ResourceLocation[] panoramaImages;

    private final int imageResolution;

    public ScreenPanorama(GuiScreen parent, ResourceLocation[] panoramaImages, int imageResolution) {
        super(parent);
        this.panoramaImages = panoramaImages;
        this.imageResolution = imageResolution;
    }

    public ScreenPanorama(ResourceLocation[] panoramaImages, int imageResolution) {
        super(null);
        this.panoramaImages = panoramaImages;
        this.imageResolution = imageResolution;
    }

    public void updateScreen() {
        panoramaTimer++;
    }

    public void initGui() {
        init();
    }

    public void init() {
        super.init();
        DynamicTexture viewportTexture = new DynamicTexture(this.imageResolution, this.imageResolution);
        this.backgroundTexture = this.mc.getTextureManager().getDynamicTextureLocation("background", viewportTexture);
    }

    public void onGuiClosed() {
        this.constructed = false;
        super.onGuiClosed();
    }

    private void drawPanorama(float partialTicks) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.matrixMode(5889);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
        GlStateManager.matrixMode(5888);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        int i = 8;
        for (int j = 0; j < i * i; j++) {
            GlStateManager.pushMatrix();
            float f = ((float) (j % i) / i - 0.5F) / 64.0F;
            float f1 = ((float) (j / i) / i - 0.5F) / 64.0F;
            float f2 = 0.0F;
            GlStateManager.translate(f, f1, f2);
            GlStateManager.rotate(MathHelper.sin(((float) panoramaTimer + partialTicks) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(-((float) panoramaTimer + partialTicks) * 0.1F, 0.0F, 1.0F, 0.0F);
            for (int k = 0; k < 6; k++) {
                GlStateManager.pushMatrix();
                if (k == 1)
                    GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                if (k == 2)
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                if (k == 3)
                    GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                if (k == 4)
                    GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                if (k == 5)
                    GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                this.mc.getTextureManager().bindTexture(this.panoramaImages[k]);
                worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                int l = 255 / (j + 1);
                worldrenderer.pos(-1.0D, -1.0D, 1.0D).tex(0.0D, 0.0D).color(255, 255, 255, l).endVertex();
                worldrenderer.pos(1.0D, -1.0D, 1.0D).tex(1.0D, 0.0D).color(255, 255, 255, l).endVertex();
                worldrenderer.pos(1.0D, 1.0D, 1.0D).tex(1.0D, 1.0D).color(255, 255, 255, l).endVertex();
                worldrenderer.pos(-1.0D, 1.0D, 1.0D).tex(0.0D, 1.0D).color(255, 255, 255, l).endVertex();
                tessellator.draw();
                GlStateManager.popMatrix();
            }
            GlStateManager.popMatrix();
            GlStateManager.colorMask(true, true, true, false);
        }
        worldrenderer.setTranslation(0.0D, 0.0D, 0.0D);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.matrixMode(5889);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableDepth();
    }

    private void rotateAndBlurSkybox() {
        this.mc.getTextureManager().bindTexture(this.backgroundTexture);
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, this.imageResolution, this.imageResolution);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.colorMask(true, true, true, false);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        GlStateManager.disableAlpha();
        int i = 3;
        float scale = getScaledScreen();
        float width = this.width / scale;
        float height = this.height / scale;
        for (int j = 0; j < i; j++) {
            float f = 1.0F / (j + 1);
            float f1 = (j - i / 2.0F) / this.imageResolution;//.0F
            worldrenderer.pos(width, height, this.zLevel).tex((0.0F + f1), 1.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
            worldrenderer.pos(width, 0.0D, this.zLevel).tex((1.0F + f1), 1.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
            worldrenderer.pos(0.0D, 0.0D, this.zLevel).tex((1.0F + f1), 0.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
            worldrenderer.pos(0.0D, height, this.zLevel).tex((0.0F + f1), 0.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
        }
        tessellator.draw();
        GlStateManager.enableAlpha();
        GlStateManager.colorMask(true, true, true, true);
    }

    private void renderSkybox(float partialTicks) {
        float scale = getScaledScreen();
        float width = this.width / scale;
        float height = this.height / scale;
        this.mc.getFramebuffer().unbindFramebuffer();
        GlStateManager.viewport(0, 0, this.imageResolution, this.imageResolution);
        drawPanorama(partialTicks);
        rotateAndBlurSkybox();
        rotateAndBlurSkybox();
        rotateAndBlurSkybox();
        rotateAndBlurSkybox();
        rotateAndBlurSkybox();
        rotateAndBlurSkybox();
        rotateAndBlurSkybox();
        this.mc.getFramebuffer().bindFramebuffer(true);
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        float f = (width > height) ? (120.0F / width) : (120.0F / height);
        float f1 = height * f / this.imageResolution;
        float f2 = width * f / this.imageResolution;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(0.0D, height, this.zLevel).tex((0.5F - f1), (0.5F + f2)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();//tex
        worldrenderer.pos(width, height, this.zLevel).tex((0.5F - f1), (0.5F - f2)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        worldrenderer.pos(width, 0.0D, this.zLevel).tex((0.5F + f1), (0.5F - f2)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        worldrenderer.pos(0.0D, 0.0D, this.zLevel).tex((0.5F + f1), (0.5F + f2)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        tessellator.draw();
    }

    public void draw(int mouseX, int mouseY, float partialTicks) {
        GL11.glDisable(3008);
        renderSkybox(partialTicks);
        GL11.glEnable(3008);
    }
}