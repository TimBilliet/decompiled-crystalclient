package co.crystaldev.client.util;

import co.crystaldev.client.Reference;
import co.crystaldev.client.util.objects.resources.MipMapSimpleTexture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class ClientTextureManager {
    private static ClientTextureManager INSTANCE;

    private static final IntBuffer dataBuffer = GLAllocation.createDirectIntBuffer(4194304);
    private final Map<ResourceLocation, ITextureObject> mipMapTextures = new HashMap<>();

    private final IResourceManager resourceManager;

    public ClientTextureManager(IResourceManager resourceManagerIn) {
        INSTANCE = this;
        this.resourceManager = resourceManagerIn;
    }

    public void bindTextureMipmapped(ResourceLocation resourceLocation) {
//        MipMapSimpleTexture mipMapSimpleTexture = null;
//        System.out.println("bindtexturemipmapped");
        MipMapSimpleTexture mipMapSimpleTexture = new MipMapSimpleTexture(resourceLocation);
        ITextureObject textureObj = this.mipMapTextures.get(resourceLocation);
        if (textureObj == null) {
            mipMapSimpleTexture = new MipMapSimpleTexture(resourceLocation);
            loadTextureMipMap(resourceLocation, mipMapSimpleTexture);
        }
//        if (mipMapSimpleTexture == null) {
//            System.out.println("mimmaptexture niet ingesteld");
//      mipMapSimpleTexture = new MipMapSimpleTexture(resourceLocation);
//      loadTextureMipMap(resourceLocation, mipMapSimpleTexture);
//        }
//        if (mipMapSimpleTexture != null) {
            GlStateManager.bindTexture(mipMapSimpleTexture.getGlTextureId());
//        }
    }

    public void bindTexture(ITextureObject texture) {
        if (texture == null)
            return;
        if (Reflector.Config$isShaders()) {
            Reflector.ShadersTex$bindTexture(texture);
        } else {
            GlStateManager.bindTexture(texture.getGlTextureId());
        }
    }

    public boolean loadTextureMipMap(ResourceLocation resourceLocation) {
        ITextureObject textureObj = this.mipMapTextures.get(resourceLocation);
        if (textureObj == null)
            return loadTextureMipMap(resourceLocation, new MipMapSimpleTexture(resourceLocation));
        return false;
    }

    public boolean loadTextureMipMap(ResourceLocation resourceLocation, ITextureObject textureObj) {
        boolean flag = true;
        try {
            textureObj.loadTexture(this.resourceManager);
        } catch (IOException ex) {
            Reference.LOGGER.warn("Failed to load texture: " + resourceLocation, ex);
            this.mipMapTextures.put(resourceLocation, textureObj);
            flag = false;
        } catch (Throwable t) {
            CrashReport crashReport = CrashReport.makeCrashReport(t, "Registering texture");
            CrashReportCategory category = crashReport.makeCategory("Resource location being registered");
            category.addCrashSection("Resource location", resourceLocation);
            category.addCrashSectionCallable("Texture object class", () -> textureObj.getClass().getName());
            throw new ReportedException(crashReport);
        }
        this.mipMapTextures.put(resourceLocation, textureObj);
        return flag;
    }

    public static void uploadTexture(int glTextureId, BufferedImage image) {
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        ByteBuffer buffer = BufferUtils.createByteBuffer(pixels.length * 4);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) (pixel >> 16 & 0xFF));
                buffer.put((byte) (pixel >> 8 & 0xFF));
                buffer.put((byte) (pixel & 0xFF));
                buffer.put((byte) (pixel >> 24 & 0xFF));
            }
        }
        buffer.flip();
        GL11.glBindTexture(3553, glTextureId);
        GL11.glTexParameteri(3553, 10242, 33071);
        GL11.glTexParameteri(3553, 10243, 33071);
        GL11.glTexParameteri(3553, 10241, 9728);
        GL11.glTexParameteri(3553, 10240, 9728);
        GL11.glTexImage2D(3553, 0, 32856, image.getWidth(), image.getHeight(), 0, 6408, 5121, buffer);
    }

    public static int uploadTextureImageAllocateMipMap(int glTextureId, BufferedImage textureImage, boolean blur, boolean clamp) {
        allocateTexture(glTextureId, 0, textureImage.getWidth(), textureImage.getHeight(), 1.0F);
        return uploadTextureImageSubMipMap(glTextureId, textureImage, 0, 0, blur, clamp);
    }

    private static void allocateTexture(int glTextureId, int p_147946_1_, int width, int height, float p_147946_4_) {
        GL11.glDeleteTextures(glTextureId);
        GL11.glBindTexture(3553, glTextureId);
        GL11.glTexImage2D(3553, 0, 6408, width, height, 0, 32993, 33639, (IntBuffer) null);
    }

    private static int uploadTextureImageSubMipMap(int glTextureId, BufferedImage image, int xOffset, int yOffset, boolean blur, boolean clamp) {
        GL11.glBindTexture(3553, glTextureId);
        uploadTextureImageSubImplMipMap(image, xOffset, yOffset, blur, clamp);
        GL30.glGenerateMipmap(3553);
        return glTextureId;
    }

    private static void uploadTextureImageSubImplMipMap(BufferedImage image, int xOffset, int yOffset, boolean blur, boolean clamp) {
        int i = image.getWidth();
        int j = image.getHeight();
        int k = 4194304 / i;
        int[] intArr = new int[k * i];
        GL11.glTexParameteri(3553, 10241, 9987);
        setTextureClamped(clamp);
        int l;
        for (l = 0; l < i * j; l += i * k) {
            int i1 = l / i;
            int j1 = Math.min(k, j - i1);
            int k1 = i * j1;
            image.getRGB(0, i1, i, j1, intArr, 0, i);
            copyToBuffer(intArr, k1);
            GL11.glTexSubImage2D(3553, 0, xOffset, yOffset + i1, i, j1, 32993, 33639, dataBuffer);
        }
    }

    private static void setTextureClamped(boolean clamp) {
        if (clamp) {
            GL11.glTexParameteri(3553, 10242, 10496);
            GL11.glTexParameteri(3553, 10243, 10496);
        } else {
            GL11.glTexParameteri(3553, 10242, 10497);
            GL11.glTexParameteri(3553, 10243, 10497);
        }
    }

    private static void copyToBuffer(int[] p_110990_0_, int p_110990_1_) {
        copyToBufferPos(p_110990_0_, 0, p_110990_1_);
    }

    private static void copyToBufferPos(int[] p_110994_0_, int p_110994_1_, int p_110994_2_) {
        int[] aint = p_110994_0_;
        if ((Minecraft.getMinecraft()).gameSettings.anaglyph)
            aint = updateAnaglyph(p_110994_0_);
        dataBuffer.clear();
        dataBuffer.put(aint, p_110994_1_, p_110994_2_);
        dataBuffer.position(0).limit(p_110994_2_);
    }

    public static int[] updateAnaglyph(int[] p_110985_0_) {
        int[] aint = new int[p_110985_0_.length];
        for (int i = 0; i < p_110985_0_.length; i++) {
            int j = p_110985_0_[i] >> 24 & 0xFF;
            int k = p_110985_0_[i] >> 16 & 0xFF;
            int l = p_110985_0_[i] >> 8 & 0xFF;
            int i1 = p_110985_0_[i] & 0xFF;
            int j1 = (k * 30 + l * 59 + i1 * 11) / 100;
            int k1 = (k * 30 + l * 70) / 100;
            int l1 = (k * 30 + i1 * 70) / 100;
            aint[i] = j << 24 | j1 << 16 | k1 << 8 | l1;
        }
        return aint;
    }

    public ITextureObject getTexture(ResourceLocation textureLocation) {
        return this.mipMapTextures.get(textureLocation);
    }

    public static ClientTextureManager getInstance() {
        return (INSTANCE == null) ? new ClientTextureManager(Minecraft.getMinecraft().getResourceManager()) : INSTANCE;
    }
}