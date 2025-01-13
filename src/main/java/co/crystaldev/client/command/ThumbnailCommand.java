package co.crystaldev.client.command;

import co.crystaldev.client.Client;
import co.crystaldev.client.Reference;
import co.crystaldev.client.Resources;
import co.crystaldev.client.command.base.AbstractCommand;
import co.crystaldev.client.command.base.CommandInfo;
import co.crystaldev.client.command.base.args.CommandArguments;
import co.crystaldev.client.cosmetic.CosmeticCache;
import co.crystaldev.client.cosmetic.CosmeticManager;
import co.crystaldev.client.cosmetic.CosmeticPlayer;
import co.crystaldev.client.cosmetic.CosmeticType;
import co.crystaldev.client.cosmetic.base.Cosmetic;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.SubscribeEvent;
import co.crystaldev.client.event.impl.render.RenderOverlayEvent;
import co.crystaldev.client.gui.screens.ScreenCosmetics;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.resources.cosmetic.ICosmeticTexture;
//import com.madgag.gif.fmsware.AnimatedGifEncoder;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

import mchorse.emoticons.common.EmoteAPI;
import mchorse.emoticons.common.emotes.Emote;
import mchorse.emoticons.common.emotes.Emotes;
import mchorse.emoticons.cosmetic.emote.IUserEmoteData;
import mchorse.emoticons.cosmetic.emote.UserEmoticonData;
import com.madgag.gif.fmsware.AnimatedGifEncoder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

@CommandInfo(name = "thumbnail", description = "Generate cosmetic thumbnails", usage = {"thumbnail [filter]"})
public class ThumbnailCommand extends AbstractCommand {
    private static boolean isRendering = false;

    public static boolean isRendering() {
        return isRendering;
    }

    private static final int BACKGROUND_COLOR = (new Color(0, 255, 0, 255)).getRGB();

    private IntBuffer pixelBuffer;

    private int[] pixelValues;

    private final Framebuffer buffer = Minecraft.getMinecraft().getFramebuffer();

    private static final Stack<Cosmetic> cosmetics = new Stack<>();

    private static File dir;

    private final List<Cosmetic> completedEmotes = new ArrayList<>();

    private final Map<Cosmetic, List<File>> gifLocations = new HashMap<>();

    private int currentGifFrame = 0;

    private int emoteFinishedFrame = 0;

    private boolean waiting = false;

    public ThumbnailCommand() {
        EventBus.register(this);
    }

    public void execute(ICommandSender sender, CommandArguments arguments) {
        Client.sendMessage("Generating thumbnails...", true);
        this.gifLocations.clear();
        List<String> filter = new ArrayList<>();
        if (!arguments.isEmpty())
            filter.addAll((Collection<? extends String>) arguments.getArguments().stream().map(arg -> arg.getAsString().toLowerCase()).collect(Collectors.toList()));
        File dir = new File(Client.getClientRunDirectory(), "generated-thumbnails");
        if (!dir.exists())
            dir.mkdirs();
        ThumbnailCommand.dir = dir;
        cosmetics.clear();
        for (CosmeticType type : CosmeticType.values()) {
            if (type != CosmeticType.COLOR)
                for (Cosmetic cosmetic : CosmeticManager.getInstance().getAllCosmetics()) {
                    if (cosmetic.getType() == type && (filter.isEmpty() || filter.stream().anyMatch(f -> f.equalsIgnoreCase(cosmetic.getName()))))
                        cosmetics.push(cosmetic);
                }
        }
    }

    @SubscribeEvent(priority = 5)
    public void onRenderOverlay(RenderOverlayEvent.All event) {
        if (this.waiting)
            if (this.currentGifFrame++ > 50) {
                this.waiting = false;
                this.currentGifFrame = 0;
            } else {
                return;
            }
        if (!cosmetics.isEmpty()) {
            isRendering = true;
            Cosmetic next = cosmetics.peek();
            if (next instanceof co.crystaldev.client.cosmetic.type.wings.Wings || next instanceof co.crystaldev.client.cosmetic.type.cloak.Cloak || next instanceof co.crystaldev.client.cosmetic.type.cloak.AnimatedCloak) {
                if (next.isAnimated()) {
                    if (handleAnimatedCosmeticThumbnail(next)) {
                        cosmetics.pop();
                        this.currentGifFrame = 0;
                    }
                } else {
                    handleCosmeticThumbnail(cosmetics.pop());
                }
            } else if (next instanceof co.crystaldev.client.cosmetic.type.Emoticon) {
                if (handleEmoteThumbnail(next)) {
                    cosmetics.pop();
                    this.currentGifFrame = 0;
                    this.waiting = true;
                }
            } else {
                cosmetics.pop();
            }
        } else {
            if (!this.gifLocations.isEmpty()) {
                File completedGifs = new File(dir, "completed-gifs");
                completedGifs.mkdirs();
                Iterator<Map.Entry<Cosmetic, List<File>>> iterator = this.gifLocations.entrySet().iterator();
                while (iterator.hasNext()) {
                    FileOutputStream stream;
                    Map.Entry<Cosmetic, List<File>> next = iterator.next();
                    Cosmetic cosmetic = next.getKey();
                    List<File> frames = next.getValue();
                    File outputGif = new File(completedGifs, cosmetic.getName() + ".gif");
                    AnimatedGifEncoder encoder = new AnimatedGifEncoder();
                    encoder.setQuality(35);
                    encoder.setDelay(50);
                    encoder.setRepeat(0);
                    try {
                        if (!outputGif.exists())
                            outputGif.createNewFile();
                        if (!encoder.start(stream = new FileOutputStream(outputGif))) {
                            Reference.LOGGER.error("GIF file creation failed");
                            iterator.remove();
                            continue;
                        }
                    } catch (IOException ex) {
                        iterator.remove();
                        Reference.LOGGER.error("Unable to write gif to file for cosmetic " + cosmetic.getName(), ex);
                        continue;
                    }
                    int index = 0;
                    for (File file : frames) {
//                        if (cosmetic.getType() == CosmeticType.EMOTE && (Emotes.get(cosmetic.getName())).looping && ++index < frames.size() / 4)
//                            continue;
                        try {
                            encoder.addFrame(ImageIO.read(file));
                        } catch (IOException ex) {
                            Reference.LOGGER.error("Unable to add frame " + file + " to gif", ex);
                        }
                    }
                    try {
                        stream.close();
                        File f = new File(dir, cosmetic.getType().getInternalName() + "_" + cosmetic.getName());
                        if (f.exists())
                            FileUtils.deleteDirectory(f);
                    } catch (IOException ex) {
                        Reference.LOGGER.error("Unable to close stream", ex);
                    }
                    iterator.remove();
                }
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            }
            isRendering = false;
        }
    }

    private boolean handleEmoteThumbnail(Cosmetic next) {
        IUserEmoteData data = UserEmoticonData.get((Minecraft.getMinecraft()).thePlayer);
        Emote emote = Emotes.get(next.getName());
        if (data != null && emote != null) {
            if (data.getEmote() == null)
                if (this.completedEmotes.contains(next)) {
                    if (this.emoteFinishedFrame == 0) {
                        this.emoteFinishedFrame = this.currentGifFrame + 5;
                    } else if (this.currentGifFrame > this.emoteFinishedFrame || emote.looping) {
                        this.emoteFinishedFrame = 0;
                        return true;
                    }
                } else {
                    this.completedEmotes.add(next);
                    EmoteAPI.setEmoteClient(next.getName(), (EntityPlayer) (Minecraft.getMinecraft()).thePlayer);
                    this.currentGifFrame = 0;
                }
            this.currentGifFrame++;
            int width = 170, height = 170;
            ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
            GL11.glPushMatrix();
            GL11.glScaled(2.0D / res.getScaleFactor(), 2.0D / res.getScaleFactor(), 1.0D);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderUtils.drawCustomSizedResource(Resources.EMOTE_BACKGROUND, 0, 0, width / 2, height / 2);
            ScreenCosmetics.setRenderingPlayer(true);
            EntityPlayerSP entityPlayerSP = (Minecraft.getMinecraft()).thePlayer;
            CosmeticPlayer cp = CosmeticCache.getInstance().fromPlayer((EntityPlayer) entityPlayerSP);
            boolean shouldUpdate = cp.isShouldUpdateCosmetic();
            cp.setShouldUpdateCosmetic(false);
            RenderUtils.drawEntityOnScreen(width / 4, (int) (width / 2.0D * 0.9375D), (int) (width / 2.0D * 0.46875D), 0.0F, 0.0F,
                    next.getType().isFront() ? 20 : 200, (EntityLivingBase) entityPlayerSP);
            cp.setShouldUpdateCosmetic(shouldUpdate);
            ScreenCosmetics.setRenderingPlayer(false);
            GL11.glPopMatrix();
            Resolution resolution = new Resolution(width, height);
            setupScreenshot(resolution);
            File completed = screenshot(resolution, "emote_" + next.getName(), next.getType().getInternalName() + "_" + next.getName() + "_" + this.currentGifFrame);
            if (completed != null)
                ((List<File>) this.gifLocations.computeIfAbsent(next, c -> new ArrayList())).add(completed);
        }
        return false;
    }

    private void handleCosmeticThumbnail(Cosmetic next) {
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        GL11.glPushMatrix();
        GL11.glScaled(2.0D / res.getScaleFactor(), 2.0D / res.getScaleFactor(), 1.0D);
        RenderUtils.drawRect(0.0F, 0.0F, 512.0F, 512.0F, BACKGROUND_COLOR);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        ScreenCosmetics.setRenderingPlayer(true);
        CosmeticPlayer player = CosmeticCache.getInstance().fromPlayer((EntityPlayer) (Minecraft.getMinecraft()).thePlayer);
        Cosmetic cloak = player.getCloak(), wings = player.getWings();
        ResourceLocation cape = player.getLocationOfCape();
        player.setLocationOfCape(null);
        player.setCloak(null);
        player.setWings(null);
        switch (next.getType()) {
            case CLOAK:
                player.setCloak(next);
                break;
            case WINGS:
                player.setWings(next);
                break;
        }
        boolean shouldUpdate = player.isShouldUpdateCosmetic();
        player.setShouldUpdateCosmetic(false);
        RenderUtils.drawEntityOnScreen(256, 480, 240, 0.0F, 0.0F,
                next.getType().isFront() ? 20 : 200, (EntityLivingBase) (Minecraft.getMinecraft()).thePlayer);
        player.setShouldUpdateCosmetic(shouldUpdate);
        player.setLocationOfCape(cape);
        player.setCloak(cloak);
        player.setWings(wings);
        ScreenCosmetics.setRenderingPlayer(false);
        GL11.glPopMatrix();
        Resolution resolution = new Resolution(1024, 1024);
        setupScreenshot(resolution);
        screenshot(resolution, null, next.getType().getInternalName() + "_" + next.getName());
    }

    private boolean handleAnimatedCosmeticThumbnail(Cosmetic next) {
        if (!next.isAnimated()) {
            handleCosmeticThumbnail(next);
            return true;
        }
        ICosmeticTexture tex = (ICosmeticTexture) next.getTexture();
        if (!tex.isTextureLoaded())
            try {
                next.getTexture().loadTexture(Minecraft.getMinecraft().getResourceManager());
            } catch (IOException ex) {
                Reference.LOGGER.error("Unable to load texture", ex);
            }
        boolean isInMap = this.gifLocations.keySet().stream().anyMatch(next::equals);
        if (tex.getCurrentFrame() != 0 && !isInMap)
            return false;
        if (this.currentGifFrame == tex.getFrameCount() - 1)
            return true;
        this.currentGifFrame = tex.getCurrentFrame();
        int width = 512, height = 512;
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        GL11.glPushMatrix();
        GL11.glScaled(2.0D / res.getScaleFactor(), 2.0D / res.getScaleFactor(), 1.0D);
        RenderUtils.drawGradientRect(0, 0, width / 2, height / 2, -16777216L, -14474461L);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        ScreenCosmetics.setRenderingPlayer(true);
        EntityPlayerSP entity = (Minecraft.getMinecraft()).thePlayer;
        CosmeticPlayer player = CosmeticCache.getInstance().fromPlayer((EntityPlayer) entity);
        Cosmetic cloak = player.getCloak(), wings = player.getWings();
        ResourceLocation cape = player.getLocationOfCape();
        player.setLocationOfCape(null);
        player.setCloak(null);
        player.setWings(null);
        int ticksExisted = entity.ticksExisted;
        entity.ticksExisted = 50;
        switch (next.getType()) {
            case CLOAK:
                player.setCloak(next);
                break;
            case WINGS:
                player.setWings(next);
                break;
        }
        boolean shouldUpdate = player.isShouldUpdateCosmetic();
        player.setShouldUpdateCosmetic(false);
        RenderUtils.drawEntityOnScreen(width / 4, (int) (width / 2.0D * 0.9375D), (int) (width / 2.0D * 0.46875D), 0.0F, 0.0F,
                next.getType().isFront() ? 20 : 200, (EntityLivingBase) entity);
        player.setShouldUpdateCosmetic(shouldUpdate);
        player.setLocationOfCape(cape);
        player.setCloak(cloak);
        player.setWings(wings);
        entity.ticksExisted = ticksExisted;
        ScreenCosmetics.setRenderingPlayer(false);
        GL11.glPopMatrix();
        Resolution resolution = new Resolution(width, height);
        setupScreenshot(resolution);
        File completed = screenshot(resolution, next.getType().getInternalName() + "_" + next.getName(), next.getType().getInternalName() + "_" + next.getName() + "_" + this.currentGifFrame);
        if (completed != null)
            ((List<File>) this.gifLocations.computeIfAbsent(next, c -> new ArrayList())).add(completed);
        return false;
    }

    private void setupScreenshot(Resolution resolution) {
        if (OpenGlHelper.isFramebufferEnabled()) {
            resolution.width = this.buffer.framebufferWidth;
            resolution.height = this.buffer.framebufferHeight;
        }
        int scale = resolution.width * resolution.height;
        if (this.pixelBuffer == null || this.pixelBuffer.capacity() < scale) {
            this.pixelBuffer = BufferUtils.createIntBuffer(scale);
            this.pixelValues = new int[scale];
        }
        GL11.glPixelStorei(3333, 1);
        GL11.glPixelStorei(3317, 1);
        this.pixelBuffer.clear();
        if (OpenGlHelper.isFramebufferEnabled()) {
            GlStateManager.bindTexture(this.buffer.framebufferTexture);
            GL11.glGetTexImage(3553, 0, 32993, 33639, this.pixelBuffer);
        } else {
            GL11.glReadPixels(0, 0, resolution.width, resolution.height, 32993, 33639, this.pixelBuffer);
        }
        this.pixelBuffer.get(this.pixelValues);
    }

    private File screenshot(Resolution resolution, String folder, String fileName) {
        processPixelValues(this.pixelValues, resolution.width, resolution.height);
        try {
            BufferedImage currentImage;
            if (OpenGlHelper.isFramebufferEnabled()) {
                currentImage = new BufferedImage(resolution.initialWidth, resolution.initialHeight, 2);
                Graphics2D g2d = currentImage.createGraphics();
                g2d.setBackground(new Color(0, 0, 0, 0));
                g2d.clearRect(0, 0, resolution.initialWidth, resolution.initialHeight);
                g2d.dispose();
                for (int h = this.buffer.framebufferTextureHeight - this.buffer.framebufferHeight, heightSize = h; h < resolution.initialWidth; h++) {
                    for (int w = 0; w < resolution.initialWidth; w++) {
                        int val = h * this.buffer.framebufferTextureWidth + w;
                        if (val < this.pixelValues.length) {
                            int color = this.pixelValues[val];
                            if (color != BACKGROUND_COLOR)
                                currentImage.setRGB(w, h - heightSize, color);
                        }
                    }
                }
            } else {
                currentImage = new BufferedImage(resolution.initialWidth, resolution.initialHeight, 2);
                currentImage.setRGB(0, 0, resolution.initialWidth, resolution.initialHeight, this.pixelValues, 0, resolution.initialWidth);
            }
            File f = null;
            if (folder != null && !(f = new File(dir, folder)).exists())
                f.mkdirs();
            ImageIO.write(currentImage, "png", f = (folder == null) ? new File(dir, fileName + ".png") : new File(f, fileName + ".png"));
            return f;
        } catch (Exception ex) {
            Reference.LOGGER.error("Unable to save cosmetic thumbnail", ex);
            return null;
        }
    }

    private void processPixelValues(int[] pixels, int displayWidth, int displayHeight) {
        int[] xValues = new int[displayWidth];
        for (int yValues = displayHeight / 2, val = 0; val < yValues; val++) {
            System.arraycopy(pixels, val * displayWidth, xValues, 0, displayWidth);
            System.arraycopy(pixels, (displayHeight - 1 - val) * displayWidth, pixels, val * displayWidth, displayWidth);
            System.arraycopy(xValues, 0, pixels, (displayHeight - 1 - val) * displayWidth, displayWidth);
        }
    }

    private static class Resolution {
        public int width;

        public int height;

        public final int initialWidth;

        public final int initialHeight;

        public Resolution(int width, int height) {
            this.width = this.initialWidth = width;
            this.height = this.initialHeight = height;
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\command\ThumbnailCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */