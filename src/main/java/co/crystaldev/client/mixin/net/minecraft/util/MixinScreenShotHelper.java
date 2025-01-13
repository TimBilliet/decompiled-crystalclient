package co.crystaldev.client.mixin.net.minecraft.util;

import co.crystaldev.client.Client;
import co.crystaldev.client.util.enums.ChatColor;
import co.crystaldev.client.util.task.ScreenshotTask;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ScreenShotHelper;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;
import java.nio.IntBuffer;

@Mixin({ScreenShotHelper.class})
public abstract class MixinScreenShotHelper {
    @Shadow
    private static IntBuffer pixelBuffer;

    @Shadow
    private static int[] pixelValues;

    @Shadow
    @Final
    private static Logger logger;

    /**
     * @author
     */
    @Overwrite(aliases = {"saveScreenshot"})
    public static IChatComponent saveScreenshot(File gameDirectory, int width, int height, Framebuffer buffer) {
        try {
            File directory = new File(gameDirectory, "screenshots");
            if (!directory.exists())
                directory.mkdir();
            if (OpenGlHelper.isFramebufferEnabled()) {
                width = buffer.framebufferWidth;
                height = buffer.framebufferHeight;
            }
            int scale = width * height;
            if (pixelBuffer == null || pixelBuffer.capacity() < scale) {
                pixelBuffer = BufferUtils.createIntBuffer(scale);
                pixelValues = new int[scale];
            }
            GL11.glPixelStorei(3333, 1);
            GL11.glPixelStorei(3317, 1);
            pixelBuffer.clear();
            if (OpenGlHelper.isFramebufferEnabled()) {
                GlStateManager.bindTexture(buffer.framebufferTexture);
                GL11.glGetTexImage(3553, 0, 32993, 33639, pixelBuffer);
            } else {
                GL11.glReadPixels(0, 0, width, height, 32993, 33639, pixelBuffer);
            }
            pixelBuffer.get(pixelValues);
            ScreenshotTask screenshotTask = new ScreenshotTask(directory, width, height, pixelValues, buffer);
            (new Thread((Runnable) screenshotTask)).start();
        } catch (Exception ex) {
            logger.warn("Couldn't save screenshot", ex);
            return (IChatComponent) new ChatComponentTranslation("screenshot.failure", new Object[]{ex.getMessage()});
        }
        return (IChatComponent) new ChatComponentText(ChatColor.translate(Client.getPrefix()) + " Taking screenshot...");
    }
}
