package co.crystaldev.client;

import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.GuiOptions;
import co.crystaldev.client.gui.ease.Animation;
import co.crystaldev.client.gui.ease.Easing;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.shader.Framebuffer;

public class SplashScreen {
    private static final int PROGRESS_STEPS = 9;

    private static boolean complete = false;

    private static Animation animation;

    private static String text = "Starting Minecraft...";

    public static int getProgress() {
        return progress;
    }

    private static int progress = 0;

    public static void renderSplash(TextureManager textureManager) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int scale = sr.getScaleFactor();
        Framebuffer framebuffer = new Framebuffer(sr.getScaledWidth() * scale, sr.getScaledHeight() * scale, true);
        framebuffer.bindFramebuffer(false);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, sr.getScaledWidth(), sr.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();
        textureManager.bindTexture(Resources.SPLASH_SCREEN);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Gui.drawScaledCustomSizeModalRect(0, 0, 0.0F, 0.0F, 1920, 1080, sr
                .getScaledWidth(), sr.getScaledHeight(), 1920.0F, 1080.0F);
        renderProgress(sr);
        if (animation != null) {
            Gui.drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), (new Color(0, 0, 0, (int) (255.0F * animation.getValue()))).getRGB());
            GlStateManager.resetColor();
        }
        framebuffer.unbindFramebuffer();
        framebuffer.framebufferRender(sr.getScaledWidth() * scale, sr.getScaledHeight() * scale);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        Minecraft.getMinecraft().updateDisplay();
    }

    private static void update() {
        if (Minecraft.getMinecraft().getTextureManager() != null)
            renderSplash(Minecraft.getMinecraft().getTextureManager());
    }

    private static void renderProgress(ScaledResolution sr) {
        if ((Minecraft.getMinecraft()).gameSettings == null || Minecraft.getMinecraft().getTextureManager() == null)
            return;
        double progressBarWidth = progress / 9.0D * sr.getScaledWidth();
        Gui.drawRect(0, sr.getScaledHeight() - 25, sr.getScaledWidth(), sr.getScaledHeight(), (new Color(0, 0, 0, 50)).getRGB());
        Gui.drawRect(0, sr.getScaledHeight() - 3, sr.getScaledWidth(), sr.getScaledHeight(), (new Color(0, 0, 0, 50)).getRGB());
        Gui.drawRect(0, sr.getScaledHeight() - 3, (int) progressBarWidth, sr.getScaledHeight(), GuiOptions.Theme.DEFAULT.mainColor.getRGB());
        Fonts.NUNITO_SEMI_BOLD_16.drawCenteredString(text + " (" + (int) (progress / 9.0D * 100.0D) + "%)", sr.getScaledWidth() / 2, sr.getScaledHeight() - 15, 16777215);
    }

    public static void markComplete() {
        complete = true;
        animation = new Animation(0.4F, 0.0F, 1.0F, Easing.IN_QUAD);
    }

    public static boolean isComplete() {
        return (complete && animation.isComplete());
    }

    public static void setProgress(int amount, String step) {
        Reference.LOGGER.info(step);
        progress = amount;
        text = step;
        update();
    }
}