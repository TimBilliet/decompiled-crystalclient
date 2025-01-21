package co.crystaldev.client.util.task;

import co.crystaldev.client.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadImageTask extends SimpleTexture {
    private final String imageUrl;

    private final IImageBuffer imageBuffer;

    private BufferedImage bufferedImage;

    private boolean executed = false;

    private boolean textureUploaded;

    public DownloadImageTask(String imageUrlIn, IImageBuffer imageBufferIn) {
        super(null);
        this.imageUrl = imageUrlIn;
        this.imageBuffer = imageBufferIn;
    }

    private void checkTextureUploaded() {
        if (!this.textureUploaded &&
                this.bufferedImage != null) {
            if (this.textureLocation != null)
                deleteGlTexture();
            TextureUtil.uploadTextureImage(super.getGlTextureId(), this.bufferedImage);
            this.textureUploaded = true;
        }
    }

    public int getGlTextureId() {
        checkTextureUploaded();
        return super.getGlTextureId();
    }

    public void setBufferedImage(BufferedImage bufferedImageIn) {
        this.bufferedImage = bufferedImageIn;
        if (this.imageBuffer != null)
            this.imageBuffer.skinAvailable();
    }

    public void loadTexture(IResourceManager resourceManager) throws IOException {
        if (this.bufferedImage == null && this.textureLocation != null)
            super.loadTexture(resourceManager);
        if (!this.executed) {
            this.executed = true;
            Thread thread = new Thread(() -> {
                HttpURLConnection conn = null;
                try {
                    conn = (HttpURLConnection) (new URL(this.imageUrl)).openConnection(Minecraft.getMinecraft().getProxy());
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.96 Safari/537.36");
                    conn.setDoInput(true);
                    conn.setDoOutput(false);
                    conn.connect();
                    if (conn.getResponseCode() / 100 == 2) {
                        BufferedImage bufferedimage = TextureUtil.readBufferedImage(conn.getInputStream());
                        if (this.imageBuffer != null)
                            bufferedimage = this.imageBuffer.parseUserSkin(bufferedimage);
                        setBufferedImage(bufferedimage);
                    }
                } catch (Exception ex) {
                    Reference.LOGGER.error("Unable to download image", ex);
                } finally {
                    if (conn != null)
                        conn.disconnect();
                }
            });
            thread.setDaemon(true);
            thread.start();
        }
    }
}
