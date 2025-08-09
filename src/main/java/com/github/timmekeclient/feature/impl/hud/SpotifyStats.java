package com.github.timmekeclient.feature.impl.hud;

import com.github.timmekeclient.Reference;
import com.github.timmekeclient.feature.annotations.properties.ModuleInfo;
import com.github.timmekeclient.feature.annotations.properties.Toggle;
import com.github.timmekeclient.feature.base.Category;
import com.github.timmekeclient.feature.base.HudModuleBackground;
import com.github.timmekeclient.util.ColorObject;
import com.github.timmekeclient.util.RenderUtils;
import com.github.timmekeclient.util.enums.AnchorRegion;
import com.github.timmekeclient.util.objects.ModulePosition;
import de.labystudio.spotifyapi.SpotifyAPI;
import de.labystudio.spotifyapi.SpotifyAPIFactory;
import de.labystudio.spotifyapi.SpotifyListener;
import de.labystudio.spotifyapi.model.Track;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;

@ModuleInfo(name = "Spotify Stats", description = "Show the spotify song that's currently playing", category = Category.HUD)

public class SpotifyStats extends HudModuleBackground implements SpotifyListener {

    @Toggle(label = "Show Album Cover")
    public boolean showCover = true;

    @Toggle(label = "Show Progress")
    public boolean showProgress = true;

    @Toggle(label = "Periodically Update Progress")
    public boolean updateProgress = false;

    private SpotifyAPI spotifyAPI;
    private Track currentTrack;
    private int length;
    private int progress;
    private ResourceLocation cover;
    private boolean isPlaying = true;

    public SpotifyStats() {
        enabled = false;
        hasInfoHud = false;
        height = 52;
        width = 180;
        position = new ModulePosition(AnchorRegion.TOP_CENTER, 0.0F, 5.0F);
        spotifyAPI = SpotifyAPIFactory.createInitialized();
        spotifyAPI.registerListener(this);
        currentTrack = new Track("", "", "No song playing", 0, null);
    }

    @Override
    public void configPostInit() {
        super.configPostInit();
        setOptionVisibility("Periodically Update Progress", f -> this.showProgress);
    }

    @Override
    public void enable() {
        super.enable();
        System.out.println("on enable spotifystats");
        spotifyAPI = SpotifyAPIFactory.createInitialized();
        spotifyAPI.registerListener(this);
    }

    @Override
    public void disable() {
        super.disable();
        if (spotifyAPI != null)
            spotifyAPI.stop();
    }

    @Override
    public String getDisplayText() {
        return "";
    }

    public void draw() {
        if (currentTrack != null) {
            int x = getRenderX();
            int y = getRenderY();
            if (drawBackground)
                drawBackground(x, y, x + width, y + height);
            int iconSize = 45;
            y += 4;
            if (currentTrack.getName().equals("Unknown"))
                RenderUtils.drawString("", x + iconSize - 3, y, textColor);
            else
                RenderUtils.drawString(currentTrack.getName(), x + iconSize - 3, y, textColor);

            y += 10;
            RenderUtils.drawString(currentTrack.getArtist(), x + iconSize - 3, y, textColor);
            y += 10;
            if (!currentTrack.getName().equals("Unknown") && !currentTrack.getName().equals("")) {
                if (isPlaying)
                    RenderUtils.drawString("Playing...", x + iconSize - 3, y, textColor);
                else
                    RenderUtils.drawString("Paused...", x + iconSize - 3, y, textColor);
            }
            y += 18;
            if (showProgress && !currentTrack.getName().equals("Unknown") && !currentTrack.getName().equals("")) {

                RenderUtils.drawString(String.format("%02d:%02d", spotifyAPI.getPosition() / 1000 / 60, spotifyAPI.getPosition() / 1000 % 60), x + 5, y, textColor);
                RenderUtils.drawString(String.format("%02d:%02d", currentTrack.getLength() / 1000 / 60, currentTrack.getLength() / 1000 % 60), x + 150, y, textColor);
                int startx = x + 35;
                RenderUtils.drawRoundedRect(startx, y - 1, x + 145, y + 8, 3, new ColorObject(150, 0, 150, 200).getRGB());
                RenderUtils.drawRoundedRect(startx, y - 1, startx + 110 * ((float) spotifyAPI.getPosition() / currentTrack.getLength()), y + 8, 3, new ColorObject(255, 255, 255, 200).getRGB());
            }
            if (showCover && cover != null) {
                GlStateManager.enableBlend();
                GlStateManager.resetColor();
                this.mc.getTextureManager().bindTexture(cover);
                Gui.drawModalRectWithCustomSizedTexture(x - 3, y - 40, 0.0F, 0.0F, iconSize, iconSize - 9, iconSize, iconSize);
                GlStateManager.disableBlend();
            }
        }
    }

    private void convertCoverImage() {
        mc.addScheduledTask(() -> {
            BufferedImage coverBI = currentTrack.getCoverArt();
            coverBI = coverBI.getSubimage(0, 0, coverBI.getWidth(), coverBI.getHeight() - 55);
            DynamicTexture dynamicTexture = new DynamicTexture(coverBI);
            mc.getTextureManager().loadTexture(new ResourceLocation("timmekeclient", "spotify_track"), dynamicTexture);
            cover = new ResourceLocation("timmekeclient", "spotify_track");
        });
    }

    @Override
    public void onConnect() {
        Reference.LOGGER.info("Connected to Spotify");
    }

    @Override
    public void onTrackChanged(Track track) {
        currentTrack = track;
        convertCoverImage();
        System.out.println(currentTrack.getLength() / 1000 / 60 + ":" + currentTrack.getLength() / 1000 % 60);
        progress = spotifyAPI.getPosition();
        length = currentTrack.getLength();
    }

    @Override
    public void onPositionChanged(int position) {
        progress = spotifyAPI.getPosition();
    }

    @Override
    public void onPlayBackChanged(boolean isPlaying) {
        this.isPlaying = isPlaying;
        if (spotifyAPI.hasTrack()) {
            currentTrack = spotifyAPI.getTrack();
            convertCoverImage();
            length = currentTrack.getLength();
            progress = spotifyAPI.getPosition();
        }
    }

    @Override
    public void onSync() {

    }

    @Override
    public void onDisconnect(Exception exception) {
    }

}
